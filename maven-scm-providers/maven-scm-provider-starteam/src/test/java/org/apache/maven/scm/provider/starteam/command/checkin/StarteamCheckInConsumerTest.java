package org.apache.maven.scm.provider.starteam.command.checkin;

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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmTestCase;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:dantran@gmail.com">Dan T. Tran</a>
 * @version
 */
public class StarteamCheckInConsumerTest
    extends ScmTestCase
{
    private static String [] TEST_OUTPUT =
    {
        "Folder: driver  (working dir: /usr/scm-starteam/driver)",
  		"maven.xml: checked in",
        "Folder: driver  (working dir: /usr/scm-starteam/driver/target/checkout)",
		"maven.xml: checked in",
		"project.properties: skipped",
		"project.xml: checked in",
        "Folder: bootstrap  (working dir: /usr/scm-starteam/driver/target/checkout/bootstrap)",
        "Folder: dev  (working dir: /usr/scm-starteam/driver/target/checkout/dev)",
		"maven.xml:skipped",
		"project.properties: skipped",
		"project.xml: checked in"
    };

    public void testParse()
        throws Exception
    {
		File basedir = new File( "/usr/scm-starteam/driver" );
        
        Logger logger = new ConsoleLogger(Logger.LEVEL_INFO, "testlogger");

        StarteamCheckInConsumer consumer = new StarteamCheckInConsumer( logger, basedir);

        for ( int i = 0; i < TEST_OUTPUT.length; ++ i )
        {
            consumer.consumeLine( TEST_OUTPUT[i] );
        }

        Collection entries = consumer.getCheckedInFiles();

        assertEquals( "Wrong number of entries returned", 4, entries.size() );
        
        ScmFile entry;

        for ( Iterator i = entries.iterator(); i.hasNext(); )
        {
            entry = (ScmFile) i.next();
            
            assertTrue ( entry.getPath().startsWith("./") );
            
            assertTrue( entry.getStatus() == ScmFileStatus.CHECKED_OUT );
        }
        

    }
}
