package nl.rws.otl.git_tools.ontology;

import static nl.rws.otl.git_tools.handler.OutputHandler.handleOutput;
import static nl.rws.otl.git_tools.hash.HashSha256.getHash;
import static nl.rws.otl.git_tools.ontology.OntologyLoader.loadOntology;
import static nl.rws.otl.git_tools.ontology.OntologySerializer.serializeOntology;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

@Slf4j
public class OntologyProcessor {

    private static final int STATUS_CHANGED = 1;

    private static final int STATUS_ERROR = 2;

    public static void processOntology(CommandLine cmdArgs,
            OWLOntologyDocumentSourceBase documentSource,
            String inputHash, Path filePath) {

        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = loadOntology(manager, documentSource);
            byte[] outputBytes = serializeOntology(manager, ontology);
            String outputHash = getHash(new ByteArrayInputStream(outputBytes));

            if (!inputHash.equals(outputHash)) {
                handleOutput(cmdArgs, outputBytes, filePath);
                System.exit(STATUS_CHANGED);
            }
        } catch (Exception e) {
            log.error("Error processing ontology: {}", e.getMessage());
            System.exit(STATUS_ERROR);
        }
    }
}
