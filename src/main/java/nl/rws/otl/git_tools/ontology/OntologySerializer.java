package nl.rws.otl.git_tools.ontology;

import java.io.ByteArrayOutputStream;
import lombok.NoArgsConstructor;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

@NoArgsConstructor
public class OntologySerializer {

    public byte[] serializeOntology(final OWLOntologyManager manager,
            final OWLOntology ontology) throws OWLOntologyStorageException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        manager.saveOntology(ontology, new TurtleDocumentFormat(), output);
        return output.toByteArray();
    }

}
