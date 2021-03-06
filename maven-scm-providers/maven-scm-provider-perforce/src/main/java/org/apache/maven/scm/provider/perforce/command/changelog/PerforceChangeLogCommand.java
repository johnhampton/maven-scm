package org.apache.maven.scm.provider.perforce.command.changelog;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.AbstractChangeLogCommand;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.perforce.command.PerforceCommand;
import org.apache.maven.scm.provider.perforce.repository.PerforceScmProviderRepository;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.util.Date;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class PerforceChangeLogCommand
    extends AbstractChangeLogCommand
    implements PerforceCommand
{
    /** {@inheritDoc} */
    protected ChangeLogScmResult executeChangeLogCommand( ScmProviderRepository repo, ScmFileSet fileSet,
                                                          Date startDate, Date endDate, ScmBranch branch,
                                                          String datePattern )
        throws ScmException
    {
        if ( branch != null && StringUtils.isNotEmpty( branch.getName() ) )
        {
            throw new ScmException( "This SCM doesn't support branches." );
        }

        PerforceScmProviderRepository p4repo = (PerforceScmProviderRepository) repo;
        String clientspec = PerforceScmProvider.getClientspecName( getLogger(), p4repo, fileSet.getBasedir() );
        Commandline cl = createCommandLine( p4repo, fileSet.getBasedir(), clientspec );

        String location = PerforceScmProvider.getRepoPath( getLogger(), p4repo, fileSet.getBasedir() );
        PerforceChangeLogConsumer consumer =
            new PerforceChangeLogConsumer( location, startDate, endDate, datePattern, getLogger() );

        try
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( PerforceScmProvider.clean( "Executing " + cl.toString() ) );
            }

            CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
            int exitCode = CommandLineUtils.executeCommandLine( cl, consumer, err );

            if ( exitCode != 0 )
            {
                String cmdLine = CommandLineUtils.toString( cl.getCommandline() );

                StringBuffer msg = new StringBuffer( "Exit code: " + exitCode + " - " + err.getOutput() );
                msg.append( '\n' );
                msg.append( "Command line was:" + cmdLine );

                throw new CommandLineException( msg.toString() );
            }
        }
        catch ( CommandLineException e )
        {
            if ( getLogger().isErrorEnabled() )
            {
                getLogger().error( "CommandLineException " + e.getMessage(), e );
            }
        }

        return new ChangeLogScmResult( cl.toString(),
                                       new ChangeLogSet( consumer.getModifications(), startDate, endDate ) );
    }

    public static Commandline createCommandLine( PerforceScmProviderRepository repo, File workingDirectory,
                                                 String clientspec )
    {
        Commandline command = PerforceScmProvider.createP4Command( repo, workingDirectory );

        if ( clientspec != null )
        {
            command.createArg().setValue( "-c" );
            command.createArg().setValue( clientspec );
        }
        command.createArg().setValue( "filelog" );
        command.createArg().setValue( "-t" );
        command.createArg().setValue( "-l" );
        command.createArg().setValue( "..." );

        return command;
    }
}
