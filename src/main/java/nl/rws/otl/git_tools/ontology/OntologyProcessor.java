package nl.rws.otl.git_tools.ontology;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rws.otl.git_tools.handler.OutputHandler;
import nl.rws.otl.git_tools.hash.HashCalculator;
import nl.rws.otl.git_tools.wrap.SystemWrapper;
import org.apache.commons.cli.CommandLine;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

@Slf4j
@RequiredArgsConstructor
public class OntologyProcessor {

    private static final int STATUS_CHANGED = 1;

    private static final int STATUS_ERROR = 2;

    private final OntologyLoader ontologyLoader;
    private final OntologySerializer serializer;
    private final OutputHandler outputHandler;
    private final HashCalculator hashCalculator;
    private final OWLOntologyManager owlOntologyManager;
    private final SystemWrapper systemWrapper;

    public void processOntology(final CommandLine cmdArgs,
            final OWLOntologyDocumentSourceBase documentSource,
            final String inputHash,
            final Path filePath) {

        try {
            final OWLOntology ontology = ontologyLoader.load(owlOntologyManager, documentSource);
            final byte[] outputBytes = serializer.serializeOntology(owlOntologyManager, ontology);
            String outputHash = hashCalculator.getHash(new ByteArrayInputStream(outputBytes));
            if (!inputHash.equals(outputHash)) {
                outputHandler.handleOutput(cmdArgs, outputBytes, filePath);
                systemWrapper.exit(STATUS_CHANGED);
            }
        } catch (Exception e) {
            log.error("Error processing ontology: {}", e.getMessage());
            systemWrapper.exit(STATUS_ERROR);
        }
    }


}
