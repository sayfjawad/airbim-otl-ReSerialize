package nl.rws.otl.git_tools.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@Slf4j
public class CliArgumentParser {

    public static CommandLine parseArguments(Options options, String[] args) {
        try {
            return new DefaultParser().parse(options, args, true);
        } catch (ParseException e) {
            log.error("Error parsing arguments: {}", e.getMessage());
            return null;
        }
    }

}
