'use strict';

/**
 * operation service
 *
 * @constructor
 */

gpjApp.factory('operationService', ['restProxyService', 'authService', 'ChewanApiProvider', 'ChewanApiEndPoint',
    function (restProxyService, authService, ChewanApiProvider, ChewanApiEndPoint) {
        return {
            getNewUserStatistics: function (type) {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/statistics/user/amount?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name + (type ? '&type=' + type : ''));
            },
            getUserLocateStatistics: function () {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/statistics/user/locate?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name);
            }
        };
    }]);