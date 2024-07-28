package onespark.intergalactic_exchange_cli.exception;

import io.micrometer.common.util.StringUtils;

public class RomanNumeralSequenceException extends Exception {
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private String sequence;

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    public void printSequenceException() {
        System.out.println(String.format(
                "Invalid Roman Numeral Sequence:\t%s\t:\t%s",
                this.sequence,
                StringUtils.isBlank(reason) ? this.getMessage() : this.getReason()));
    }
}
