# Secrets Management Guide

This document outlines how to manage secrets and sensitive configuration for the e-commerce platform.

## Environment Variables Overview

### Database Secrets
- `AUTH_DB_PASSWORD` - Authentication service database password
- `PRODUCT_DB_PASSWORD` - Product service database password  
- `ORDER_DB_PASSWORD` - Order service database password
- `PAYMENT_DB_PASSWORD` - Payment service database password

### JWT Security
- `JWT_SECRET` - Secret key for JWT token signing and validation (256-bit minimum)
- `JWT_EXPIRATION` - Token expiration time in seconds

### External API Keys (when integrated)
- `STRIPE_SECRET_KEY` - Payment processing (Stripe)
- `SENDGRID_API_KEY` - Email service
- `AWS_SECRET_ACCESS_KEY` - Cloud services

## Local Development Setup

### 1. Copy Environment Template
```bash
cp .env.example .env
```

### 2. Update Local Values
Edit `.env` with your local development values:
```bash
# Database passwords - use simple values for local dev
AUTH_DB_PASSWORD=dev_password
PRODUCT_DB_PASSWORD=dev_password
ORDER_DB_PASSWORD=dev_password
PAYMENT_DB_PASSWORD=dev_password

# JWT secret - generate a new one for security
JWT_SECRET=your-local-development-secret-key-change-this
JWT_EXPIRATION=86400

# Spring profiles
SPRING_PROFILES_ACTIVE=dev
```

### 3. Generate Secure JWT Secret
```bash
# Generate a secure 256-bit secret
openssl rand -base64 32
```

## Server Deployment

### Production Environment Variables

#### Method 1: Environment Variables (Recommended)
Set these environment variables on your production server:

```bash
export AUTH_DB_PASSWORD="$(cat /etc/secrets/auth_db_password)"
export PRODUCT_DB_PASSWORD="$(cat /etc/secrets/product_db_password)"
export ORDER_DB_PASSWORD="$(cat /etc/secrets/order_db_password)"
export PAYMENT_DB_PASSWORD="$(cat /etc/secrets/payment_db_password)"
export JWT_SECRET="$(cat /etc/secrets/jwt_secret)"
export JWT_EXPIRATION=3600
export SPRING_PROFILES_ACTIVE=prod
```

#### Method 2: Docker Secrets (For Docker Swarm)
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  auth-service:
    image: ecommerce/auth-service
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    secrets:
      - auth_db_password
      - jwt_secret
    environment:
      - AUTH_DB_PASSWORD_FILE=/run/secrets/auth_db_password
      - JWT_SECRET_FILE=/run/secrets/jwt_secret

secrets:
  auth_db_password:
    external: true
  jwt_secret:
    external: true
```

#### Method 3: Kubernetes Secrets
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: ecommerce-secrets
type: Opaque
data:
  auth_db_password: <base64-encoded-password>
  jwt_secret: <base64-encoded-secret>
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  template:
    spec:
      containers:
      - name: auth-service
        env:
        - name: AUTH_DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: ecommerce-secrets
              key: auth_db_password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: ecommerce-secrets
              key: jwt_secret
```

## Spring Profiles Configuration

### Development Profile (`dev`)
- Relaxed security settings
- Debug logging enabled
- Local database connections
- Non-production JWT secrets

### Staging Profile (`staging`)
- Production-like security
- Limited debug logging
- Staging database connections
- Staging-specific secrets

### Production Profile (`prod`)
- Maximum security settings
- Minimal logging
- Production database connections
- Production-grade secrets

### Profile-Specific Properties

Create `application-{profile}.properties` files for environment-specific configurations:

#### `application-dev.properties`
```properties
# Development database with relaxed settings
spring.jpa.show-sql=true
logging.level.org.springframework.security=DEBUG
security.jwt.enabled=true
```

#### `application-prod.properties`
```properties
# Production optimizations
spring.jpa.show-sql=false
logging.level.root=WARN
logging.level.com.ecommerce=INFO
security.jwt.enabled=true
```

## Secret Generation Guidelines

### Database Passwords
```bash
# Generate 32-character password
openssl rand -base64 32 | cut -c1-32
```

### JWT Secrets
```bash
# Generate 256-bit base64-encoded secret
openssl rand -base64 32
```

### API Keys
- Use official key generation tools from service providers
- Stripe: Use dashboard-generated keys
- AWS: Use IAM-generated access keys

## Security Best Practices

### 1. Never Commit Secrets
- `.env` files are gitignored
- Use `.env.example` for templates only
- Never hardcode secrets in source code

### 2. Use Different Secrets Per Environment
- Development: Simple, non-sensitive values
- Staging: Environment-specific secrets
- Production: Strong, unique secrets

### 3. Rotate Secrets Regularly
- Database passwords: Every 90 days
- JWT secrets: Every 30 days (coordinate with all services)
- API keys: Follow provider recommendations

### 4. Principle of Least Privilege
- Each service only gets secrets it needs
- Use separate database users per service
- Limit API key permissions

### 5. Monitor Secret Usage
- Log authentication failures
- Monitor unusual access patterns
- Set up alerts for secret rotation

## Troubleshooting

### Common Issues

#### "Invalid JWT signature"
- Ensure all services use the same JWT_SECRET
- Check for typos in environment variables
- Verify secret is properly base64 encoded

#### Database connection failures
- Verify database passwords are correct
- Check network connectivity
- Ensure database users exist and have proper permissions

#### Environment variable not found
- Check variable name spelling
- Verify the variable is exported in shell
- For Docker, ensure environment section is correctly configured

### Debug Commands

#### Verify environment variables are loaded
```bash
# In running container
printenv | grep -E "(JWT_|DB_|SPRING_)"
```

#### Test JWT secret
```bash
# Generate test token with current secret
curl -X POST localhost:8080/auth/test-token
```

#### Check Spring profiles
```bash
# View active profiles in logs
grep "active profiles" application.log
```

## Emergency Procedures

### Secret Compromise Response
1. **Immediate**: Rotate compromised secret
2. **Update**: All services using the secret
3. **Monitor**: For unauthorized access
4. **Document**: Incident and response

### Service Recovery
1. **Backup**: Current configurations
2. **Replace**: Compromised secrets
3. **Restart**: Affected services
4. **Verify**: All services are functioning

### Contact Information
- DevOps Team: devops@company.com
- Security Team: security@company.com
- On-call Engineer: +1-xxx-xxx-xxxx

## Automated Secret Management (Future)

### HashiCorp Vault Integration
```properties
# Vault configuration
spring.cloud.vault.enabled=true
spring.cloud.vault.host=vault.company.com
spring.cloud.vault.port=8200
spring.cloud.vault.scheme=https
spring.cloud.vault.kv.enabled=true
spring.cloud.vault.kv.backend=secret
spring.cloud.vault.kv.profile-separator=/
```

### AWS Secrets Manager
```yaml
# AWS Secrets Manager configuration
aws:
  region: us-east-1
  secretsmanager:
    secrets:
      - name: ecommerce/database/auth
        key: password
        property: spring.datasource.password
```

This document should be updated as new secrets are added or deployment methods change.