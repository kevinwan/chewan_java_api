'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('authenticationService', ['restProxyService', 'authService', 'ChewanApiProvider', 'ChewanApiEndPoint',
        function (restProxyService, authService, ChewanApiProvider, ChewanApiEndPoint) {
            var applicationId;

            return {
                getApplicationList: function (criteria) {
                    var param = "userId=" + authService.getUser().userId;
                    param += "&token=" + authService.getUser().token;
                    param += (criteria.status ? ("&status=" + criteria.status) : "");
                    param += (criteria.type ? ("&type=" + criteria.type) : "");
                    param += (criteria.start ? ("&start=" + criteria.start.getTime()) : "");
                    param += (criteria.end ? ("&end=" + criteria.end.getTime()) : "");

                    return restProxyService.sendHttpGet(ChewanApiProvider + ChewanApiEndPoint, '/official/authentication/list?' + param);
                },

                setApplication: function (aApplicationId) {
                    applicationId = aApplicationId;
                },

                getApplicationInfo: function () {
                    return restProxyService.sendHttpGet(ChewanApiProvider + ChewanApiEndPoint, '/application/' + applicationId + '/info?token='
                    + authService.getUser().token + '&userId=' + authService.getUser().userId);
                },

                processApplication: function (action, remarks, application) {
                    return restProxyService.sendHttpPost(ChewanApiProvider + ChewanApiEndPoint, '/official/approve?token='
                    + authService.getUser().token + '&userId=' + authService.getUser().userId, JSON.stringify({
                        applicationId: application.applicationId,
                        status: action,
                        content: remarks,
                        license: application.authentication.license,
                        driver: application.authentication.driver
                    }));
                }
            }
        }]
)
