package nl.rws.otl.git_tools.ontology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;

import static org.assertj.core.api.Assertions.assertThat;

class CustomOntologyLoaderConfigurationTest {

    private CustomOntologyLoaderConfiguration config;

    @BeforeEach
    void setUp() {
        config = new CustomOntologyLoaderConfiguration();
    }

    @Test
    @DisplayName("Given any IRI When isIgnoredImport is called Then it should always return true")
    void testIsIgnoredImportAlwaysReturnsTrue() {
        IRI testIri1 = IRI.create("http://example.com/ontology");
        IRI testIri2 = IRI.create("http://another-example.com/resource");

        assertThat(config.isIgnoredImport(testIri1)).isTrue();
        assertThat(config.isIgnoredImport(testIri2)).isTrue();
    }
}
