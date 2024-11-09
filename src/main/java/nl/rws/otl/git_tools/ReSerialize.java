package nl.rws.otl.git_tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class ReSerialize {
    private static final String OPTION_INPUT = "f";
    private static final String OPTION_OUTPUT = "o";
    private static final String OPTION_REPLACE = "r";
    private static final int STATUS_CHANGED = 1;
    private static final int STATUS_ERROR = 2;

    public static void main(String[] args) {
        Options options = createOptions();
        CommandLine cmdArgs = parseArguments(options, args);

        if (cmdArgs == null) {
            printHelpAndExit(options);
        }

        Path filePath = getFilePath(cmdArgs);
        OWLOntologyDocumentSourceBase documentSource = createDocumentSource(cmdArgs, filePath);
        if (documentSource == null) {
            System.exit(STATUS_ERROR);
        }

        String inputHash = computeFileHash(documentSource);
        if (inputHash == null) {
            System.exit(STATUS_ERROR);
        }

        processOntology(cmdArgs, documentSource, inputHash, filePath);
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addRequiredOption(OPTION_INPUT, "file", true, "File to validate (or - for stdin)");
        options.addOption(OPTION_REPLACE, "replace", false, "Replace the file with the re-serialized version");
        options.addOption(OPTION_OUTPUT, "output", true, "File to write with the re-serialized version");
        return options;
    }

    private static CommandLine parseArguments(Options options, String[] args) {
        try {
            return new DefaultParser().parse(options, args, true);
        } catch (ParseException e) {
            log.error("Error parsing arguments: {}", e.getMessage());
            return null;
        }
    }

    private static void printHelpAndExit(Options options) {
        new HelpFormatter().printHelp("ReSerialize", options, true);
        System.exit(STATUS_ERROR);
    }

    private static Path getFilePath(CommandLine cmdArgs) {
        return cmdArgs.getOptionValue(OPTION_INPUT).equals("-") ? null : Paths.get(cmdArgs.getOptionValue(OPTION_INPUT));
    }

    private static OWLOntologyDocumentSourceBase createDocumentSource(CommandLine cmdArgs, Path filePath) {
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

    private static String computeFileHash(OWLOntologyDocumentSourceBase documentSource) {
        try (InputStream inputStream = getInputStreamFromSource(documentSource)) {
            return getHash(inputStream);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Failed to compute file hash: {}", e.getMessage());
            return null;
        }
    }

    private static InputStream getInputStreamFromSource(OWLOntologyDocumentSourceBase documentSource) throws IOException {
        if (documentSource instanceof StreamDocumentSource) {
            return (documentSource).getInputStream().get();
        } else if (documentSource instanceof FileDocumentSource) {
            // Cast to FileDocumentSource and use the file path directly
            return new FileInputStream(new File((documentSource).getDocumentIRI().toURI()));
        }
        return null;
    }


    private static void processOntology(CommandLine cmdArgs, OWLOntologyDocumentSourceBase documentSource,
            String inputHash, Path filePath) {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = loadOntology(manager, documentSource);
            byte[] outputBytes = serializeOntology(manager, ontology);
            String outputHash = getHash(new ByteArrayInputStream(outputBytes));

            if (!inputHash.equals(outputHash)) {
                handleOutput(cmdArgs, outputBytes, filePath);
                System.exit(STATUS_CHANGED);
            }
        } catch (Exception e) {
            log.error("Error processing ontology: {}", e.getMessage());
            System.exit(STATUS_ERROR);
        }
    }

    private static OWLOntology loadOntology(OWLOntologyManager manager, OWLOntologyDocumentSourceBase documentSource) throws OWLOntologyCreationException {
        CustomOntologyLoaderConfiguration config = new CustomOntologyLoaderConfiguration();
        return manager.loadOntologyFromOntologyDocument(documentSource, config);
    }

    private static byte[] serializeOntology(OWLOntologyManager manager, OWLOntology ontology) throws OWLOntologyStorageException, IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        manager.saveOntology(ontology, new TurtleDocumentFormat(), output);
        return output.toByteArray();
    }

    private static void handleOutput(CommandLine cmdArgs, byte[] outputBytes, Path filePath) throws IOException {
        if (cmdArgs.hasOption(OPTION_REPLACE) && filePath != null) {
            writeFile(outputBytes, filePath.toFile());
        } else if (cmdArgs.hasOption(OPTION_OUTPUT)) {
            writeFile(outputBytes, new File(cmdArgs.getOptionValue(OPTION_OUTPUT)));
        }
    }

    public static class CustomOntologyLoaderConfiguration extends OWLOntologyLoaderConfiguration {
        @Override
        public boolean isIgnoredImport(IRI iri) {
            return true;  // Always ignore imports
        }
    }

    public static String getHash(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            while (dis.read() != -1) { /* Read through the stream */ }
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static void writeFile(byte[] outputBytes, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(outputBytes);
        }
    }
}
