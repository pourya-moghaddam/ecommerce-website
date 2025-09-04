package com.ecommerce.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityPropertiesTest {

    private SecurityProperties securityProperties;

    @BeforeEach
    void setUp() {
        securityProperties = new SecurityProperties();
    }

    @Test
    void defaultValues_ShouldBeSetCorrectly() {
        assertTrue(securityProperties.isEnabled());
        assertEquals("defaultSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity", securityProperties.getSecret());
        assertEquals(86400, securityProperties.getExpiration());
    }

    @Test
    void setEnabled_WithTrue_ShouldSetEnabled() {
        securityProperties.setEnabled(true);
        assertTrue(securityProperties.isEnabled());
    }

    @Test
    void setEnabled_WithFalse_ShouldSetDisabled() {
        securityProperties.setEnabled(false);
        assertFalse(securityProperties.isEnabled());
    }

    @Test
    void setSecret_WithValidSecret_ShouldSetSecret() {
        String newSecret = "myCustomSecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity";
        securityProperties.setSecret(newSecret);
        assertEquals(newSecret, securityProperties.getSecret());
    }

    @Test
    void setSecret_WithNullSecret_ShouldSetNull() {
        securityProperties.setSecret(null);
        assertEquals(null, securityProperties.getSecret());
    }

    @Test
    void setSecret_WithEmptySecret_ShouldSetEmpty() {
        securityProperties.setSecret("");
        assertEquals("", securityProperties.getSecret());
    }

    @Test
    void setExpiration_WithPositiveValue_ShouldSetExpiration() {
        long expiration = 3600L;
        securityProperties.setExpiration(expiration);
        assertEquals(expiration, securityProperties.getExpiration());
    }

    @Test
    void setExpiration_WithZero_ShouldSetZero() {
        securityProperties.setExpiration(0L);
        assertEquals(0L, securityProperties.getExpiration());
    }

    @Test
    void setExpiration_WithNegativeValue_ShouldSetNegative() {
        securityProperties.setExpiration(-1L);
        assertEquals(-1L, securityProperties.getExpiration());
    }

    @Test
    void setExpiration_WithLargeValue_ShouldSetLargeValue() {
        long largeExpiration = 31536000L; // 1 year in seconds
        securityProperties.setExpiration(largeExpiration);
        assertEquals(largeExpiration, securityProperties.getExpiration());
    }

    @Test
    void multiplePropertyChanges_ShouldMaintainIndependentValues() {
        securityProperties.setEnabled(false);
        securityProperties.setSecret("newSecret");
        securityProperties.setExpiration(1800L);

        assertFalse(securityProperties.isEnabled());
        assertEquals("newSecret", securityProperties.getSecret());
        assertEquals(1800L, securityProperties.getExpiration());
    }

    @Test
    void propertyOverwrite_ShouldReplaceExistingValues() {
        securityProperties.setSecret("firstSecret");
        securityProperties.setSecret("secondSecret");
        assertEquals("secondSecret", securityProperties.getSecret());

        securityProperties.setExpiration(1000L);
        securityProperties.setExpiration(2000L);
        assertEquals(2000L, securityProperties.getExpiration());

        securityProperties.setEnabled(false);
        securityProperties.setEnabled(true);
        assertTrue(securityProperties.isEnabled());
    }
}