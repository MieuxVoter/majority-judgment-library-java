package fr.mieuxvoter.mj;

/** Raised when the provided tally holds negative values, or infinity. */
class IncoherentTallyException extends InvalidTallyException {

    private static final long serialVersionUID = 5858986651601202903L;

    @Override
    public String getMessage() {
        return ("The provided tally holds negative values, or infinity. "
                + (null == super.getMessage() ? "" : super.getMessage()));
    }
}
