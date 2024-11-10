package nl.rws.otl.git_tools.file;

import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_INPUT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

@NoArgsConstructor
public class FileUtils {

    public Path getFilePath(final CommandLine cmdArgs) {

        return cmdArgs.getOptionValue(OPTION_INPUT).equals("-") ? null
                : Paths.get(cmdArgs.getOptionValue(OPTION_INPUT));
    }

    public InputStream getInputStreamFromSource(
            final OWLOntologyDocumentSourceBase documentSource) throws IOException {

        if (documentSource instanceof StreamDocumentSource) {
            return (documentSource).getInputStream().get();
        } else if (documentSource instanceof FileDocumentSource) {
            // Cast to FileDocumentSource and use the file path directly
            return new FileInputStream(new File((documentSource).getDocumentIRI().toURI()));
        }
        return null;
    }

    public void writeFile(final byte[] outputBytes, final File file) throws IOException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(outputBytes);
        }
    }

}
