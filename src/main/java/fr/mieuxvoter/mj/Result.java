package fr.mieuxvoter.mj;

public class Result implements ResultInterface {

    protected ProposalResultInterface[] proposalResults;

    public ProposalResultInterface[] getProposalResults() {
        return proposalResults;
    }

    public void setProposalResults(ProposalResultInterface[] proposalResults) {
        this.proposalResults = proposalResults;
    }
}
