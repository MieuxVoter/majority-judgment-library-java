package fr.mieuxvoter.mj;

import java.math.BigInteger;

public interface TallyInterface {

    public ProposalTallyInterface[] getProposalsTallies();

    public BigInteger getAmountOfJudges();

    public Integer getAmountOfProposals();
}
