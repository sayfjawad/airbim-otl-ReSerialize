package nl.rws.otl.git_tools.ontology;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

@Slf4j
public class OntologyDocumentCreator {

    public OWLOntologyDocumentSourceBase createDocumentSource(final Path filePath) {

        try {
            if (filePath == null) {
                byte[] input = System.in.readAllBytes();
                return new StreamDocumentSource(new ByteArrayInputStream(input));
            }
            return new FileDocumentSource(filePath.toFile());
        } catch (IOException e) {
            log.error("Failed to load document source: {}", e.getMessage());
            return null;
        }
    }
    
}
