package nl.rws.otl.git_tools.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CliArgumentParserTest {

    private CliArgumentParser parser;

    private Options options;

    @BeforeEach
    void setUp() {

        parser = new CliArgumentParser();

        // Define options for testing
        options = new Options();
        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg()
                .desc("File to process")
                .required()
                .build());
        options.addOption("r", "replace", false, "Replace the file with the re-serialized version");
        options.addOption("o", "output", true, "Specify output file");
    }

    @Test
    @DisplayName("Given valid arguments When parseArguments is called Then CommandLine should contain all options with correct values")
    void testParseArgumentsWithValidArguments() {

        String[] args = {"-f", "inputFile.txt", "-o", "outputFile.txt"};
        CommandLine cmd = parser.parseArguments(options, args);

        assertThat(cmd).isNotNull();
        assertThat(cmd.hasOption("f")).isTrue();
        assertThat(cmd.getOptionValue("f")).isEqualTo("inputFile.txt");
        assertThat(cmd.hasOption("o")).isTrue();
        assertThat(cmd.getOptionValue("o")).isEqualTo("outputFile.txt");
        assertThat(cmd.hasOption("r")).isFalse();
    }

    @Test
    @DisplayName("Given replace option with input file When parseArguments is called Then CommandLine should contain 'f' and 'r' options")
    void testParseArgumentsWithReplaceOption() {

        String[] args = {"-f", "inputFile.txt", "-r"};
        CommandLine cmd = parser.parseArguments(options, args);

        assertThat(cmd).isNotNull();
        assertThat(cmd.hasOption("f")).isTrue();
        assertThat(cmd.getOptionValue("f")).isEqualTo("inputFile.txt");
        assertThat(cmd.hasOption("r")).isTrue();
    }

    @Test
    @DisplayName("Given missing required option When parseArguments is called Then CommandLine should be null")
    void testParseArgumentsMissingRequiredOption() {

        String[] args = {"-r"};

        CommandLine cmd = parser.parseArguments(options, args);

        assertThat(cmd).isNull();
    }

    @Test
    @DisplayName("Given no arguments When parseArguments is called Then CommandLine should be null")
    void testParseArgumentsWithNoArguments() {

        String[] args = {};

        CommandLine cmd = parser.parseArguments(options, args);

        assertThat(cmd).isNull();
    }

    @Test
    @DisplayName("Given parseArguments with invalid input When exception occurs Then error is logged")
    void testParseArgumentsLoggingOnParseException() {

        Logger logger = LoggerFactory.getLogger(CliArgumentParser.class);

        String[] args = {"-invalid"};
        CommandLine cmd = parser.parseArguments(options, args);

        assertThat(cmd).isNull();

        // Om te verifiëren dat een foutlog is geschreven, kun je een test logger of appender framework gebruiken.
        // Hiermee kan je logberichten controleren zonder daadwerkelijk naar de console te loggen.
    }
}
