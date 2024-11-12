package nl.rws.otl.git_tools;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rws.otl.git_tools.cli.CliArgumentParser;
import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import nl.rws.otl.git_tools.hash.HashCalculator;
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
    private final HashCalculator hashCalculator;
    private final OntologyDocumentCreator ontologyDocumentCreator;

    private static final int STATUS_ERROR = 2;

    public int reserialize(String[] args) {
        Options options = cliConfig.createOptions();
        CommandLine cmdArgs = cliArgumentParser.parseArguments(options, args);

        //
        if (cmdArgs == null) {
            printHelpAndExit(options);
            System.exit(STATUS_ERROR);
        }

        Optional<Path> filePath = getFilePath(cmdArgs);
        if (filePath.isEmpty()) {
            log.error("Invalid or missing file path");
            System.exit(STATUS_ERROR);
        }

        Optional<OWLOntologyDocumentSourceBase> documentSource = ontologyDocumentCreator.createDocumentSource(filePath.get());
        if (documentSource.isEmpty()) {
            log.error("Failed to create document source");
            System.exit(STATUS_ERROR);
        }

        Optional<String> inputHash = computeHash(documentSource.get());
        if (inputHash.isEmpty()) {
            log.error("Failed to compute file hash");
            System.exit(STATUS_ERROR);
        }

        return processOntology(cmdArgs, documentSource.get(), inputHash.get(), filePath.get());
    }

    private Optional<Path> getFilePath(CommandLine cmdArgs) {
        return Optional.ofNullable(fileUtils.getFilePath(cmdArgs));
    }

    private Optional<String> computeHash(OWLOntologyDocumentSourceBase documentSource) {
        return Optional.ofNullable(hashCalculator.computeFileHash(documentSource));
    }

    private int processOntology(CommandLine cmdArgs, OWLOntologyDocumentSourceBase documentSource, String inputHash, Path filePath) {
        try {
            ontologyProcessor.processOntology(cmdArgs, documentSource, inputHash, filePath);
            return 0;  // Successful processing
        } catch (Exception e) {
            log.error("Error processing ontology: {}", e.getMessage(), e);
            return STATUS_ERROR;
        }
    }

    private int printHelpAndExit(final Options options) {
        new HelpFormatter().printHelp("ReSerialize", options, true);
        return STATUS_ERROR;
    }
}
