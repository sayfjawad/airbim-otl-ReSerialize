package nl.rws.otl.git_tools.ontology;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

@ExtendWith(MockitoExtension.class)
class OntologySerializerTest {

    @Mock
    private OWLOntologyManager mockManager;

    @Mock
    private OWLOntology mockOntology;

    @InjectMocks
    private OntologySerializer ontologySerializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given a valid OWLOntology and OWLOntologyManager When serializeOntology is called Then it returns the serialized byte array")
    void testSerializeOntologyWithValidInput() throws OWLOntologyStorageException {
        // Arrange: Mock the behavior to write dummy data to the output stream
        doAnswer(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(2);
            outputStream.write("mock serialized data".getBytes());
            return null;
        }).when(mockManager).saveOntology(any(OWLOntology.class), any(TurtleDocumentFormat.class), any(ByteArrayOutputStream.class));

        // Act: Call the method under test
        byte[] result = ontologySerializer.serializeOntology(mockManager, mockOntology);

        // Assert: Verify the output is not empty and contains the expected data
        assertThat(result).isNotEmpty();
        assertThat(new String(result)).isEqualTo("mock serialized data");
        verify(mockManager).saveOntology(eq(mockOntology), any(TurtleDocumentFormat.class), any(OutputStream.class));
    }

    @Test
    @DisplayName("Given a storage exception When serializeOntology is called Then it throws OWLOntologyStorageException")
    void testSerializeOntologyThrowsStorageException() throws OWLOntologyStorageException {
        OWLOntologyStorageException exception = new OWLOntologyStorageException("Simulated storage failure");
        doThrow(exception).when(mockManager).saveOntology(eq(mockOntology), any(TurtleDocumentFormat.class), any(OutputStream.class));

        assertThatThrownBy(() -> ontologySerializer.serializeOntology(mockManager, mockOntology))
                .isInstanceOf(OWLOntologyStorageException.class)
                .hasMessageContaining("Simulated storage failure");
    }


}
