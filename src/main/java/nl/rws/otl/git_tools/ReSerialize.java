package nl.rws.otl.git_tools;

import static nl.rws.otl.git_tools.cli.CliArgumentParser.parseArguments;
import static nl.rws.otl.git_tools.cli.CliConfig.createOptions;
import static nl.rws.otl.git_tools.file.FileUtils.getFilePath;
import static nl.rws.otl.git_tools.hash.HashSha256.computeFileHash;
import static nl.rws.otl.git_tools.ontology.OntologyProcessor.processOntology;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

@Slf4j
public class ReSerialize {


    private static final int STATUS_ERROR = 2;

    public static void main(String[] args) {

        Options options = createOptions();
        CommandLine cmdArgs = parseArguments(options, args);

        if (cmdArgs == null) {
            printHelpAndExit(options);
        }

        Path filePath = getFilePath(cmdArgs);
        OWLOntologyDocumentSourceBase documentSource = createDocumentSource(filePath);
        if (documentSource == null) {
            System.exit(STATUS_ERROR);
        }

        String inputHash = computeFileHash(documentSource);
        if (inputHash == null) {
            System.exit(STATUS_ERROR);
        }

        processOntology(cmdArgs, documentSource, inputHash, filePath);
    }


    private static void printHelpAndExit(Options options) {

        new HelpFormatter().printHelp("ReSerialize", options, true);
        System.exit(STATUS_ERROR);
    }


    private static OWLOntologyDocumentSourceBase createDocumentSource(
            Path filePath) {

        try {
            if (filePath == null) {
                byte[] input = System.in.readAllBytes();
                return new StreamDocumentSource(new ByteArrayInputStream(input));
            }
            return new FileDocumentSource(filePath.toFile());
        } catch (IOException e) {
            log.error("Failed to load document source: {}", e.getMessage());
            return null;
        }
    }
}
