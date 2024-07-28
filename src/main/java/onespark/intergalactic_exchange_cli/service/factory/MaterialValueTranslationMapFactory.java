package onespark.intergalactic_exchange_cli.service.factory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import onespark.intergalactic_exchange_cli.definition.Material;
import onespark.intergalactic_exchange_cli.definition.RomanNumeral;
import onespark.intergalactic_exchange_cli.exception.RomanNumeralSequenceException;
import onespark.intergalactic_exchange_cli.service.NumeralValueCalculator;

@Service
public class MaterialValueTranslationMapFactory {

    private final NumeralValueCalculator numeralValueCalculator;

    public MaterialValueTranslationMapFactory(final NumeralValueCalculator numeralValueCalculator) {
        this.numeralValueCalculator = numeralValueCalculator;
    }

    public Map<Material, BigDecimal> mapMaterialExchanges(final Map<String, RomanNumeral> numeralConfig,
            final List<String> configLines) {
        final Map<Material, BigDecimal> materialExchangeMap = new HashMap<>();
        configLines.forEach(configLine -> {
            final String line = configLine.toString().toLowerCase();
            final Optional<Material> materialOpt = Arrays.asList(Material.values()).stream()
                    .filter(m -> line.contains(m.name().toLowerCase())).findFirst();
            if (materialOpt.isEmpty()) {
                throw new RuntimeException("No material found in config line");
            }

            final Material material = materialOpt.get();
            final String[] splitLine = line.split(material.name().toLowerCase());
            final String[] terms = splitLine[0].trim().split("\s");

            final int creditAmount = Integer.parseInt(splitLine[1].trim().split("\s")[1]);

            try {
                final int numeralResult = numeralValueCalculator.translateAndCalculate(numeralConfig,
                        Arrays.asList(terms));
                materialExchangeMap.put(material,
                        new BigDecimal(creditAmount).divide(new BigDecimal(numeralResult)).setScale(2));
            } catch (RomanNumeralSequenceException e) {
                System.out.println(String.format("Could not complete parsing line \"%s\"", configLine.toString()));
                e.printSequenceException();
            }
        });

        return materialExchangeMap;
    }

}
