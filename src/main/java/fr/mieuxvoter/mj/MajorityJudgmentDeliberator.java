package fr.mieuxvoter.mj;

import java.math.BigInteger;
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

        // I. Compute the scores of each Proposal
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
                (Comparator<ProposalResultInterface>) (p0, p1) -> p1.getScore().compareTo(p0.getScore()));

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
     * @see computeScore() below
     */
    private String computeScore(ProposalTallyInterface tally, BigInteger amountOfJudges) {
        return computeScore(tally, amountOfJudges, this.favorContestation, this.numerizeScore);
    }

    /**
     * A higher score means a better rank. Assumes that grades' tallies are provided from "worst"
     * grade to "best" grade.
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

    private int countDigits(int number) {
        return ("" + number).length();
    }

    private int countDigits(BigInteger number) {
        return ("" + number).length();
    }
}
