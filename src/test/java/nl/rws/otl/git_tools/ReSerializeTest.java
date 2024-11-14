package nl.rws.otl.git_tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import nl.rws.otl.git_tools.cli.CliArgumentParser;
import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import nl.rws.otl.git_tools.hash.HashCalculator;
import nl.rws.otl.git_tools.ontology.OntologyDocumentCreator;
import nl.rws.otl.git_tools.ontology.OntologyProcessor;
import nl.rws.otl.git_tools.wrap.SystemWrapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;

@ExtendWith(MockitoExtension.class)
class ReSerializeTest {

    @Mock
    private CliArgumentParser cliArgumentParser;

    @Mock
    private OntologyProcessor ontologyProcessor;

    @Mock
    private CliConfig cliConfig;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private HashCalculator hashCalculator;

    @Mock
    private OntologyDocumentCreator ontologyDocumentCreator;

    @Mock
    private SystemWrapper systemWrapper;

    @Mock
    private CommandLine cmdArgs;

    @Mock
    private OWLOntologyDocumentSourceBase documentSource;

    private ReSerialize reSerialize;


    private static final URL testTtl = ReSerializeTest.class.getClassLoader()
            .getResource("basicsemantics-owl.ttl");

/*    private static final URL testTtlFixed = ReSerializeTest.class.getClassLoader()
            .getResource("basicsemantics-owl_fixed.ttl");
    */
    private static final Path VALID_PATH = Path.of(testTtl.getPath());
    private static final String VALID_HASH = "validHash";
    private static final int STATUS_ERROR = 2;

    @BeforeEach
    void setUp() {
        reSerialize = new ReSerialize(cliArgumentParser, ontologyProcessor, cliConfig, fileUtils, hashCalculator, ontologyDocumentCreator, systemWrapper);
    }

    @Test
    @DisplayName("Given valid arguments When reserialize is called Then ontology is processed successfully")
    void testReserializeWithValidArguments() throws Exception {
        // Arrange
        Options options = new Options();
        when(cliConfig.createOptions()).thenReturn(options);
        when(cliArgumentParser.parseArguments(options, new String[]{"arg1", "arg2"})).thenReturn(cmdArgs);
        when(fileUtils.getFilePath(cmdArgs)).thenReturn(VALID_PATH);
        when(ontologyDocumentCreator.createDocumentSource(VALID_PATH)).thenReturn(Optional.of(documentSource));
        when(hashCalculator.computeFileHash(documentSource)).thenReturn(VALID_HASH);

        // Act
        int result = reSerialize.reserialize(new String[]{"arg1", "arg2"});

        // Assert
        assertThat(result).isEqualTo(0);  // Success status
        verify(ontologyProcessor).processOntology(cmdArgs, documentSource, VALID_HASH, VALID_PATH);
        verify(systemWrapper, never()).exit(anyInt());
    }

    @Test
    @DisplayName("Given invalid CLI arguments When reserialize is called Then exit with STATUS_ERROR")
    void testReserializeWithInvalidCLIArguments() {
        // Arrange
        Options options = new Options();
        when(cliConfig.createOptions()).thenReturn(options);
        when(cliArgumentParser.parseArguments(options, new String[]{"invalid"})).thenReturn(null);

        // Act
        int result = reSerialize.reserialize(new String[]{"invalid"});

        // Assert
        assertThat(result).isEqualTo(STATUS_ERROR);
        verify(systemWrapper).exit(STATUS_ERROR);
    }

    @Test
    @DisplayName("Given missing file path When reserialize is called Then exit with STATUS_ERROR")
    void testReserializeWithMissingFilePath() {
        // Arrange
        Options options = new Options();
        when(cliConfig.createOptions()).thenReturn(options);
        when(cliArgumentParser.parseArguments(options, new String[]{"arg1"})).thenReturn(cmdArgs);
        when(fileUtils.getFilePath(cmdArgs)).thenReturn(null);

        // Act
        int result = reSerialize.reserialize(new String[]{"arg1"});

        // Assert
        assertThat(result).isEqualTo(STATUS_ERROR);
        verify(systemWrapper).exit(STATUS_ERROR);
    }

    @Test
    @DisplayName("Given failed document source creation When reserialize is called Then exit with STATUS_ERROR")
    void testReserializeWithFailedDocumentSourceCreation() {
        // Arrange
        Options options = new Options();
        when(cliConfig.createOptions()).thenReturn(options);
        when(cliArgumentParser.parseArguments(options, new String[]{"arg1"})).thenReturn(cmdArgs);
        when(fileUtils.getFilePath(cmdArgs)).thenReturn(VALID_PATH);
        when(ontologyDocumentCreator.createDocumentSource(VALID_PATH)).thenReturn(Optional.empty());

        // Act
        int result = reSerialize.reserialize(new String[]{"arg1"});

        // Assert
        assertThat(result).isEqualTo(STATUS_ERROR);
        verify(systemWrapper).exit(STATUS_ERROR);
    }

    @Test
    @DisplayName("Given failed hash computation When reserialize is called Then exit with STATUS_ERROR")
    void testReserializeWithFailedHashComputation() {
        // Arrange
        Options options = new Options();
        when(cliConfig.createOptions()).thenReturn(options);
        when(cliArgumentParser.parseArguments(options, new String[]{"arg1"})).thenReturn(cmdArgs);
        when(fileUtils.getFilePath(cmdArgs)).thenReturn(VALID_PATH);
        when(ontologyDocumentCreator.createDocumentSource(VALID_PATH)).thenReturn(Optional.of(documentSource));
        when(hashCalculator.computeFileHash(documentSource)).thenReturn(null);

        // Act
        int result = reSerialize.reserialize(new String[]{"arg1"});

        // Assert
        assertThat(result).isEqualTo(STATUS_ERROR);
        verify(systemWrapper).exit(STATUS_ERROR);
    }

    @Test
    @DisplayName("Given exception during ontology processing When reserialize is called Then return STATUS_ERROR")
    void testReserializeWithProcessingException() throws Exception {
        // Arrange
        Options options = new Options();
        when(cliConfig.createOptions()).thenReturn(options);
        when(cliArgumentParser.parseArguments(options, new String[]{"arg1"})).thenReturn(cmdArgs);
        when(fileUtils.getFilePath(cmdArgs)).thenReturn(VALID_PATH);
        when(ontologyDocumentCreator.createDocumentSource(VALID_PATH)).thenReturn(Optional.of(documentSource));
        when(hashCalculator.computeFileHash(documentSource)).thenReturn(VALID_HASH);
        doThrow(new RuntimeException("Processing error")).when(ontologyProcessor).processOntology(cmdArgs, documentSource, VALID_HASH, VALID_PATH);

        // Act
        int result = reSerialize.reserialize(new String[]{"arg1"});

        // Assert
        assertThat(result).isEqualTo(STATUS_ERROR);
        verify(systemWrapper, never()).exit(anyInt());  // Process should return error code without directly exiting
    }
}
