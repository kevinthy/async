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

import java.io.File;
import java.io.RandomAccessFile;

import org.slf4j.LoggerFactory;

import com.ning.http.client.Response;
import com.ning.http.client.SimpleAsyncHttpClient;
import com.ning.http.client.consumers.FileBodyConsumer;

/**
 * @author Benjamin Hanzelmann
 */
public class SAHCResumingExternalDownload
        extends ForkJvm {

    public static void main(String[] args)
        throws Exception
    {
        String url = args[0];
        String fPath = args[1];

        SimpleAsyncHttpClient client =
            new SimpleAsyncHttpClient.Builder().setUrl( url ).setRequestTimeoutInMs( 60000 ).setResumableDownload( true ).build();

        RandomAccessFile target = new RandomAccessFile(new File(fPath), "rw");
        FileBodyConsumer consumer = new FileBodyConsumer( target );

        // initializing logback takes much time on slow VMs...
        LoggerFactory.getLogger("initialize");

        setup();

        Response response = client.get( consumer ).get();
        System.err.println(response.toString());
    }

}
