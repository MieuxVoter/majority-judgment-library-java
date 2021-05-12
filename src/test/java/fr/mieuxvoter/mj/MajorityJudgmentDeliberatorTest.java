package fr.mieuxvoter.mj;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import net.joshka.junit.json.params.JsonFileSource;


class MajorityJudgmentDeliberatorTest {

	@Test
	public void testDemoUsage() {
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		TallyInterface tally = new Tally(new ProposalTallyInterface[] {
				new ProposalTally(new Integer[]{4, 5, 2, 1, 3, 1, 2}),
				new ProposalTally(new Integer[]{3, 6, 2, 1, 3, 1, 2}),
		});
		
		ResultInterface result = mj.deliberate(tally);
		
		assertNotNull(result);
		assertEquals(2, result.getProposalResults().length);
		assertEquals(2, result.getProposalResults()[0].getRank());
		assertEquals(1, result.getProposalResults()[1].getRank());
	}

	@Test
	public void testDemoUsageWithBigNumbers() {
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		TallyInterface tally = new Tally(new ProposalTallyInterface[] {
				new ProposalTally(new Long[]{11312415004L, 21153652410L, 24101523299L, 18758623562L}),
				new ProposalTally(new Long[]{11312415004L, 21153652400L, 24101523299L, 18758623572L}),
//				new ProposalTally(new Long[]{14526586452L, 40521123260L, 14745623120L, 40526235129L}),
		});
		ResultInterface result = mj.deliberate(tally);
		
//		System.out.println("Score 0: "+result.getProposalResults()[0].getScore());
//		System.out.println("Score 1: "+result.getProposalResults()[1].getScore());
		
		assertNotNull(result);
		assertEquals(2, result.getProposalResults().length);
		assertEquals(2, result.getProposalResults()[0].getRank());
		assertEquals(1, result.getProposalResults()[1].getRank());
	}

	@DisplayName("Test majority judgment deliberation")
	@ParameterizedTest(name="#{index} {0}")
	@JsonFileSource(resources = "/assertions.json")
	public void testFromJson(JsonObject datum) {
		JsonArray jsonTallies = datum.getJsonArray("tallies");
		int amountOfProposals = jsonTallies.size();
		BigInteger amountOfParticipants = new BigInteger(datum.get("participants").toString());
		ProposalTallyInterface[] tallies = new ProposalTallyInterface[amountOfProposals];
		for (int i = 0; i < amountOfProposals; i++) {
			JsonArray jsonTally = jsonTallies.getJsonArray(i);
			int amountOfGrades = jsonTally.size();
			BigInteger[] tally = new BigInteger[amountOfGrades];
			for (int g = 0; g < amountOfGrades; g++) {
				JsonValue amountForGrade = jsonTally.get(g);
				tally[g] = new BigInteger(amountForGrade.toString());
			}
			tallies[i] = new ProposalTally(tally);
		}
		
		String mode = datum.getString("mode", "None");
		TallyInterface tally;
		if ("StaticDefault".equalsIgnoreCase(mode)) {
			tally = new TallyWithDefaultGrade(tallies, amountOfParticipants, datum.getInt("default"));
		} else if ("Normalized".equalsIgnoreCase(mode)) {
			tally = new TallyNormalized(tallies);
		} else {
			tally = new Tally(tallies, amountOfParticipants);
		}
		
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		ResultInterface result = mj.deliberate(tally);
		
		assertNotNull(result);
		JsonArray jsonRanks = datum.getJsonArray("ranks");
		for (int i = 0; i < amountOfProposals; i++) {
			assertEquals(
					jsonRanks.getInt(i),
					result.getProposalResults()[i].getRank(),
					"Rank of tally #"+i
			);
		}
	}

	@Test
	public void testWithStaticDefaultGrade() {
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		TallyInterface tally = new TallyWithDefaultGrade(new ProposalTallyInterface[] {
				new ProposalTally(new Integer[]{ 0, 0, 1 }),
				new ProposalTally(new Integer[]{ 0, 3, 0 }),
		}, 3L, 0);
		
		ResultInterface result = mj.deliberate(tally);
		
		assertNotNull(result);
		assertEquals(2, result.getProposalResults().length);
		assertEquals(2, result.getProposalResults()[0].getRank());
		assertEquals(1, result.getProposalResults()[1].getRank());
	}

	@Test
	public void testWithThousandsOfProposals() {
		int amountOfProposals = 1337;
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		ProposalTallyInterface[] tallies = new ProposalTallyInterface[amountOfProposals];
		for (int i = 0 ; i < amountOfProposals ; i++) {
			tallies[i] = new ProposalTally(new Integer[]{ 0, 2, 1 });
		}
		TallyInterface tally = new TallyWithDefaultGrade(tallies, 3, 0);
		
		ResultInterface result = mj.deliberate(tally);
		
		assertNotNull(result);
		assertEquals(amountOfProposals, result.getProposalResults().length);
		for (int i = 0 ; i < amountOfProposals ; i++) {
			assertEquals(1, result.getProposalResults()[i].getRank());
		}
	}

//	@Test
//	public void runBenchmarks() throws Exception {
//		Options options = new OptionsBuilder()
//				.include(this.getClass().getName() + ".*")
//				.mode(Mode.AverageTime)
//				.warmupTime(TimeValue.seconds(1))
//				.warmupIterations(6)
//				.threads(1)
//				.measurementIterations(6)
//				.forks(1)
//				.shouldFailOnError(true)
//				.shouldDoGC(true)
//				.build();
//
//		new Runner(options).run();
//	}

}
