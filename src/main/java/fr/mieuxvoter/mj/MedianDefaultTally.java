package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * Fill the missing judgments into the median grade.
 * Useful when the proposals have not received the exact same amount of votes and
 * the median grade is considered a sane default.
 */
public class MedianDefaultTally extends Tally implements TallyInterface {

	public MedianDefaultTally(ProposalTallyInterface[] proposalsTallies, BigInteger amountOfJudges) {
		super(proposalsTallies, amountOfJudges);
		fillWithDefaultGrade();
	}

	public MedianDefaultTally(ProposalTallyInterface[] proposalsTallies, Long amountOfJudges) {
		super(proposalsTallies, amountOfJudges);
		fillWithDefaultGrade();
	}

	public MedianDefaultTally(ProposalTallyInterface[] proposalsTallies, Integer amountOfJudges) {
		super(proposalsTallies, amountOfJudges);
		fillWithDefaultGrade();
	}

	protected void fillWithDefaultGrade() {
		int amountOfProposals = getAmountOfProposals();
		for (int i = 0 ; i < amountOfProposals ; i++) {
			ProposalTallyInterface proposal = getProposalsTallies()[i];
			ProposalTallyAnalysis analysis = new ProposalTallyAnalysis(proposal);
			Integer defaultGrade = analysis.getMedianGrade();
			BigInteger amountOfJudgments = proposal.getAmountOfJudgments();
			BigInteger missingAmount = this.amountOfJudges.subtract(amountOfJudgments);
			int missingSign = missingAmount.compareTo(BigInteger.ZERO);
			assert(0 <= missingSign);  // ERROR: More judgments than judges!
			if (0 < missingSign) {
				proposal.getTally()[defaultGrade] = proposal.getTally()[defaultGrade].add(missingAmount); 
			}
		}
	}

}
