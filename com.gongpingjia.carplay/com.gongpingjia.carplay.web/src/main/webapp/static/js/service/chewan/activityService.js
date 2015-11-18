'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('activityService', ['$http', 'restProxyService', 'authService', 'ChewanOfficialApiEndPoint',
    function ($http, restProxyService, authService, ChewanOfficialApiEndPoint) {
        var USER_ID = authService.getUser().userId;
        var USER_TOKEN = authService.getUser().token;
        return {
            saveActivity:function(data){
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/userActivity/register?token=' + USER_TOKEN + "&userId=" + USER_ID, data);
            },
            getActivityList: function (criteria) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/userActivity/list?token=' + USER_TOKEN + "&userId=" + USER_ID, criteria);
            },
            updateActivity: function (data, activityId) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/userActivity/update?' + 'token=' + USER_TOKEN + "&userId=" + USER_ID + "&activityId=" + activityId, data);
            },
            deleteActivities: function (ids) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/userActivity/deleteIds?token=' + USER_TOKEN + "&userId=" + USER_ID, ids);
            },
            viewActivity: function (id) {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/official/userActivity/view?token=' + USER_TOKEN + "&userId=" + USER_ID + "&activityId=" + id);
            },
            getAreaInfo:function(areCode) {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/area/list?parentId=' + areCode);
            },
            getUserInfo:function(phone){
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/official/userInfo/phone?token=' + USER_TOKEN + "&userId=" + USER_ID + "&phone=" + phone);
            }
        }
    }]);