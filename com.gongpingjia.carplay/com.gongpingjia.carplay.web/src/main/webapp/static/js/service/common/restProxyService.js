'use strict';

/**
 * AuthController
 *
 * @constructor
 */

gpjApp.factory('restProxyService', ['$http', function ($http) {

    return {
        /**
         * Send http get request
         */
        sendHttpGet: function (apiPrefix, path, params) {
            return $http.get(apiPrefix + encodeURI(path), {params: params});
        },

        /**
         * Send http post request
         */
        sendHttpPost: function (apiPrefix, path, data) {
            return $http.post(apiPrefix + path, data);
        }
    }
}]);