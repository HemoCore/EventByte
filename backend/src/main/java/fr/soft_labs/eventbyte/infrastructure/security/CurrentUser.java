package fr.soft_labs.eventbyte.infrastructure.security;

import fr.soft_labs.eventbyte.domain.models.user.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public UserId get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return new UserId(jwtAuthentication.getToken().getSubject());
        }
        throw new IllegalStateException("No authenticated user for this request");
    }
}