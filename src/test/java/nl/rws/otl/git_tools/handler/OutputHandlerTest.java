package nl.rws.otl.git_tools.handler;

import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OutputHandlerTest {

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private OutputHandler outputHandler;

    @Mock
    private CommandLine cmdArgs;

    private final byte[] outputBytes = "test output".getBytes();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given OPTION_REPLACE and filePath provided, When handleOutput is called, Then writeFile is called with filePath")
    void testHandleOutputWithReplaceOption() throws IOException {
        Path filePath = mock(Path.class);
        File outputFile = mock(File.class);

        when(cmdArgs.hasOption(CliConfig.OPTION_REPLACE)).thenReturn(true);
        when(filePath.toFile()).thenReturn(outputFile);

        outputHandler.handleOutput(cmdArgs, outputBytes, filePath);

        verify(fileUtils).writeFile(outputBytes, outputFile);
    }

    @Test
    @DisplayName("Given OPTION_OUTPUT provided, When handleOutput is called, Then writeFile is called with output file from OPTION_OUTPUT")
    void testHandleOutputWithOutputOption() throws IOException {
        File outputFile = mock(File.class);

        when(cmdArgs.hasOption(CliConfig.OPTION_REPLACE)).thenReturn(false);
        when(cmdArgs.hasOption(CliConfig.OPTION_OUTPUT)).thenReturn(true);
        when(cmdArgs.getOptionValue(CliConfig.OPTION_OUTPUT)).thenReturn("outputFile.txt");

        outputHandler.handleOutput(cmdArgs, outputBytes, null);

        verify(fileUtils).writeFile(eq(outputBytes), argThat(file -> file.getName().equals("outputFile.txt")));
    }

    @Test
    @DisplayName("Given neither OPTION_REPLACE nor OPTION_OUTPUT, When handleOutput is called, Then writeFile is not called")
    void testHandleOutputWithoutReplaceOrOutputOption() throws IOException {
        when(cmdArgs.hasOption(CliConfig.OPTION_REPLACE)).thenReturn(false);
        when(cmdArgs.hasOption(CliConfig.OPTION_OUTPUT)).thenReturn(false);

        outputHandler.handleOutput(cmdArgs, outputBytes, null);

        verify(fileUtils, never()).writeFile(any(byte[].class), any(File.class));
        verify(fileUtils, never()).writeFile(any(byte[].class), any(File.class));
    }

    @Test
    @DisplayName("Given IOException thrown by fileUtils, When handleOutput is called, Then exception is propagated")
    void testHandleOutputThrowsIOException() throws IOException {
        Path filePath = mock(Path.class);
        File outputFile = mock(File.class);

        when(cmdArgs.hasOption(CliConfig.OPTION_REPLACE)).thenReturn(true);
        when(filePath.toFile()).thenReturn(outputFile);
        doThrow(IOException.class).when(fileUtils).writeFile(outputBytes, outputFile);

        assertThatThrownBy(() -> outputHandler.handleOutput(cmdArgs, outputBytes, filePath))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Given valid options, When handleOutput is called, Then no exception is thrown")
    void testHandleOutputNoException() {
        Path filePath = mock(Path.class);
        File outputFile = mock(File.class);

        when(cmdArgs.hasOption(CliConfig.OPTION_REPLACE)).thenReturn(true);
        when(filePath.toFile()).thenReturn(outputFile);

        assertThatNoException().isThrownBy(() -> outputHandler.handleOutput(cmdArgs, outputBytes, filePath));
    }
}
