package fr.mieuxvoter.mj;

import java.math.BigInteger;

public class TallyWithDefaultGrade extends Tally implements TallyInterface {

	protected Integer defaultGrade = 0;
	
	public TallyWithDefaultGrade(ProposalTallyInterface[] proposalsTallies, BigInteger amountOfJudges, Integer defaultGrade) {
		super(proposalsTallies, amountOfJudges);
		this.defaultGrade = defaultGrade;
		fillWithDefaultGrade();
	}

	public TallyWithDefaultGrade(ProposalTallyInterface[] proposalsTallies, Long amountOfJudges, Integer defaultGrade) {
		super(proposalsTallies, amountOfJudges);
		this.defaultGrade = defaultGrade;
		fillWithDefaultGrade();
	}
	
	public TallyWithDefaultGrade(ProposalTallyInterface[] proposalsTallies, Integer amountOfJudges, Integer defaultGrade) {
		super(proposalsTallies, amountOfJudges);
		this.defaultGrade = defaultGrade;
		fillWithDefaultGrade();
	}
	
	protected void fillWithDefaultGrade() {
		int amountOfProposals = getAmountOfProposals();
		for (int i = 0 ; i < amountOfProposals ; i++) {
			ProposalTallyInterface proposal = getProposalsTallies()[i];
			BigInteger amountOfJudgments = proposal.getAmountOfJudgments();
			BigInteger missingAmount = this.amountOfJudges.subtract(amountOfJudgments);
			int missingSign = missingAmount.compareTo(BigInteger.ZERO);
			assert(0 <= missingSign);  // ERROR: More judgments than judges!
			if (0 < missingSign) {
				proposal.getTally()[this.defaultGrade] = proposal.getTally()[this.defaultGrade].add(missingAmount); 
			}
		}
	}

}
