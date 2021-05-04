package fr.mieuxvoter.mj;

import java.util.Arrays;
import java.util.Comparator;


/**
 * Deliberate using Majority Judgment.
 * 
 * Sorts Proposals by their median Grade.
 * When two proposals share the same median Grade,
 * give reason to the largest group of people that did not give the median Grade.
 * 
 * This algorithm is score-based, for performance (and possible parallelization).
 * Each Proposal gets a score, higher (lexicographically) is "better" (depends of the meaning of the Grades).
 * We use Strings instead of Integers or raw Bits for the score.  Improve if you feel like it and can benchmark things.
 * 
 * https://en.wikipedia.org/wiki/Majority_judgment
 * https://fr.wikipedia.org/wiki/Jugement_majoritaire
 * 
 * Should this class be "final" ?
 */
public class MajorityJudgmentDeliberator implements DeliberatorInterface {

	public ResultInterface deliberate(TallyInterface tally) {
		ProposalTallyInterface[] tallies = tally.getProposalsTallies();
		Long amountOfJudges = tally.getAmountOfJudges();
		Integer amountOfProposals = tally.getAmountOfProposals();
		
		Result result = new Result();
		ProposalResult[] proposalResults = new ProposalResult[amountOfProposals];
		
		// I. Compute the scores of each Proposal
		for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
			ProposalTallyInterface proposalTally = tallies[proposalIndex];
			String score = computeScore(proposalTally, amountOfJudges);
			ProposalResult proposalResult = new ProposalResult();
			proposalResult.setScore(score);
			//proposalResult.setRank(???); // rank is computed below, AFTER the score pass
			proposalResults[proposalIndex] = proposalResult;
		}
		
		// II. Sort Proposals by score
		ProposalResult[] proposalResultsSorted = proposalResults.clone();
		assert(proposalResultsSorted[0].hashCode() == proposalResults[0].hashCode()); // we need a shallow clone
		Arrays.sort(proposalResultsSorted, new Comparator<ProposalResultInterface>() {
			@Override
			public int compare(ProposalResultInterface p0, ProposalResultInterface p1) {
				return p1.getScore().compareTo(p0.getScore());
			}
		});
		
		// III. Attribute a rank to each Proposal
		Integer rank = 1;
		for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
			ProposalResult proposalResult = proposalResultsSorted[proposalIndex];
			Integer actualRank = rank;
			if (proposalIndex > 0) {
				ProposalResult proposalResultBefore = proposalResultsSorted[proposalIndex-1];
				if (proposalResult.getScore().contentEquals(proposalResultBefore.getScore())) {
					actualRank = proposalResultBefore.getRank();
				}
			}
			proposalResult.setRank(actualRank);
			rank += 1;
		}
		
		result.setProposalResults(proposalResults);
		return result;
	}
	
	public String computeScore(ProposalTallyInterface tally, Long amountOfJudges) {
		return computeScore(tally, amountOfJudges, true, false);
	}

	public String computeScore(
			ProposalTallyInterface tally,
			Long amountOfJudges,
			Boolean favorContestation,
			Boolean onlyNumbers
	) {
		ProposalTallyAnalysis analysis = new ProposalTallyAnalysis();
		int amountOfGrades = tally.getTally().length;
		int digitsForGrade = ("" + amountOfGrades).length();
		int digitsForGroup = ("" + amountOfJudges).length() + 1;
		
		ProposalTallyInterface currentTally = tally.duplicate();
		
		String score = "";
		for (int i = 0; i < amountOfGrades; i++) {
			
			analysis.reanalyze(currentTally, favorContestation);
			
			if (0 < i && ! onlyNumbers) {
				score += "/";
			}
			
			score += String.format(
					"%0"+digitsForGrade+"d",
					analysis.getMedianGrade()
			);

			if ( ! onlyNumbers) {
				score += "_";
			}
			
			
			score += String.format(
					"%0"+digitsForGroup+"d",
					amountOfJudges + analysis.getSecondMedianGroupSize() * analysis.getSecondMedianGroupSign()
			);
			
			currentTally.moveJudgments(analysis.getMedianGrade(), analysis.getSecondMedianGrade());
		}
		
		return score;
	}

}
