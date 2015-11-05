'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('statisticService', ['authService', 'restProxyService', 'ChewanOfficialApiEndPoint',
    function (authService, restProxyService, ChewanOfficialApiEndPoint) {

        var userId = authService.getUser().userId;
        var token = authService.getUser().token;

        return {
            getUnRegisterInfo: function (data) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, "/statistic/unRegisterInfo?userId="
                + userId+ "&token=" + token,data);
            }
        }
    }]);