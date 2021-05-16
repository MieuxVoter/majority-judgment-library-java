package fr.mieuxvoter.mj;

import java.math.BigInteger;

public class TallyWithDefaultGrade extends DefaultGradeTally implements TallyInterface {

	/**
	 * Grades are represented as numbers, as indices in a list.
	 * Grades start from 0 ("worst" grade, most conservative) and go upwards.
	 * Values out of the range of grades defined in the tally will yield errors.
	 * 
	 * Example:
	 * 
	 *     0 == REJECT
	 *     1 == PASSABLE
	 *     2 == GOOD
	 *     3 == EXCELLENT
	 */
	protected Integer defaultGrade = 0;

	public TallyWithDefaultGrade(TallyInterface tally, Integer defaultGrade) {
		super(tally.getProposalsTallies(), tally.getAmountOfJudges());
		this.defaultGrade = defaultGrade;
		fillWithDefaultGrade();
	}
	
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

	@Override
	protected Integer getDefaultGradeForProposal(ProposalTallyInterface proposalTally) {
		return this.defaultGrade;
	}

}
