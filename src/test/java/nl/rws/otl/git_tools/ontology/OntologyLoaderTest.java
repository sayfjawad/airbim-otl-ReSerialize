package nl.rws.otl.git_tools.ontology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OntologyLoaderTest {

    @Mock
    private OWLOntologyManager manager;

    @Mock
    private OWLOntologyDocumentSourceBase documentSource;

    @Mock
    private OWLOntology ontology;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given valid OWLOntologyManager and document source When loadOntology is called Then ontology is loaded successfully")
    void testLoadOntologySuccessfully() throws OWLOntologyCreationException {
        // Arrange
        when(manager.loadOntologyFromOntologyDocument(eq(documentSource), any(CustomOntologyLoaderConfiguration.class)))
                .thenReturn(ontology);

        // Act
        OWLOntology result = new OntologyLoader().load(manager, documentSource);

        // Assert
        assertThat(result).isEqualTo(ontology);
        verify(manager).loadOntologyFromOntologyDocument(eq(documentSource), any(CustomOntologyLoaderConfiguration.class));
    }

    @Test
    @DisplayName("Given invalid document source When loadOntology is called Then OWLOntologyCreationException is thrown")
    void testLoadOntologyThrowsException() throws OWLOntologyCreationException {
        // Arrange
        when(manager.loadOntologyFromOntologyDocument(eq(documentSource), any(CustomOntologyLoaderConfiguration.class)))
                .thenThrow(new OWLOntologyCreationException("Failed to load ontology"));

        // Act & Assert
        assertThatThrownBy(() -> new OntologyLoader().load(manager, documentSource))
                .isInstanceOf(OWLOntologyCreationException.class)
                .hasMessageContaining("Failed to load ontology");

        verify(manager).loadOntologyFromOntologyDocument(eq(documentSource), any(CustomOntologyLoaderConfiguration.class));
    }
}
