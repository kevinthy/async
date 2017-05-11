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

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import com.ning.http.client.extra.ResumableRandomAccessFileListener;
import com.ning.http.client.resumable.PropertiesBasedResumableProcessor;
import com.ning.http.client.resumable.ResumableAsyncHandler;

import java.io.File;
import java.io.RandomAccessFile;

import org.slf4j.LoggerFactory;

/**
 * @author Benjamin Hanzelmann
 */
public class ResumingExternalDownload
        extends ForkJvm {

    public static void main(String[] args)
        throws Exception
    {
        String url = args[0];
        String fPath = args[1];


        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setConnectionTimeoutInMs(60000).setIdleConnectionInPoolTimeoutInMs(60000).setRequestTimeoutInMs(60000);

        AsyncHttpClientConfig cfg = builder.build();
        AsyncHttpClient client = new AsyncHttpClient(cfg);

        Request request = client.prepareGet(url).build();
        RandomAccessFile target = new RandomAccessFile(new File(fPath), "rw");
        ResumableAsyncHandler<Response> handler =
                new ResumableAsyncHandler<Response>(new PropertiesBasedResumableProcessor());
        handler.setResumableListener(new ResumableRandomAccessFileListener(target));

        // initializing logback takes much time on slow VMs...
        LoggerFactory.getLogger("initialize");

        setup();

        Response response = client.executeRequest(request, handler).get();
        System.err.println(response.toString());
    }

}
