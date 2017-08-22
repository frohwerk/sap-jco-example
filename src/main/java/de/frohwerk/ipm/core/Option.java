package de.frohwerk.ipm.core;

public enum Option {
    EQ(false), BT(true), CP(false);

    private Option(boolean requiresUpperBound) {
        this.requiresUpperBound = requiresUpperBound;
    }

    public boolean requiresUpperBound() {
        return requiresUpperBound;
    }

    private final boolean requiresUpperBound;
}
