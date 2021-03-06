package org.apache.maven.scm.provider.svn.svnexe.command.changelog;

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

import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.log.DefaultLog;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class SvnChangeLogConsumerTest
    extends PlexusTestCase
{
    Logger logger;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        logger = getContainer().getLogger();
    }

    public void testConsumerWithPattern1()
        throws Exception
    {
        StringBuffer out = new StringBuffer();

        SvnChangeLogConsumer consumer = new SvnChangeLogConsumer( new DefaultLog(), null );

        File f = getTestFile( "/src/test/resources/svn/changelog/svnlog.txt" );

        BufferedReader r = new BufferedReader( new FileReader( f ) );

        String line;

        while ( ( line = r.readLine() ) != null )
        {
            consumer.consumeLine( line );
        }

        List modifications = consumer.getModifications();

        out.append( "Text format:" );

        out.append( "nb modifications : " + modifications.size() );

        for ( Iterator i = modifications.iterator(); i.hasNext(); )
        {
            ChangeSet entry = (ChangeSet) i.next();

            out.append( "Author:" + entry.getAuthor() );

            out.append( "Date:" + entry.getDate() );

            out.append( "Comment:" + entry.getComment() );

            List files = entry.getFiles();

            for ( Iterator it = files.iterator(); it.hasNext(); )
            {
                ChangeFile file = (ChangeFile) it.next();

                out.append( "File:" + file.getName() );
            }

            out.append( "==============================" );
        }

        out.append( "XML format:" );

        out.append( "nb modifications : " + modifications.size() );

        for ( Iterator i = modifications.iterator(); i.hasNext(); )
        {
            ChangeSet entry = (ChangeSet) i.next();

            out.append( entry.toXML() );

            out.append( "==============================" );
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug( out.toString() );
        }
    }

    public void testConsumerWithPattern2()
        throws Exception
    {
        StringBuffer out = new StringBuffer();

        SvnChangeLogConsumer consumer = new SvnChangeLogConsumer( new DefaultLog(), null );

        File f = getTestFile( "/src/test/resources/svn/changelog/svnlog2.txt" );

        BufferedReader r = new BufferedReader( new FileReader( f ) );

        String line;

        while ( ( line = r.readLine() ) != null )
        {
            consumer.consumeLine( line );
        }

        List modifications = consumer.getModifications();

        out.append( "nb modifications : " + modifications.size() );

        for ( Iterator i = modifications.iterator(); i.hasNext(); )
        {
            ChangeSet entry = (ChangeSet) i.next();

            out.append( "Author:" + entry.getAuthor() );

            out.append( "Date:" + entry.getDate() );

            out.append( "Comment:" + entry.getComment() );

            List files = entry.getFiles();

            for ( Iterator it = files.iterator(); it.hasNext(); )
            {
                ChangeFile file = (ChangeFile) it.next();

                out.append( "File:" + file.getName() );
            }

            out.append( "==============================" );
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug( out.toString() );
        }
    }
}