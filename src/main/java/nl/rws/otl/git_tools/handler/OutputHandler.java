package nl.rws.otl.git_tools.handler;

import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_OUTPUT;
import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_REPLACE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import nl.rws.otl.git_tools.file.FileUtils;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class OutputHandler {
    private final FileUtils fileUtils;
    public void handleOutput(
            CommandLine cmdArgs, byte[] outputBytes, Path filePath) throws IOException {
        if (cmdArgs.hasOption(OPTION_REPLACE) && filePath != null) {
            fileUtils.writeFile(outputBytes, filePath.toFile());
        } else if (cmdArgs.hasOption(OPTION_OUTPUT)) {
            fileUtils.writeFile(outputBytes, new File(cmdArgs.getOptionValue(OPTION_OUTPUT)));
        }
    }

}
