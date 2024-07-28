package onespark.intergalactic_exchange_cli.definition;

import java.util.regex.Pattern;

public enum InputClassification {
    NUMERAL_CONFIG("\\w+\sis\s[I,V,X,C,L,D,M]"),
    MATERIAL_CONFIG("^.*[Silver,Gold,Iron]\sis\s\\d+\sCredits"),
    QUESTION("^how m.+\sis \\w+.*\s?"),
    INVALID(".*");

    private final Pattern pattern;

    public Pattern getPattern() {
        return pattern;
    }

    private InputClassification(final String patternString) {
        this.pattern = Pattern.compile(patternString);
    }

}
