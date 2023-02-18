package fr.mieuxvoter.mj;

import java.math.BigInteger;
import java.security.InvalidParameterException;

/**
 * The deliberator expects the proposals' tallies to hold the same amount of judgments. This
 * NormalizedTally accepts tallies with disparate amounts of judgments per proposal, and normalizes
 * them to their least common multiple, which amounts to using percentages, except we don't use
 * floating-point arithmetic.
 *
 * <p>This is useful when there are too many proposals for judges to be expected to judge them all,
 * and all the proposals received reasonably similar amounts of judgments.
 */
public class NormalizedTally extends Tally implements TallyInterface {

    public NormalizedTally(ProposalTallyInterface[] proposalsTallies) {
        super(proposalsTallies);
        initializeFromProposalsTallies(proposalsTallies);
    }

    public NormalizedTally(TallyInterface tally) {
        super(tally.getProposalsTallies());
        initializeFromProposalsTallies(tally.getProposalsTallies());
    }

    protected void initializeFromProposalsTallies(ProposalTallyInterface[] proposalsTallies) {
        Integer amountOfProposals = getAmountOfProposals();

        // Compute the Least Common Multiple
        BigInteger amountOfJudges = BigInteger.ONE;
        for (ProposalTallyInterface proposalTally : proposalsTallies) {
            amountOfJudges = lcm(amountOfJudges, proposalTally.getAmountOfJudgments());
        }

        if (0 == amountOfJudges.compareTo(BigInteger.ZERO)) {
            throw new InvalidParameterException(
                    "Cannot normalize: one or more proposals have no judgments.");
        }

        // Normalize proposals to the LCM
        ProposalTally[] normalizedTallies = new ProposalTally[amountOfProposals];
        for (int i = 0; i < amountOfProposals; i++) {
            ProposalTallyInterface proposalTally = proposalsTallies[i];
            ProposalTally normalizedTally = new ProposalTally(proposalTally);
            BigInteger factor = amountOfJudges.divide(proposalTally.getAmountOfJudgments());
            Integer amountOfGrades = proposalTally.getTally().length;
            BigInteger[] gradesTallies = normalizedTally.getTally();
            for (int j = 0; j < amountOfGrades; j++) {
                gradesTallies[j] = gradesTallies[j].multiply(factor);
            }
            normalizedTallies[i] = normalizedTally;
        }

        setProposalsTallies(normalizedTallies);
        setAmountOfJudges(amountOfJudges);
    }

    /**
     * Least Common Multiple
     *
     * <p>http://en.wikipedia.org/wiki/Least_common_multiple
     *
     * <p>lcm( 6, 9 ) = 18 lcm( 4, 9 ) = 36 lcm( 0, 9 ) = 0 lcm( 0, 0 ) = 0
     *
     * @author www.java2s.com
     * @param a first integer
     * @param b second integer
     * @return least common multiple of a and b
     */
    public static BigInteger lcm(BigInteger a, BigInteger b) {
        if (a.signum() == 0 || b.signum() == 0) return BigInteger.ZERO;
        return a.divide(a.gcd(b)).multiply(b).abs();
    }
}
