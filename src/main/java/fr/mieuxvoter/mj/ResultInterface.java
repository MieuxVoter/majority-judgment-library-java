package fr.mieuxvoter.mj;

public interface ResultInterface {

    /**
     * ProposalResults are not ordered by rank, they are in the order the proposals' tallies were
     * submitted.
     *
     * @return an array of `ProposalResult`, in the order the `ProposalTally`s were submitted.
     */
    public ProposalResultInterface[] getProposalResults();
}
