package fr.mieuxvoter.mj;

public class Tally implements TallyInterface {

	protected ProposalTallyInterface[] proposalsTallies;
	
	protected Long amountOfJudges = 0L;

	public Tally(ProposalTallyInterface[] proposalsTallies, Long amountOfJudges) {
		setProposalsTallies(proposalsTallies);
		setAmountOfJudges(amountOfJudges);
	}
	
	public Tally(ProposalTallyInterface[] proposalsTallies, Integer amountOfJudges) {
		setProposalsTallies(proposalsTallies);
		setAmountOfJudges(Long.valueOf(amountOfJudges));
	}

	public ProposalTallyInterface[] getProposalsTallies() {
		return proposalsTallies;
	}

	public void setProposalsTallies(ProposalTallyInterface[] proposalsTallies) {
		this.proposalsTallies = proposalsTallies;
	}

	public Integer getAmountOfProposals() {
		return proposalsTallies.length;
	}

	public Long getAmountOfJudges() {
		return amountOfJudges;
	}

	public void setAmountOfJudges(Long amountOfJudges) {
		this.amountOfJudges = amountOfJudges;
	}
	
}
