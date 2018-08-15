/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.http.servlet;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.http.HttpHandler;
import org.apache.dubbo.remoting.http.support.AbstractHttpServer;

/**
 * 基于 Servlet 的服务器实现类
 *
 * 需要配置 DispatcherServlet 到 web.xml 中。通过这样的方式，让外部的 Servlet 容器，可以进行转发
 */
public class ServletHttpServer extends AbstractHttpServer {

    public ServletHttpServer(URL url, HttpHandler handler) {
        super(url, handler);
        // 注册 HttpHandler 到 DispatcherServlet 中
        DispatcherServlet.addHttpHandler(url.getParameter(Constants.BIND_PORT_KEY, 8080), handler);
    }

}