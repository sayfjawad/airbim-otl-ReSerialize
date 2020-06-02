package nl.rws.otl.git_tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
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
        Options options = new Options();
        options.addRequiredOption(OPTION_INPUT, "file", true, "File to validate");
        options.addOption(OPTION_REPLACE, "replace", false, "Replace the file with the re-serialized version");
        options.addOption(OPTION_OUTPUT, "output", true, "File to write with the re-serialized version");

        CommandLine cmdArgs = null;
        try {
            cmdArgs = new DefaultParser().parse(options, args, true);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("ReSerialize", options, true);
            System.exit(STATUS_ERROR);
        }

        Path filePath = Paths.get(cmdArgs.getOptionValue(OPTION_INPUT));
        FileDocumentSource fileDocumentSource = new FileDocumentSource(filePath.toFile());
        CustomOntologyLoaderConfiguration customOntologyLoaderConfiguration = new CustomOntologyLoaderConfiguration();

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            String inputHash = getHash(new FileInputStream(filePath.toFile()));

            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(fileDocumentSource, customOntologyLoaderConfiguration);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            manager.saveOntology(ontology, new TurtleDocumentFormat(), output);
            byte[] outputBytes = output.toByteArray();
            String outputHash = getHash(new ByteArrayInputStream(outputBytes));

            if (!inputHash.equals(outputHash)){
                log.info("ReSerialized version is different from input.");

                if(cmdArgs.hasOption(OPTION_REPLACE)) {
                    writeFile(outputBytes, filePath.toFile());
                } else if(cmdArgs.hasOption(OPTION_OUTPUT)) {
                    writeFile(outputBytes, new File(cmdArgs.getOptionValue(OPTION_OUTPUT)));
                }

                System.exit(STATUS_CHANGED);
            }
        } catch (IOException | OWLOntologyCreationException e) {
            log.error("Failed to load: {}", e.getMessage());
            System.exit(STATUS_ERROR);
        } catch (OWLOntologyStorageException e) {
            log.error("Failed to save: {}", e.getMessage());
            System.exit(STATUS_ERROR);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to use algorithm: {}", e.getMessage());
            System.exit(STATUS_ERROR);
        }

        System.exit(0);
    }

    public static class CustomOntologyLoaderConfiguration extends OWLOntologyLoaderConfiguration {
        @Override
        public boolean isIgnoredImport(@SuppressWarnings("NullableProblems") IRI iri) {
            // Always ignore imports
            return true;
        }
    }

    public static String getHash(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest md;
        try (DigestInputStream dis = new DigestInputStream(inputStream, MessageDigest.getInstance("SHA-256"))) {
            //noinspection StatementWithEmptyBody
            while (dis.read() != -1) {
                //empty loop to clear the data
            }
            md = dis.getMessageDigest();
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
