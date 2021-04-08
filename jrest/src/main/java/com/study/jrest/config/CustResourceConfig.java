package com.study.jrest.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * @author jiayq
 * @Date 2021-03-25
 */
@Component
public class CustResourceConfig extends ResourceConfig {

    public CustResourceConfig() {
        packages("com.study");
    }

}
