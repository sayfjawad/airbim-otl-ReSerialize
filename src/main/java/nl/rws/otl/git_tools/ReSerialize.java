package nl.rws.otl.git_tools;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rws.otl.git_tools.cli.CliArgumentParser;
import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import nl.rws.otl.git_tools.handler.OutputHandler;
import nl.rws.otl.git_tools.hash.HashSha256;
import nl.rws.otl.git_tools.ontology.OntologyProcessor;
import nl.rws.otl.git_tools.ontology.OntologySerializer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

@Slf4j
@RequiredArgsConstructor
public class ReSerialize {


    private final CliArgumentParser cliArgumentParser;

    private final OntologyProcessor ontologyProcessor;

    private final CliConfig cliConfig;

    private final FileUtils fileUtils;
    private final HashSha256 hashSha256;

    private static final int STATUS_ERROR = 2;

    public static void main(String[] args) {

        final FileUtils fileUtils1 = new FileUtils();
        final CliArgumentParser cliArgumentParser1 = new CliArgumentParser();
        final OntologySerializer serializer1 = new OntologySerializer();
        final HashSha256 hashSha256 = new HashSha256(fileUtils1);
        final OntologyProcessor ontologyProcessor1 = new OntologyProcessor(serializer1, new OutputHandler(fileUtils1), hashSha256);
        final CliConfig cliConfig1 = new CliConfig();

        final var serializer = new ReSerialize(cliArgumentParser1,
                ontologyProcessor1,
                cliConfig1,
                fileUtils1,
                hashSha256);

        serializer.reserialize(args);
    }

    public void reserialize(String[] args) {

        Options options = cliConfig.createOptions();
        CommandLine cmdArgs = cliArgumentParser.parseArguments(options, args);

        if (cmdArgs == null) {
            printHelpAndExit(options);
        }

        Path filePath = fileUtils.getFilePath(cmdArgs);
        OWLOntologyDocumentSourceBase documentSource = createDocumentSource(filePath);
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


    private static OWLOntologyDocumentSourceBase createDocumentSource(final Path filePath) {

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
