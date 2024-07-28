package onespark.intergalactic_exchange_cli.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import onespark.intergalactic_exchange_cli.definition.InputClassification;
import onespark.intergalactic_exchange_cli.definition.Material;
import onespark.intergalactic_exchange_cli.definition.QuestionConfig;
import onespark.intergalactic_exchange_cli.definition.RomanNumeral;
import onespark.intergalactic_exchange_cli.exception.RomanNumeralSequenceException;
import onespark.intergalactic_exchange_cli.service.factory.MaterialValueTranslationMapFactory;
import onespark.intergalactic_exchange_cli.service.factory.NumeralValueTranslationMapFactory;

@Service
public class QuestionService {

    private final NumeralValueTranslationMapFactory numeralValueFactory;
    private final MaterialValueTranslationMapFactory materialValueFactory;
    private final NumeralValueCalculator numeralValueCalculator;

    public QuestionService(final NumeralValueTranslationMapFactory numeralValueFactory,
            final MaterialValueTranslationMapFactory materialValueFactory,
            final NumeralValueCalculator numeralValueCalculator) {
        this.numeralValueFactory = numeralValueFactory;
        this.materialValueFactory = materialValueFactory;
        this.numeralValueCalculator = numeralValueCalculator;
    }

    public void runQuestions(final QuestionConfig config) {
        for (String q : config.getQuestions()) {
            final boolean isCreditValueCheck = q.contains("many") || q.contains("Credits");
            if (!isCreditValueCheck && Arrays.asList(Material.values()).stream().anyMatch(m -> {
                return q.contains(StringUtils.capitalize(m.name().toLowerCase()));
            })) {
                System.out.println(String.format("\nCould not complete question \"%s\"", q));
                System.out.println("Double check the sentence structure.");
                continue;
            }

            final String splitOn = isCreditValueCheck ? "Credits is " : " is ";
            final List<String> vars = Arrays.asList(
                    q.replace("?", "").trim().split(splitOn)[1].split(" "));
            final List<String> numeralVars = isCreditValueCheck ? vars.subList(0, vars.size() - 1) : vars;
            final int numeralValue;
            try {
                numeralValue = numeralValueCalculator
                        .translateAndCalculate(config.getNumeralValueMap(), numeralVars);
            } catch (RomanNumeralSequenceException e) {
                System.out.println(String.format("\nCould not complete question \"%s\"", q));
                e.printSequenceException();
                continue;
            }

            final StringBuilder output = new StringBuilder();
            numeralVars.forEach(n -> {
                output.append(n);
                output.append(" ");
            });

            if (isCreditValueCheck) {
                final Material material = Material.valueOf(vars.get(vars.size() - 1).toUpperCase());
                output.append(StringUtils.capitalize(material.name().toLowerCase()));
                output.append(" is ");
                final int creditValue = config.getMaterialExchangeValueMap().get(material)
                        .multiply(new BigDecimal(numeralValue))
                        .intValue();
                output.append(creditValue);
                output.append(" Credits");

                System.out.println(output.toString());
            } else {
                output.append("is ");
                output.append(numeralValue);
                System.out.println(output.toString());
            }

        }
    }

    public QuestionConfig translateInputToConfiguration(final Map<InputClassification, List<String>> input) {
        final Map<String, RomanNumeral> numeralConfig = numeralValueFactory
                .build(input.get(InputClassification.NUMERAL_CONFIG));
        return new QuestionConfig.QuestionConfigBuilder()
                .questions(input.get(InputClassification.QUESTION))
                .numeralValueMap(numeralConfig)
                .materialExchangeValueMap(materialValueFactory.mapMaterialExchanges(numeralConfig,
                        input.get(InputClassification.MATERIAL_CONFIG)))
                .build();
    }

}
