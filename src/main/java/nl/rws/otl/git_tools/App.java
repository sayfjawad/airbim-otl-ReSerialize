package nl.rws.otl.git_tools;

import nl.rws.otl.git_tools.cli.CliArgumentParser;
import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import nl.rws.otl.git_tools.handler.OutputHandler;
import nl.rws.otl.git_tools.hash.HashCalculator;
import nl.rws.otl.git_tools.ontology.OntologyDocumentCreator;
import nl.rws.otl.git_tools.ontology.OntologyProcessor;
import nl.rws.otl.git_tools.ontology.OntologySerializer;

public class App {

    public static void main(String[] args) {

        final var serializer = new ReSerialize(new CliArgumentParser(),
                new OntologyProcessor(
                        new OntologySerializer(),
                        new OutputHandler(new FileUtils()),
                        new HashCalculator(new FileUtils())
                ),
                new CliConfig(),
                new FileUtils(),
                new HashCalculator(new FileUtils()),
                new OntologyDocumentCreator());

        serializer.reserialize(args);
    }
}
