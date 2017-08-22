package de.frohwerk.ipm.core;

public final class Criteria {
    protected Criteria(char sign, Option option, String lowerBound, String upperBound) {
        this.sign = sign;
        this.option = option;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public final char sign;
    public final Option option;
    public final String lowerBound;
    public final String upperBound;
}
