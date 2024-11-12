package nl.rws.otl.git_tools;


import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rws.otl.git_tools.cli.CliArgumentParser;
import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import nl.rws.otl.git_tools.hash.HashSha256;
import nl.rws.otl.git_tools.ontology.OntologyDocumentCreator;
import nl.rws.otl.git_tools.ontology.OntologyProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;

@Slf4j
@RequiredArgsConstructor
public class ReSerialize {


    private final CliArgumentParser cliArgumentParser;

    private final OntologyProcessor ontologyProcessor;

    private final CliConfig cliConfig;

    private final FileUtils fileUtils;

    private final HashSha256 hashSha256;

    private static final int STATUS_ERROR = 2;

    public void reserialize(String[] args) {

        Options options = cliConfig.createOptions();
        CommandLine cmdArgs = cliArgumentParser.parseArguments(options, args);

        if (cmdArgs == null) {
            printHelpAndExit(options);
        }

        Path filePath = fileUtils.getFilePath(cmdArgs);
        OWLOntologyDocumentSourceBase documentSource = new OntologyDocumentCreator().createDocumentSource(
                filePath);
        if (documentSource == null) {
            System.exit(STATUS_ERROR);
        }

        String inputHash = hashSha256.computeFileHash(documentSource);
        if (inputHash == null) {
            System.exit(STATUS_ERROR);
        }

        ontologyProcessor.processOntology(cmdArgs, documentSource, inputHash, filePath);
    }


    private static void printHelpAndExit(final Options options) {

        new HelpFormatter().printHelp("ReSerialize", options, true);
        System.exit(STATUS_ERROR);
    }
}
