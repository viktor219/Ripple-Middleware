/*
 * Copyright 2015 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.common.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.rippleosi.common.types.lookup.RepoSource;

/**
 */
public class RepoSourceTypeTest {

    //
    @Test
    public void validateTSESize() {
        assertEquals(9, RepoSourceTypes.values().length);
    }

    @Test
    public void verifyNONESourceName() {
        final String repoSourceName = RepoSourceTypes.NONE.getSourceName();
        assertEquals("Not Configured", repoSourceName);
    }

    @Test
    public void verifyACTIVEMQSourceName() {
        final String repoSourceName = RepoSourceTypes.ACTIVEMQ.getSourceName();
        assertEquals("ActiveMQ", repoSourceName);
    }

    @Test
    public void verifyLEGACYSourceName() {
        final String repoSourceName = RepoSourceTypes.LEGACY.getSourceName();
        assertEquals("Legacy", repoSourceName);
    }

    @Test
    public void verifyMARANDSourceName() {
        final String repoSourceName = RepoSourceTypes.MARAND.getSourceName();
        assertEquals("Marand", repoSourceName);
    }

    @Test
    public void verifyETHERCISSourceName() {
        final String repoSourceName = RepoSourceTypes.ETHERCIS.getSourceName();
        assertEquals("EtherCIS", repoSourceName);
    }

    @Test
    public void verifyORTHANCSourceName() {
        final String repoSourceName = RepoSourceTypes.ORTHANC.getSourceName();
        assertEquals("Orthanc", repoSourceName);
    }

    @Test
    public void verifyVISTASourceName() {
        final String repoSourceName = RepoSourceTypes.VISTA.getSourceName();
        assertEquals("Vista", repoSourceName);
    }

    @Test
    public void verifyAUDITSourceName() {
        final String repoSourceName = RepoSourceTypes.AUDIT.getSourceName();
        assertEquals("Audit", repoSourceName);
    }

    @Test
    public void verifyTERMINOLOGYSourceName() {
        final String repoSourceName = RepoSourceTypes.TERMINOLOGY.getSourceName();
        assertEquals("Terminology", repoSourceName);
    }

    // Reverse Lookup Tests
    @Test
    public void reverseLookupOfTSEFromEmptyString() {
        final String tseAsString = "";
        RepoSourceType sourceType = RepoSourceTypes.fromString(tseAsString);
        assertNull(sourceType);
    }

    @Test
    public void reverseLookupOfTSEFromNull() {
        RepoSourceType sourceType = RepoSourceTypes.fromString(null);
        assertNull(sourceType);
    }

    @Test
    public void reverseLookupOfTSEFromCaseSensitivePopulatedString() {
        final String tseAsString = "EtherCIS";
        RepoSourceType sourceType = RepoSourceTypes.fromString(tseAsString);
        assertEquals(RepoSourceTypes.ETHERCIS, sourceType);
    }

    @Test
    public void reverseLookupOfTSEFromLowercasePopulatedString() {
        final String tseAsString = "ethercis";
        RepoSourceType sourceType = RepoSourceTypes.fromString(tseAsString);
        assertEquals(RepoSourceTypes.ETHERCIS, sourceType);
    }

    @Test
    public void reverseLookupOfNONE() {
        final String tseAsString = "Not Configured";
        RepoSourceType sourceType = RepoSourceTypes.fromString(tseAsString);
        assertEquals(RepoSourceTypes.NONE, sourceType);
    }

}
