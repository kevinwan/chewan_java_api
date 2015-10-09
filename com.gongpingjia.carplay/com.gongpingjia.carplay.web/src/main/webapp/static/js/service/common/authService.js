'use strict';

/**
 * AuthController
 *
 * @constructor
 */

gpjApp.factory('authService', ['$window', function ($window) {

    var user;
    return {
        getUser: function () {
            if (!user && !$window.localStorage.user && !$window.sessionStorage.user) {
                return undefined;
            }

            return user ? user : (($window.localStorage.user && $window.localStorage.user !== 'undefined') ?
                JSON.parse($window.localStorage.user) : JSON.parse($window.sessionStorage.user));
        },
        setUser: function (aUser) {
            user = aUser;
            if (!user) {
                $window.localStorage.user = undefined;
                $window.sessionStorage.user = undefined;
            } else {
                if (aUser.remember === true)
                    $window.localStorage.user = (user ? JSON.stringify(user) : '');
                else
                    $window.sessionStorage.user = (user ? JSON.stringify(user) : '');
            }
        }
    }
}]);