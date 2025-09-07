define(['../accUtils', 'knockout'],
    function (accUtils, ko) {
        function InventoryViewModel() {
            var self = this;

            // UI state
            self.isLoading = ko.observable(false);
            self.errorMsg = ko.observable('');
            self.successMsg = ko.observable('');

            // Table rows for the plain HTML table (bound via foreach: rows)
            self.rows = ko.observableArray([]);

            // Role from local storage 
            self.role = ko.observable(localStorage.getItem('role'));

            self.isCustomer = ko.pureComputed(function () {
                return self.role() === 'customer';
            });

            self.isAdmin = ko.pureComputed(function () {
                return self.role() === 'admin';
            });

            // Keep role updated when login/logout happens
            window.addEventListener('auth-change', function () {
                self.role(localStorage.getItem('role'));
            });


            const Stock_Base = 'http://pher-6mcffb4:8091';
            const Transaction_Base = 'http://pher-6mcffb4:8086'
            const STOCKS_URL = `${Stock_Base}/stock`; // GET all stocks
            const TRANSACTIONS_URL = `${Transaction_Base}/transaction`; // POST create transaction

            // Load all stocks
            self.loadStocks = async function () {
                self.errorMsg('');
                self.successMsg('');
                self.isLoading(true);
                try {
                    const resp = await fetch(`${STOCKS_URL}?t=${Date.now()}`, {
                        method: 'GET',
                        headers: { 'Content-Type': 'application/json' }
                    });
                    if (!resp.ok) {
                        const txt = await resp.text().catch(() => '');
                        throw new Error(`Failed to load stocks (${resp.status}) ${txt}`);
                    }

                    // const text = await resp.text();
                    // let data = [];
                    // if (text && text.trim()) {
                    //     const parsed = JSON.parse(text);
                    //     data = Array.isArray(parsed) ? parsed : (parsed ? [parsed] : []);
                    // }

                    // Safe parse and normalize to array
                    let data;
                    try {
                        data = await resp.json(); // expect array, but could be object or empty
                    } catch (e) {
                        data = [];
                    }
                    if (!Array.isArray(data)) {
                        data = data && typeof data === 'object' ? [data] : [];
                    }

                    // Decorate each row with per-row UI observables
                    data.forEach(d => {
                        d._editing = ko.observable(false);
                        d._buyVolume = ko.observable(1);
                        d._isBuying = ko.observable(false);
                        d._error = ko.observable('');
                    });
                    // console.log('stocks data:', data, 'isArray=', Array.isArray(data));
                    self.rows(data);
                } catch (e) {
                    self.rows([]);
                    self.errorMsg(e.message || 'Unable to load stocks.');
                } finally {
                    self.isLoading(false);
                }
            };

            // Dialog state
            self.dialogMode = ko.observable('add'); // 'add', 'update', 'delete'
            self.dialogTitle = ko.computed(function () {
                if (self.dialogMode() === 'add') return "Add New Stock";
                if (self.dialogMode() === 'update') return "Update Stock";
                if (self.dialogMode() === 'delete') return "Delete Stock";
                return "";
            });
            self.dialogError = ko.observable('');
            self.dialogStock = {
                stockId: ko.observable(''),
                stockName: ko.observable(''),
                stockPrice: ko.observable(''),
                stockVolume: ko.observable(''),
                listedPrice: ko.observable(''),
                listedDate: ko.observable(''),
                listedExchange: ko.observable('')
            };

            // Utility to open dialog
            function openDialog(mode, stock) {
                self.dialogMode(mode);
                self.dialogError('');
                if (stock) {
                    // Fill all observables from the stock obj
                    for (const k in self.dialogStock) self.dialogStock[k](stock[k]);
                } else {
                    for (const k in self.dialogStock) self.dialogStock[k]('');
                }
                document.getElementById('adminCrudDialog').style.display = 'block';
            }
            self.closeDialog = function () {
                document.getElementById('adminCrudDialog').style.display = 'none';
            };

            // Show dialogs for each operation
            self.showAddDialog = function () {
                openDialog('add');
            };
            self.showUpdateDialog = async function () {
                const id = prompt('Enter Stock ID to update:');
                if (!id) return;
                try {
                    const resp = await fetch(`${STOCKS_URL}/${id}`, { headers: { 'Content-Type': 'application/json' } });
                    if (!resp.ok) throw new Error('Stock Not found');
                    const stock = await resp.json();
                    openDialog('update', stock);
                } catch (e) {
                    alert(e.message || 'Stock  found');
                }
            };
            self.showDeleteDialog = function () {
                const id = prompt('Enter Stock ID to delete:');
                if (!id) return;
                openDialog('delete', { stockId: id });
            };

            // Handle dialog submission for all modes
            self.submitDialog = async function (_, evt) {
                if (evt) evt.preventDefault();
                const mode = self.dialogMode();
                const stockId = self.dialogStock.stockId();
                let payload = {};
                Object.keys(self.dialogStock).forEach(k => {
                    let v = self.dialogStock[k]();
                    if (k === "stockId") return; // do not send stockId in PATCH/PUT body
                    if (v !== "" && v != null) payload[k] = v; // send only non-empty fields
                });

                try {
                    if (mode === 'add') {
                        // Add always needs all fields
                        if (!stockId || !payload.stockName) {
                            self.dialogError('All the fields are required.');
                            return;
                        }
                        const resp = await fetch(STOCKS_URL, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ stockId, ...payload })
                        });
                        if (!resp.ok)
                            throw new Error(await resp.text());
                        alert('Stock added successfully!');
                        self.closeDialog();
                        self.loadStocks();
                    } else if (mode === 'update') {
                        if (!stockId) {
                            self.dialogError('Enter the required fields.');
                            return;
                        }
                        // If fewer than all fields are filled, use PATCH (partial update)
                        const filledFields = Object.keys(payload).length;
                        const totalFields = 6; // stockName, stockPrice, stockVolume, listedPrice, listedDate, listedExchange
                        let method = filledFields < totalFields ? 'PATCH' : 'PUT';
                        const resp = await fetch(`${STOCKS_URL}/${stockId}`, {
                            method,
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(payload)
                        });
                        if (!resp.ok)
                            throw new Error(await resp.text());
                        alert(method === "PATCH" ? 'Stock updated(patched) successfully!' : 'Stock updated successfully!');
                        self.closeDialog();
                        self.loadStocks();
                    } else if (mode === 'delete') {
                        const resp = await fetch(`${STOCKS_URL}/${stockId}`, {
                            method: 'DELETE',
                            headers: { 'Content-Type': 'application/json' }
                        });

                        if (!resp.ok) {
                            const txt = await resp.text().catch(() => '');
                            if (resp.status === 409) {
                                alert(txt || "Cannot delete: This stock is already owned by a customer.");
                            } else {
                                throw new Error(`Failed to delete stock (${resp.status}) ${txt}`);
                            }
                            return;
                        }
                        alert('Stock deleted successfully');
                        self.closeDialog();
                        self.loadStocks();
                    }
                } catch (e) {
                    self.dialogError(e.message || 'Error processing request');
                }
            };

            self.fetchAllStocks = function () {
                if (!self.isAdmin()) return;
                self.loadStocks();
            };

            // Start inline buy for a row
            self.startBuy = function (row) {
                if (!self.isCustomer())
                    return; // block for admin/guest

                // Close any other open editors
                self.rows().forEach(r => r._editing && r._editing(false));
                row._error('');
                row._buyVolume(1);
                row._editing(true);
                self.successMsg('');
            };

            // Cancel inline buy
            self.cancelBuy = function (row) {
                row._editing(false);
                row._buyVolume(1);
                row._error('');
            };

            // Confirm buy for a row
            self.confirmBuy = async function (row) {
                row._error('');
                const customerId = localStorage.getItem('customerId');
                if (!customerId) {
                    row._error('Customer ID not found. Please log in again.');
                    return;
                }
                const vol = parseInt(row._buyVolume(), 10);
                if (!Number.isInteger(vol) || vol <= 0) {
                    row._error('Enter a valid positive volume.');
                    return;
                }

                const payload = {
                    customerId: Number(customerId),
                    stockId: Number(row.stockId),
                    transactionType: 'BUY',
                    transactionPrice: Number(row.stockPrice), // using current stock price
                    volume: vol
                    // transactionTime: let server set it
                };

                row._isBuying(true);
                try {
                    const resp = await fetch(TRANSACTIONS_URL, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });
                    if (!resp.ok) {
                        const txt = await resp.text().catch(() => '');
                        throw new Error(`Failed to create transaction (${resp.status}) ${txt}`);
                    }

                    // Success
                    self.successMsg(`Bought ${vol} of ${row.stockName} successfully.`);
                    alert(self.successMsg())
                    row._editing(false);
                    row._buyVolume(1);
                } catch (e) {
                    row._error(e.message || 'Unable to complete purchase.');
                    alert(self.errorMsg())
                } finally {
                    row._isBuying(false);
                }
            };

            // Auto-load on view connect
            this.connected = () => {
                const loggedIn = localStorage.getItem('loggedIn') === 'true';
                if (!loggedIn) {
                    if (window.router) window.router.go({ path: 'login' });
                    else {
                        window.history.pushState({}, '', '/login');
                        window.dispatchEvent(new PopStateEvent('popstate'));
                    }
                    return;
                }
                self.loadStocks();
            };

            this.disconnected = () => { };
            this.transitionCompleted = () => { };
        }
        return InventoryViewModel;
    });