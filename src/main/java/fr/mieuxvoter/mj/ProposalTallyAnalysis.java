package fr.mieuxvoter.mj;

import java.math.BigInteger;

/**
 * Collect useful data on a proposal tally. Does NOT compute the rank, but provides all we need.
 *
 * <p>This uses BigInteger because in a normalization scenario we use the smallest common multiple
 * of the amounts of judges of proposals. It makes the code harder to read and understand, but it
 * allows us to bypass the floating-point nightmare of the normalization of merit profiles, which is
 * one way to handle default grades on some polls.
 */
public class ProposalTallyAnalysis {

    protected ProposalTallyInterface tally;

    protected BigInteger totalSize = BigInteger.ZERO; // amount of judges

    protected Integer medianGrade = 0;

    protected BigInteger medianGroupSize = BigInteger.ZERO; // amount of judges in the median group

    protected Integer contestationGrade = 0; // "best" grade of the contestation group

    protected BigInteger contestationGroupSize = BigInteger.ZERO; // of lower grades than median

    protected Integer adhesionGrade = 0; // "worst" grade of the adhesion group

    protected BigInteger adhesionGroupSize = BigInteger.ZERO; // of higher grades than median

    protected Integer secondMedianGrade = 0; // grade of the biggest group out of the median

    protected BigInteger secondMedianGroupSize = BigInteger.ZERO; // either contestation or adhesion

    protected Integer secondMedianGroupSign =
            0; // -1 for contestation, +1 for adhesion, 0 for empty group size

    public ProposalTallyAnalysis() {}

    public ProposalTallyAnalysis(ProposalTallyInterface tally) {
        reanalyze(tally);
    }

    public ProposalTallyAnalysis(ProposalTallyInterface tally, Boolean favorContestation) {
        reanalyze(tally, favorContestation);
    }

    public void reanalyze(ProposalTallyInterface tally) {
        reanalyze(tally, true);
    }

    public void reanalyze(ProposalTallyInterface tally, Boolean favorContestation) {
        this.tally = tally;
        this.totalSize = BigInteger.ZERO;
        this.medianGrade = 0;
        this.medianGroupSize = BigInteger.ZERO;
        this.contestationGrade = 0;
        this.contestationGroupSize = BigInteger.ZERO;
        this.adhesionGrade = 0;
        this.adhesionGroupSize = BigInteger.ZERO;
        this.secondMedianGrade = 0;
        this.secondMedianGroupSize = BigInteger.ZERO;
        this.secondMedianGroupSign = 0;

        BigInteger[] gradesTallies = this.tally.getTally();
        int amountOfGrades = gradesTallies.length;

        for (int grade = 0; grade < amountOfGrades; grade++) {
            BigInteger gradeTally = gradesTallies[grade];
            // assert(0 <= gradeTally);  // Negative tallies are not allowed.
            this.totalSize = this.totalSize.add(gradeTally);
        }

        Integer medianOffset = 1;
        if (!favorContestation) {
            medianOffset = 2;
        }
        BigInteger medianCursor =
                this.totalSize.add(BigInteger.valueOf(medianOffset)).divide(BigInteger.valueOf(2));
        //		Long medianCursor = (long) Math.floor((this.totalSize + medianOffset) / 2.0);

        BigInteger tallyBeforeCursor = BigInteger.ZERO;
        BigInteger tallyCursor = BigInteger.ZERO;
        Boolean foundMedian = false;
        Integer contestationGrade = 0;
        Integer adhesionGrade = 0;
        for (int grade = 0; grade < amountOfGrades; grade++) {
            BigInteger gradeTally = gradesTallies[grade];
            tallyBeforeCursor = tallyCursor;
            tallyCursor = tallyCursor.add(gradeTally);

            if (!foundMedian) {
                if (-1 < tallyCursor.compareTo(medianCursor)) { // tallyCursor >= medianCursor
                    foundMedian = true;
                    this.medianGrade = grade;
                    this.contestationGroupSize = tallyBeforeCursor;
                    this.medianGroupSize = gradeTally;
                    this.adhesionGroupSize =
                            this.totalSize
                                    .subtract(this.contestationGroupSize)
                                    .subtract(this.medianGroupSize);
                } else {
                    if (1 == gradeTally.compareTo(BigInteger.ZERO)) { // 0 < gradeTally
                        contestationGrade = grade;
                    }
                }
            } else {
                if (1 == gradeTally.compareTo(BigInteger.ZERO) && 0 == adhesionGrade) {
                    adhesionGrade = grade;
                }
            }
        }

        this.contestationGrade = contestationGrade;
        this.adhesionGrade = adhesionGrade;
        this.secondMedianGroupSize = this.contestationGroupSize.max(this.adhesionGroupSize);
        this.secondMedianGroupSign = 0;
        //		if (this.contestationGroupSize < this.adhesionGroupSize) {
        if (1 == this.adhesionGroupSize.compareTo(this.contestationGroupSize)) {
            this.secondMedianGrade = this.adhesionGrade;
            this.secondMedianGroupSign = 1;
            //		} else if (this.contestationGroupSize > this.adhesionGroupSize) {
        } else if (1 == this.contestationGroupSize.compareTo(this.adhesionGroupSize)) {
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
        if (0 == this.secondMedianGroupSize.compareTo(BigInteger.ZERO)) {
            this.secondMedianGroupSign = 0;
        }
    }

    public BigInteger getTotalSize() {
        return totalSize;
    }

    public Integer getMedianGrade() {
        return medianGrade;
    }

    public BigInteger getMedianGroupSize() {
        return medianGroupSize;
    }

    public Integer getContestationGrade() {
        return contestationGrade;
    }

    public BigInteger getContestationGroupSize() {
        return contestationGroupSize;
    }

    public Integer getAdhesionGrade() {
        return adhesionGrade;
    }

    public BigInteger getAdhesionGroupSize() {
        return adhesionGroupSize;
    }

    public Integer getSecondMedianGrade() {
        return secondMedianGrade;
    }

    public BigInteger getSecondMedianGroupSize() {
        return secondMedianGroupSize;
    }

    public Integer getSecondMedianGroupSign() {
        return secondMedianGroupSign;
    }
}
