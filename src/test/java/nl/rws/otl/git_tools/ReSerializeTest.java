package nl.rws.otl.git_tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class ReSerializeTest {
    private static final URL testTtl = ReSerializeTest.class.getClassLoader().getResource("basicsemantics-owl.ttl");
    private static final URL testTtlFixed = ReSerializeTest.class.getClassLoader().getResource("basicsemantics-owl_fixed.ttl");

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testMainNoCommand() throws NoSuchAlgorithmException {
        exit.expectSystemExitWithStatus(2);
        String[] args = {};
        ReSerialize.main(args);
    }

    @Test
    public void testMainStdin() throws IOException, NoSuchAlgorithmException {
        exit.expectSystemExitWithStatus(0);
        assertNotNull(testTtlFixed);
        System.setIn(testTtlFixed.openStream());
        String[] args = {"-f","-"};
        ReSerialize.main(args);
    }

    @Test
    public void testMainValid() throws NoSuchAlgorithmException {
        exit.expectSystemExitWithStatus(0);
        assertNotNull(testTtlFixed);
        String[] args = {"-f",testTtlFixed.getPath()};
        ReSerialize.main(args);
    }

    @Test
    public void testMainDifferent() throws NoSuchAlgorithmException {
        exit.expectSystemExitWithStatus(1);
        assertNotNull(testTtl);
        String[] args = {"-f",testTtl.getPath()};
        ReSerialize.main(args);
    }

    @Test
    public void testMainOutput() throws IOException, NoSuchAlgorithmException {
        exit.expectSystemExitWithStatus(1);
        assertNotNull(testTtl);

        Path tempFile = Files.createTempFile("unit_test_", ".ttl");
        System.out.println("Creating testfile: "+tempFile.toString());

        String[] args = {"-f",testTtl.getPath(),"-o",tempFile.toString()};
        ReSerialize.main(args);

        assertTrue(Files.deleteIfExists(tempFile));
        System.out.println("Deleted testfile: "+tempFile.toString());
    }

    @Test
    public void testMainReplace() throws IOException, NoSuchAlgorithmException {
        exit.expectSystemExitWithStatus(1);
        assertNotNull(testTtl);

        Path tempFile = Files.createTempFile("unit_test_", ".ttl");
        Files.delete(tempFile);
        Files.copy(new File(testTtl.getPath()).toPath(),tempFile);

        System.out.println("Created testfile: "+tempFile.toString());
        assertTrue(Files.exists(tempFile));

        String[] args = {"-f",tempFile.toString(),"-r"};
        ReSerialize.main(args);

        assertTrue(Files.deleteIfExists(tempFile));
        System.out.println("Deleted testfile: "+tempFile.toString());
    }

    @Test
    public void testGetHash() throws IOException, NoSuchAlgorithmException {
        assertNotNull(testTtl);
        String hash = ReSerialize.getHash(testTtl.openStream());
        assertEquals("76da850bba013f577570078580749c69364be5cb734816666b671bf8cca9f930",hash);
    }

    @Test
    public void testWriteFile() throws IOException {
        assertNotNull(testTtl);
        Path tempFile = Files.createTempFile("unit_test_", ".ttl");
        System.out.println("Creating testfile: "+tempFile.toString());
        byte[] bytes = testTtl.openStream().readAllBytes();
        ReSerialize.writeFile(bytes,tempFile.toFile());
        assertTrue(Files.deleteIfExists(tempFile));
        System.out.println("Deleted testfile: "+tempFile.toString());
    }
}
