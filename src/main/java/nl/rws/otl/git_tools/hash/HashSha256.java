package nl.rws.otl.git_tools.hash;

import static nl.rws.otl.git_tools.file.FileUtils.getInputStreamFromSource;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;

@Slf4j
public class HashSha256 {

    public static String getHash(
            InputStream inputStream) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            while (dis.read() != -1) { /* Read through the stream */ }
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static String computeFileHash(OWLOntologyDocumentSourceBase documentSource) {

        try (InputStream inputStream = getInputStreamFromSource(documentSource)) {
            return getHash(inputStream);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Failed to compute file hash: {}", e.getMessage());
            return null;
        }
    }

}
