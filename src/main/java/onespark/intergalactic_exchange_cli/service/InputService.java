package onespark.intergalactic_exchange_cli.service;

import static onespark.intergalactic_exchange_cli.definition.InputClassification.MATERIAL_CONFIG;
import static onespark.intergalactic_exchange_cli.definition.InputClassification.NUMERAL_CONFIG;
import static onespark.intergalactic_exchange_cli.definition.InputClassification.QUESTION;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import onespark.intergalactic_exchange_cli.definition.InputClassification;

@Service
public class InputService {
    private static final String CONTENT_BORDER = "\n<<====================================>>\n";

    public Map<InputClassification, List<String>> extractInputClassificationMap(final File input) throws IOException {
        final List<String> lines = FileUtils.readLines(input, StandardCharsets.UTF_8)
                .stream().filter(l -> !StringUtils.isEmpty(l.trim()))
                .collect(Collectors.toList());
        final Map<InputClassification, List<String>> inputMap = new HashMap<>();

        final EnumSet<InputClassification> validPatterns = EnumSet.of(NUMERAL_CONFIG, MATERIAL_CONFIG, QUESTION);
        validPatterns.forEach(inputLookup -> {
            final List<String> matches = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                final String l = lines.get(i).trim();
                final List<String> results = inputLookup.getPattern().matcher(l).results()
                        .map(m -> m.group())
                        .collect(Collectors.toList());
                matches.addAll(results);
            }
            inputMap.put(inputLookup, matches);
        });

        lines.removeIf(l -> inputMap.values().stream()
                .flatMap(List::stream).collect(Collectors.toList()).stream()
                .anyMatch(r -> l.contains(r)));
        inputMap.put(InputClassification.INVALID, lines);
        return inputMap;
    }

    public File readInputFromFile(final String filename) throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("in/" + filename).getFile());
        final String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        System.out.println(String.format("\n\n***\t\tIntergalactic Exchange Script\t\t***\n\n%s\n%s\n%s\n",
                CONTENT_BORDER, content, CONTENT_BORDER));
        return file;
    }

    public Set<String> getFilesInSrc() {
        final ClassLoader classLoader = getClass().getClassLoader();
        return Stream.of(new File(classLoader.getResource("in").getFile()).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
}