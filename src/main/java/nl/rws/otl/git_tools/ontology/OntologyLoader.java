package nl.rws.otl.git_tools.ontology;

import nl.rws.otl.git_tools.ontology.OntologyConfig.CustomOntologyLoaderConfiguration;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyLoader {

    public static OWLOntology loadOntology(OWLOntologyManager manager, OWLOntologyDocumentSourceBase documentSource) throws OWLOntologyCreationException {
        CustomOntologyLoaderConfiguration config = new CustomOntologyLoaderConfiguration();
        return manager.loadOntologyFromOntologyDocument(documentSource, config);
    }

}
