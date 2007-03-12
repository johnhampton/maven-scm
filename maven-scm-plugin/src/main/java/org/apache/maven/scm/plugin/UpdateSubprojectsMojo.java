package org.apache.maven.scm.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.command.update.UpdateScmResultWithRevision;
import org.apache.maven.scm.repository.ScmRepository;

import java.io.IOException;

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

/**
 * @goal update-subprojects
 * @description Updates all projects in a multi project build. This is useful for users who have adopted the flat project structure where the aggregator project is a sibling of the sub projects rather than sitting in the parent directory.
 */
public class UpdateSubprojectsMojo
    extends AbstractScmMojo
{
    /**
     * Branch name.
     *
     * @parameter expression="${branch}"
     */
    private String branch;

    /**
     * Tag name.
     *
     * @parameter expression="${tag}"
     */
    private String tag;

    /**
     * The project property where to store the revision name.
     *
     * @parameter expression="${revisionKey}" default-value="scm.revision"
     */
    private String revisionKey;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            ScmRepository repository = getScmRepository();

            String currentTag = null;

            if ( branch != null )
            {
                currentTag = branch;
            }

            if ( tag != null )
            {
                currentTag = tag;
            }

            UpdateScmResult result =
                getScmManager().getProviderByRepository( repository ).update( repository, getFileSet(), currentTag );

            checkResult( result );

            if ( result instanceof UpdateScmResultWithRevision )
            {
                getLog().info( "Storing revision in '" + revisionKey + "' project property." );

                if ( project.getProperties() != null ) // Remove the test when we'll use plugin-test-harness 1.0-alpha-2
                {
                    project.getProperties().put( revisionKey, ( (UpdateScmResultWithRevision) result ).getRevision() );
                }
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Cannot run update command : ", e );
        }
        catch ( ScmException e )
        {
            throw new MojoExecutionException( "Cannot run update command : ", e );
        }
    }
}