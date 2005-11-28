package org.apache.maven.scm.provider.perforce.command.update;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
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
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.ChangeLogCommand;
import org.apache.maven.scm.command.update.AbstractUpdateCommand;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.perforce.command.PerforceCommand;
import org.apache.maven.scm.provider.perforce.command.changelog.PerforceChangeLogCommand;
import org.apache.maven.scm.provider.perforce.repository.PerforceScmProviderRepository;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author Mike Perham
 * @version $Id: PerforceChangeLogCommand.java 264804 2005-08-30 16:09:04Z
 *          evenisse $
 */
public class PerforceUpdateCommand
    extends AbstractUpdateCommand
    implements PerforceCommand
{

    protected UpdateScmResult executeUpdateCommand( ScmProviderRepository repo, ScmFileSet files, String tag )
        throws ScmException
    {
        Commandline cl = createCommandLine( (PerforceScmProviderRepository) repo, files.getBasedir(), tag );
        PerforceUpdateConsumer consumer = new PerforceUpdateConsumer();
        try
        {
            Process proc = cl.execute();
            BufferedReader br = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );
            String line = null;
            while ( ( line = br.readLine() ) != null )
            {
                consumer.consumeLine( line );
            }
        }
        catch ( CommandLineException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        if ( consumer.isSuccess() )
        {
            return new UpdateScmResult( cl.toString(), consumer.getUpdates() );
        }
        else
        {
            return new UpdateScmResult( cl.toString(), "Unable to sync", consumer.getOutput(), consumer.isSuccess() );
        }
    }

    public static Commandline createCommandLine( PerforceScmProviderRepository repo, File workingDirectory, String tag )
    {
        Commandline command = PerforceScmProvider.createP4Command( repo, workingDirectory );

        command.createArgument().setValue( "sync" );
        // I'm unclear whether we should be
        // sync'ing each file individually to the label or just sync the
        // entire contents of the workingDir. I'm going to assume the
        // latter until the exact semantics are clearer.
        command.createArgument().setValue( "..." + ( tag != null ? "@" + tag : "" ) );
        return command;
    }

    protected ChangeLogCommand getChangeLogCommand()
    {
        return new PerforceChangeLogCommand();
    }
}