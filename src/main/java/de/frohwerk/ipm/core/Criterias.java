package de.frohwerk.ipm.core;

import static com.google.common.base.Preconditions.checkArgument;

public class Criterias {
    public static Criteria include(final Option option, final String value) {
        checkArgument(!option.requiresUpperBound(), "Option %s requires upper bound", option);
        return new Criteria('I', option, value, null);
    }

    public static Criteria include(final Option option, final String lowerBound, final String upperBound) {
        checkArgument(option.requiresUpperBound(), "Option %s requires no upper bound", option);
        return new Criteria('I', option, lowerBound, upperBound);
    }

    public static Criteria exclude(final Option option, final String value) {
        checkArgument(!option.requiresUpperBound(), "Option %s requires upper bound", option);
        return new Criteria('E', option, value, null);
    }

    public static Criteria exclude(final Option option, final String lowerBound, final String upperBound) {
        checkArgument(option.requiresUpperBound(), "Option %s requires no upper bound", option);
        return new Criteria('E', option, lowerBound, upperBound);
    }
}
