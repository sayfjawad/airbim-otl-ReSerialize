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
    public static void main(String[] args) {
        if(args.length != 1){
            log.error("This application requires exactly 1 parameter, namely the file to reserialize");
            return;
        }
        Path filePath = Paths.get(args[0]);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        FileDocumentSource fileDocumentSource = new FileDocumentSource(filePath.toFile());
        CustomOntologyLoaderConfiguration customOntologyLoaderConfiguration = new CustomOntologyLoaderConfiguration();

        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(fileDocumentSource, customOntologyLoaderConfiguration);
            manager.saveOntology(ontology, new TurtleDocumentFormat(), IRI.create(filePath.toUri()));
        } catch (OWLOntologyStorageException e) {
            log.error("Failed to save: {}", e.getMessage());
        } catch (OWLOntologyCreationException e) {
            log.error("Failed to load: {}",e.getMessage());
        }
    }

    public static class CustomOntologyLoaderConfiguration extends OWLOntologyLoaderConfiguration {
        @Override
        public boolean isIgnoredImport(@SuppressWarnings("NullableProblems") IRI iri) {
            // Always ignore imports
            return true;
        }
    }
}
