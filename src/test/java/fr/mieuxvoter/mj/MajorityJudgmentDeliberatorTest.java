package fr.mieuxvoter.mj;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MajorityJudgmentDeliberatorTest {

	@Test
	void testDemoUsage() {
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		TallyInterface tally = new Tally(new ProposalTallyInterface[] {
				new ProposalTally(new Integer[]{4, 5, 2, 1, 3, 1, 2}),
				new ProposalTally(new Integer[]{3, 6, 2, 1, 3, 1, 2}),
		}, 18L);
		ResultInterface result = mj.deliberate(tally);
		
//		System.out.println("Score 0: "+result.getProposalResults()[0].getScore());
//		System.out.println("Score 1: "+result.getProposalResults()[1].getScore());
		
		assertNotNull(result);
		assertEquals(2, result.getProposalResults().length);
		assertEquals(2, result.getProposalResults()[0].getRank());
		assertEquals(1, result.getProposalResults()[1].getRank());
	}
	
	@Test
	void testUsageWithBigNumbers() {
		DeliberatorInterface mj = new MajorityJudgmentDeliberator();
		TallyInterface tally = new Tally(new ProposalTallyInterface[] {
				new ProposalTally(new Long[]{11312415004L, 21153652410L, 24101523299L, 18758623562L}),
				new ProposalTally(new Long[]{11312415004L, 21153652400L, 24101523299L, 18758623572L}),
//				new ProposalTally(new Long[]{14526586452L, 40521123260L, 14745623120L, 40526235129L}),
		}, 75326214275L);
		ResultInterface result = mj.deliberate(tally);
		
//		System.out.println("Score 0: "+result.getProposalResults()[0].getScore());
//		System.out.println("Score 1: "+result.getProposalResults()[1].getScore());
//		System.out.println("Total "+(11312415004L+21153652410L+24101523299L+18758623562L));
		
		assertNotNull(result);
		assertEquals(2, result.getProposalResults().length);
		assertEquals(2, result.getProposalResults()[0].getRank());
		assertEquals(1, result.getProposalResults()[1].getRank());
	}

}
