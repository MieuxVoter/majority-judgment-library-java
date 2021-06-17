package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * Fill the missing judgments into the median grade of each proposal. Useful when the proposals have
 * not received the exact same amount of votes and the median grade is considered a sane default.
 */
public class MedianDefaultTally extends DefaultGradeTally implements TallyInterface {

    public MedianDefaultTally(TallyInterface tally) {
        super(tally.getProposalsTallies(), tally.getAmountOfJudges());
        fillWithDefaultGrade();
    }

    public MedianDefaultTally(
            ProposalTallyInterface[] proposalsTallies, BigInteger amountOfJudges) {
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

    @Override
    protected Integer getDefaultGradeForProposal(ProposalTallyInterface proposalTally) {
        ProposalTallyAnalysis analysis = new ProposalTallyAnalysis(proposalTally);
        return analysis.getMedianGrade();
    }
}
