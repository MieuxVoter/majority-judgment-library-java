package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * A Basic implementation of a TallyInterface that reads from an array of ProposalTallyInterface.
 */
public class Tally implements TallyInterface {

    protected ProposalTallyInterface[] proposalsTallies;

    protected BigInteger amountOfJudges = BigInteger.ZERO;

    public Tally(ProposalTallyInterface[] proposalsTallies) {
        setProposalsTallies(proposalsTallies);
        guessAmountOfJudges();
    }

    public Tally(ProposalTallyInterface[] proposalsTallies, BigInteger amountOfJudges) {
        setProposalsTallies(proposalsTallies);
        setAmountOfJudges(amountOfJudges);
    }

    public Tally(ProposalTallyInterface[] proposalsTallies, Long amountOfJudges) {
        setProposalsTallies(proposalsTallies);
        setAmountOfJudges(BigInteger.valueOf(amountOfJudges));
    }

    public Tally(ProposalTallyInterface[] proposalsTallies, Integer amountOfJudges) {
        setProposalsTallies(proposalsTallies);
        setAmountOfJudges(BigInteger.valueOf(amountOfJudges));
    }

    public ProposalTallyInterface[] getProposalsTallies() {
        return proposalsTallies;
    }

    public void setProposalsTallies(ProposalTallyInterface[] proposalsTallies) {
        this.proposalsTallies = proposalsTallies;
    }

    public Integer getAmountOfProposals() {
        return proposalsTallies.length;
    }

    public BigInteger getAmountOfJudges() {
        return amountOfJudges;
    }

    public void setAmountOfJudges(BigInteger amountOfJudges) {
        this.amountOfJudges = amountOfJudges;
    }

    protected void guessAmountOfJudges() {
        BigInteger amountOfJudges = BigInteger.ZERO;
        for (ProposalTallyInterface proposalTally : getProposalsTallies()) {
            amountOfJudges = proposalTally.getAmountOfJudgments().max(amountOfJudges);
        }
        setAmountOfJudges(amountOfJudges);
    }
}
