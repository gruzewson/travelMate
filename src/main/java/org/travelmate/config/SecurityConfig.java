package org.travelmate.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@ApplicationScoped
@FormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage = "/login.xhtml",
        errorPage = "/login.xhtml?error=true",
        useForwardToLoginExpression = "${false}"
    )
)
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "jdbc/TravelMateDS",
    callerQuery = "SELECT password FROM users WHERE login = ?",
    groupsQuery = "SELECT role FROM users WHERE login = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class,
    hashAlgorithmParameters = {
        "Pbkdf2PasswordHash.Iterations=210000",
        "Pbkdf2PasswordHash.Algorithm=PBKDF2WithHmacSHA256",
        "Pbkdf2PasswordHash.SaltSizeBytes=32"
    }
)
public class SecurityConfig {
}

