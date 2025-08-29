package com.ecommerce.common.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.ecommerce.common.security")
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {
}