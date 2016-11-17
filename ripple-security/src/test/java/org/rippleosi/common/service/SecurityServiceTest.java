package org.rippleosi.common.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SecurityServiceTest {

    private SecurityService securityService;

    @Before
    public void setUp() {
        securityService = new SecurityService();
    }

    @Test
    public void shouldNotGenerateLocationHeaderIfRedirectUrlIsMalformed() {
        final String invalidUrl = "Malformed" + "\n" + "URL";

        final ResponseEntity<String> responseEntity =
            securityService.generateRedirectResponseEntity(invalidUrl, null, HttpStatus.I_AM_A_TEAPOT);

        assertNull("The redirect location header must not be set if invalid.", responseEntity.getHeaders().getLocation());
    }

    @Test
    public void shouldExpandRedirectUrlIfParamsIncluded() {
        final String expectedUrl = "http://www.google.co.uk/search?param1=test&param2=test";

        final String inputUrlBasePath = "http://www.google.co.uk/search";

        final Map<String, String> params = new HashMap<>();
        params.put("param1", "test");
        params.put("param2", "test");

        final ResponseEntity<String> responseEntity =
            securityService.generateRedirectResponseEntity(inputUrlBasePath, params, HttpStatus.I_AM_A_TEAPOT);

        final URI outputUrl = responseEntity.getHeaders().getLocation();

        assertNotNull("The redirect location URL must not be null using valid inputs.", outputUrl);
        assertEquals("The redirect location URL must be fully expanded using the given params.", expectedUrl, outputUrl.toString());
    }
}
