package nl.rws.otl.git_tools.handler;

import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_OUTPUT;
import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_REPLACE;
import static nl.rws.otl.git_tools.file.FileUtils.writeFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;

public class OutputHandler {
    public static void handleOutput(
            CommandLine cmdArgs, byte[] outputBytes, Path filePath) throws IOException {
        if (cmdArgs.hasOption(OPTION_REPLACE) && filePath != null) {
            writeFile(outputBytes, filePath.toFile());
        } else if (cmdArgs.hasOption(OPTION_OUTPUT)) {
            writeFile(outputBytes, new File(cmdArgs.getOptionValue(OPTION_OUTPUT)));
        }
    }

}
