package fr.mieuxvoter.mj;


/**
 * Collect useful data on a proposal tally.
 * Does NOT compute the rank, but provides all we need
 */
public class ProposalTallyAnalysis {

	protected ProposalTallyInterface tally;
	
	protected Long totalSize = 0L;  // amount of judges

	protected Integer medianGrade = 0;

	protected Long medianGroupSize = 0L;  // amount of judges in the median group
	
	protected Integer contestationGrade = 0;  // "best" grade of the contestation group 
	
	protected Long contestationGroupSize = 0L;  // of lower grades than median
	
	protected Integer adhesionGrade = 0;  // "worst" grade of the adhesion group

	protected Long adhesionGroupSize = 0L;  // of higher grades than median
	
	protected Integer secondMedianGrade = 0;  // grade of the biggest group out of the median
	
	protected Long secondMedianGroupSize = 0L;  // either contestation or adhesion
	
	protected Integer secondMedianGroupSign = 0;  // -1 for contestation, +1 for adhesion, 0 for empty group size

	
	public ProposalTallyAnalysis() {}
	
	public ProposalTallyAnalysis(ProposalTallyInterface tally) {
		reanalyze(tally);
	}

	public void reanalyze(ProposalTallyInterface tally) {
		reanalyze(tally, true);
	}
	
	public void reanalyze(ProposalTallyInterface tally, Boolean favorContestation) {
		this.tally = tally;
		this.totalSize = 0L;
		this.medianGrade = 0;
		this.medianGroupSize = 0L;
		this.contestationGrade = 0;
		this.contestationGroupSize = 0L;
		this.adhesionGrade = 0;
		this.adhesionGroupSize = 0L;
		this.secondMedianGrade = 0;
		this.secondMedianGroupSize = 0L;
		this.secondMedianGroupSign = 0;
		
		Long[] gradesTallies = this.tally.getTally();
		int amountOfGrades = gradesTallies.length;

		for (int grade = 0; grade < amountOfGrades; grade++) {
			Long gradeTally = gradesTallies[grade];
			assert(0 <= gradeTally);  // Negative tallies are not allowed.
			this.totalSize += gradeTally;
		}
		
		Integer medianOffset = 1;
		if ( ! favorContestation) {
			medianOffset = 2;
		}
		Long medianCursor = (long) Math.floor((this.totalSize + medianOffset) / 2.0);
		
		Long tallyBeforeCursor = 0L;
		Long tallyCursor = 0L;
		Boolean foundMedian = false;
		Integer contestationGrade = 0;
		Integer adhesionGrade = 0;
		for (int grade = 0; grade < amountOfGrades; grade++) {
			Long gradeTally = gradesTallies[grade];
			tallyBeforeCursor = tallyCursor;
			tallyCursor += gradeTally;
			
			if ( ! foundMedian) {
				if (tallyCursor >= medianCursor) {
					foundMedian = true;
					this.medianGrade = grade;
					this.contestationGroupSize = tallyBeforeCursor;
					this.medianGroupSize = gradeTally;
					this.adhesionGroupSize = this.totalSize - this.contestationGroupSize - this.medianGroupSize;
				} else {
					if (0 < gradeTally) {
						contestationGrade = grade;
					}
				}
			} else {
				if (0 < gradeTally && 0 == adhesionGrade) {
					adhesionGrade = grade;
				}
			}
		}
		
		this.contestationGrade = contestationGrade;
		this.adhesionGrade = adhesionGrade;
		this.secondMedianGroupSize = Math.max(this.contestationGroupSize, this.adhesionGroupSize);
		this.secondMedianGroupSign = 0;
		if (this.contestationGroupSize < this.adhesionGroupSize) {
			this.secondMedianGrade = this.adhesionGrade;
			this.secondMedianGroupSign = 1;
		} else if (this.contestationGroupSize > this.adhesionGroupSize) {
			this.secondMedianGrade = this.contestationGrade;
			this.secondMedianGroupSign = -1;
		} else {
			if (favorContestation) {
				this.secondMedianGrade = this.contestationGrade;
				this.secondMedianGroupSign = -1;
			} else {
				this.secondMedianGrade = this.adhesionGrade;
				this.secondMedianGroupSign = 1;
			}
		}
		if (0 == this.secondMedianGroupSize) {
			this.secondMedianGroupSign = 0;
		}
	}
	
	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public Integer getMedianGrade() {
		return medianGrade;
	}

	public void setMedianGrade(Integer medianGrade) {
		this.medianGrade = medianGrade;
	}
	
	public Long getMedianGroupSize() {
		return medianGroupSize;
	}

	public void setMedianGroupSize(Long medianGroupSize) {
		this.medianGroupSize = medianGroupSize;
	}
	
	public Integer getContestationGrade() {
		return contestationGrade;
	}

	public void setContestationGrade(Integer contestationGrade) {
		this.contestationGrade = contestationGrade;
	}

	public Long getContestationGroupSize() {
		return contestationGroupSize;
	}

	public void setContestationGroupSize(Long contestationGroupSize) {
		this.contestationGroupSize = contestationGroupSize;
	}

	public Integer getAdhesionGrade() {
		return adhesionGrade;
	}

	public void setAdhesionGrade(Integer adhesionGrade) {
		this.adhesionGrade = adhesionGrade;
	}
	
	public Long getAdhesionGroupSize() {
		return adhesionGroupSize;
	}

	public void setAdhesionGroupSize(Long adhesionGroupSize) {
		this.adhesionGroupSize = adhesionGroupSize;
	}
	
	public Integer getSecondMedianGrade() {
		return secondMedianGrade;
	}

	public void setSecondMedianGrade(Integer secondMedianGrade) {
		this.secondMedianGrade = secondMedianGrade;
	}

	public Long getSecondMedianGroupSize() {
		return secondMedianGroupSize;
	}

	public void setSecondMedianGroupSize(Long secondMedianGroupSize) {
		this.secondMedianGroupSize = secondMedianGroupSize;
	}

	public Integer getSecondMedianGroupSign() {
		return secondMedianGroupSign;
	}

	public void setSecondMedianGroupSign(Integer sign) {
		this.secondMedianGroupSign = sign;
	}

}
