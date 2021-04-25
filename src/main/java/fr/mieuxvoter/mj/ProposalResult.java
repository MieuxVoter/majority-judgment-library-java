package fr.mieuxvoter.mj;


public class ProposalResult implements ProposalResultInterface {
	
	protected Integer rank;
	
	protected String score;
	
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

}
