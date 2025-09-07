define(['../accUtils', 'knockout'],
    function (accUtils, ko) {
        function LoginViewModel() {
            var self = this;

            // Observables bound to the inputs
            self.email = ko.observable('');
            self.password = ko.observable('');

            // UI state
            self.isLoading = ko.observable(false);
            self.errorMsg = ko.observable('');

            const BASE = 'http://pher-6mcffb4:8085';

            self.connected = function () {
                // Clear anything leftover when arriving on login
                self.email('');
                self.password('');
                self.errorMsg('');
            };

            self.goToSignup = function () {
                if (window.router)
                    window.router.go({ path: 'signup' });
                else {
                    window.location.hash = '#/signup';
                }
            };

            // Called when user clicks Login
            self.login = async function (data, event) {
                if (event && event.preventDefault)
                    event.preventDefault();
                self.errorMsg('');
                const email = (self.email() || '').trim();
                const pwd = self.password() || '';

                // Basic client-side validation
                if (!email || !pwd) {
                    self.errorMsg('Please enter email and password.');
                    return;
                }

                self.isLoading(true);
                try {
                    // Call your Spring auth endpoint; adjust URL as needed
                    const resp = await fetch(`${BASE}/customer/login`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ emailId: email, password: pwd }),
                        // credentials: 'include' // keep only if server is configured for CORS with credentials
                    });

                    if (!resp.ok) {
                        let message = 'Login failed.';
                        try {
                            const err = await resp.json();
                            message = err.message || message;
                        } catch (e) { /* ignore JSON parse errors */ }
                        throw new Error(message);
                    }
                    else {
                        // Expected response shape: { message: "Login successful!" } (no token)
                        const data = await resp.json(); // Expect: { message, role: 'admin' | 'customer' }

                        // Store minimal login state
                        localStorage.setItem('userEmail', email);
                        localStorage.setItem('loggedIn', 'true');

                        // IMPORTANT: set role from server response, not hard-coded
                        if (data.role === 'admin') {
                            localStorage.setItem('role', 'admin');
                            //alert loin success
                            alert("Logged in as admin");
                        } else {
                            // Fallback if backend didn't include role
                            localStorage.setItem('role', 'customer');
                            //alert loin success
                            alert("Login Successfull");
                        }

                    }


                    // Fetch customer details by email to get customerId, then store it
                    var user_role = localStorage.getItem('role');
                    if (user_role === 'customer') {
                        try {
                            const custResp = await fetch(
                                `${BASE}/customer/${encodeURIComponent(email)}`,
                                {
                                    method: 'GET',
                                    headers: { 'Content-Type': 'application/json' }
                                }
                            );

                            if (custResp.ok) {
                                const customer = await custResp.json();
                                // localStorage.setItem('customer', JSON.stringify(customer));
                                if (customer && customer.customerId != null) {
                                    localStorage.setItem('customerId', String(customer.customerId));
                                }
                            } else {
                                // If this fails, dashboard can try again; show a non-blocking warning if needed
                                console.warn('Could not fetch customer by email:', custResp.status);
                            }
                        } catch (e) {
                            console.warn('Lookup by email failed:', e);
                        }
                    }

                    // Show toast and update shell
                    window.dispatchEvent(new CustomEvent('toast', {
                        detail: { severity: 'confirmation', summary: 'Login successful' }
                    }));

                    window.dispatchEvent(new CustomEvent('auth-change'));
                    const role = localStorage.getItem('role');
                    window.router?.go({ path: role === 'admin' ? 'inventory' : 'dashboard' }); // navigate

                } catch (err) {
                    self.errorMsg(err.message || 'Unexpected error. Please try again.');
                } finally {
                    self.isLoading(false);
                }
            };
        }

        return new LoginViewModel();

    });