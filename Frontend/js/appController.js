/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your application specific code will go here
 */
define(['knockout', 'ojs/ojcontext', 'ojs/ojmodule-element-utils', 'ojs/ojknockouttemplateutils',
  'ojs/ojcorerouter', 'ojs/ojmodulerouter-adapter', 'ojs/ojknockoutrouteradapter',
  'ojs/ojurlparamadapter', 'ojs/ojresponsiveutils', 'ojs/ojresponsiveknockoututils', 'ojs/ojarraydataprovider',
  'ojs/ojdrawerpopup', 'ojs/ojmodule-element', 'ojs/ojknockout', 'ojs/ojdialog', 'ojs/ojbutton'],
  function (ko, Context, moduleUtils, KnockoutTemplateUtils, CoreRouter, ModuleRouterAdapter,
    KnockoutRouterAdapter, UrlParamAdapter, ResponsiveUtils, ResponsiveKnockoutUtils, ArrayDataProvider) {

    function ControllerViewModel() {

      var self = this;
      this.KnockoutTemplateUtils = KnockoutTemplateUtils;

      const CUSTOMER_API_BASE = 'http://pher-6mcffb4:8085';
      // Profile dialog state
      self.profileLoading = ko.observable(false);
      self.profileError = ko.observable('');
      self.profile = {
        customerId: ko.observable(''),
        firstName: ko.observable(''),
        lastName: ko.observable(''),
        emailId: ko.observable(''),
        phoneNumber: ko.observable('')
      };

      // Auth state
      self.loggedIn = ko.observable(localStorage.getItem('loggedIn') === 'true');
      self.role = ko.observable(localStorage.getItem('role')); // 'guest' | 'customer' | 'admin'

      // Show Update Profile only for non-admin users
      self.showProfileButton = ko.pureComputed(function () {
        return self.loggedIn() && self.role() !== 'admin';
      });

      this.messages = ko.observableArray([]);


      // Handle announcements sent when pages change, for Accessibility.
      this.manner = ko.observable('polite');
      this.message = ko.observable();
      announcementHandler = (event) => {
        this.message(event.detail.message);
        this.manner(event.detail.manner);
      };

      document.getElementById('globalBody').addEventListener('announce', announcementHandler, false);


      // Media queries for responsive layouts
      const smQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.SM_ONLY);
      this.smScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(smQuery);
      const mdQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.MD_UP);
      this.mdScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(mdQuery);

      let navData = [
        { path: '', redirect: 'login' },
        { path: 'login', detail: { label: 'Login', iconClass: 'oj-ux-ico-log-in' } },
        { path: 'dashboard', detail: { label: 'Dashboard', iconClass: 'oj-ux-ico-bar-chart' } },
        { path: 'inventory', detail: { label: 'Inventory', iconClass: 'oj-ux-ico-store' } },
        { path: 'customers', detail: { label: 'Customers', iconClass: 'oj-ux-ico-contact-group' } },
        { path: 'transactions', detail: { label: 'Transactions', iconClass: 'oj-ux-ico-accordion' } },
        { path: 'about', detail: { label: 'About', iconClass: 'oj-ux-ico-information-s' } },
        { path: 'signup', detail: { label: 'Sign Up', iconClass: 'oj-ux-ico-add-user' } }
      ];

      // Filter nav items based on auth/role
      function buildNav(loggedIn, role) {
        if (!loggedIn) {
          return [navData.find(r => r.path === 'login')];
        }
        if (role === 'admin') {
          return [
            navData.find(r => r.path === 'inventory'),
            navData.find(r => r.path === 'customers'),
            navData.find(r => r.path === 'about')
          ];
        }
        // default: customer
        return [
          navData.find(r => r.path === 'dashboard'),
          navData.find(r => r.path === 'inventory'),
          navData.find(r => r.path === 'transactions'),
          navData.find(r => r.path === 'about')
        ];
      }
      // Router setup
      let router = new CoreRouter(navData, {
        urlAdapter: new UrlParamAdapter()
      });
      router.sync();
      window.router = router

      this.moduleAdapter = new ModuleRouterAdapter(router);

      this.selection = new KnockoutRouterAdapter(router);

      // Setup the navDataProvider with the routes, excluding the first redirected route.
      // this.navDataProvider = new ArrayDataProvider(navData.slice(1), { keyAttributes: "path" });

      // Nav data provider (observable so we can refresh it on auth change)
      this.navDataProvider = ko.observable(new ArrayDataProvider(buildNav(self.loggedIn(), self.role()), { keyAttributes: 'path' }));

      function refreshNav() {
        const items = buildNav(self.loggedIn(), self.role()).filter(Boolean);
        self.navDataProvider(new ArrayDataProvider(items, { keyAttributes: 'path' }));

        // If current route is not permitted, redirect appropriately
        const current = self.selection.path();
        const allowed = items.map(i => i.path);
        // if (current && !allowed.includes(current)) {
        //   if (!self.loggedIn()) {
        //     router.go({ path: 'login' });
        //   } else if (self.role() === 'admin') {
        //     router.go({ path: 'inventory' });
        //   } else {
        //     router.go({ path: 'dashboard' });
        //   }
        // }
      }

      // Listen to login/logout/role changes broadcast by pages
      window.addEventListener('auth-change', () => {
        self.loggedIn(localStorage.getItem('loggedIn') === 'true');
        self.role(localStorage.getItem('role') || 'guest');
        // Update header label too
        self.userLogin(localStorage.getItem('userEmail') || '');
        refreshNav();
      });

      // Drawer
      self.sideDrawerOn = ko.observable(false);

      // Close drawer on medium and larger screens
      this.mdScreen.subscribe(() => { self.sideDrawerOn(false) });

      // Called by navigation drawer toggle button and after selection of nav drawer item
      this.toggleDrawer = () => {
        self.sideDrawerOn(!self.sideDrawerOn());
      }

      // Header
      // Application Name used in Branding Area
      this.appName = ko.observable("Stock Manager");
      // User Info used in Global Navigation area
      emilForDisplay = localStorage.getItem('userEmail');
      this.userLogin = ko.observable(emilForDisplay);


      // Logout handler
      self.logout = function () {
        try {
          // Optional: backend logout with sessions/cookies
          // await fetch('http:///logout', { method: 'POST', credentials: 'include' });
        } finally {
          // Clear client-side state
          localStorage.clear();
          sessionStorage.clear();

          // Flip observables immediately
          self.loggedIn(false);
          self.role('guest');
          self.userLogin('');

          // Tell the shell to rebuild nav
          window.dispatchEvent(new CustomEvent('auth-change'));


          // Route to login
          if (window.router) {
            window.router.go({ path: 'login' });

          } else {
            window.history.pushState({}, '', '/login');
            window.dispatchEvent(new PopStateEvent('popstate'));
          }
        }
        //alert logout
        alert("Logout Successfull");
      };

      self.openProfileDialog = async () => {
        const dlg = document.getElementById('profileDialog');
        if (!dlg) {
          console.warn('profileDialog not found');
          return;
        }
        self.profileError('');
        self.profileLoading(true);
        dlg.open(); // opens on top without changing the page below

        try {
          const email = localStorage.getItem('userEmail');
          if (!email) throw new Error('No email in session. Please log in again.');
          const resp = await fetch(
            `${CUSTOMER_API_BASE}/customer/${encodeURIComponent(email)}`,
            {
              method: 'GET',
              headers: { 'Content-Type': 'application/json' }
            });
          if (!resp.ok) throw new Error(`Failed to load profile(${resp.status})`);
          const c = await resp.json();
          self.profile.customerId(c?.customerId ?? '');
          self.profile.firstName(c?.firstName ?? '');
          self.profile.lastName(c?.lastName ?? '');
          self.profile.emailId(c?.emailId ?? '');
          self.profile.phoneNumber(c?.phoneNumber ?? '');
        } catch (e) {
          self.profileError(e.message || 'Unable to load profile.');
        } finally {
          self.profileLoading(false);
        }
      };

      self.closeProfileDialog = () => {
        document.getElementById('profileDialog').close();
      };


      // Menu action handler (for oj-menu)
      self.onMenuAction = function (event) {
        const val = event.detail.selectedValue || event.detail.value;
        if (val === 'updateprofile') {
          self.openProfileDialog();
        } else if (val === 'out') {
          self.logout();
        }
        // handle other menu items if needed (e.g., updateprofile)
      };

      refreshNav();

      // Footer
      this.footerLinks = [
      ];
    }
    // release the application bootstrap busy state
    Context.getPageContext().getBusyContext().applicationBootstrapComplete();

    return new ControllerViewModel();
  }
);
