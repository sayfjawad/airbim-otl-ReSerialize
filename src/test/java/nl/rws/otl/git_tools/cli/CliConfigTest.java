package nl.rws.otl.git_tools.cli;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CliConfigTest {

    private CliConfig cliConfig;

    @BeforeEach
    void setUp() {
        cliConfig = new CliConfig();
    }

    @Test
    @DisplayName("Given CliConfig When createOptions is called Then Options should contain all defined options")
    void testCreateOptionsContainsAllDefinedOptions() {
        Options options = cliConfig.createOptions();

        // Assert that options are not null and contain the expected options
        assertThat(options).isNotNull();
        assertThat(options.hasOption(CliConfig.OPTION_INPUT)).isTrue();
        assertThat(options.hasOption(CliConfig.OPTION_REPLACE)).isTrue();
        assertThat(options.hasOption(CliConfig.OPTION_OUTPUT)).isTrue();
    }

    @Test
    @DisplayName("Given CliConfig When createOptions is called Then OPTION_INPUT should be required and have an argument")
    void testOptionInputIsRequiredAndHasArgument() {
        Options options = cliConfig.createOptions();

        assertThat(options.getOption(CliConfig.OPTION_INPUT).isRequired()).isTrue();
        assertThat(options.getOption(CliConfig.OPTION_INPUT).hasArg()).isTrue();
        assertThat(options.getOption(CliConfig.OPTION_INPUT).getDescription()).isEqualTo("File to validate (or - for stdin)");
    }

    @Test
    @DisplayName("Given CliConfig When createOptions is called Then OPTION_REPLACE should be optional and not have an argument")
    void testOptionReplaceIsOptionalAndHasNoArgument() {
        Options options = cliConfig.createOptions();

        assertThat(options.getOption(CliConfig.OPTION_REPLACE).isRequired()).isFalse();
        assertThat(options.getOption(CliConfig.OPTION_REPLACE).hasArg()).isFalse();
        assertThat(options.getOption(CliConfig.OPTION_REPLACE).getDescription()).isEqualTo("Replace the file with the re-serialized version");
    }

    @Test
    @DisplayName("Given CliConfig When createOptions is called Then OPTION_OUTPUT should be optional and have an argument")
    void testOptionOutputIsOptionalAndHasArgument() {
        Options options = cliConfig.createOptions();

        assertThat(options.getOption(CliConfig.OPTION_OUTPUT).isRequired()).isFalse();
        assertThat(options.getOption(CliConfig.OPTION_OUTPUT).hasArg()).isTrue();
        assertThat(options.getOption(CliConfig.OPTION_OUTPUT).getDescription()).isEqualTo("File to write with the re-serialized version");
    }
}
