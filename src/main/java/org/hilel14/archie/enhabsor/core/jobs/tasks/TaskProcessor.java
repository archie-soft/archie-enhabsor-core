package org.hilel14.archie.enhabsor.core.jobs.tasks;

import java.nio.file.Path;
import org.hilel14.archie.enhabsor.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public interface TaskProcessor {

    public void process(ImportFileTicket ticket, Path path) throws Exception;
}
