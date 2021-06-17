package fr.mieuxvoter.mj;

import java.math.BigInteger;
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
 */
final public class MajorityJudgmentDeliberator implements DeliberatorInterface {

	@Override
	public ResultInterface deliberate(TallyInterface tally) {
		ProposalTallyInterface[] tallies = tally.getProposalsTallies();
		BigInteger amountOfJudges = tally.getAmountOfJudges();
		Integer amountOfProposals = tally.getAmountOfProposals();
		
		Result result = new Result();
		ProposalResult[] proposalResults = new ProposalResult[amountOfProposals];
		
		// I. Compute the scores of each Proposal
		for (int proposalIndex = 0; proposalIndex < amountOfProposals; proposalIndex++) {
			ProposalTallyInterface proposalTally = tallies[proposalIndex];
			String score = computeScore(proposalTally, amountOfJudges);
			ProposalTallyAnalysis analysis = new ProposalTallyAnalysis();
			analysis.reanalyze(proposalTally);
			ProposalResult proposalResult = new ProposalResult();
			proposalResult.setScore(score);
			proposalResult.setAnalysis(analysis);
			//proposalResult.setRank(???); // rank is computed below, AFTER the score pass
			proposalResults[proposalIndex] = proposalResult;
		}
		
		// II. Sort Proposals by score (lexicographical inverse)
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

	protected String computeScore(ProposalTallyInterface tally, BigInteger amountOfJudges) {
		return computeScore(tally, amountOfJudges, true, false);
	}

	/**
	 * A higher score means a better rank.
	 * Assumes that grades' tallies are provided from "worst" grade to "best" grade.
	 * 
	 * @param tally              Holds the tallies of each Grade for a single Proposal
	 * @param amountOfJudges
	 * @param favorContestation  Use the lower median, for example
	 * @param onlyNumbers        Do not use separation characters, match `^[0-9]+$`
	 * @return the score of the proposal
	 */
	protected String computeScore(
			ProposalTallyInterface tally,
			BigInteger amountOfJudges,
			Boolean favorContestation,
			Boolean onlyNumbers
	) {
		ProposalTallyAnalysis analysis = new ProposalTallyAnalysis();
		int amountOfGrades = tally.getTally().length;
		int digitsForGrade = countDigits(amountOfGrades);
		int digitsForGroup = countDigits(amountOfJudges) + 1;
		
		ProposalTallyInterface currentTally = tally.duplicate();
		
		String score = "";
		for (int i = 0; i < amountOfGrades; i++) {
			
			analysis.reanalyze(currentTally, favorContestation);
			
			if (0 < i && ! onlyNumbers) {
				score += "/";
			}
			
			score += String.format(
					"%0" + digitsForGrade + "d",
					analysis.getMedianGrade()
			);
			
			if ( ! onlyNumbers) {
				score += "_";
			}
			
			score += String.format(
					"%0" + digitsForGroup + "d",
					// We offset by amountOfJudges to keep a lexicographical order (no negatives)
					// amountOfJudges + secondMedianGroupSize * secondMedianGroupSign
					amountOfJudges.add(
							analysis.getSecondMedianGroupSize().multiply(
									BigInteger.valueOf(analysis.getSecondMedianGroupSign())
							)
					)
			);
			
			currentTally.moveJudgments(analysis.getMedianGrade(), analysis.getSecondMedianGrade());
		}
		
		return score;
	}

	protected int countDigits(int number) {
		return ("" + number).length();
	}

	protected int countDigits(BigInteger number) {
		return ("" + number).length();
	}

}
