package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * Fill the missing judgments into the grade defined by `getDefaultGrade()`.
 * This is an abstract class to dry code between static default grade and median default grade.
 */
abstract public class DefaultGradeTally extends Tally implements TallyInterface {

	public DefaultGradeTally(TallyInterface tally) {
		super(tally.getProposalsTallies(), tally.getAmountOfJudges());
	}

	public DefaultGradeTally(ProposalTallyInterface[] proposalsTallies, Integer amountOfJudges) {
		super(proposalsTallies, amountOfJudges);
	}
	
	public DefaultGradeTally(ProposalTallyInterface[] proposalsTallies, Long amountOfJudges) {
		super(proposalsTallies, amountOfJudges);
	}
	
	public DefaultGradeTally(ProposalTallyInterface[] proposalsTallies, BigInteger amountOfJudges) {
		super(proposalsTallies, amountOfJudges);
	}

	protected void fillWithDefaultGrade() {
		int amountOfProposals = getAmountOfProposals();
		for (int i = 0 ; i < amountOfProposals ; i++) {
			ProposalTallyInterface proposal = getProposalsTallies()[i];
			Integer defaultGrade = getDefaultGrade(proposal);
			BigInteger amountOfJudgments = proposal.getAmountOfJudgments();
			BigInteger missingAmount = this.amountOfJudges.subtract(amountOfJudgments);
			int missingSign = missingAmount.compareTo(BigInteger.ZERO);
			assert(0 <= missingSign);  // ERROR: More judgments than judges!
			if (0 < missingSign) {
				proposal.getTally()[defaultGrade] = proposal.getTally()[defaultGrade].add(missingAmount); 
			}
		}
	}

	abstract protected Integer getDefaultGrade(ProposalTallyInterface proposalTally);

}
