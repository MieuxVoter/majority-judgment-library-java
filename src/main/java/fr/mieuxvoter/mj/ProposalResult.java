package fr.mieuxvoter.mj;

import java.math.BigInteger;

import static java.lang.Math.round;

public class ProposalResult implements ProposalResultInterface {

    protected Integer index;

    protected Integer rank;

    protected String score;

    protected BigInteger merit = BigInteger.ZERO;

    protected Double meritAsPercentage = 0.0;

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

    public Double getMeritAsPercentage() {
        return meritAsPercentage;
    }

    public void computeMeritAsPercentage(BigInteger sumOfMerits) {
        if (sumOfMerits.compareTo(BigInteger.ZERO) == 0) {
            this.meritAsPercentage = 0.0;
            return;
        }

        long precision = 1_000_000_000; // big enough for 7 billion humans
        this.meritAsPercentage = round(this.getMerit().multiply(
                BigInteger.valueOf(100 * precision * 10)
        ).divide(sumOfMerits).doubleValue() / 10.0) / (double) precision;
    }

    public ProposalTallyAnalysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ProposalTallyAnalysis analysis) {
        this.analysis = analysis;
    }
}
