/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ning.http.client.async.jdk;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.async.ProviderUtil;
import com.ning.http.client.async.ProxyTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class JDKProxyTest extends ProxyTest {
    @Override
    public AsyncHttpClient getAsyncHttpClient(AsyncHttpClientConfig config) {
        return ProviderUtil.jdkProvider(config);
    }

   @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testRequestLevelProxy() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }

    @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testGlobalProxy() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }

    @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testBothProxies() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }


    @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testNonProxyHosts() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }

    @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testProxyProperties() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }

    @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testIgnoreProxyPropertiesByDefault() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }

    @Test(groups = {"standalone", "default_provider"}, enabled = false)
    public void testProxyActivationProperty() throws IOException, ExecutionException, TimeoutException, InterruptedException {
    }
}
