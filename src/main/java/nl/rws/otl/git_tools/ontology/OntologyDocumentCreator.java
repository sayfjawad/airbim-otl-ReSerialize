package nl.rws.otl.git_tools.ontology;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

@Slf4j
public class OntologyDocumentCreator {

    /**
     * Creates an ontology document source based on the input path or System.in.
     *
     * @param filePath the path of the file to create a document source from; if null, uses System.in.
     * @return an Optional containing the created OWLOntologyDocumentSourceBase if successful, or an empty Optional otherwise.
     */
    public Optional<OWLOntologyDocumentSourceBase> createDocumentSource(final Path filePath) {
        try {
            if (filePath == null) {
                return createStreamDocumentSourceFromSystemIn();
            }
            return createFileDocumentSource(filePath);
        } catch (IOException e) {
            log.error("Failed to load document source: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Optional<OWLOntologyDocumentSourceBase> createFileDocumentSource(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            log.error("File does not exist at path: {}", filePath);
            return Optional.empty();
        }
        return Optional.of(new FileDocumentSource(filePath.toFile()));
    }

    private Optional<OWLOntologyDocumentSourceBase> createStreamDocumentSourceFromSystemIn() throws IOException {
        byte[] inputData = readSystemIn();
        if (inputData.length == 0) {
            log.warn("No input data provided from System.in");
            return Optional.empty();
        }
        return Optional.of(new StreamDocumentSource(new ByteArrayInputStream(inputData)));
    }

    /**
     * Reads all bytes from System.in.
     *
     * @return a byte array containing all input from System.in
     * @throws IOException if an I/O error occurs while reading
     */
    private byte[] readSystemIn() throws IOException {
        return System.in.readAllBytes();
    }
}
