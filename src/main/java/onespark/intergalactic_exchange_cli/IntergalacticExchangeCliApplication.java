package onespark.intergalactic_exchange_cli;

import static onespark.intergalactic_exchange_cli.definition.InputClassification.INVALID;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import onespark.intergalactic_exchange_cli.definition.InputClassification;
import onespark.intergalactic_exchange_cli.service.InputService;
import onespark.intergalactic_exchange_cli.service.QuestionService;

@SpringBootApplication
public class IntergalacticExchangeCliApplication implements CommandLineRunner {

	private final InputService inputService;
	private final QuestionService questionService;

	public IntergalacticExchangeCliApplication(final InputService inputService,
			final QuestionService questionService) {
		this.inputService = inputService;
		this.questionService = questionService;
	}

	public static void main(String[] args) {
		SpringApplication.run(IntergalacticExchangeCliApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		final List<String> filenames = new ArrayList<>(inputService.getFilesInSrc());

		dash(filenames);
		final Scanner scanner = new Scanner(System.in);
		final String userInput = scanner.nextLine();
		if (!Pattern.compile("\\d+").matcher(userInput).matches()) {
			System.exit(0);
		}

		final int selectionIndex = Integer.valueOf(userInput) - 1;
		if (selectionIndex >= filenames.size()) {
			System.exit(0);
		}
		final File file = inputService.readInputFromFile(filenames.get(selectionIndex));
		final Map<InputClassification, List<String>> input = inputService.extractInputClassificationMap(file);
		questionService.runQuestions(questionService.translateInputToConfiguration(input));
		input.get(INVALID).forEach(in -> {
			System.out.println(String.format("I have no idea what you are talking about\t\t:ErrorLine:[%s]\t", in));
		});

		scanner.close();
	}

	private void dash(final List<String> filenames) {
		hello();
		System.out.println("Choose an option...");
		final List<Integer> choices = new ArrayList<>();
		IntStream.range(0, filenames.size()).forEach(i -> choices.add(i));
		choices.forEach(i -> System.out.println(String.format("%d) %s", i + 1, filenames.get(i))));
	}

	private void hello() {
		String banner = "Intergalactic Exchange";
		try {
			final ClassLoader classLoader = getClass().getClassLoader();
			final File file = new File(classLoader.getResource("banner.txt").getFile());
			final String bannerFileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			banner = bannerFileContent;
		} catch (IOException e) {
		}
		System.out.println(banner);
		System.out.println("Hi there! Welcome to the Intergalactic Exchange Service.");
		System.out.println("We are here to help you escape planetary financial collapse. \n\n");
		System.out.println(
				"Add more files to the resource/in path or choose your own path to an Intergalactic Exchange Script.\n");
	}

}
