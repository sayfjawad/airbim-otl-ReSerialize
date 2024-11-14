package nl.rws.otl.git_tools.wrap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemWrapper {

    public void exit(final int exitCode) {

        log.info("System exitting with exit code ({})", exitCode);
        System.exit(exitCode);
    }

}
