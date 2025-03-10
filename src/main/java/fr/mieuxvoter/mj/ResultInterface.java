package fr.mieuxvoter.mj;

public interface ResultInterface {

    /**
     * ProposalResults here are not ordered by rank.
     * They are in the order the proposals' tallies were submitted.
     *
     * @return an array of `ProposalResult`, in the order the `ProposalTally`s were submitted.
     */
    ProposalResultInterface[] getProposalResults();

    /**
     * ProposalResults here are ordered by rank.
     *
     * @return an array of `ProposalResult`, in the order the `ProposalTally`s were ranked.
     */
    ProposalResultInterface[] getProposalResultsRanked();
}
