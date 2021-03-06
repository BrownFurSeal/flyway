/**
 * Copyright (C) 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.flyway.sample.webapp;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.util.FeatureDetector;
import com.googlecode.flyway.core.util.jdbc.DriverDataSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for serving the home page.
 */
public class EnvInfoServlet extends HttpServlet {
    /**
     * The Flyway instance to use.
     */
    private final Flyway flyway = Environment.createFlyway();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new ServletException("POST not supported");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appserver;
        if (Environment.runningOnGoogleAppEngine()) {
            appserver = "Google AppEngine";
        } else if (FeatureDetector.isJBossVFSv2Available()) {
            appserver = "JBoss 5";
        } else if (FeatureDetector.isJBossVFSv3Available()) {
            appserver = "JBoss 6+";
        } else {
            appserver = "Other";
        }

        String database = ((DriverDataSource) flyway.getDataSource()).getUrl();

        PrintWriter writer = response.getWriter();
        writer.print("{\"status\":\"OK\"," +
                " \"appserver\":\"" + appserver + "\"," +
                " \"database\":\"" + database + "\"}");
    }
}
