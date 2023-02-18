package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * Also known as the merit profile of a proposal (aka. candidate), this holds the amounts of
 * judgments received per grade.
 */
public interface ProposalTallyInterface {

    /**
     * The tallies of each Grade, that is the amount of judgments received for each Grade by the
     * Proposal, from "worst" ("most conservative") Grade to "best" Grade.
     */
    public BigInteger[] getTally();

    /**
     * Should be the sum of getTally()
     *
     * @return The total amount of judgments received by this proposal.
     */
    public BigInteger getAmountOfJudgments();

    /** Homemade factory to skip the clone() shenanigans. Used by the score calculus. */
    public ProposalTallyInterface duplicate();

    /** Move judgments that were fromGrade into intoGrade. Used by the score calculus. */
    public void moveJudgments(Integer fromGrade, Integer intoGrade);
}
