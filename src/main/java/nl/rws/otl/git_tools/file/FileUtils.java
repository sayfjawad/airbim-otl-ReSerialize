package nl.rws.otl.git_tools.file;

import static nl.rws.otl.git_tools.cli.CliConfig.OPTION_INPUT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;

public class FileUtils {

    public static Path getFilePath(CommandLine cmdArgs) {

        return cmdArgs.getOptionValue(OPTION_INPUT).equals("-") ? null
                : Paths.get(cmdArgs.getOptionValue(OPTION_INPUT));
    }

    public static InputStream getInputStreamFromSource(
            OWLOntologyDocumentSourceBase documentSource) throws IOException {

        if (documentSource instanceof StreamDocumentSource) {
            return (documentSource).getInputStream().get();
        } else if (documentSource instanceof FileDocumentSource) {
            // Cast to FileDocumentSource and use the file path directly
            return new FileInputStream(new File((documentSource).getDocumentIRI().toURI()));
        }
        return null;
    }

    public static void writeFile(byte[] outputBytes, File file) throws IOException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(outputBytes);
        }
    }

}
