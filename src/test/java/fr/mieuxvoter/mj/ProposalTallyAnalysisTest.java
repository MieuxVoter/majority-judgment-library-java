package fr.mieuxvoter.mj;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
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
			BigInteger medianGroupSize,
			Integer contestationGrade,
			BigInteger contestationGroupSize,
			Integer adhesionGrade,
			BigInteger adhesionGroupSize,
			Integer secondMedianGrade,
			BigInteger secondMedianGroupSize,
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
			Arguments.of(
					/* name */ "Very empty tallies yield zeroes",
					/* tally */ new Integer[]{ 0 },
					/* medianGrade */                0,
					/* medianGroupSize */            BigInteger.ZERO,
					/* contestationGrade */          0,
					/* contestationGroupSize */      BigInteger.ZERO,
					/* adhesionGrade */              0,
					/* adhesionGroupSize */          BigInteger.ZERO,
					/* secondMedianGrade */          0,
					/* secondMedianGroupSize */      BigInteger.ZERO,
					/* secondMedianGroupSign */      0
			),
			Arguments.of(
					/* name */ "Empty tallies yield zeroes",
					/* tally */ new Integer[]{ 0, 0, 0, 0 },
					/* medianGrade */                0,
					/* medianGroupSize */            BigInteger.ZERO,
					/* contestationGrade */          0,
					/* contestationGroupSize */      BigInteger.ZERO,
					/* adhesionGrade */              0,
					/* adhesionGroupSize */          BigInteger.ZERO,
					/* secondMedianGrade */          0,
					/* secondMedianGroupSize */      BigInteger.ZERO,
					/* secondMedianGroupSign */      0
			),
			Arguments.of(
					/* name */ "Absurd tally of 1 Grade",
					/* tally */ new Integer[]{ 7 },
					/* medianGrade */                0,
					/* medianGroupSize */            BigInteger.valueOf(7),
					/* contestationGrade */          0,
					/* contestationGroupSize */      BigInteger.ZERO,
					/* adhesionGrade */              0,
					/* adhesionGroupSize */          BigInteger.ZERO,
					/* secondMedianGrade */          0,
					/* secondMedianGroupSize */      BigInteger.ZERO,
					/* secondMedianGroupSign */      0
			),
			Arguments.of(
					/* name */ "Approbation",
					/* tally */ new Integer[]{ 31, 72 },
					/* medianGrade */                1,
					/* medianGroupSize */            BigInteger.valueOf(72),
					/* contestationGrade */          0,
					/* contestationGroupSize */      BigInteger.valueOf(31),
					/* adhesionGrade */              0,
					/* adhesionGroupSize */          BigInteger.ZERO,
					/* secondMedianGrade */          0,
					/* secondMedianGroupSize */      BigInteger.valueOf(31),
					/* secondMedianGroupSign */      -1
			),
			Arguments.of(
					/* name */ "Equality favors contestation",
					/* tally */ new Integer[]{ 42, 42 },
					/* medianGrade */                0,
					/* medianGroupSize */            BigInteger.valueOf(42),
					/* contestationGrade */          0,
					/* contestationGroupSize */      BigInteger.ZERO,
					/* adhesionGrade */              1,
					/* adhesionGroupSize */          BigInteger.valueOf(42),
					/* secondMedianGrade */          1,
					/* secondMedianGroupSize */      BigInteger.valueOf(42),
					/* secondMedianGroupSign */      1
			),
			Arguments.of(
					/* name */ "Example with seven grades",
					/* tally */ new Integer[]{ 4, 2, 0, 1, 2, 2, 3 },
					/* medianGrade */                3,
					/* medianGroupSize */            BigInteger.valueOf(1),
					/* contestationGrade */          1,
					/* contestationGroupSize */      BigInteger.valueOf(6),
					/* adhesionGrade */              4,
					/* adhesionGroupSize */          BigInteger.valueOf(7),
					/* secondMedianGrade */          4,
					/* secondMedianGroupSize */      BigInteger.valueOf(7),
					/* secondMedianGroupSign */      1
			),
			Arguments.of(
					/* name */ "Works even if multiple grades are at zero",
					/* tally */ new Integer[]{ 4, 0, 0, 1, 0, 0, 4 },
					/* medianGrade */                3,
					/* medianGroupSize */            BigInteger.valueOf(1),
					/* contestationGrade */          0,
					/* contestationGroupSize */      BigInteger.valueOf(4),
					/* adhesionGrade */              6,
					/* adhesionGroupSize */          BigInteger.valueOf(4),
					/* secondMedianGrade */          0,
					/* secondMedianGroupSize */      BigInteger.valueOf(4),
					/* secondMedianGroupSign */      -1
			),
			Arguments.of(
					/* name */ "Weird tally",
					/* tally */ new Integer[]{ 1, 1, 1, 1, 1, 1, 1 },
					/* medianGrade */                3,
					/* medianGroupSize */            BigInteger.valueOf(1),
					/* contestationGrade */          2,
					/* contestationGroupSize */      BigInteger.valueOf(3),
					/* adhesionGrade */              4,
					/* adhesionGroupSize */          BigInteger.valueOf(3),
					/* secondMedianGrade */          2,
					/* secondMedianGroupSize */      BigInteger.valueOf(3),
					/* secondMedianGroupSign */      -1
			)
		);
	}
}
