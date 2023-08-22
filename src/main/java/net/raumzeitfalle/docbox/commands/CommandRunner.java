/*-
 * #%L
 * docdrop
 * %%
 * Copyright (C) 2023 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.docbox.commands;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.raumzeitfalle.docbox.Configuration;

public class CommandRunner implements Runnable {

    private static final Logger LOG = Logger.getLogger(CommandRunner.class.getName());

    private final Executable executable;

    private String startMessage = "Starting {0} ...";

    private String completedMessage = "Command {0} successfully completed with return code: {1}";

    private String completedWithError = "{0} completed with return code {1} indicating an error! Check command line options.";

    private String ioErrorMessage = " failed with IO Error!";

    private String interruptedErrorMessage = " execution was interrupted!";

    private final Configuration configuration;

    public CommandRunner(Executable executable, Configuration config) {
        this.executable = executable;
        this.configuration = config;
    }

    public void run() {
        var commandName = this.executable.getClass().getName();
        try {
            LOG.log(Level.INFO, startMessage, commandName);
            this.executable.configure(this.configuration);
            int returnCode = this.executable.exec();
            if (returnCode != 0) {
                LOG.log(Level.WARNING, completedWithError, new Object[] {commandName, returnCode});
            } else {
                LOG.log(Level.INFO, completedMessage, new Object[] {commandName, returnCode});
            }
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, commandName + ioErrorMessage, ioe);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOG.log(Level.SEVERE, commandName + interruptedErrorMessage, ie);
        }
    }
}
