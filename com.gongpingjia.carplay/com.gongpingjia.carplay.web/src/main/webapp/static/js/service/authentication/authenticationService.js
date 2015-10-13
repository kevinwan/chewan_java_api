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

                processApplication: function (accept, remarks, application) {
                    return restProxyService.sendHttpPost(ChewanApiProvider + ChewanApiEndPoint, '/official/approve/driving?token='
                        + authService.getUser().token + '&userId=' + authService.getUser().userId,
                        JSON.stringify({
                            applicationId: application.applicationId,
                            accept: accept,
                            content: remarks,
                            license: application.authentication.license,
                            driver: application.authentication.driver
                        }));
                },

                updateUserLicense: function (userId, authentication) {
                    return restProxyService.sendHttpPost(ChewanApiProvider + ChewanApiEndPoint, '/authentication/' + userId + '/update?userId='
                    + authService.getUser().userId + '&token=' + authService.getUser().token,
                    JSON.stringify({
                        license: application.authentication.license,
                        driver: application.authentication.driver
                    }))
                }
            }
        }]
)
