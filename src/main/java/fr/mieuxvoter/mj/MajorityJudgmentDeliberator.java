package fr.mieuxvoter.mj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;

// Don't worry, these imports are only used for the affine merit approximation, and not used in ranking.
import static java.lang.Math.sin;
import static java.lang.Math.pow;
import static java.lang.Math.PI;
import static java.lang.Math.E;

/**
 * Deliberate (rank proposals) using Majority Judgment.
 *
 * <p>Sorts Proposals by their median Grade. When two proposals share the same median Grade, give
 * reason to the largest group of people that did not give the median Grade.
 *
 * <p>This algorithm is score-based, for performance (and possible parallelization). Each Proposal
 * gets a score, higher (lexicographically) is "better" (depends on the meaning of the Grades). We
 * use Strings instead of Integers or raw Bits for the score. Improve if you feel like it and can
 * benchmark things.
 *
 * <p><a href="https://en.wikipedia.org/wiki/Majority_judgment">More about Majority Judgment</a>
 */
public final class MajorityJudgmentDeliberator implements DeliberatorInterface {

    private boolean favorContestation = true;
    private boolean numerizeScore = false;

    public MajorityJudgmentDeliberator() {
    }

    public MajorityJudgmentDeliberator(boolean favorContestation) {
        this.favorContestation = favorContestation;
    }

    public MajorityJudgmentDeliberator(boolean favorContestation, boolean numerizeScore) {
        this.favorContestation = favorContestation;
        this.numerizeScore = numerizeScore;
    }

    @Override
    public ResultInterface deliberate(TallyInterface tally) throws InvalidTallyException {
        checkTally(tally);

        ProposalTallyInterface[] tallies = tally.getProposalsTallies();
        BigInteger amountOfJudges = tally.getAmountOfJudges();
        Integer amountOfProposals = tally.getAmountOfProposals();

        BigInteger sumOfMerits = BigInteger.ZERO;
        Double sumOfAdjustedMerits = 0.0;

        // O. Compute the (maximum!) merit a 100% EXCELLENT proposal would get.
        BigInteger maxMerit = BigInteger.ONE;
        if (tallies.length > 0) {
            int amountOfGrades = tallies[0].getTally().length;
            BigInteger[] bestTally = new BigInteger[amountOfGrades];
            Arrays.fill(bestTally, BigInteger.ZERO);
            bestTally[bestTally.length - 1] = amountOfJudges;
            maxMerit = computeMerit(new ProposalTally(bestTally), amountOfJudges, this.favorContestation);
        }

        Result result = new Result();
        ProposalResult[] proposalResults = new ProposalResult[amountOfProposals];

        // I. Compute the score and merit of each Proposal
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalTallyInterface proposalTally = tallies[proposalIndex];
            String score = computeScore(proposalTally, amountOfJudges);
            BigInteger merit = computeMerit(proposalTally, amountOfJudges, this.favorContestation);
            sumOfMerits = sumOfMerits.add(merit);
            ProposalTallyAnalysis analysis = new ProposalTallyAnalysis(
                    proposalTally, this.favorContestation
            );
            ProposalResult proposalResult = new ProposalResult();
            proposalResult.setIndex(proposalIndex);
            proposalResult.setScore(score);
            proposalResult.setMerit(merit);
            proposalResult.setAnalysis(analysis);
            // proposalResult.setRank(???); // rank is computed below, AFTER the score pass
            proposalResults[proposalIndex] = proposalResult;
        }

        // II. Sort Proposals by score (lexicographical inverse)
        ProposalResult[] proposalResultsSorted = proposalResults.clone(); // MUST be shallow
        Arrays.sort(
                proposalResultsSorted,
                (Comparator<ProposalResultInterface>) (p0, p1) -> p1.getScore().compareTo(p0.getScore())
        );

        // III. Attribute a rank to each Proposal and compute their relative merit
        int rank = 1;
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalResult proposalResult = proposalResultsSorted[proposalIndex];
            // Attribute the rank
            Integer actualRank = rank;
            if (proposalIndex > 0) {
                ProposalResult proposalResultBefore = proposalResultsSorted[proposalIndex - 1];
                if (proposalResult.getScore().contentEquals(proposalResultBefore.getScore())) {
                    actualRank = proposalResultBefore.getRank();
                }
            }
            proposalResult.setRank(actualRank);
            rank += 1;

            // Adjust (make affine) the merit
            proposalResult.setAffineMerit(adjustMeritToAffine(
                    proposalResult.getMerit(), maxMerit, amountOfJudges
            ));
            sumOfAdjustedMerits += proposalResult.getAffineMerit();
        }

        // Compute the relative merits
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalResult proposalResult = proposalResultsSorted[proposalIndex];
            proposalResult.computeRelativeMerit(sumOfMerits);
            proposalResult.computeRelativeAffineMerit(sumOfAdjustedMerits);
        }

        result.setProposalResults(proposalResults);
        result.setProposalResultsRanked(proposalResultsSorted);

        return result;
    }

    private void checkTally(TallyInterface tally) throws UnbalancedTallyException {
        if (!isTallyCoherent(tally)) {
            throw new IncoherentTallyException();
        }
        if (!isTallyBalanced(tally)) {
            throw new UnbalancedTallyException();
        }
    }

    private boolean isTallyCoherent(TallyInterface tally) {
        for (ProposalTallyInterface proposalTally : tally.getProposalsTallies()) {
            for (BigInteger gradeTally : proposalTally.getTally()) {
                if (0 > gradeTally.compareTo(BigInteger.ZERO)) {
                    return false; // negative tallies are not coherent
                }
            }
        }

        return true;
    }

    private boolean isTallyBalanced(TallyInterface tally) {
        BigInteger amountOfJudges = BigInteger.ZERO;
        boolean firstProposal = true;
        for (ProposalTallyInterface proposalTally : tally.getProposalsTallies()) {
            if (firstProposal) {
                amountOfJudges = proposalTally.getAmountOfJudgments();
                firstProposal = false;
            } else {
                if (0 != amountOfJudges.compareTo(proposalTally.getAmountOfJudgments())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @see this#computeScore(ProposalTallyInterface, BigInteger, Boolean, Boolean) below
     */
    private String computeScore(
            ProposalTallyInterface tally,
            BigInteger amountOfJudges
    ) {
        return computeScore(
                tally,
                amountOfJudges,
                this.favorContestation,
                this.numerizeScore
        );
    }

    /**
     * A higher score means a better rank. Assumes that grades' tallies are provided from "worst"
     * grade to "best" grade.  This score is fast to compute but is not meaningful.
     * For a meaningful scalar value, see this#computeMerit().
     *
     * @param tally             Holds the tallies of each Grade for a single Proposal
     * @param amountOfJudges    Amount of judges participating
     * @param favorContestation Use the lower median when dealing with an even amount of judgments.
     * @param onlyNumbers       Do not use separation characters for the score, ie match `^[0-9]+$`
     * @return the score of the proposal, also known as median gauge.
     */
    private String computeScore(
            ProposalTallyInterface tally,
            BigInteger amountOfJudges,
            Boolean favorContestation,
            Boolean onlyNumbers
    ) {
        ProposalTallyAnalysis analysis = new ProposalTallyAnalysis();
        int amountOfGrades = tally.getTally().length;
        int digitsForGrade = countDigits(amountOfGrades);
        int digitsForGroup = countDigits(amountOfJudges) + 1;

        ProposalTallyInterface currentTally = tally.duplicate();

        StringBuilder score = new StringBuilder();
        for (int i = 0; i < amountOfGrades; i++) {

            analysis.reanalyze(currentTally, favorContestation);

            if (0 < i && onlyNumbers.equals(Boolean.FALSE)) {
                score.append("/");
            }

            score.append(String.format(
                    String.format("%%0%dd", digitsForGrade),
                    analysis.getMedianGrade()
            ));

            if (onlyNumbers.equals(Boolean.FALSE)) {
                score.append("_");
            }

            score.append(String.format(
                    String.format("%%0%dd", digitsForGroup),
                    // amountOfJudges + secondMedianGroupSize * secondMedianGroupSign
                    analysis.getSecondMedianGroupSize()
                            .multiply(
                                    BigInteger.valueOf(
                                            analysis.getSecondMedianGroupSign()
                                    )
                            )
                            // We offset by amountOfJudges to keep a lexicographical order,
                            // which would be broken by any negative number here.
                            .add(amountOfJudges)
            ));

            currentTally.moveJudgments(analysis.getMedianGrade(), analysis.getSecondMedianGrade());
        }

        return score.toString();
    }

    private BigInteger computeMerit(
            ProposalTallyInterface tally,
            BigInteger amountOfJudges,
            Boolean favorContestation
    ) {
        ProposalTallyAnalysis analysis = new ProposalTallyAnalysis();
        analysis.reanalyze(tally, favorContestation);

        int amountOfGrades = tally.getTally().length;

        ProposalTallyInterface currentTally = tally.duplicate();

        BigInteger merit = BigInteger.valueOf(analysis.getMedianGrade());
        Integer cursorGrade = analysis.getMedianGrade();
        Integer minProcessedGrade = cursorGrade;
        Integer maxProcessedGrade = cursorGrade;

        for (int i = 0; i < amountOfGrades - 1; i++) {

            merit = merit.multiply(amountOfJudges);

            if (analysis.getSecondMedianGroupSize().compareTo(BigInteger.ZERO) == 0) {
                continue;
            }

            if (analysis.getSecondMedianGroupSign() > 0) {
                cursorGrade = maxProcessedGrade + 1;
                maxProcessedGrade = cursorGrade;
            } else {
                cursorGrade = minProcessedGrade - 1;
                minProcessedGrade = cursorGrade;
            }

            merit = merit.add(
                    analysis.getSecondMedianGroupSize().multiply(
                            BigInteger.valueOf(analysis.getSecondMedianGroupSign())
                    )
            );

            currentTally.moveJudgments(analysis.getMedianGrade(), cursorGrade);
            analysis.reanalyze(currentTally, favorContestation);
        }

        return merit;
    }

    private Double adjustMeritToAffine(
            BigInteger merit,
            BigInteger maxMerit,
            BigInteger amountOfJudges
    ) {
        double meritNormalized = (new BigDecimal(merit).divide(
                new BigDecimal(maxMerit), 15, RoundingMode.HALF_EVEN
        )).doubleValue();

        double rankNormalized = meritToRankModel(meritNormalized, amountOfJudges.intValue());

        return (1.0 - rankNormalized);
    }

    private double meritToRankModel(double merit, Integer amountOfJudges) {
        int amountOfGrades = 7; // FIXME

        class SigmoidAmplitudeModel {
            final Double coefficient;
            final Double offset;
            final Double origin;
            final Double sin_amplitude;
            final Double sin_origin;
            final Double sin_phase;

            public SigmoidAmplitudeModel(
                    Double coefficient,
                    Double offset,
                    Double origin,
                    Double sin_amplitude,
                    Double sin_origin,
                    Double sin_phase
            ) {
                this.coefficient = coefficient;
                this.offset = offset;
                this.origin = origin;
                this.sin_amplitude = sin_amplitude;
                this.sin_origin = sin_origin;
                this.sin_phase = sin_phase;
            }

            public Double computeAmplitude(Integer amountOfJudges) {
                return
                        this.offset + (this.coefficient / (amountOfJudges - this.origin))
                        +
                        this.sin_amplitude * sin(amountOfJudges * PI + this.sin_phase) / (amountOfJudges - this.sin_origin);
            }
        }

        // Values derived from rough model fitting ; they can be improved
        SigmoidAmplitudeModel[] sam = new SigmoidAmplitudeModel[]{
                new SigmoidAmplitudeModel(0.5151336373041772, 0.0304017096437998, -0.1560819745436698, -0.0642768687910415, 3.7019618565115722, -0.2267673450950530),
                new SigmoidAmplitudeModel(0.8321495032592745, 0.1538010001096599, -10.1403742732170450, 0.1452337649130754, 2.9093303593527824, 0.1670760936959231),
                new SigmoidAmplitudeModel(-0.5832534017217945, 0.3128738036537556, -2.4481699553712186, 1.7698591489043021, 0.0064898411429031, -3.1491904326892173),
                new SigmoidAmplitudeModel(-0.9135479603269890, 0.3121169039235479, -4.0419384013683608, -0.0398619334678863, 2.2608983418537969, -3.5661704309341040),
                new SigmoidAmplitudeModel(-0.0358891062680384, 0.1592742142625385, 0.8473094470570051, -0.1720450496934443, 0.8776512589952787, 0.1900715592340584),
                new SigmoidAmplitudeModel(0.2965479931458628, 0.0309932939590777, -2.7064785369970221, -0.0616634512919992, 3.3069369590264279, -3.3295936102008192),
        };

        Double sumOfAmplitudes = 0.0;
        Double[] amplitudes = new Double[amountOfGrades];
        for (int i = 0; i < amountOfGrades - 1; i++) {
            amplitudes[i] = sam[i].computeAmplitude(amountOfJudges);
            sumOfAmplitudes += amplitudes[i];
        }
        for (int i = 0; i < amountOfGrades - 1; i++) {
            amplitudes[i] = amplitudes[i] / sumOfAmplitudes;
        }

        double tightness = 96.0; // derived from fitting
        double rank = 0.0;  // from 0.0 (exclusive) to 1.0 (inclusive) ; is 'double' enough precision?
        for (int i = 0; i < amountOfGrades - 1; i++) {
            rank += amplitudes[i] * sigmoid(
                    merit,
                    tightness,
                    (2.0 * i + 1.0) / (2.0 * (amountOfGrades - 1))
            );
        }

        return rank;
    }

    private double sigmoid(double x, double tightness, double origin) {
        return 1.0 / (1.0 + pow(E, tightness * (x - origin)));
    }

    private int countDigits(int number) {
        //noinspection StringTemplateMigration
        return ("" + number).length();
    }

    private int countDigits(BigInteger number) {
        //noinspection StringTemplateMigration
        return ("" + number).length();
    }
}
