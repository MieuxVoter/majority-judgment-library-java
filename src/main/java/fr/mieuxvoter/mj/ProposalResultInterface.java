package fr.mieuxvoter.mj;

public interface ProposalResultInterface {

    /**
     * Rank starts at 1 ("best" proposal), and goes upwards. Multiple Proposals may receive the same
     * rank, in the extreme case where they received the exact same judgments, or the same judgment
     * repartition in normalized tallies.
     */
    public Integer getRank();

    /**
     * This score was used to compute the rank. It is made of integer characters, with zeroes for
     * padding. Inverse lexicographical order: "higher" is "better". You're probably never going to
     * need this, but it's here anyway.
     */
    public String getScore();

    /** Get more data about the proposal tally, such as the median grade. */
    public ProposalTallyAnalysis getAnalysis();
}
