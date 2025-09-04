# Security Configuration Guide

This guide explains how to use the JWT security scaffolding provided by common-lib.

## Default Behavior

By default, all endpoints are **permitAll** - no authentication is required. The JWT filter is installed but authentication is optional until explicitly enabled.

## Configuration Properties

Configure JWT security in your `application.properties`:

```properties
# Enable/disable JWT security (default: true)
security.jwt.enabled=true

# JWT secret key (MUST be changed in production)
security.jwt.secret=your-256-bit-secret-key-here

# JWT expiration time in seconds (default: 86400 = 24 hours)
security.jwt.expiration=86400
```

## Enabling Authentication

### Option 1: Override Default Configuration

Create a security configuration in your service:

```java
@Configuration
@EnableWebSecurity
public class AuthServiceSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public AuthServiceSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    @Primary  // Override the default config
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()  // Require authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

### Option 2: Disable Default Config

Disable the default configuration and create your own:

```properties
security.jwt.enabled=false
```

Then create your custom security configuration.

## Using JWT Utilities

### Generate Token (typically in AuthService)

```java
@Service
public class AuthService {
    
    private final JwtUtil jwtUtil;
    
    public String login(String username, String password) {
        // Validate credentials...
        
        // Generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        
        return jwtUtil.generateToken(username, claims);
    }
}
```

### Validate Token (automatic via filter)

The `JwtAuthenticationFilter` automatically:
- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token signature and expiration
- Sets Spring Security context if valid

### Access Current User

```java
@RestController
public class SecuredController {
    
    @GetMapping("/profile")
    public String getProfile(Authentication authentication) {
        String username = authentication.getName();
        return "Profile for: " + username;
    }
}
```

## Service-Specific Examples

### Auth Service (Token Generation)
```properties
# Auth service generates tokens
security.jwt.enabled=true
security.jwt.secret=production-secret-key
```

### Gateway Service (Token Validation)
```properties
# Gateway validates all tokens
security.jwt.enabled=true
security.jwt.secret=production-secret-key  # Same secret as auth service
```

### Product Service (Protected Resources)
```properties
# Product service validates tokens for protected endpoints
security.jwt.enabled=true
security.jwt.secret=production-secret-key  # Same secret
```

## Security Best Practices

1. **Use strong secrets**: Generate a cryptographically secure 256-bit key
2. **Environment-specific secrets**: Use different secrets per environment
3. **Secret rotation**: Plan for periodic secret rotation
4. **HTTPS only**: Never send JWT tokens over HTTP
5. **Short expiration**: Use reasonable token expiration times
6. **Refresh tokens**: Implement refresh token mechanism for long-lived sessions

## Testing

### With Authentication Disabled (Default)
```bash
curl http://localhost:8080/products
# Should work without Authorization header
```

### With Authentication Enabled
```bash
# Generate token first
TOKEN=$(curl -X POST http://localhost:8080/auth/login -d '{"username":"user","password":"pass"}' -H "Content-Type: application/json")

# Use token for protected endpoints
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/products
```

## Troubleshooting

### Common Issues

1. **401 Unauthorized**: Token is missing, invalid, or expired
2. **403 Forbidden**: Token is valid but user lacks required permissions
3. **Compilation errors**: Ensure all services use the same common-lib version

### Debug JWT Issues

Enable debug logging:
```properties
logging.level.com.ecommerce.common.security=DEBUG
```

This will log JWT validation attempts and failures.