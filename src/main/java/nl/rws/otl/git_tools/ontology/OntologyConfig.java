package nl.rws.otl.git_tools.ontology;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;

public class OntologyConfig {

    public static class CustomOntologyLoaderConfiguration extends OWLOntologyLoaderConfiguration {

        @Override
        public boolean isIgnoredImport(IRI iri) {

            return true;  // Always ignore imports
        }
    }
}
