package com.ecommerce.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public class SecurityProperties {
    
    private boolean enabled = true;
    private String secret = "defaultSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity";
    private long expiration = 86400;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}