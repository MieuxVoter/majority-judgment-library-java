package fr.mieuxvoter.mj;

import java.math.BigInteger;

public class StaticDefaultTally extends DefaultGradeTally implements TallyInterface {

    /**
     * Grades are represented as numbers, as indices in a list. Grades start from 0 ("worst" grade,
     * most conservative) and go upwards. Values out of the range of grades defined in the tally
     * will yield errors.
     *
     * <p>Example:
     *
     * <p>0 == REJECT 1 == PASSABLE 2 == GOOD 3 == EXCELLENT
     */
    protected Integer defaultGrade = 0;

    public StaticDefaultTally(TallyInterface tally, Integer defaultGrade) {
        super(tally.getProposalsTallies(), tally.getAmountOfJudges());
        this.defaultGrade = defaultGrade;
        fillWithDefaultGrade();
    }

    public StaticDefaultTally(
            ProposalTallyInterface[] proposalsTallies,
            BigInteger amountOfJudges,
            Integer defaultGrade) {
        super(proposalsTallies, amountOfJudges);
        this.defaultGrade = defaultGrade;
        fillWithDefaultGrade();
    }

    public StaticDefaultTally(
            ProposalTallyInterface[] proposalsTallies, Long amountOfJudges, Integer defaultGrade) {
        super(proposalsTallies, amountOfJudges);
        this.defaultGrade = defaultGrade;
        fillWithDefaultGrade();
    }

    public StaticDefaultTally(
            ProposalTallyInterface[] proposalsTallies,
            Integer amountOfJudges,
            Integer defaultGrade) {
        super(proposalsTallies, amountOfJudges);
        this.defaultGrade = defaultGrade;
        fillWithDefaultGrade();
    }

    @Override
    protected Integer getDefaultGradeForProposal(ProposalTallyInterface proposalTally) {
        return this.defaultGrade;
    }
}
