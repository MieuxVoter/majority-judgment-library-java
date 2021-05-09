package fr.mieuxvoter.mj;

import java.math.BigInteger;
import java.util.Arrays;

public class ProposalTally implements ProposalTallyInterface {

	protected BigInteger[] tally;

	// Should we allow this as well?
	//public ProposalTally() {}
	
	public ProposalTally(Integer[] tally) {
		int tallyLength = tally.length;
		BigInteger[] bigTally = new BigInteger[tallyLength];
		for (int i = 0 ; i < tallyLength ; i++) {
			bigTally[i] = BigInteger.valueOf(tally[i]);
		}
		setTally(bigTally);
	}
	
	public ProposalTally(Long[] tally) {
		int tallyLength = tally.length;
		BigInteger[] bigTally = new BigInteger[tallyLength];
		for (int i = 0 ; i < tallyLength ; i++) {
			bigTally[i] = BigInteger.valueOf(tally[i]);
		}
		setTally(bigTally);
	}
	
	public ProposalTally(BigInteger[] tally) {
		setTally(tally);
	}
	
	public void setTally(BigInteger[] tally) {
		this.tally = tally;
	}
	
	@Override
	public BigInteger[] getTally() {
		return this.tally;
	}

	@Override
	public ProposalTallyInterface duplicate() {
		return new ProposalTally(Arrays.copyOf(this.tally, this.tally.length));
	}

	@Override
	public void moveJudgments(Integer fromGrade, Integer intoGrade) {
//		this.tally[intoGrade] += this.tally[fromGrade]; 
		this.tally[intoGrade] = this.tally[intoGrade].add(this.tally[fromGrade]); 
		this.tally[fromGrade] = BigInteger.ZERO; 
	}

	@Override
	public BigInteger getAmountOfJudgments() {
		BigInteger sum = BigInteger.ZERO;
		int tallyLength = this.tally.length;
		for (int i = 0 ; i < tallyLength ; i++) {
			sum = sum.add(this.tally[i]);
		}
		return sum;
	}

}
