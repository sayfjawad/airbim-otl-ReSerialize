package nl.rws.otl.git_tools.ontology;

import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyLoader {

    public OWLOntology load(OWLOntologyManager manager, OWLOntologyDocumentSourceBase documentSource) throws OWLOntologyCreationException {
        CustomOntologyLoaderConfiguration config = new CustomOntologyLoaderConfiguration();
        return manager.loadOntologyFromOntologyDocument(documentSource, config);
    }

}
