package nl.rws.otl.git_tools.file;

import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_INPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

class FileUtilsTest {

    @Mock
    private CommandLine cmdArgs;

    private FileUtils fileUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileUtils = new FileUtils();
    }

    @Test
    @DisplayName("Given '-' as input file path When getFilePath is called Then it returns null")
    void testGetFilePathWithDashInput() {
        when(cmdArgs.getOptionValue(OPTION_INPUT)).thenReturn("-");

        Path result = fileUtils.getFilePath(cmdArgs);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Given a valid file path When getFilePath is called Then it returns the correct Path")
    void testGetFilePathWithValidInput() {
        String inputPath = "/path/to/file.owl";
        when(cmdArgs.getOptionValue(OPTION_INPUT)).thenReturn(inputPath);

        Path result = fileUtils.getFilePath(cmdArgs);

        assertThat(result).isEqualTo(Paths.get(inputPath));
    }

    @Test
    @DisplayName("Given a StreamDocumentSource When getInputStreamFromSource is called Then it returns the correct InputStream")
    void testGetInputStreamFromStreamDocumentSource() throws IOException {
        InputStream mockInputStream = new ByteArrayInputStream("mock data".getBytes());
        OWLOntologyDocumentSourceBase documentSource = new StreamDocumentSource(mockInputStream);

        InputStream result = fileUtils.getInputStreamFromSource(documentSource);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(GZIPInputStream.class);
    }

    @Test
    @DisplayName("Given a FileDocumentSource When getInputStreamFromSource is called Then it returns the correct InputStream")
    void testGetInputStreamFromFileDocumentSource() throws IOException {
        File tempFile = File.createTempFile("test", ".owl");
        tempFile.deleteOnExit();
        FileInputStream fileInputStream = new FileInputStream(tempFile);
        OWLOntologyDocumentSourceBase documentSource = new FileDocumentSource(tempFile);

        try (InputStream result = fileUtils.getInputStreamFromSource(documentSource)) {
            assertThat(result).isInstanceOf(FileInputStream.class);
        }
    }

    @Test
    @DisplayName("Given an invalid document source When getInputStreamFromSource is called Then it returns null")
    void testGetInputStreamFromInvalidSource() throws IOException {
        OWLOntologyDocumentSourceBase documentSource = mock(OWLOntologyDocumentSourceBase.class);

        InputStream result = fileUtils.getInputStreamFromSource(documentSource);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Given a file and data When writeFile is called Then it writes data to the file")
    void testWriteFile() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();
        byte[] data = "test data".getBytes();

        fileUtils.writeFile(data, tempFile);

        assertThat(tempFile.length()).isEqualTo(data.length);
    }

    @Test
    @DisplayName("Given an invalid file path When writeFile is called Then it throws an IOException")
    void testWriteFileWithInvalidPath() {
        byte[] data = "test data".getBytes();
        File invalidFile = new File("/invalid/path/to/file.txt");

        assertThatThrownBy(() -> fileUtils.writeFile(data, invalidFile))
                .isInstanceOf(IOException.class);
    }
}
