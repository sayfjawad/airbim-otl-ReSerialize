package nl.rws.otl.git_tools.ontology;

import static junit.runner.TestRunListener.STATUS_ERROR;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import nl.rws.otl.git_tools.handler.OutputHandler;
import nl.rws.otl.git_tools.hash.HashCalculator;
import nl.rws.otl.git_tools.wrap.SystemWrapper;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

@ExtendWith(MockitoExtension.class)
class OntologyProcessorTest {

    public static final int STATUS_FAILURE = 1;
    public static final int STATUS_ERROR = 2;
    @Mock
    private OntologyLoader ontologyLoader;

    @Mock
    private OntologySerializer serializer;

    @Mock
    private OutputHandler outputHandler;

    @Mock
    private HashCalculator hashCalculator;

    @Mock
    private OWLOntologyManager owlOntologyManager;

    @Mock
    private SystemWrapper systemWrapper;

    @Mock
    private CommandLine cmdArgs;

    @Mock
    private OWLOntologyDocumentSourceBase documentSource;

    @Mock
    private OWLOntology ontology;

    private OntologyProcessor ontologyProcessor;

    private static final String INPUT_HASH = "inputHash";
    private static final String DIFFERENT_OUTPUT_HASH = "differentHash";
    private static final String SAME_OUTPUT_HASH = "inputHash";

    @BeforeEach
    void setUp() {

        ontologyProcessor = new OntologyProcessor(ontologyLoader, serializer, outputHandler, hashCalculator, owlOntologyManager, systemWrapper);
    }

    @Test
    @DisplayName("Given differing input and output hashes When processOntology is called Then outputHandler is called and System exits with STATUS_CHANGED")
    void testProcessOntologyWithDifferentHashes() throws Exception {

        byte[] mockSerializedData = "mock serialized data".getBytes();
        when(ontologyLoader.load(owlOntologyManager, documentSource)).thenReturn(ontology);
        when(serializer.serializeOntology(owlOntologyManager, ontology)).thenReturn(mockSerializedData);
        when(hashCalculator.getHash(any(ByteArrayInputStream.class))).thenReturn(DIFFERENT_OUTPUT_HASH);

        final Path filePath = Path.of("dummy/path");
        ontologyProcessor.processOntology(cmdArgs, documentSource, INPUT_HASH, filePath);
        verify(outputHandler).handleOutput(any(CommandLine.class), eq(mockSerializedData), eq(filePath));
        verify(systemWrapper).exit(STATUS_FAILURE);
    }

    @Test
    @DisplayName("Given matching input and output hashes When processOntology is called Then outputHandler is not called and System exits without STATUS_CHANGED")
    void testProcessOntologyWithSameHashes() throws Exception {

        byte[] mockSerializedData = "mock serialized data".getBytes();
        when(ontologyLoader.load(owlOntologyManager, documentSource)).thenReturn(ontology);
        when(serializer.serializeOntology(owlOntologyManager, ontology)).thenReturn(mockSerializedData);
        when(hashCalculator.getHash(any(ByteArrayInputStream.class))).thenReturn(SAME_OUTPUT_HASH);

        ontologyProcessor.processOntology(cmdArgs, documentSource, INPUT_HASH, Path.of("dummy/path"));

        verify(outputHandler, never()).handleOutput(any(CommandLine.class), any(byte[].class), any(Path.class));
        verify(systemWrapper,never()).exit(anyInt());
    }

    @Test
    @DisplayName("Given an exception during processing When processOntology is called Then System exits with STATUS_ERROR and outputHandler is not called")
    void testProcessOntologyWithException() throws Exception {

        // Act
        ontologyProcessor.processOntology(cmdArgs, documentSource, INPUT_HASH, Path.of("dummy/path"));

        // Assert: Verify SystemWrapper exits with STATUS_ERROR and outputHandler is not called
        verify(systemWrapper).exit(STATUS_ERROR);
        verify(outputHandler, never()).handleOutput(any(CommandLine.class), any(byte[].class), any(Path.class));
    }
}
