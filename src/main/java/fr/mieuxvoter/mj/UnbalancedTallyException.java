package fr.mieuxvoter.mj;

/**
 * Raised when the provided tally does not hold the same amount of judgments for each proposal, and
 * normalization is required.
 */
class UnbalancedTallyException extends InvalidTallyException {

    private static final long serialVersionUID = 5041093000505081735L;

    @Override
    public String getMessage() {
        return ("The provided tally is unbalanced, as some proposals received more judgments than"
                + " others. \n"
                + "You need to set a strategy for balancing tallies. To that effect, \n"
                + "you may use StaticDefaultTally, MedianDefaultTally, or NormalizedTally"
                + " instead of Tally. \n"
                + (null == super.getMessage() ? "" : super.getMessage()));
    }
}
