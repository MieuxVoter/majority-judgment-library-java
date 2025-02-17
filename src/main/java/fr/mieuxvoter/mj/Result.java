package fr.mieuxvoter.mj;

public class Result implements ResultInterface {

    protected ProposalResultInterface[] proposalResults;
    protected ProposalResultInterface[] proposalResultsRanked;

    @Override
    public ProposalResultInterface[] getProposalResults() {
        return proposalResults;
    }

    public void setProposalResults(ProposalResultInterface[] proposalResults) {
        this.proposalResults = proposalResults;
    }

    @Override
    public ProposalResultInterface[] getProposalResultsRanked() {
        return proposalResultsRanked;
    }

    public void setProposalResultsRanked(ProposalResultInterface[] proposalResultsRanked) {
        this.proposalResultsRanked = proposalResultsRanked;
    }
}
