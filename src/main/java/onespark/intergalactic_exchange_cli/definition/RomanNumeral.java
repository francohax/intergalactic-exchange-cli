package onespark.intergalactic_exchange_cli.definition;

public enum RomanNumeral {
    I(1, 1, true),
    V(2, 5, false),
    X(3, 10, true),
    L(4, 50, false),
    C(5, 100, true),
    D(6, 500, false),
    M(7, 1000, true);

    private final int index;
    public int getIndex() {
        return this.index;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    private final boolean repeatable;

    public boolean isRepeatable() {
        return repeatable;
    }

    private RomanNumeral(final int index, final int value, final boolean canRepeat) {
        this.index = index;
        this.value = value;
        this.repeatable = canRepeat;
    }

}