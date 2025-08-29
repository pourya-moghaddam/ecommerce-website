package com.ecommerce.common.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

class SecurityAutoConfigurationTest {

    @Test
    void class_ShouldHaveConfigurationAnnotation() {
        assertTrue(SecurityAutoConfiguration.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    void class_ShouldHaveComponentScanAnnotation() {
        assertTrue(SecurityAutoConfiguration.class.isAnnotationPresent(ComponentScan.class));
    }

    @Test
    void class_ShouldHaveEnableConfigurationPropertiesAnnotation() {
        assertTrue(SecurityAutoConfiguration.class.isAnnotationPresent(EnableConfigurationProperties.class));
    }

    @Test
    void componentScan_ShouldScanSecurityPackage() {
        ComponentScan componentScan = SecurityAutoConfiguration.class.getAnnotation(ComponentScan.class);
        String[] basePackages = componentScan.basePackages();
        
        assertNotNull(basePackages);
        assertTrue(basePackages.length > 0);
        assertTrue(java.util.Arrays.asList(basePackages).contains("com.ecommerce.common.security"));
    }

    @Test
    void enableConfigurationProperties_ShouldEnableSecurityProperties() {
        EnableConfigurationProperties enableConfigProps = SecurityAutoConfiguration.class.getAnnotation(EnableConfigurationProperties.class);
        Class<?>[] value = enableConfigProps.value();
        
        assertNotNull(value);
        assertTrue(value.length > 0);
        assertTrue(java.util.Arrays.asList(value).contains(SecurityProperties.class));
    }

    @Test
    void constructor_ShouldCreateInstance() {
        SecurityAutoConfiguration config = new SecurityAutoConfiguration();
        assertNotNull(config);
    }

    @Test
    void class_ShouldBePublic() {
        assertTrue(java.lang.reflect.Modifier.isPublic(SecurityAutoConfiguration.class.getModifiers()));
    }

    @Test
    void class_ShouldNotBeFinal() {
        assertTrue(!java.lang.reflect.Modifier.isFinal(SecurityAutoConfiguration.class.getModifiers()));
    }
}