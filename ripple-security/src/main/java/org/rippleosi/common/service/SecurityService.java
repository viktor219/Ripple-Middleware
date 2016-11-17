package org.rippleosi.common.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    public ResponseEntity<String> generateRedirectResponseEntity(final String defaultUrl, final Map<String, String> params,
                                                                 final HttpStatus httpStatus) {
        CommonHelper.assertNotNull("defaultUrl", defaultUrl);
        CommonHelper.assertNotBlank("defaultUrl", defaultUrl);

        final String redirectUrl = getExpandedUrl(defaultUrl, params);
        final HttpHeaders httpHeaders = new HttpHeaders();

        try {
            final URI redirectPage = new URI(redirectUrl);
            httpHeaders.setLocation(redirectPage);
        }
        catch (final URISyntaxException e) {
            LOGGER.warn("The security service has failed to redirect to the requested URL.");
            LOGGER.debug("The security service has failed to redirect to the requested URL.", e);
        }

        return new ResponseEntity<>(httpHeaders, httpStatus);
    }

    private String getExpandedUrl(final String defaultUrl, final Map<String, String> params) {
        if (params == null) {
            return defaultUrl;
        }

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(defaultUrl);

        for (final Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return builder.toUriString();
    }
}
