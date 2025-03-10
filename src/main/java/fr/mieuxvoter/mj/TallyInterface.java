package fr.mieuxvoter.mj;

import java.math.BigInteger;

public interface TallyInterface {

    ProposalTallyInterface[] getProposalsTallies();

    BigInteger getAmountOfJudges();

    Integer getAmountOfProposals();
}
