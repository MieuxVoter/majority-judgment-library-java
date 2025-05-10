package fr.mieuxvoter.mj;

import java.math.BigInteger;

public interface ProposalResultInterface {

    /**
     * Index of the proposal, as submitted in the tally.
     * Each proposal has a unique index, and indices start at 0.
     * This property is especially useful when reading Result.getProposalResultsRanked().
     */
    Integer getIndex();

    /**
     * Rank starts at 1 ("best" proposal), and goes upwards. Multiple Proposals may receive the same
     * rank, in the extreme case where they received the exact same judgments, or the same judgment
     * repartition in normalized tallies.
     */
    Integer getRank();

    /**
     * This score was used to compute the rank. It is made of integer characters, with zeroes for
     * padding. Inverse lexicographical order: "higher" is "better". You're probably never going to
     * need this, but it's here anyway.
     * The value is not meaningful, but it is fast to compute.
     * If you need a numerical 'score' value, prefer using the merit, which is meaningful.
     */
    String getScore();

    /**
     * @return the MJ-Score merit, as suggested by Marc Paraire.
     * This merit is isomorphic with Majority Judgment ranking.
     * See study/output_14_0.png to see how it is distributed over all possible merit profiles.
     */
    BigInteger getMerit();

    /**
     * @return the proportional representation computed from the MJ-Score merit.
     * This is a value between 0 and 1.
     * The sum of all the relative merits of the candidates of a poll is 1.
     */
    Double getRelativeMerit();

    /**
     * @deprecated
     * This method is very experimental and might be subject to changes.
     * Furthermore, right now it only works for 7 grades.
     * Best not use this 'til it's stable.
     *
     * @return an approximation of the merit from absolute rank, normalized.
     * This merit's distribution is therefore quasi-affine over all possible merit profiles.
     * It is an approximation because the exact value is quickly un-computable as the amount of judges grows.
     * Contrary to the BigInteger merit, this value is between 0 and 1.
     */
    Double getAffineMerit();

    /**
     * @deprecated
     * This method is very experimental and might be subject to changes.
     */
    Double getRelativeAffineMerit();

    /** Get more data about the proposal tally, such as the median grade. */
    ProposalTallyAnalysis getAnalysis();
}
