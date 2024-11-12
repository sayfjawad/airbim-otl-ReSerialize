package nl.rws.otl.git_tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class ReSerializeTest {

    private static final URL testTtl = ReSerializeTest.class.getClassLoader()
            .getResource("basicsemantics-owl.ttl");

    private static final URL testTtlFixed = ReSerializeTest.class.getClassLoader()
            .getResource("basicsemantics-owl_fixed.ttl");

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testMainNoCommand() {

        exit.expectSystemExitWithStatus(2);
        String[] args = {};
        App.main(args);
    }

    @Test
    public void testMainStdin() throws IOException {

        exit.expectSystemExitWithStatus(0);
        assertNotNull(testTtlFixed);
        System.setIn(testTtlFixed.openStream());
        String[] args = {"-f", "-"};
        App.main(args);
    }

    @Test
    public void testMainValid() {

        exit.expectSystemExitWithStatus(0);
        assertNotNull(testTtlFixed);
        String[] args = {"-f", testTtlFixed.getPath()};
        App.main(args);
    }

    @Test
    public void testMainDifferent() {

        exit.expectSystemExitWithStatus(1);
        assertNotNull(testTtl);
        String[] args = {"-f", testTtl.getPath()};
        App.main(args);
    }

    @Test
    public void testMainOutput() throws IOException {

        exit.expectSystemExitWithStatus(1);
        assertNotNull(testTtl);

        Path tempFile = Files.createTempFile("unit_test_", ".ttl");
        System.out.println("Creating testfile: " + tempFile.toString());

        String[] args = {"-f", testTtl.getPath(), "-o", tempFile.toString()};
        App.main(args);

        assertTrue(Files.deleteIfExists(tempFile));
        System.out.println("Deleted testfile: " + tempFile.toString());
    }

    @Test
    public void testMainReplace() throws IOException {

        exit.expectSystemExitWithStatus(1);
        assertNotNull(testTtl);

        Path tempFile = Files.createTempFile("unit_test_", ".ttl");
        Files.delete(tempFile);
        Files.copy(new File(testTtl.getPath()).toPath(), tempFile);

        System.out.println("Created testfile: " + tempFile);
        assertTrue(Files.exists(tempFile));

        String[] args = {"-f", tempFile.toString(), "-r"};
        App.main(args);

        assertTrue(Files.deleteIfExists(tempFile));
        System.out.println("Deleted testfile: " + tempFile);
    }
}
