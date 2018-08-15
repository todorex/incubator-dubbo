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
package org.apache.dubbo.common.compiler.support;

import org.apache.dubbo.common.utils.ClassHelper;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavassistCompiler. (SPI, Singleton, ThreadSafe)
 * Javassist操作 http://www.cnblogs.com/sunfie/p/5154246.html
 */
public class JavassistCompiler extends AbstractCompiler {

    /**
     * import正则
     */
    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([\\w\\.\\*]+);\n");

    /**
     * extends正则
     */
    private static final Pattern EXTENDS_PATTERN = Pattern.compile("\\s+extends\\s+([\\w\\.]+)[^\\{]*\\{\n");

    /**
     * Implement正则
     */
    private static final Pattern IMPLEMENTS_PATTERN = Pattern.compile("\\s+implements\\s+([\\w\\.]+)\\s*\\{\n");

    /**
     * method正则
     */
    private static final Pattern METHODS_PATTERN = Pattern.compile("\n(private|public|protected)\\s+");

    /**
     * field正则
     */
    private static final Pattern FIELD_PATTERN = Pattern.compile("[^\n]+=[^\n]+;");

    @Override
    public Class<?> doCompile(String name, String source) throws Throwable {
        // 获得类名
        int i = name.lastIndexOf('.');
        String className = i < 0 ? name : name.substring(i + 1);
        // 创建 ClassPool 对象
        ClassPool pool = new ClassPool(true);
        // 设置类搜索路径
        pool.appendClassPath(new LoaderClassPath(ClassHelper.getCallerClassLoader(getClass())));
        Matcher matcher = IMPORT_PATTERN.matcher(source);
        // 匹配 import
        List<String> importPackages = new ArrayList<String>();
        Map<String, String> fullNames = new HashMap<String, String>();
        while (matcher.find()) {
            // 匹配包名
            String pkg = matcher.group(1);
            if (pkg.endsWith(".*")) {
                String pkgName = pkg.substring(0, pkg.length() - 2);
                pool.importPackage(pkgName);
                importPackages.add(pkgName);
            } else {
                int pi = pkg.lastIndexOf('.');
                if (pi > 0) {
                    String pkgName = pkg.substring(0, pi);
                    pool.importPackage(pkgName);
                    importPackages.add(pkgName);
                    fullNames.put(pkg.substring(pi + 1), pkg);
                }
            }
        }
        String[] packages = importPackages.toArray(new String[0]);
        // 匹配 extends
        matcher = EXTENDS_PATTERN.matcher(source);
        CtClass cls;
        if (matcher.find()) {
            String extend = matcher.group(1).trim();
            String extendClass;
            if (extend.contains(".")) {
                extendClass = extend;
            } else if (fullNames.containsKey(extend)) {
                extendClass = fullNames.get(extend);
            } else {
                extendClass = ClassUtils.forName(packages, extend).getName();
            }
            cls = pool.makeClass(name, pool.get(extendClass));
        } else {
            cls = pool.makeClass(name);
        }
        // 匹配 implements
        matcher = IMPLEMENTS_PATTERN.matcher(source);
        if (matcher.find()) {
            String[] ifaces = matcher.group(1).trim().split("\\,");
            for (String iface : ifaces) {
                iface = iface.trim();
                String ifaceClass;
                if (iface.contains(".")) {
                    ifaceClass = iface;
                } else if (fullNames.containsKey(iface)) {
                    ifaceClass = fullNames.get(iface);
                } else {
                    ifaceClass = ClassUtils.forName(packages, iface).getName();
                }
                cls.addInterface(pool.get(ifaceClass));
            }
        }
        String body = source.substring(source.indexOf("{") + 1, source.length() - 1);
        String[] methods = METHODS_PATTERN.split(body);
        for (String method : methods) {
            method = method.trim();
            if (method.length() > 0) {
                if (method.startsWith(className)) {
                    cls.addConstructor(CtNewConstructor.make("public " + method, cls));
                } else if (FIELD_PATTERN.matcher(method).matches()) {
                    cls.addField(CtField.make("private " + method, cls));
                } else {
                    cls.addMethod(CtNewMethod.make("public " + method, cls));
                }
            }
        }
        // 生成类
        return cls.toClass(ClassHelper.getCallerClassLoader(getClass()), JavassistCompiler.class.getProtectionDomain());
    }

}
