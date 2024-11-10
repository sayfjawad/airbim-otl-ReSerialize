package nl.rws.otl.git_tools.ontology;

import static nl.rws.otl.git_tools.ontology.OntologyLoader.loadOntology;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rws.otl.git_tools.handler.OutputHandler;
import nl.rws.otl.git_tools.hash.HashSha256;
import org.apache.commons.cli.CommandLine;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

@Slf4j
@RequiredArgsConstructor
public class OntologyProcessor {

    private static final int STATUS_CHANGED = 1;

    private static final int STATUS_ERROR = 2;

    private final OntologySerializer serializer;
    private final OutputHandler outputHandler;
    private final HashSha256 hashSha256;

    public void processOntology(final CommandLine cmdArgs,
            final OWLOntologyDocumentSourceBase documentSource,
            final String inputHash,
            final Path filePath) {

        try {
            final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            final OWLOntology ontology = loadOntology(manager, documentSource);
            final byte[] outputBytes = serializer.serializeOntology(manager, ontology);
            String outputHash = hashSha256.getHash(new ByteArrayInputStream(outputBytes));

            if (!inputHash.equals(outputHash)) {
                outputHandler.handleOutput(cmdArgs, outputBytes, filePath);
                System.exit(STATUS_CHANGED);
            }
        } catch (Exception e) {
            log.error("Error processing ontology: {}", e.getMessage());
            System.exit(STATUS_ERROR);
        }
    }
}
