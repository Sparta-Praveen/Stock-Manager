define(['../accUtils', 'knockout'],
    function (accUtils,ko) {
        function SignupViewModel() {
            var self = this;

            // Form fields
            self.firstName = ko.observable('');
            self.lastName = ko.observable('');
            self.emailId = ko.observable('');
            self.phoneNumber = ko.observable('');
            self.password = ko.observable('');
            self.confirmPassword = ko.observable('');

            // UI state
            self.isLoading = ko.observable(false);
            self.errorMsg = ko.observable('');
            self.successMsg = ko.observable('');

            const BASE = 'http://pher-6mcffb4:8085'; 

            self.goToLogin = function () {
                if (window.router) window.router.go({ path: 'login' });
                else { window.location.hash = '#/login'; }
            };

            self.signup = async function (data, event) {
                if (event && event.preventDefault) event.preventDefault();
                self.errorMsg('');
                const payload = {
                    firstName: (self.firstName() || '').trim(),
                    lastName: (self.lastName() || '').trim(),
                    emailId: (self.emailId() || '').trim(),
                    phoneNumber: (self.phoneNumber() || '').trim(),
                    password: self.password() || ''
                };
                const cpw = self.confirmPassword() || '';

                // basic validation
                if (!payload.firstName || !payload.lastName || !payload.emailId || !payload.phoneNumber || !payload.password || !cpw) {
                    self.errorMsg('All fields are required.');
                    return;
                }
                if (payload.password !== cpw) {
                    self.errorMsg('Passwords do not match.');
                    return;
                }

                self.isLoading(true);
                try {
                    // Call your controller: POST /customer
                    const resp = await fetch(`${BASE}/customer`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });

                    const text = await resp.text().catch(() => '');
                    if (!resp.ok) {
                        throw new Error(text || `Signup failed (${resp.status})`);
                    }

                    // Success (controller returns plain text)
                    self.successMsg(text || 'Customer Added Successfully');
                    alert("Customer added successfully");
                    // Navigate to login after a short delay
                    setTimeout(self.goToLogin, 800);
                } catch (e) {
                    self.errorMsg(e.message || 'Unable to create account.');
                } finally {
                    self.isLoading(false);
                }
            };
        }
        return SignupViewModel;
    });