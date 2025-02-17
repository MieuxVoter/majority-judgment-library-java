package fr.mieuxvoter.mj;

public class ProposalResult implements ProposalResultInterface {

    protected Integer index;

    protected Integer rank;

    protected String score;

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

    public ProposalTallyAnalysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ProposalTallyAnalysis analysis) {
        this.analysis = analysis;
    }
}
