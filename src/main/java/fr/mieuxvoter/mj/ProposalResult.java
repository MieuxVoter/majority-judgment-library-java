package fr.mieuxvoter.mj;

import java.math.BigInteger;

import static java.lang.Math.round;

public class ProposalResult implements ProposalResultInterface {

    protected Integer index;

    protected Integer rank;

    protected String score;

    protected BigInteger merit = BigInteger.ZERO;

    protected Double meritAsPercentage = 0.0;

    protected Double meritAdjusted = 0.0;

    protected Double meritAdjustedAsPercentage = 0.0;

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

    public Double getMeritAdjusted() {
        return meritAdjusted;
    }

    public ProposalResult setMeritAdjusted(Double meritAdjusted) {
        this.meritAdjusted = meritAdjusted;
        return this;
    }

    public Double getMeritAdjustedAsPercentage() {
        return meritAdjustedAsPercentage;
    }

    public void computeMeritAsPercentage(BigInteger sumOfMerits) {
        if (sumOfMerits.compareTo(BigInteger.ZERO) == 0) {
            this.meritAsPercentage = 0.0;
            this.meritAdjustedAsPercentage = 0.0;
            return;
        }

        this.meritAsPercentage = divideAsPercentage(getMerit(), sumOfMerits);
    }

    public void computeMeritAdjustedAsPercentage(Double sumOfAdjustedMerits) {
        if (sumOfAdjustedMerits == 0) {
            this.meritAdjustedAsPercentage = 0.0;
            return;
        }

        this.meritAdjustedAsPercentage = 100.0 * getMeritAdjusted() / sumOfAdjustedMerits;
    }

    // This is a HACK ; improve it !
    private Double divideAsPercentage(BigInteger numerator, BigInteger denominator) {
        long precision = 1_000_000_000; // big enough for 7 billion humans
        return round(numerator.multiply(
                BigInteger.valueOf(100 * precision * 10)
        ).divide(denominator).doubleValue() / 10.0) / (double) precision;
    }

    public ProposalTallyAnalysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ProposalTallyAnalysis analysis) {
        this.analysis = analysis;
    }
}
