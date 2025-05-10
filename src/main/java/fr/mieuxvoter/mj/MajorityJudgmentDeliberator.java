package fr.mieuxvoter.mj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;

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

        Result result = new Result();
        ProposalResult[] proposalResults = new ProposalResult[amountOfProposals];

        // I. Compute the score and merit of each Proposal
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalTallyInterface proposalTally = tallies[proposalIndex];
            String score = computeScore(proposalTally, amountOfJudges);
            ProposalTallyAnalysis analysis = new ProposalTallyAnalysis(
                    proposalTally, this.favorContestation
            );

            ProposalResult proposalResult = new ProposalResult();
            proposalResult.setIndex(proposalIndex);
            proposalResult.setScore(score);
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

        // III. Attribute a rank to each Proposal
        int rank = 1;
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalResult proposalResult = proposalResultsSorted[proposalIndex];
            Integer actualRank = rank;
            if (proposalIndex > 0) {
                ProposalResult proposalResultBefore = proposalResultsSorted[proposalIndex - 1];
                if (proposalResult.getScore().contentEquals(proposalResultBefore.getScore())) {
                    actualRank = proposalResultBefore.getRank();
                }
            }
            proposalResult.setRank(actualRank);
            rank += 1;
        }

        // Steps IV, V and VI are not required to rank the proposals, but they're nice to have around.

        // IV. Compute the scalar "merit from MJ-Score" of each Proposal
        BigInteger sumOfMerits = BigInteger.ZERO;
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalTallyInterface proposalTally = tallies[proposalIndex];
            ProposalResult proposalResult = proposalResults[proposalIndex];

            BigInteger merit = computeMerit(proposalTally, amountOfJudges, this.favorContestation);

            proposalResult.setMerit(merit);
            sumOfMerits = sumOfMerits.add(merit);
        }

        // V.a Compute the (maximum!) merit a 100% EXCELLENT proposal would get
        BigInteger maxMerit = BigInteger.ONE;
        if (tallies.length > 0) {
            int amountOfGrades = tallies[0].getTally().length;
            BigInteger[] bestTally = new BigInteger[amountOfGrades];
            Arrays.fill(bestTally, BigInteger.ZERO);
            bestTally[bestTally.length - 1] = amountOfJudges;
            maxMerit = computeMerit(new ProposalTally(bestTally), amountOfJudges, this.favorContestation);
        }

        // V.b Approximate the scalar "merit from absolute rank" of each Proposal (Affine Merit)
        double sumOfAffineMerits = 0.0;
        if (tallies.length > 0) {
            for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
                ProposalResult proposalResult = proposalResults[proposalIndex];
                int amountOfGrades = tallies[0].getTally().length;

                Double affineMerit = adjustMeritToAffine(
                        proposalResult.getMerit(),
                        maxMerit,
                        amountOfJudges,
                        amountOfGrades
                );

                proposalResult.setAffineMerit(affineMerit);
                sumOfAffineMerits += affineMerit;
            }
        }

        // VI. Compute the relative merit(s) of each Proposal
        for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
            ProposalResult proposalResult = proposalResultsSorted[proposalIndex];
            proposalResult.computeRelativeMerit(sumOfMerits);
            proposalResult.computeRelativeAffineMerit(sumOfAffineMerits);
        }

        // VII. All done, let's output
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

    /**
     * This method is not used in ranking, but helps compute a scalar merit for a given merit profile.
     * Such a scalar merit is handy for deriving a proportional representation for example.
     * This merit is isomorphic with MJ ranking and could be used for ranking. (bigger is better)
     * Marc Paraire calls this merit the "MJ-Score".
     * As you can see, this algorithm is quite similar to the string score one.
     * The main difference is that it's a little slower to compute, but the output value is more meaningful.
     */
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

    private int countDigits(int number) {
        //noinspection StringTemplateMigration
        return ("" + number).length();
    }

    private int countDigits(BigInteger number) {
        //noinspection StringTemplateMigration
        return ("" + number).length();
    }

    /**
     * This method is NOT used in ranking, but helps compute yet another scalar merit for a given merit profile.
     * Such a scalar merit is handy for deriving a proportional representation for example.
     * This method adjusts the MJ-Score to make its distribution quasi-affine over all possible merit profiles.
     * See study/output_30_0.png
     * You can safely pretend that this does not exist, since it is NOT used in ranking.
     */
    private Double adjustMeritToAffine(
            BigInteger merit,
            BigInteger maxMerit,
            BigInteger amountOfJudges,
            int amountOfGrades
    ) {
        double meritNormalized = (new BigDecimal(merit).divide(
                new BigDecimal(maxMerit), 15, RoundingMode.HALF_EVEN
        )).doubleValue();

        double rankNormalized = new MeritToAbsoluteRankModel().apply(
                meritNormalized,
                amountOfGrades,
                amountOfJudges.intValue()
        );

        return (1.0 - rankNormalized);
    }
}
