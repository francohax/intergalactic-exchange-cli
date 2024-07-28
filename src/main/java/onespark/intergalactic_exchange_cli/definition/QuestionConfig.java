package onespark.intergalactic_exchange_cli.definition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionConfig {

    private final Map<String, RomanNumeral> numeralValueMap;
    public Map<String, RomanNumeral> getNumeralValueMap() {
        return numeralValueMap;
    }

    private final Map<Material, BigDecimal> materialExchangeValueMap;
    public Map<Material, BigDecimal> getMaterialExchangeValueMap() {
        return materialExchangeValueMap;
    }

    private final List<String> questions;
    public List<String> getQuestions() {
        return questions;
    }

    public QuestionConfig(final Map<String, RomanNumeral> numeralValueMap,
            final Map<Material, BigDecimal> materialExchangeValueMap,
            final List<String> questions) {
        this.numeralValueMap = numeralValueMap;
        this.materialExchangeValueMap = materialExchangeValueMap;
        this.questions = questions;
    }

    public static class QuestionConfigBuilder {
        private Map<String, RomanNumeral> numeralValueMap;
        private Map<Material, BigDecimal> materialExchangeValueMap;
        private List<String> questions;

        public QuestionConfigBuilder() {
            this.numeralValueMap = new HashMap<>();
            this.materialExchangeValueMap = new HashMap<>();
            this.questions = new ArrayList<>();
        }

        public QuestionConfigBuilder numeralValueMap(final Map<String, RomanNumeral> numValueMap) {
            this.numeralValueMap = numValueMap;
            return this;
        }

        public QuestionConfigBuilder materialExchangeValueMap(final Map<Material, BigDecimal> materialValueMap) {
            this.materialExchangeValueMap = materialValueMap;
            return this;
        }

        public QuestionConfigBuilder questions(final List<String> questions) {
            this.questions = questions;
            return this;
        }

        public QuestionConfig build() {
            return new QuestionConfig(this.numeralValueMap, this.materialExchangeValueMap, this.questions);
        }
    }

}
