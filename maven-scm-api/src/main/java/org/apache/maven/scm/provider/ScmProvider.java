package org.apache.maven.scm.provider;

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

import java.util.List;

import org.apache.maven.scm.CommandParameters;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.repository.ScmRepositoryException;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public interface ScmProvider
{
    String ROLE = ScmProvider.class.getName();

    String getScmType();

    void addListener( ScmLogger logger );

    ScmProviderRepository makeProviderScmRepository( String scmSpecificUrl, char delimiter )
        throws ScmRepositoryException;

    List validateScmUrl( String scmSpecificUrl, char delimiter );

    ScmResult execute( String commandName, ScmProviderRepository repository, ScmFileSet fileSet,
                       CommandParameters parameters )
        throws ScmException;
}
