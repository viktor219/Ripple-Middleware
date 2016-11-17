package org.rippleosi.login.service;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LoginServiceTest {

    private static final String ID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDOD" +
        "QtOEI1RS01NUZCN0VFMDI3Q0UiLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiU21pdGgiLCJlbWFpbCI6ImpvaG4uc21" +
        "pdGhAbmhzLmdvdi51ayIsImVtYWlsX3ZlcmlmaWVkIjoidHJ1ZSIsImV4cCI6MTQ2MzY3MzIzM30.RqlR3KFgxTgERllenBUyZlpMYd3w" +
        "iMI4EfBjHQqBNio";

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfaWQiOiJUZXN0LUNsa" +
        "WVudCIsInNjb3BlIjpbInRlc3RTY29wZSJdLCJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01NUZCN0VFMDI3Q0UiLCJ0ZW5hbn" +
        "QiOiJUZXN0LVRlbmFudCIsInJvbGUiOiJQSFIiLCJuaHNfbnVtYmVyIjoiOTk5OTk5OTAwMCIsImV4cCI6MTQ2MzY3NjUzM30.DyqEi4b" +
        "8fNLR-HouJvGA0YHn86FxNx4j6sfmH7_N2Ko";

    private LoginService loginService;

    @Before
    public void setUp() {
        loginService = new LoginService();
    }

    private J2EContext setUpWebContext() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.setParameter("access_token", ACCESS_TOKEN);
        request.setParameter("id_token", ID_TOKEN);
        request.setParameter("scope", "test");
        request.setParameter("expires_in", "3600");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        return new J2EContext(request, response);
    }

    @Test
    public void userProfileMustBeSavedWithValidTokenInputs() {
        final WebContext context = setUpWebContext();

        loginService.saveUserProfile(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        assertNotNull("The user profile wasn't saved whilst setting up the security context.", userProfile);
    }

    @Test
    public void userIdAttributesMustBeSavedWithValidTokenInputs() {
        final WebContext context = setUpWebContext();

        loginService.saveUserProfile(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final Map<String, Object> attributes = userProfile.getAttributes();

        assertNotNull("The user's ID attributes weren't saved whilst setting up the security context.", attributes);
        assertTrue("The user's ID attributes weren't saved whilst setting up the security context.", attributes.size() > 0);
    }

    @Test
    public void userRoleMustBeSavedWithValidTokenInputs() {
        final WebContext context = setUpWebContext();

        loginService.saveUserProfile(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final List<String> roles = userProfile.getRoles();

        assertNotNull("The user role wasn't saved whilst setting up the security context.", roles);
        assertEquals("The user role saved doesn't correspond to the input value.", "PHR", roles.get(0));
    }

    @Test
    public void userProfileMustContainThePermissionsOfTheRole() {
        final WebContext context = setUpWebContext();

        loginService.saveUserProfile(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final List<String> permissions = userProfile.getPermissions();

        assertNotNull("The user's permissions have not been saved in the user profile.", permissions);
    }
}
