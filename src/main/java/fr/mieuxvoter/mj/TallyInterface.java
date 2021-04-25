package fr.mieuxvoter.mj;

public interface TallyInterface {
	
	public ProposalTallyInterface[] getProposalsTallies();
	
	public Long getAmountOfJudges();
	
	public Integer getAmountOfProposals();
	
}
