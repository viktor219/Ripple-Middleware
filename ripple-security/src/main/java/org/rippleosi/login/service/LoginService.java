package org.rippleosi.login.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.profile.OidcProfile;
import org.rippleosi.users.model.UserPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    public void saveUserProfile(final WebContext context) {
        final String rawIDToken = context.getRequestParameter("id_token");
        final String rawAccessToken = context.getRequestParameter("access_token");

        final Jwt idToken = JwtHelper.decode(rawIDToken);
        final Jwt accessToken = JwtHelper.decode(rawAccessToken);

        final OidcProfile profile = generateOidcProfile(context, rawAccessToken);
        profile.setIdTokenString(rawIDToken);

        final String idClaims = idToken.getClaims();
        final String accessClaims = accessToken.getClaims();

        try {
            final JWTClaimsSet idClaimsSet = JWTClaimsSet.parse(idClaims);

            final UserInfo userInfo = new UserInfo(idClaimsSet);
            profile.addAttributes(userInfo.toJWTClaimsSet().getClaims());

            final JWTClaimsSet accessClaimsSet = JWTClaimsSet.parse(accessClaims);
            final String role = accessClaimsSet.getStringClaim("role");
            profile.addRole(role);
            profile.addAttribute("tenant", accessClaimsSet.getClaim("tenant"));
            profile.addAttribute("nhs_number", accessClaimsSet.getClaim("nhs_number"));

            final UserPermissions userPermissions = new UserPermissions(role);
            profile.addPermissions(userPermissions.loadUserPermissions());

            LOGGER.debug("profile: {}", profile);
            saveToProfileManager(context, profile);
        }
        catch (ParseException pe) {
            LOGGER.error("Could not parse id claims raw json", pe);
        }
        catch (com.nimbusds.oauth2.sdk.ParseException pe) {
            LOGGER.error("Could not parse id claims set into attribute map", pe);
        }
    }

    private OidcProfile generateOidcProfile(final WebContext context, final String rawAccessToken) {
        final String tokenScope = context.getRequestParameter("scope");
        final String tokenExpiry = context.getRequestParameter("expires_in");

        final BearerAccessToken bearerAccessToken =
            new BearerAccessToken(rawAccessToken, Long.parseLong(tokenExpiry), Scope.parse(tokenScope));

        return new OidcProfile(bearerAccessToken);
    }

    private void saveToProfileManager(final WebContext context, final UserProfile profile) {
        if (profile != null) {
            final ProfileManager manager = new ProfileManager(context);

            manager.save(true, profile);
        }
    }
}
