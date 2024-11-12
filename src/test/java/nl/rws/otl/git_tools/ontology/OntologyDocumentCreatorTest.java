package nl.rws.otl.git_tools.ontology;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OntologyDocumentCreatorTest {

    private OntologyDocumentCreator creator;
    private final InputStream originalSystemIn = System.in;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        creator = new OntologyDocumentCreator();
        tempFile = Files.createTempFile("test", ".ttl");  // Create a real temporary file for testing
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setIn(originalSystemIn); // Restore System.in to its original state after each test
        Files.deleteIfExists(tempFile); // Clean up temporary file after each test
    }

    @Test
    @DisplayName("Given a valid file path When createDocumentSource is called Then a FileDocumentSource is returned")
    void testCreateDocumentSourceWithValidFilePath() throws IOException {
        Optional<OWLOntologyDocumentSourceBase> result = creator.createDocumentSource(tempFile);

        assertThat(result).isPresent().get().isInstanceOf(FileDocumentSource.class);
    }

    @Test
    @DisplayName("Given a non-existent file path When createDocumentSource is called Then an empty Optional is returned")
    void testCreateDocumentSourceWithNonExistentFilePath() throws IOException {
        Path nonExistentPath = tempFile.resolveSibling("nonexistent.ttl");

        Optional<OWLOntologyDocumentSourceBase> result = creator.createDocumentSource(nonExistentPath);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given System.in with data When createDocumentSource is called with null Then a StreamDocumentSource is returned")
    void testCreateDocumentSourceWithSystemInData() throws IOException {
        byte[] inputData = "ontology data".getBytes();
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputData);
        System.setIn(inStream); // Set System.in to the test data stream

        Optional<OWLOntologyDocumentSourceBase> result = creator.createDocumentSource(null);

        assertThat(result).isPresent().get().isInstanceOf(StreamDocumentSource.class);
    }

    @Test
    @DisplayName("Given empty System.in When createDocumentSource is called with null Then an empty Optional is returned")
    void testCreateDocumentSourceWithEmptySystemIn() throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(new byte[0]);
        System.setIn(inStream); // Set System.in to an empty stream

        Optional<OWLOntologyDocumentSourceBase> result = creator.createDocumentSource(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given an IOException when accessing file When createDocumentSource is called Then an empty Optional is returned")
    void testCreateDocumentSourceHandlesIOException() {
        // Delete the tempFile and then try to access it to simulate an IOException
        try {
            Files.deleteIfExists(tempFile);

            Optional<OWLOntologyDocumentSourceBase> result = creator.createDocumentSource(tempFile);

            assertThat(result).isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
