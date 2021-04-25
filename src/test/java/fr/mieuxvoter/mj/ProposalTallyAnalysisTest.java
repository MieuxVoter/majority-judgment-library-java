package fr.mieuxvoter.mj;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


// CTRL+F11 in Eclipse to run

class ProposalTallyAnalysisTest {

    @DisplayName("Test the proposal tally analysis")
    @ParameterizedTest(name="#{index} {0} ; tally = {1}")
    @MethodSource("testProvider")
	void test(
			String testName,
			Integer[] rawTally,
			Integer medianGrade,
			Long medianGroupSize,
			Integer contestationGrade,
			Long contestationGroupSize,
			Integer adhesionGrade,
			Long adhesionGroupSize,
			Integer secondMedianGrade,
			Long secondMedianGroupSize,
			Integer secondMedianGroupSign
	) {
		ProposalTally tally = new ProposalTally(rawTally);
		ProposalTallyAnalysis pta = new ProposalTallyAnalysis(tally);
		assertEquals(medianGrade, pta.getMedianGrade(), "Median Grade");
		assertEquals(medianGroupSize, pta.getMedianGroupSize(), "Median Group Size");
		assertEquals(contestationGrade, pta.getContestationGrade(), "Contestation Grade");
		assertEquals(contestationGroupSize, pta.getContestationGroupSize(), "Contestation Group Size");
		assertEquals(adhesionGrade, pta.getAdhesionGrade(), "Adhesion Grade");
		assertEquals(adhesionGroupSize, pta.getAdhesionGroupSize(), "Adhesion Group Size");
		assertEquals(secondMedianGrade, pta.getSecondMedianGrade(), "Second Median Grade");
		assertEquals(secondMedianGroupSize, pta.getSecondMedianGroupSize(), "Second Median Group Size");
		assertEquals(secondMedianGroupSign, pta.getSecondMedianGroupSign(), "Second Median Group Sign");
	}

    protected static Stream<Arguments> testProvider() {
        return Stream.of(
//        		Arguments.of(
//        				/* name */ "Void tallies yield ???", // perhaps raise ? later
//        				/* tally */ new Integer[]{},
//        				/* medianGrade */                0,
//        				/* medianGroupSize */            0,
//        				/* contestationGrade */          0,
//        				/* contestationGroupSize */      0,
//        				/* adhesionGrade */              0,
//        				/* adhesionGroupSize */          0,
//        				/* secondMedianGrade */          0,
//        				/* secondMedianGroupSize */      0,
//        				/* secondMedianGroupSign */      0
//        		),
        		Arguments.of(
        				/* name */ "Very empty tallies yield zeroes",
        				/* tally */ new Integer[]{ 0 },
        				/* medianGrade */                0,
        				/* medianGroupSize */            0L,
        				/* contestationGrade */          0,
        				/* contestationGroupSize */      0L,
        				/* adhesionGrade */              0,
        				/* adhesionGroupSize */          0L,
        				/* secondMedianGrade */          0,
        				/* secondMedianGroupSize */      0L,
        				/* secondMedianGroupSign */      0
        		),
        		Arguments.of(
        				/* name */ "Empty tallies yield zeroes",
        				/* tally */ new Integer[]{ 0, 0, 0, 0 },
        				/* medianGrade */                0,
        				/* medianGroupSize */            0L,
        				/* contestationGrade */          0,
        				/* contestationGroupSize */      0L,
        				/* adhesionGrade */              0,
        				/* adhesionGroupSize */          0L,
        				/* secondMedianGrade */          0,
        				/* secondMedianGroupSize */      0L,
        				/* secondMedianGroupSign */      0
        		),
        		Arguments.of(
        				/* name */ "Absurd tally of 1 Grade",
        				/* tally */ new Integer[]{ 7 },
        				/* medianGrade */                0,
        				/* medianGroupSize */            7L,
        				/* contestationGrade */          0,
        				/* contestationGroupSize */      0L,
        				/* adhesionGrade */              0,
        				/* adhesionGroupSize */          0L, 
        				/* secondMedianGrade */          0,
        				/* secondMedianGroupSize */      0L,
        				/* secondMedianGroupSign */      0
        		),
        		Arguments.of(
        				/* name */ "Approbation",
        				/* tally */ new Integer[]{ 31, 72 },
        				/* medianGrade */                1,
        				/* medianGroupSize */            72L,
        				/* contestationGrade */          0,
        				/* contestationGroupSize */      31L,
        				/* adhesionGrade */              0,
        				/* adhesionGroupSize */          0L,
        				/* secondMedianGrade */          0,
        				/* secondMedianGroupSize */      31L,
        				/* secondMedianGroupSign */      -1
        		),
        		Arguments.of(
        				/* name */ "Equality favors contestation",
        				/* tally */ new Integer[]{ 42, 42 },
        				/* medianGrade */                0,
        				/* medianGroupSize */            42L,
        				/* contestationGrade */          0,
        				/* contestationGroupSize */      0L,
        				/* adhesionGrade */              1,
        				/* adhesionGroupSize */          42L, 
        				/* secondMedianGrade */          1,
        				/* secondMedianGroupSize */      42L,
        				/* secondMedianGroupSign */      1
        		),
        		Arguments.of(
        				/* name */ "Example with seven grades",
        				/* tally */ new Integer[]{ 4, 2, 0, 1, 2, 2, 3 },
        				/* medianGrade */                3,
        				/* medianGroupSize */            1L,
        				/* contestationGrade */          1,
        				/* contestationGroupSize */      6L,
        				/* adhesionGrade */              4,
        				/* adhesionGroupSize */          7L, 
        				/* secondMedianGrade */          4,
        				/* secondMedianGroupSize */      7L,
        				/* secondMedianGroupSign */      1
        		),
        		Arguments.of(
        				/* name */ "Works even if multiple grades are at zero",
        				/* tally */ new Integer[]{ 4, 0, 0, 1, 0, 0, 4 },
        				/* medianGrade */                3,
        				/* medianGroupSize */            1L,
        				/* contestationGrade */          0,
        				/* contestationGroupSize */      4L,
        				/* adhesionGrade */              6,
        				/* adhesionGroupSize */          4L, 
        				/* secondMedianGrade */          0,
        				/* secondMedianGroupSize */      4L,
        				/* secondMedianGroupSign */      -1
        		),
        		Arguments.of(
        				/* name */ "Weird tally",
        				/* tally */ new Integer[]{ 1, 1, 1, 1, 1, 1, 1 },
        				/* medianGrade */                3,
        				/* medianGroupSize */            1L,
        				/* contestationGrade */          2,
        				/* contestationGroupSize */      3L,
        				/* adhesionGrade */              4,
        				/* adhesionGroupSize */          3L, 
        				/* secondMedianGrade */          2,
        				/* secondMedianGroupSize */      3L,
        				/* secondMedianGroupSign */      -1
        		)
        );
    }
}
