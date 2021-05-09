package fr.mieuxvoter.mj;

import java.math.BigInteger;
import java.util.Arrays;

public class ProposalTally implements ProposalTallyInterface {

	protected Long[] tally;

	// Should we allow this as well?
	//public ProposalTally() {}
	
	public ProposalTally(Integer[] tally) {
		int tallyLength = tally.length;
		Long[] doublesTally = new Long[tallyLength];
		for (int i = 0 ; i < tallyLength ; i++) {
			doublesTally[i] = Long.valueOf(tally[i]);
		}
		setTally(doublesTally);
	}
	
	public ProposalTally(Long[] tally) {
		setTally(tally);
	}
	
	public void setTally(Long[] tally) {
		this.tally = tally;
	}
	
	@Override
	public Long[] getTally() {
		return this.tally;
	}

	@Override
	public ProposalTallyInterface duplicate() {
		return new ProposalTally(Arrays.copyOf(this.tally, this.tally.length));
	}

	@Override
	public void moveJudgments(Integer fromGrade, Integer intoGrade) {
		this.tally[intoGrade] += this.tally[fromGrade]; 
		this.tally[fromGrade] = 0L; 
	}

	@Override
	public BigInteger getAmountOfJudgments() {
		BigInteger sum = BigInteger.valueOf(0);
		int tallyLength = this.tally.length;
		for (int i = 0 ; i < tallyLength ; i++) {
			sum = sum.add(BigInteger.valueOf(this.tally[i]));
		}
		return sum;
	}

}
