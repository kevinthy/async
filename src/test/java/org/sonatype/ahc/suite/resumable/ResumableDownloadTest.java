package org.sonatype.ahc.suite.resumable;

/*
 * Copyright (c) 2010-2011 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0, 
 * and you may not use this file except in compliance with the Apache License Version 2.0. 
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the Apache License Version 2.0 is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.LogbackException;
import com.ning.http.client.filter.ResponseFilter;
import org.jboss.netty.channel.Channel;
import org.slf4j.LoggerFactory;
import org.sonatype.ahc.suite.util.AsyncSuiteConfiguration;
import org.sonatype.tests.http.server.api.Behaviour;
import org.testng.annotations.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Benjamin Hanzelmann
 */
public class ResumableDownloadTest
        extends AsyncSuiteConfiguration {

    /**
     * @author Benjamin Hanzelmann
     *
     */
    private final class Resume
        implements Behaviour
    {
        /**
         * 
         */
        private final int half;

        /**
         * 
         */
        private final int length;

        private boolean firstInvocation = true;

        private byte[] bytes;

        /**
         * @param half
         * @param length
         * @throws UnsupportedEncodingException
         */
        private Resume( int half, int length )
            throws UnsupportedEncodingException
        {
            this.half = half;
            this.length = length;
            bytes = "12345678901234567890".getBytes( "UTF-16" );
        }

        public boolean execute(HttpServletRequest request, HttpServletResponse response, Map<Object, Object> ctx)
                throws Exception {

            response.addHeader("Accept-Ranges", "bytes");
            response.setStatus(200);

            ServletOutputStream out = response.getOutputStream();

            if (firstInvocation) {
                response.setContentLength(length);

                sendBytes(half, out);

                response.flushBuffer();
                out.flush();
                int sleep = 300000;
                firstInvocation = false;
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                }
                return false;
            } else {
                String range = request.getHeader("Range");
                if (range == null) {
                    response.sendError(500, "No Range Header");
                    return false;
                }
                if (!range.startsWith("bytes")) {
                    response.sendError(500, "range header not valid: " + range);
                    return false;
                }

                response.setContentLength(half);

                sendBytes(half, out);

                response.flushBuffer();
                out.close();
            }

            return false;
        }

        public void sendBytes(final int count, ServletOutputStream out)
                throws IOException {
            int total = 0;
            while (total < count) {
                int toWrite = Math.min(bytes.length, count - total);
                out.write(bytes, 0, toWrite);
                total += toWrite;
            }
        }
    }

    @Test( groups = "standalone", enabled = true )
    public void testResumeDownloadForkVM()
            throws IOException, InterruptedException {
        final int length = 128 * 1024;
        final int half = length / 2;

        provider().addBehaviour("/resume/*", new Resume( half, length ));

        ForkJvm fork = new ResumingExternalDownload();

        fork.setKillAfter(3500);
        fork.setSyncOn( "target/testResumeDownloadForkVM.sync" );

        fork.addClassPathEntry(ResumingExternalDownload.class);
        fork.addClassPathEntry(ResponseFilter.class);
        fork.addClassPathEntry(LoggerFactory.class);
        fork.addClassPathEntry(Channel.class);
        fork.addClassPathEntry(LogbackException.class);
        fork.addClassPathEntry(Level.class);

        File tmpFile = File.createTempFile("ExternalDownloadTest", "testResumeDownloadForkVM");
        tmpFile.deleteOnExit();

        fork.setParameters( url( "resume", "test" ), tmpFile.getAbsolutePath() );

        Process process = fork.run();

        process.waitFor();

        assertEquals(half, tmpFile.length());

        // fork.debug( 1044 );

        process = fork.run();

        process.waitFor();
        assertEquals(length, tmpFile.length());
    }

    @Test( groups = "standalone", enabled = true )
    public void testSimpleAHCResumeDownloadForkVM()
            throws IOException, InterruptedException {
        final int length = 128 * 1024;
        final int half = length / 2;
    
        provider().addBehaviour("/resume/*", new Resume( half, length ));
    
        ForkJvm fork = new SAHCResumingExternalDownload();

        fork.setKillAfter(3500);
        fork.setSyncOn( "target/testSimpleAHCResumeDownloadForkVM.sync" );
    
        fork.addClassPathEntry(ResumingExternalDownload.class);
        fork.addClassPathEntry(ResponseFilter.class);
        fork.addClassPathEntry(LoggerFactory.class);
        fork.addClassPathEntry(Channel.class);
        fork.addClassPathEntry(LogbackException.class);
        fork.addClassPathEntry(Level.class);
    
        File tmpFile = File.createTempFile("ExternalDownloadTest", "testResumeDownloadForkVM");
        tmpFile.deleteOnExit();
    
        fork.setParameters( url( "resume", "test" ), tmpFile.getAbsolutePath() );
    
        Process process = fork.run();
    
        process.waitFor();
    
        assertEquals(half, tmpFile.length());
    
        // fork.debug( 1044 );
    
        process = fork.run();
    
        process.waitFor();
        assertEquals(length, tmpFile.length());
    }

}
