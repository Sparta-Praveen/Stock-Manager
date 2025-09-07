/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your dashboard ViewModel code goes here
 */
define(['../accUtils', 'knockout'],
  function (accUtils, ko) {
    function DashboardViewModel() {

      var self = this;

      self.isLoading = ko.observable(false);
      self.errorMsg = ko.observable('');
      self.rows = ko.observableArray([]);
      self.successMsg = ko.observable('');

      // for the customers havnig no stock holdings
      self.noDataMsg = ko.observable('');
      self.hasNoHoldings = ko.pureComputed(function () {
        return !self.isLoading() && !self.errorMsg() && self.rows().length === 0;
      });

      const CUSTOMER_BASE = 'http://pher-6mcffb4:8085';
      const TRANSACTION_BASE = 'http://pher-6mcffb4:8086';
      const TRANSACTION_URL = `${TRANSACTION_BASE}/transaction`;

      // Get customerId from storage (fallback: fetch by email -> store id)
      async function ensureCustomerId() {
        let cid = localStorage.getItem('customerId');
        if (cid) return cid; // already stored

        const email = localStorage.getItem('userEmail');
        if (!email) throw new Error('Not logged in (email missing).');

        const resp = await fetch(`${CUSTOMER_BASE}/customer/${encodeURIComponent(email)}`, {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' }
        });
        if (!resp.ok) throw new Error('Failed to resolve customer by email');
        const customer = await resp.json();

        if (!customer || customer.customerId == null) {
          throw new Error('Customer id not found for this email.');
        }
        localStorage.setItem('customer', JSON.stringify(customer));
        localStorage.setItem('customerId', String(customer.customerId));
        return String(customer.customerId);
      }

      // Load portfolio using the id
      self.loadPortfolio = async function () {
        self.errorMsg('');
        self.isLoading(true);
        try {
          const customerId = await ensureCustomerId(); // <= get id here

          const CUSTOMER_BASE = 'http://pher-6mcffb4:8085';
          const url = `${CUSTOMER_BASE}/customer/stocksDetails/${customerId}`;
          const resp = await fetch(url, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
          });

          if (!resp.ok) {
            let msg = `Failed to load portfolio (${resp.status})`;
            try {
              const err = await resp.json();
              msg = err.message || msg;
            } catch (_) { }
            throw new Error(msg);
          }

          const data = await resp.json(); // expect array of rows
          if (!Array.isArray(data))
            throw new Error('Unexpected portfolio response.');
          data.forEach(d => {
            d._selling = ko.observable(false);
            d._sellVolume = ko.observable(1);
            d._isSelling = ko.observable(false);
            d._sellError = ko.observable('');
          });
          self.rows(Array.isArray(data) ? data : []);

          if (Array.isArray(data) && data.length === 0) {
            self.noDataMsg('No stock holdings found. Go to Inventory to buy stocks.');
          } else {
            self.noDataMsg('');
          }

        } catch (e) {
          self.rows([]);
          self.errorMsg(e.message || 'Unable to load portfolio.');
        } finally {
          self.isLoading(false);
        }
      };

      // Start inline sell for a row
      self.startSell = function (row) {
        // Close any other open inline editors
        self.rows().forEach(r => r._selling && r._selling(false));
        row._sellError('');
        // default volume = 1 (or prefill with currentVolume)
        row._sellVolume(1);
        row._selling(true);
      };

      // Cancel inline sell
      self.cancelSell = function (row) {
        row._selling(false);
        row._sellVolume(1);
        row._sellError('');
      };

      // Confirm sell for a row
      self.confirmSell = async function (row) {
        row._sellError('');
        const customerId = localStorage.getItem('customerId');
        if (!customerId) {
          row._sellError('Customer ID not found. Please log in again.');
          return;
        }
        const vol = parseInt(row._sellVolume(), 10);
        if (!Number.isInteger(vol) || vol <= 0) {
          row._sellError('Enter a valid positive volume.');
          return;
        }
        if (vol > Number(row.currentVolume)) {
          row._sellError(`Cannot sell more than current volume (${row.currentVolume}).`);
          return;
        }

        const payload = {
          customerId: Number(customerId),
          stockId: Number(row.stockId),
          transactionType: 'SELL',
          transactionPrice: Number(row.currentPrice), // selling at current price shown
          volume: vol
          // transactionTime: let the server set it
        };

        row._isSelling(true);
        try {
          const resp = await fetch(TRANSACTION_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
          });
          if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            throw new Error(`Failed to create SELL transaction (${resp.status}) ${txt}`);
          }

          // Success
          self.successMsg(`Sold ${vol} of ${row.stockName} successfully.`);
          alert(self.successMsg())

          // Update UI quantities locally
          const newVol = Number(row.currentVolume) - vol;
          row.currentVolume = newVol; // primitive on row object
          // Force KO to re-render by replacing the row in the observableArray
          const arr = self.rows();
          const idx = arr.findIndex(r => r.stockId === row.stockId);
          if (idx >= 0) {
            // Recompute currentValue; netInvested update depends on your cost basis rules, so we leave it as-is
            const updated = Object.assign({}, row, {
              currentVolume: newVol,
              currentValue: newVol * Number(row.currentPrice)
            });
            // Preserve the per-row observables
            updated._selling = row._selling;
            updated._sellVolume = row._sellVolume;
            updated._isSelling = row._isSelling;
            updated._sellError = row._sellError;

            arr.splice(idx, 1, updated);
            self.rows(arr);
          }

          // Close inline editor
          row._selling(false);
          row._sellVolume(1);
        } catch (e) {
          row._sellError(e.message || 'Unable to complete SELL.');
        } finally {
          row._isSelling(false);
        }
      };

      self.connected = function () {
        // const loggedIn = localStorage.getItem('loggedIn') === 'true';
        // if (!loggedIn) {
        //   if (window.router)
        //     window.router.go({ path: 'login' });
        //   else {
        //     window.history.pushState({}, '', '/login');
        //     window.dispatchEvent(new PopStateEvent('popstate'));
        //   }
        //   return;
        // }
        // self.loadPortfolio();
        const role = localStorage.getItem('role');
        if (localStorage.getItem('loggedIn') !== 'true' || role !== 'customer') {
          window.router?.go({ path: 'login' });
          return;
        }
        self.loadPortfolio(); // or loadTransactions()
      };

      // Below are a set of the ViewModel methods invoked by the oj-module component.
      // Please reference the oj-module jsDoc for additional information.


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
    }

    /*
     * Returns an instance of the ViewModel providing one instance of the ViewModel. If needed,
     * return a constructor for the ViewModel so that the ViewModel is constructed
     * each time the view is displayed.
     */
    return DashboardViewModel;
  }
);
