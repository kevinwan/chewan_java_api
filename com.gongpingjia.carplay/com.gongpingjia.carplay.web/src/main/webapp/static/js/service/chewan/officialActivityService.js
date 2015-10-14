'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('officialActivityService', ['$http','restProxyService', 'authService', 'ChewanOfficialApiEndPoint',
    function ($http,restProxyService, authService, ChewanOfficialApiEndPoint) {
        var USER_ID = authService.getUser().userId;
        var USER_TOKEN = authService.getUser().token;
        var officialActivityId = "";
        return {
            setOfficialActivityId: function (id) {
                officialActivityId = id;
            },
            getOfficialActivityId: function () {
                return officialActivityId;
            },
            getOfficialActivityList: function (criteria) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/activity/list?token=' + USER_TOKEN + "&userId=" + USER_ID, criteria);
            },
            saveOfficialActivity: function (data) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/activity/register?token=' + USER_TOKEN + "&userId=" + USER_ID, data);
            },
            getOfficialActivity: function (officialActivityId) {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/official/activity/info?token=' + USER_TOKEN + "&userId=" + USER_ID + "&officialActivityId=" + officialActivityId);
            },
            sendOnFlag: function (officialActivityId) {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/official/activity/onFlag?token=' + USER_TOKEN + "&userId=" + USER_ID + "&officialActivityId=" + officialActivityId);
            },
            updateOfficialActivity: function (data) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/activity/update?token=' + USER_TOKEN + "&userId=" + USER_ID + "&officialActivityId=" + officialActivityId, data);
            },
            uploadFile: function(data) {
                return $http({
                    method: 'POST',
                    url: ChewanOfficialApiEndPoint + '/activity/cover/upload?userId=' + USER_ID
                    + '&token=' + USER_TOKEN,
                    data: data,
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                });
            }
        }
    }]);