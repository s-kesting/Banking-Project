# Vue router
found in router/index.js

matches a url to a vue component, opens that component in the <router-view/> in App.view

## format:
routes = [
    {
        path: '/login', --route url
        name: 'Login',
        component: Authentication, --the component that will be loaded when going to that url
        meta: { requiresGuest: true } --extra data related to that page
                                      -- currently we have 2             
                                            requiresAuth: true, --if you need to be logged in for this page
                                            requiresRole: "CUSTOMER" --what role you must be to access this page
    },
]
