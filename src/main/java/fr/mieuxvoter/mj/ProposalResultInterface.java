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
     */
    String getScore();

    /**
     * @return the MJ-Score merit, as suggested by Marc Paraire.
     * See study/output_14_0.png to see how it is distributed.
     */
    BigInteger getMerit();

    /**
     * @return the proportional representation computed from MJ-Score merit.
     */
    Double getMeritAsPercentage();

    /** experimental **/
    Double getMeritAdjusted();

    /** experimental **/
    Double getMeritAdjustedAsPercentage();

    /** Get more data about the proposal tally, such as the median grade. */
    ProposalTallyAnalysis getAnalysis();
}
