package fr.mieuxvoter.mj;

/**
 * A Deliberator takes in a poll's Tally, which holds the amount of judgments of each grade received
 * by each Proposal, and outputs that poll's Result, that is the final rank of each Proposal.
 *
 * <p>Ranks start at 1 ("best"), and increment towards "worst". Two proposal may share the same
 * rank, in extreme equality cases.
 *
 * <p>This is the main API of this library.
 *
 * <p>See MajorityJudgmentDeliberator for an implementation. One could implement other deliberators,
 * such as: - CentralJudgmentDeliberator - UsualJudgmentDeliberator
 */
public interface DeliberatorInterface {

    public ResultInterface deliberate(TallyInterface tally) throws InvalidTallyException;
}
