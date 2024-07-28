package onespark.intergalactic_exchange_cli.service.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import onespark.intergalactic_exchange_cli.definition.RomanNumeral;

@Service
public class NumeralValueTranslationMapFactory {

    public Map<String, RomanNumeral> build(final List<String> configLines) {
        final Map<String, RomanNumeral> map = new HashMap<>();
        configLines.forEach(l -> {
            final String[] values = l.split(" is ");
            map.put(values[0], RomanNumeral.valueOf(values[1]));
        });
        return map;
    }

}
