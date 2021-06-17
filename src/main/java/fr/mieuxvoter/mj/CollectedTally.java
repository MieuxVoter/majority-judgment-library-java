package fr.mieuxvoter.mj;

import java.math.BigInteger;

public class CollectedTally implements TallyInterface {

    Integer amountOfProposals = 0;
    Integer amountOfGrades = 0;

    ProposalTally[] proposalsTallies;

    public CollectedTally(Integer amountOfProposals, Integer amountOfGrades) {
        setAmountOfProposals(amountOfProposals);
        setAmountOfGrades(amountOfGrades);

        proposalsTallies = new ProposalTally[amountOfProposals];
        for (int i = 0; i < amountOfProposals; i++) {
            ProposalTally proposalTally = new ProposalTally();
            Integer[] tally = new Integer[amountOfGrades];
            for (int j = 0; j < amountOfGrades; j++) {
                tally[j] = 0;
            }
            proposalTally.setTally(tally);
            proposalsTallies[i] = proposalTally;
        }
    }

    @Override
    public ProposalTallyInterface[] getProposalsTallies() {
        return proposalsTallies;
    }

    @Override
    public BigInteger getAmountOfJudges() {
        return guessAmountOfJudges();
    }

    @Override
    public Integer getAmountOfProposals() {
        return this.amountOfProposals;
    }

    public void setAmountOfProposals(Integer amountOfProposals) {
        this.amountOfProposals = amountOfProposals;
    }

    public Integer getAmountOfGrades() {
        return amountOfGrades;
    }

    public void setAmountOfGrades(Integer amountOfGrades) {
        this.amountOfGrades = amountOfGrades;
    }

    protected BigInteger guessAmountOfJudges() {
        BigInteger amountOfJudges = BigInteger.ZERO;
        for (ProposalTallyInterface proposalTally : getProposalsTallies()) {
            amountOfJudges = proposalTally.getAmountOfJudgments().max(amountOfJudges);
        }
        return amountOfJudges;
    }

    public void collect(Integer proposal, Integer grade) {
        assert (0 <= proposal);
        assert (amountOfProposals > proposal);
        assert (0 <= grade);
        assert (amountOfGrades > grade);

        BigInteger[] tally = proposalsTallies[proposal].getTally();
        tally[grade] = tally[grade].add(BigInteger.ONE);
    }
}
