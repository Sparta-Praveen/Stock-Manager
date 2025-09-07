/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your incidents ViewModel code goes here
 */

define(['../accUtils', 'knockout'],
  function (accUtils, ko) {
    function TransactionsViewModel() {
      var self = this;

      self.isLoading = ko.observable(false);
      self.errorMsg = ko.observable('');
      self.rows = ko.observableArray([]);

      // for the customers havnig no stock holdings
      self.noDataMsg = ko.observable('');
      self.hasNoHoldings = ko.pureComputed(function () {
        return !self.isLoading() && !self.errorMsg() && self.rows().length === 0;
      });

      self.formatCurrency = function (v) {
        try { return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v || 0); }
        catch (e) { return v; }
      };
      self.formatDateTime = function (v) {
        if (!v) return '';
        try {
          // For ISO string like "2024-06-10T14:35:28.123"
          const dt = new Date(v);
          if (isNaN(dt)) return String(v);
          // Format as "YYYY-MM-DD HH:mm"
          const y = dt.getFullYear();
          const m = String(dt.getMonth() + 1).padStart(2, '0');
          const d = String(dt.getDate()).padStart(2, '0');
          const hh = String(dt.getHours()).padStart(2, '0');
          const mm = String(dt.getMinutes()).padStart(2, '0');
          return `${y}-${m}-${d} ${hh}:${mm}`;
        } catch (_) {
          return String(v);
        }
      };

      const CUSTOMER_BASE = 'http://pher-6mcffb4:8085';

      self.loadTransactions = async function () {
        self.errorMsg('');
        self.isLoading(true);
        try {
          const customerId = localStorage.getItem('customerId');
          if (!customerId) throw new Error('Customer ID not found. Please log in again.');

          const url = `${CUSTOMER_BASE}/customer/transactions/${encodeURIComponent(customerId)}`;
          const resp = await fetch(url,
            {
              method: 'GET',
              headers: { 'Content-Type': 'application/json' }
            });
          if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            throw new Error(`Failed to load transactions (${resp.status}) ${txt}`);
          }

          // Safely parse JSON (handle empty body)
          let data = null;
          const text = await resp.text();
          if (text && text.trim().length > 0) {
            try {
              data = JSON.parse(text);
            } catch (e) {
              throw new Error('Invalid JSON from server');
            }
          } else {
            data = []; // treat empty as no rows
          }

          // Normalize to array
          if (Array.isArray(data)) {
            self.rows(data);
          } else if (data && typeof data === 'object') {
            self.rows([data]); // single object -> one row
          } else {
            self.rows([]); // anything else -> empty
          }

          if (Array.isArray(data) && data.length === 0) {
            self.noDataMsg('No transactions found. Go to Inventory to buy stocks.');
          } else {
            self.noDataMsg('');
          }

        } catch (e) {
          self.rows([]); // never leave rows undefined
          self.errorMsg(e.message || 'Unable to load transactions.');
        } finally {
          self.isLoading(false);
        }
      };


      this.connected = () => {
        // const loggedIn = localStorage.getItem('loggedIn') === 'true';
        // if (!loggedIn) {
        //   if (window.router) window.router.go({ path: 'login' });
        //   else {
        //     window.history.pushState({}, '', '/login');
        //     window.dispatchEvent(new PopStateEvent('popstate'));
        //   }
        //   return;
        // }
        // self.loadTransactions();
        const role = localStorage.getItem('role');
        if (localStorage.getItem('loggedIn') !== 'true' || role !== 'customer') {
          window.router?.go({ path: 'login' });
          return;
        }
        self.loadTransactions();
      };

      this.disconnected = () => { };
      this.transitionCompleted = () => { };
    }

    return TransactionsViewModel;
  });