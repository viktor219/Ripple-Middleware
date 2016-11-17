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
package org.rippleosi.patient.contacts.rest;

import java.util.List;

import org.rippleosi.common.types.RepoSourceType;
import org.rippleosi.common.types.lookup.RepoSourceLookupFactory;
import org.rippleosi.patient.contacts.model.ContactDetails;
import org.rippleosi.patient.contacts.model.ContactHeadline;
import org.rippleosi.patient.contacts.model.ContactSummary;
import org.rippleosi.patient.contacts.search.ContactSearch;
import org.rippleosi.patient.contacts.search.ContactSearchFactory;
import org.rippleosi.patient.contacts.store.ContactStore;
import org.rippleosi.patient.contacts.store.ContactStoreFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
@RequestMapping("/patients/{patientId}/contacts")
public class ContactsController {

    @Autowired
    private RepoSourceLookupFactory repoSourceLookup;
    
    @Autowired
    private ContactSearchFactory contactSearchFactory;

    @Autowired
    private ContactStoreFactory contactStoreFactory;

    @RequestMapping(method = RequestMethod.GET)
    public List<ContactSummary> findAllContacts(@PathVariable("patientId") String patientId,
                                                @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ContactSearch contactSearch = contactSearchFactory.select(sourceType);

        return contactSearch.findAllContacts(patientId);
    }

    @RequestMapping(value = "/headlines", method = RequestMethod.GET)
    public List<ContactHeadline> findContactHeadlines(@PathVariable("patientId") String patientId,
                                                      @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ContactSearch contactSearch = contactSearchFactory.select(sourceType);

        return contactSearch.findContactHeadlines(patientId);
    }

    @RequestMapping(value = "/{contactId}", method = RequestMethod.GET)
    public ContactDetails findContact(@PathVariable("patientId") String patientId,
                                      @PathVariable("contactId") String contactId,
                                      @RequestParam(required = false) String source) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ContactSearch contactSearch = contactSearchFactory.select(sourceType);

        return contactSearch.findContact(patientId, contactId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createContact(@PathVariable("patientId") String patientId,
                              @RequestParam(required = false) String source,
                              @RequestBody ContactDetails contact) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ContactStore contactStore = contactStoreFactory.select(sourceType);

        contactStore.create(patientId, contact);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateContact(@PathVariable("patientId") String patientId,
                              @RequestParam(required = false) String source,
                              @RequestBody ContactDetails contact) {
        final RepoSourceType sourceType = repoSourceLookup.lookup(source);
        ContactStore contactStore = contactStoreFactory.select(sourceType);

        contactStore.update(patientId, contact);
    }
}
