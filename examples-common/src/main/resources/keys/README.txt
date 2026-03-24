This directory holds the RSA public key used for local JWT validation.

The public key is safe to commit. The private key must never be committed.
Add **/keys/private.pem to .gitignore (already done in the repo root).

Generate the key pair with:

  openssl genrsa -out private.pem 2048
  openssl rsa -in private.pem -pubout -out public.pem

Keep private.pem in this directory locally for token generation.
public.pem is read by Spring Security at startup via:
  spring.security.oauth2.resourceserver.jwt.public-key-location=classpath:keys/public.pem

For production replace public-key-location with issuer-uri or jwk-set-uri
pointing at your real IdP in application-secured.yml.
