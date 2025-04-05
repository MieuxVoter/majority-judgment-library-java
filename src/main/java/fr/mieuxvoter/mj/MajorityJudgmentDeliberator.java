package fr.mieuxvoter.mj;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;

import static java.lang.Math.*;

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
                (Comparator<ProposalResultInterface>) (p0, p1) -> p1.getScore().compareTo(p0.getScore()));

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
            proposalResult.setMeritAdjusted(adjustMerit(
                    proposalResult.getMerit(), maxMerit, amountOfJudges
            ));
            sumOfAdjustedMerits += proposalResult.getMeritAdjusted();
        }

        // Compute the relative merits
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalResult proposalResult = proposalResultsSorted[proposalIndex];
            proposalResult.computeMeritAsPercentage(sumOfMerits);
            proposalResult.computeMeritAdjustedAsPercentage(sumOfAdjustedMerits);
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
    private String computeScore(ProposalTallyInterface tally, BigInteger amountOfJudges) {
        return computeScore(tally, amountOfJudges, this.favorContestation, this.numerizeScore);
    }

    /**
     * A higher score means a better rank. Assumes that grades' tallies are provided from "worst"
     * grade to "best" grade.  This score is fast to compute but is not meaningful.
     * For a meaningful scalar value, see computeMerit.
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

    private Double adjustMerit(BigInteger merit, BigInteger maxMerit, BigInteger amountOfJudges) {
        // We're "cheating" here, at the expense of numerical stability
        // We could use a recursive Euclidean div ?
        long precision = 1_000_000_000;
        double meritNormalized = round(merit.multiply(
                BigInteger.valueOf(precision * 10)
        ).divide(maxMerit).doubleValue() / 10.0) / (double) precision;

        double rankNormalized = meritToRankModel(meritNormalized, amountOfJudges.intValue());

//        BigInteger adjustedMerit = BigInteger.ZERO.add(merit);  // copy
//        adjustedMerit = BigInteger.valueOf(round(
//                (1.0 - rankNormalized) * (double) precision
//        )).multiply(maxMerit).divide(
//                BigInteger.valueOf(precision)
//        );
//        return adjustedMerit;

        return (1.0 - rankNormalized);
    }

    private double meritToRankModel(double merit, Integer amountOfJudges) {
        int amountOfGrades = 7; // FIXME

        class SigmoidAmplitudeModel {
            Double coeff;
            Double offset;
            Double origin;

            public SigmoidAmplitudeModel(Double coeff, Double offset, Double origin) {
                this.coeff = coeff;
                this.offset = offset;
                this.origin = origin;
            }

            public Double computeAmplitude(Integer amountOfJudges) {
                return this.offset + (this.coeff / (amountOfJudges - this.origin));
            }
        }

        // Values derived from rough model fitting ; they can be improved
        SigmoidAmplitudeModel[] sam = new SigmoidAmplitudeModel[]{
                new SigmoidAmplitudeModel(0.5918756749697929, 0.0283650284831609, -1.1514768060735074),
                new SigmoidAmplitudeModel(1.2239424469290872, 0.1482010931224683, -16.3552159899377614),
                new SigmoidAmplitudeModel(-0.5592816818757540, 0.3123093902719977, -2.0931675704689443),
                new SigmoidAmplitudeModel(-0.9888738957553507, 0.3136696647459276, -4.8529251973066447),
                new SigmoidAmplitudeModel(-0.0370903838252796, 0.1591017470094314, -0.9399076517275036),
                new SigmoidAmplitudeModel(0.2367789010429392, 0.0324575577968845, -0.8907160168896431),
        };

        // Values derived from rough model fitting by hand ; they can be improved.
//        double[] amplitudes = new double[]{
//                0.358682, 1.09248, 1.69632, 1.61568, 0.95616, 0.2808,
//        };

        Double sumOfAmplitudes = 0.0;
        Double[] amplitudes = new Double[amountOfGrades];
        for (int i = 0; i < amountOfGrades - 1; i++) {
            amplitudes[i] = sam[i].computeAmplitude(amountOfJudges);
            sumOfAmplitudes += amplitudes[i];
        }
        for (int i = 0; i < amountOfGrades - 1; i++) {
            amplitudes[i] = amplitudes[i] / sumOfAmplitudes;
        }

        double tightness = 96.0;
        double rank = 0.0;  // from 0.0 (exclusive) to 1.0 (inclusive) ; is 'double' enough precision?
        for (int i = 0; i < amountOfGrades - 1; i++) {
            rank += amplitudes[i] * sigmoid(
                    merit,
                    tightness,
                    (2.0 * i + 1.0) / (2.0 * (amountOfGrades - 1))
            );
        }

        return rank;
//        return rank * (1.0 / (amountOfGrades - 1));
    }

    private double sigmoid(double x, double tightness, double origin) {
        return 1.0 / (1.0 + pow(E, tightness * (x - origin)));
    }

    private int countDigits(int number) {
        return ("" + number).length();
    }

    private int countDigits(BigInteger number) {
        return ("" + number).length();
    }
}
