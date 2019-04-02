package com.dcis.netboot.nettyconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "netty")
@PropertySource(value = "classpath:nettyConfig.properties")
public class Nettyconfig {
    private String port;
    private String webpath;
    private String controllerPackage;

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public int getPort() {
        return Integer.valueOf(port);
    }


    public void setPort(String port) {
        this.port = port;
    }

    public String getWebpath() {
        if(webpath==null){
            return "";
        }
        return webpath;
    }

    public void setWebpath(String webpath) {
        this.webpath = webpath;
    }



}
