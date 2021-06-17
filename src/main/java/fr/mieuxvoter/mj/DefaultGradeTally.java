package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * Fill the missing judgments into the grade defined by `getDefaultGrade()`. This is an abstract
 * class to dry code between static default grade and median default grade.
 */
public abstract class DefaultGradeTally extends Tally implements TallyInterface {

    /** Override this to choose the default grade for a given proposal. */
    protected abstract Integer getDefaultGradeForProposal(ProposalTallyInterface proposalTally);

    // <domi41> /me is confused with why we need constructors in an abstract class?

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
        for (int i = 0; i < amountOfProposals; i++) {
            ProposalTallyInterface proposalTally = getProposalsTallies()[i];
            Integer defaultGrade = getDefaultGradeForProposal(proposalTally);
            BigInteger amountOfJudgments = proposalTally.getAmountOfJudgments();
            BigInteger missingAmount = this.amountOfJudges.subtract(amountOfJudgments);
            int missingSign = missingAmount.compareTo(BigInteger.ZERO);
            assert (0 <= missingSign); // ERROR: More judgments than judges!
            if (0 < missingSign) {
                BigInteger[] rawTally = proposalTally.getTally();
                rawTally[defaultGrade] = rawTally[defaultGrade].add(missingAmount);
            }
        }
    }
}
