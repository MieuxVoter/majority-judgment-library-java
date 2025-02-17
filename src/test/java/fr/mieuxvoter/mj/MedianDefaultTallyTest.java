package fr.mieuxvoter.mj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class MedianDefaultTallyTest {
    @Test
    @DisplayName("Test instantiations")
    void testInstantiations() {

        ProposalTallyInterface[] proposalTallies = new ProposalTallyInterface[] {
                new ProposalTally(new Integer[] {0, 0, 1}),
                new ProposalTally(new Integer[] {0, 1, 0}),
                new ProposalTally(new Integer[] {1, 1, 1}),
                new ProposalTally(new Integer[] {1, 0, 1}),
                new ProposalTally(new Integer[] {1, 0, 0}),
        };

        // Construct using a Integer (10) as amount of judges
        TallyInterface tally = new MedianDefaultTally(proposalTallies,10);

        // Construct using a Long (10L) as amount of judges
        TallyInterface tallyLong = new MedianDefaultTally(proposalTallies,10L);

        // Construct using a BigInteger (10) as amount of judges
        TallyInterface tallyBig = new MedianDefaultTally(proposalTallies, BigInteger.valueOf(10));

        // Construct from a TallyInterface
        TallyInterface tallyShallow = new MedianDefaultTally(tally);


        assertEquals(tally.getAmountOfProposals(), tallyLong.getAmountOfProposals());
        assertEquals(tally.getAmountOfJudges(), tallyLong.getAmountOfJudges());

        assertEquals(tally.getAmountOfProposals(), tallyBig.getAmountOfProposals());
        assertEquals(tally.getAmountOfJudges(), tallyBig.getAmountOfJudges());

        assertEquals(tally.getAmountOfProposals(), tallyShallow.getAmountOfProposals());
        assertEquals(tally.getAmountOfJudges(), tallyShallow.getAmountOfJudges());
    }
}