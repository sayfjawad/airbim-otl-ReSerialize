package nl.rws.otl.git_tools.ontology;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class OntologySerializer {

    public static byte[] serializeOntology(OWLOntologyManager manager, OWLOntology ontology) throws OWLOntologyStorageException, IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        manager.saveOntology(ontology, new TurtleDocumentFormat(), output);
        return output.toByteArray();
    }

}
