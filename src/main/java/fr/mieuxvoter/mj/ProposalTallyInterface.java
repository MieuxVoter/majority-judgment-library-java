package fr.mieuxvoter.mj;

public interface ProposalTallyInterface {
	
	/**
	 * The amount of judgments received for each Grade, from "worst" Grade to "best" Grade.
	 */
	public Long[] getTally();
	
	/**
	 * Homemade factory to skip the clone() shenanigans.
	 * Used by the score calculus.
	 */
	public ProposalTallyInterface duplicate();
	
	/**
	 * Move judgments that were fromGrade into intoGrade.
	 * Used by the score calculus.
	 */
	public void moveJudgments(Integer fromGrade, Integer intoGrade);
	
}
