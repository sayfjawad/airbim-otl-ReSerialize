package nl.rws.otl.git_tools;

import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ReSerialize {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            throw new RuntimeException("This application requires exactly 1 parameter, namely the file to reserialize");
        }
        Path filePath = Paths.get(args[0]);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        FileDocumentSource fileDocumentSource = new FileDocumentSource(filePath.toFile());
        CustomOntologyLoaderConfiguration customOntologyLoaderConfiguration = new CustomOntologyLoaderConfiguration();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(fileDocumentSource, customOntologyLoaderConfiguration);

        //Files.move(filePath, filePath.resolveSibling(filePath.getFileName()+".old"));
        manager.saveOntology(ontology, new TurtleDocumentFormat(), IRI.create(filePath.toUri()+".new.ttl"));
    }

    public static class CustomOntologyLoaderConfiguration extends OWLOntologyLoaderConfiguration {
        @Override
        public boolean isIgnoredImport(IRI iri) {
            // Always ignore imports
            return true;
        }
    }
}
