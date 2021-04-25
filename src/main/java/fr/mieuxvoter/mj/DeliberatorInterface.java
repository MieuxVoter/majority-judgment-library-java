package fr.mieuxvoter.mj;


/**
 * A Deliberator takes in a poll's Tally,
 * that is the amount of grades received by each Proposal,
 * and outputs the poll's Result,
 * that is the final rank of each Proposal.
 * 
 * This is the main API of this library.
 * 
 * See MajorityJudgmentDeliberator for an implementation.
 */
public interface DeliberatorInterface {
	public ResultInterface deliberate(TallyInterface tally);
}
