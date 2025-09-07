/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your customer ViewModel code goes here
 */
define(['../accUtils', 'knockout'],
  function (accUtils, ko) {
    function CustomerViewModel() {

      var self = this;

      self.isLoading = ko.observable(false);
      self.errorMsg = ko.observable('');
      self.rows = ko.observableArray([]); // [{ customerId, firstName, lastName, phoneNumber, emailId }, ...]

      // const BASE = 'http://10.120.159.41:8085'; // adjust if different
      const BASE = 'http://pher-6mcffb4:8085';
      const ENDPOINT = `${BASE}/customers`; // adjust if your API is /customers or /customer/all

      self.loadCustomers = async function () {
        self.errorMsg('');
        self.isLoading(true);
        try {
          // avoid cached 304 responses during dev
          const url = `${ENDPOINT}?t=${Date.now()}`;
          const resp = await fetch(url, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
            }
          });

          if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            throw new Error(`Failed to load customers (${resp.status}) ${txt}`);
          }

          const text = await resp.text();
          let data = [];
          if (text && text.trim()) {
            const parsed = JSON.parse(text);
            data = Array.isArray(parsed) ? parsed : (parsed ? [parsed] : []);
          }
          // Expecting fields: customerId, firstName, lastName, phoneNumber, emailId
          self.rows(data);
        } catch (e) {
          self.rows([]);
          self.errorMsg(e.message || 'Unable to load customers.');
        } finally {
          self.isLoading(false);
        }
      };

      this.connected = () => {
        const role = localStorage.getItem('role');
        if (localStorage.getItem('loggedIn') !== 'true' || role !== 'admin') {
          window.router?.go({ path: 'login' });
          return;
        }
        self.loadCustomers();
      };

    }

    /**
     * Optional ViewModel method invoked after the View is disconnected from the DOM.
     */
    this.disconnected = () => {
      // Implement if needed
    };

    /**
     * Optional ViewModel method invoked after transition to the new View is complete.
     * That includes any possible animation between the old and the new View.
     */
    this.transitionCompleted = () => {
      // Implement if needed
    };

    /*
     * Returns an instance of the ViewModel providing one instance of the ViewModel. If needed,
     * return a constructor for the ViewModel so that the ViewModel is constructed
     * each time the view is displayed.
     */
    return CustomerViewModel;
  }
);
