//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.start;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;

public class ModulesTest
{
    @Test
    public void testLoadAllModules() throws IOException
    {
        File homeDir = MavenTestingUtils.getTestResourceDir("usecases/home");
        BaseHome basehome = new BaseHome(homeDir,homeDir);

        Modules modules = new Modules();
        modules.registerAll(basehome);
        Assert.assertThat("Module count",modules.count(),is(11));
    }

    @Test
    public void testResolve_ServerHttp() throws IOException
    {
        File homeDir = MavenTestingUtils.getTestResourceDir("usecases/home");
        BaseHome basehome = new BaseHome(homeDir,homeDir);

        // Register modules
        Modules modules = new Modules();
        modules.registerAll(basehome);
        modules.buildGraph();

        // Enable 2 modules
        modules.enable("server");
        modules.enable("http");

        // Collect active module list
        List<Module> active = modules.resolveEnabled();

        // Assert names are correct, and in the right order
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("base");
        expectedNames.add("server");
        expectedNames.add("http");

        List<String> actualNames = new ArrayList<>();
        for (Module actual : active)
        {
            actualNames.add(actual.getName());
        }

        Assert.assertThat("Resolved Names: " + actualNames,actualNames,contains(expectedNames.toArray()));

        // Assert Library List
        List<String> expectedLibs = new ArrayList<>();
        expectedLibs.add("lib/jetty-util-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-io-${jetty.version}.jar");
        expectedLibs.add("lib/servlet-api-3.1.jar");
        expectedLibs.add("lib/jetty-schemas-3.1.jar");
        expectedLibs.add("lib/jetty-http-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-continuation-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-server-${jetty.version}.jar");

        List<String> actualLibs = modules.normalizeLibs(active);
        Assert.assertThat("Resolved Libs: " + actualLibs,actualLibs,contains(expectedLibs.toArray()));
        
        // Assert XML List
        List<String> expectedXmls = new ArrayList<>();
        expectedXmls.add("etc/jetty.xml");
        expectedXmls.add("etc/jetty-http.xml");
        
        List<String> actualXmls = modules.normalizeXmls(active);
        Assert.assertThat("Resolved XMLs: " + actualXmls,actualXmls,contains(expectedXmls.toArray()));
    }

    @Test
    public void testResolve_WebSocket() throws IOException
    {
        File homeDir = MavenTestingUtils.getTestResourceDir("usecases/home");
        BaseHome basehome = new BaseHome(homeDir,homeDir);

        // Register modules
        Modules modules = new Modules();
        modules.registerAll(basehome);
        modules.buildGraph();
        // modules.dump();

        // Enable 2 modules
        modules.enable("websocket");
        modules.enable("http");

        // Collect active module list
        List<Module> active = modules.resolveEnabled();
        
        // Assert names are correct, and in the right order
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("base");
        expectedNames.add("server");
        expectedNames.add("http");
        expectedNames.add("plus");
        expectedNames.add("annotations");
        expectedNames.add("websocket");

        List<String> actualNames = new ArrayList<>();
        for (Module actual : active)
        {
            actualNames.add(actual.getName());
        }

        Assert.assertThat("Resolved Names: " + actualNames,actualNames,contains(expectedNames.toArray()));

        // Assert Library List
        List<String> expectedLibs = new ArrayList<>();
        expectedLibs.add("lib/jetty-util-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-io-${jetty.version}.jar");
        expectedLibs.add("lib/servlet-api-3.1.jar");
        expectedLibs.add("lib/jetty-schemas-3.1.jar");
        expectedLibs.add("lib/jetty-http-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-continuation-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-server-${jetty.version}.jar");
        expectedLibs.add("lib/jetty-plus-${jetty.version}.xml");
        expectedLibs.add("lib/jetty-annotations-${jetty.version}.jar");
        expectedLibs.add("lib/annotations/*.jar");
        expectedLibs.add("lib/websockets/*.jar");
        
        List<String> actualLibs = modules.normalizeLibs(active);
        Assert.assertThat("Resolved Libs: " + actualLibs,actualLibs,contains(expectedLibs.toArray()));
        
        // Assert XML List
        List<String> expectedXmls = new ArrayList<>();
        expectedXmls.add("etc/jetty.xml");
        expectedXmls.add("etc/jetty-http.xml");
        expectedXmls.add("etc/jetty-plus.xml");
        expectedXmls.add("etc/jetty-annotations.xml");
        expectedXmls.add("etc/jetty-websocket.xml");
        
        List<String> actualXmls = modules.normalizeXmls(active);
        Assert.assertThat("Resolved XMLs: " + actualXmls,actualXmls,contains(expectedXmls.toArray()));
    }
}
