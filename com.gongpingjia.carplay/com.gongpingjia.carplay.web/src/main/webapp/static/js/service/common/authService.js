'use strict';

/**
 * AuthController
 *
 * @constructor
 */

gpjApp.factory('authService', ['$window', 'commonService', function ($window, commonService) {

    var user;

    return {
        getUser: function () {
            var retVal = undefined;
            if (user) {
                retVal = user;
            } else if (commonService.isDefined($window.localStorage.user)) {
                retVal = JSON.parse($window.localStorage.user);
            } else if (commonService.isDefined($window.sessionStorage.user)) {
                retVal = JSON.parse($window.sessionStorage.user);
            }
            return retVal;
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
}
])
;