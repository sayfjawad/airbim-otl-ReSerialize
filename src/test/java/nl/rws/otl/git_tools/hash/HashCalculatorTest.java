package nl.rws.otl.git_tools.hash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import nl.rws.otl.git_tools.file.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;

class HashCalculatorTest {

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private HashCalculator hashCalculator;

    private static final String TEST_DATA = "test data";

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given a valid input stream When getHash is called Then correct SHA-256 hash is returned")
    void testGetHashWithValidInput() throws NoSuchAlgorithmException, IOException {

        InputStream inputStream = new ByteArrayInputStream(TEST_DATA.getBytes());
        String expectedHash = "916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9";

        String hash = hashCalculator.getHash(inputStream);

        assertThat(hash).isEqualTo(expectedHash);
    }

    @Test
    @DisplayName("Given a valid OWLOntologyDocumentSourceBase When computeFileHash is called Then correct hash is returned")
    void testComputeFileHashWithValidSource() throws IOException, NoSuchAlgorithmException {

        OWLOntologyDocumentSourceBase documentSource = mock(OWLOntologyDocumentSourceBase.class);
        InputStream inputStream = new ByteArrayInputStream(TEST_DATA.getBytes());
        String expectedHash = "916f0027a575074ce72a331777c3478d6513f786a591bd892da1a577bf2335f9";

        when(fileUtils.getInputStreamFromSource(documentSource)).thenReturn(inputStream);

        String hash = hashCalculator.computeFileHash(documentSource);

        assertThat(hash).isEqualTo(expectedHash);
        verify(fileUtils).getInputStreamFromSource(documentSource);
    }

    @Test
    @DisplayName("Given an IOException from FileUtils When computeFileHash is called Then null is returned and error is logged")
    void testComputeFileHashHandlesIOException() throws IOException {

        OWLOntologyDocumentSourceBase documentSource = mock(OWLOntologyDocumentSourceBase.class);

        when(fileUtils.getInputStreamFromSource(documentSource)).thenThrow(IOException.class);

        String hash = hashCalculator.computeFileHash(documentSource);

        assertThat(hash).isNull();
        verify(fileUtils).getInputStreamFromSource(documentSource);
    }
}
