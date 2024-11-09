package nl.rws.otl.git_tools.cli;

import org.apache.commons.cli.Options;

public class CliConfig {

    public static final String OPTION_INPUT = "f";
    public static final String OPTION_OUTPUT = "o";
    public static final String OPTION_REPLACE = "r";

    public static Options createOptions() {
        Options options = new Options();
        options.addRequiredOption(OPTION_INPUT, "file", true, "File to validate (or - for stdin)");
        options.addOption(OPTION_REPLACE, "replace", false, "Replace the file with the re-serialized version");
        options.addOption(OPTION_OUTPUT, "output", true, "File to write with the re-serialized version");
        return options;
    }

}
