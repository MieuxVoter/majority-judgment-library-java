package fr.mieuxvoter.mj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectedTallyTest {

    @Test
    @DisplayName("Test failing to pass adequate data to collect()")
    void testCollectLimitations() {

        int amountOfProposals = 10;
        int amountOfGrades = 7;
        CollectedTally tally = new CollectedTally(amountOfProposals, amountOfGrades);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        tally.collect(-1, 0) // negative proposal index
        );
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        tally.collect(0, -1) // negative grade index
        );
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        tally.collect(0, amountOfGrades) // out of bounds grade index
        );
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        tally.collect(amountOfProposals, 0) // out of bounds proposal index
        );

        // All the other combinations in range should not fail.
        for (int proposalIndex = 0 ; proposalIndex < amountOfProposals ; proposalIndex++) {
            for (int gradeIndex = 0; gradeIndex < amountOfGrades ; gradeIndex++) {
                tally.collect(proposalIndex, gradeIndex);
            }
        }

    }
}