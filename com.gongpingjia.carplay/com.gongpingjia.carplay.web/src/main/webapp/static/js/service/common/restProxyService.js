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
            $http.headers = {"Accept": "application/json; charset=UTF-8", "Content-Type": "application/json"};
            return $http.post(apiPrefix + path, data);
        },

        sendFormData: function(apiPrefix,path,data) {
            $http.headers = {"Content-Type": "multipart/form-data"};
            return $http.post(apiPrefix + path, data);
        }
    }
}]);