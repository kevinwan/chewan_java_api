'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('authenticationService', ['restProxyService', 'authService', 'ChewanOfficialApiEndPoint',
        function (restProxyService, authService, ChewanOfficialApiEndPoint) {
            var applicationId;

            return {
                getApplicationList: function (criteria) {
                    var param = "userId=" + authService.getUser().userId;
                    param += "&token=" + authService.getUser().token;
                    param += (criteria.status ? ("&status=" + criteria.status) : "");
                    param += (criteria.type ? ("&type=" + criteria.type) : "");
                    param += (criteria.start ? ("&start=" + criteria.start.getTime()) : "");
                    param += (criteria.end ? ("&end=" + criteria.end.getTime()) : "");

                    return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/official/authentication/list?' + param);
                },

                setApplication: function (aApplicationId) {
                    applicationId = aApplicationId;
                },

                getApplicationInfo: function () {
                    return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/application/' + applicationId + '/info?token='
                    + authService.getUser().token + '&userId=' + authService.getUser().userId);
                },

                processApplication: function (accept, remarks, application) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/approve/driving?token='
                        + authService.getUser().token + '&userId=' + authService.getUser().userId,
                        JSON.stringify({
                            applicationId: application.applicationId,
                            accept: accept,
                            content: remarks,
                            license: application.authentication.license,
                            driver: application.authentication.driver
                        }));
                },

                updateUserLicense: function (authenticationId, authentication) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/authentication/' + authenticationId + '/update?userId='
                    + authService.getUser().userId + '&token=' + authService.getUser().token,
                    JSON.stringify({
                        license: application.authentication.license,
                        driver: application.authentication.driver
                    }))
                }
            }
        }]
)
