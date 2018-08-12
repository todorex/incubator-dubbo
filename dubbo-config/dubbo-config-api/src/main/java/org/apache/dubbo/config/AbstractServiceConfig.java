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
package org.apache.dubbo.config;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.support.Parameter;
import org.apache.dubbo.rpc.ExporterListener;

import java.util.Arrays;
import java.util.List;

/**
 * AbstractServiceConfig
 * 抽象服务配置类
 * @export
 */
public abstract class AbstractServiceConfig extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 1L;

    /**
     * 服务版本
     * 建议使用两位数字版本，如：1.0
     */
    protected String version;

    /**
     * 服务分组
     * 当一个接口有多个实现，可以用分组区分
     */
    protected String group;

    /**
     * 服务是否过时
     * 如果设为true，消费方引用时将打印服务过时警告error日志
     */
    protected Boolean deprecated;

    /**
     * 延迟注册服务时间(毫秒)
     */
    protected Integer delay;

    /**
     * 是否暴露
     */
    protected Boolean export;

    /**
     * 务权重
     */
    protected Integer weight;

    /**
     * 服务文档URL
     */
    protected String document;

    /**
     * 服务是否动态注册
     */
    protected Boolean dynamic;

    /**
     * 令牌验证
     * 为空表示不开启
     * 如果为true，表示随机生成动态令牌
     * 否则使用静态令牌
     * 令牌的作用是防止消费者绕过注册中心直接访问，保证注册中心的授权功能有效
     * 如果使用点对点调用，需关闭令牌功能
     */
    protected String token;

    /**
     * 访问日志路径
     */
    protected String accesslog;

    /**
     *     协议列表
     */
    protected List<ProtocolConfig> protocols;

    /**
     * 服务提供者每服务每方法最大可并行执行请求数
     */
    private Integer executes;

    /**
     * 该协议的服务是否注册到注册中心
     */
    private Boolean register;

    /**
     * warm up period
     * ？？
     */
    private Integer warmup;

    /**
     * 序列化方式
     */
    private String serialization;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        checkKey("version", version);
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        checkKey("group", group);
        this.group = group;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Parameter(escaped = true)
    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        checkName("token", token);
        this.token = token;
    }

    public void setToken(Boolean token) {
        if (token == null) {
            setToken((String) null);
        } else {
            setToken(String.valueOf(token));
        }
    }

    public Boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    @SuppressWarnings({"unchecked"})
    public void setProtocols(List<? extends ProtocolConfig> protocols) {
        this.protocols = (List<ProtocolConfig>) protocols;
    }

    public ProtocolConfig getProtocol() {
        return protocols == null || protocols.isEmpty() ? null : protocols.get(0);
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocols = Arrays.asList(protocol);
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public void setAccesslog(Boolean accesslog) {
        if (accesslog == null) {
            setAccesslog((String) null);
        } else {
            setAccesslog(String.valueOf(accesslog));
        }
    }

    public Integer getExecutes() {
        return executes;
    }

    public void setExecutes(Integer executes) {
        this.executes = executes;
    }

    @Override
    @Parameter(key = Constants.SERVICE_FILTER_KEY, append = true)
    public String getFilter() {
        return super.getFilter();
    }

    @Override
    @Parameter(key = Constants.EXPORTER_LISTENER_KEY, append = true)
    public String getListener() {
        return listener;
    }

    @Override
    public void setListener(String listener) {
        checkMultiExtension(ExporterListener.class, "listener", listener);
        this.listener = listener;
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Integer getWarmup() {
        return warmup;
    }

    public void setWarmup(Integer warmup) {
        this.warmup = warmup;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

}
