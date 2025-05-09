package fr.mieuxvoter.mj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;


public class ProposalResult implements ProposalResultInterface {

    protected Integer index;

    protected Integer rank;

    protected String score;

    protected BigInteger merit = BigInteger.ZERO;

    protected Double relativeMerit = 0.0;

    protected Double affineMerit = 0.0;

    protected Double relativeAffineMerit = 0.0;

    protected ProposalTallyAnalysis analysis;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public BigInteger getMerit() {
        return merit;
    }

    public void setMerit(BigInteger merit) {
        this.merit = merit;
    }

    public Double getRelativeMerit() {
        return relativeMerit;
    }

    public Double getAffineMerit() {
        return affineMerit;
    }

    public void setAffineMerit(Double affineMerit) {
        this.affineMerit = affineMerit;
    }

    public Double getRelativeAffineMerit() {
        return relativeAffineMerit;
    }

    public void computeRelativeMerit(BigInteger sumOfMerits) {
        if (sumOfMerits.compareTo(BigInteger.ZERO) == 0) {
            this.relativeMerit = 0.0;
            return;
        }

        this.relativeMerit = divide(getMerit(), sumOfMerits);
    }

    public void computeRelativeAffineMerit(Double sumOfAdjustedMerits) {
        if (sumOfAdjustedMerits == 0) {
            this.relativeAffineMerit = 0.0;
            return;
        }

        this.relativeAffineMerit = getAffineMerit() / sumOfAdjustedMerits;
    }

    public ProposalTallyAnalysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ProposalTallyAnalysis analysis) {
        this.analysis = analysis;
    }

    /**
     * This method assumes that the result will fit in a Double.
     * As we use it, the denominator should always be bigger than the numerator, so it's OK.
     */
    private Double divide(BigInteger numerator, BigInteger denominator) {
        return (new BigDecimal(numerator).divide(
                new BigDecimal(denominator), 15, RoundingMode.HALF_EVEN
        )).doubleValue();
    }
}
