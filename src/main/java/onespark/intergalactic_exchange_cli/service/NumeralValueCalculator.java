package onespark.intergalactic_exchange_cli.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import onespark.intergalactic_exchange_cli.definition.RomanNumeral;
import onespark.intergalactic_exchange_cli.exception.RomanNumeralSequenceException;

@Service
public class NumeralValueCalculator {

    public int translateAndCalculate(final Map<String, RomanNumeral> numeralConfig,
            final List<String> terms) throws RomanNumeralSequenceException {
        final List<RomanNumeral> seq = new LinkedList<>();
        terms.forEach(t -> seq.add(numeralConfig.get(t)));
        final List<RomanNumeral> sequence = seq.stream().filter(rn -> rn != null)
                .collect(Collectors.toList());
        validateSequence(sequence);

        if (sequence.size() == 1) {
            return sequence.get(0).getValue();
        }

        final AtomicInteger value = new AtomicInteger(0);
        for (int i = 0; i < sequence.size() - 1; i++) {
            final RomanNumeral currentSymbol = sequence.get(i);
            final RomanNumeral nextSymbol = sequence.get(i + 1);
            if (currentSymbol.getValue() < nextSymbol.getValue()) {
                validateSubtraction(sequence, currentSymbol, nextSymbol);
                value.set(value.get() + (nextSymbol.getValue() - currentSymbol.getValue()));
                i++;
            } else {
                i = process(new AtomicInteger(i), value, sequence);
                if (sequence.size() - i == 2) {
                    value.set(value.get() + sequence.get(i + 1).getValue());
                }
            }
        }

        return value.get();
    }

    private void validateSubtraction(List<RomanNumeral> sequence, RomanNumeral currentSymbol, RomanNumeral nextSymbol)
            throws RomanNumeralSequenceException {
        if (currentSymbol.getIndex() % 2 == 0 || nextSymbol.getIndex() - currentSymbol.getIndex() > 2) {
            final RomanNumeralSequenceException e = new RomanNumeralSequenceException();
            e.setSequence(makeSequenceString(sequence));
            e.setReason(String.format("Character %s cannot be subtracted from %s", currentSymbol.name(),
                    nextSymbol.name()));
            throw e;
        }
    }

    private void validateSequence(final List<RomanNumeral> sequence) throws RomanNumeralSequenceException {
        final List<RomanNumeral> cleanSequence = sequence.stream().filter(rn -> rn != null)
                .collect(Collectors.toList());
        final String sequenceString = makeSequenceString(sequence);

        final boolean invalidRepeating = cleanSequence.stream().anyMatch(rn -> {
            final String dupString = rn.name().concat(rn.name());
            final Pattern dupCheck = Pattern.compile(dupString);
            final Pattern quadCheck = Pattern.compile(dupString.concat(dupString));
            final boolean isRepeated = dupCheck.matcher(sequenceString).find();
            return (!rn.isRepeatable() && isRepeated)
                    || (rn.isRepeatable() && quadCheck.matcher(sequenceString).find());
        });

        if (invalidRepeating) {
            final RomanNumeralSequenceException e = new RomanNumeralSequenceException();
            e.setSequence(sequenceString);
            e.setReason("Character exceeds maximum repeatability.");
            throw e;
        }
    }

    private int process(final AtomicInteger index, final AtomicInteger value, final List<RomanNumeral> sequence) {
        final RomanNumeral currentSymbol = sequence.get(index.get());
        value.set(value.get() + currentSymbol.getValue());

        if (index.get() == sequence.size() - 1) {
            return index.get();
        }

        final RomanNumeral nextSymbol = sequence.get(index.get() + 1);

        if (nextSymbol.getValue() == currentSymbol.getValue()) {
            index.set(index.incrementAndGet());
            process(index, value, sequence);
        }

        return index.get();
    }

    private String makeSequenceString(final List<RomanNumeral> sequence) {
        final List<RomanNumeral> cleanSequence = sequence.stream().filter(rn -> rn != null)
                .collect(Collectors.toList());
        final StringBuilder sb = new StringBuilder();
        cleanSequence.forEach(s -> sb.append(s.name()));
        return sb.toString();
    }
}
