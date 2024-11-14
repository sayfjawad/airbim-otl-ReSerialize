package nl.rws.otl.git_tools;

import nl.rws.otl.git_tools.cli.CliArgumentParser;
import nl.rws.otl.git_tools.cli.CliConfig;
import nl.rws.otl.git_tools.file.FileUtils;
import nl.rws.otl.git_tools.handler.OutputHandler;
import nl.rws.otl.git_tools.hash.HashCalculator;
import nl.rws.otl.git_tools.ontology.OntologyDocumentCreator;
import nl.rws.otl.git_tools.ontology.OntologyLoader;
import nl.rws.otl.git_tools.ontology.OntologyProcessor;
import nl.rws.otl.git_tools.ontology.OntologySerializer;
import nl.rws.otl.git_tools.wrap.SystemWrapper;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class App {

    private static final OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();

    public static void main(String[] args) {

        final SystemWrapper systemWrapper = new SystemWrapper();
        final var serializer = new ReSerialize(new CliArgumentParser(),
                new OntologyProcessor(
                        new OntologyLoader(),
                        new OntologySerializer(),
                        new OutputHandler(new FileUtils()),
                        new HashCalculator(new FileUtils()),
                        owlOntologyManager,
                        systemWrapper
                ),
                new CliConfig(),
                new FileUtils(),
                new HashCalculator(new FileUtils()),
                new OntologyDocumentCreator(),
                systemWrapper);

        serializer.reserialize(args);
    }
}
