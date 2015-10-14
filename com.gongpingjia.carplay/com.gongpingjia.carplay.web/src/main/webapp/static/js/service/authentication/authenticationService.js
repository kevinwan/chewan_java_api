'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('authenticationService', ['restProxyService', 'authService', 'ChewanOfficialApiEndPoint', 'commonService',
        function (restProxyService, authService, ChewanOfficialApiEndPoint, commonService) {
            var applicationId;

            return {
                getApplicationList: function (criteria) {
                    var param = "userId=" + authService.getUser().userId;
                    param += "&token=" + authService.getUser().token;
                    param += (criteria.status ? ("&status=" + criteria.status) : "");
                    param += (criteria.type ? ("&type=" + criteria.type) : "");
                    param += (criteria.startDate ? ("&start=" + criteria.startDate.getTime()) : "");
                    param += (criteria.endDate ? ("&end=" + criteria.endDate.getTime()) : "");

                    return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/official/authentication/list?' + param);
                },

                setApplication: function (aApplicationId) {
                    applicationId = aApplicationId;
                },

                getApplicationInfo: function () {
                    return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/application/' + applicationId + '/info?token='
                    + authService.getUser().token + '&userId=' + authService.getUser().userId);
                },

                initialApplication: function (authentication) {
                    var driver = authentication.driver;

                    driver.birthday = commonService.transferLongToDateString(driver.birthday);
                    driver.issueDate = commonService.transferLongToDateString(driver.issueDate);
                    driver.validFrom = commonService.transferLongToDateString(driver.validFrom);

                    var license = authentication.license;
                    license.registerTime = commonService.transferLongToDateString(license.registerTime);
                    license.issueTime = commonService.transferLongToDateString(license.issueTime);
                },

                refreshApplication: function (authentication) {
                    var driver = authentication.driver;
                    driver.birthday = commonService.transferDateStringToLong(driver.birthday);
                    driver.issueDate = commonService.transferDateStringToLong(driver.issueDate);
                    driver.validFrom = commonService.transferDateStringToLong(driver.validFrom);

                    var license = authentication.license;
                    license.registerTime = commonService.transferDateStringToLong(license.registerTime);
                    license.issueTime = commonService.transferDateStringToLong(license.issueTime);
                },

                processApplication: function (accept, remarks, application) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/approve/driving?token='
                        + authService.getUser().token + '&userId=' + authService.getUser().userId,
                        JSON.stringify({
                            applicationId: application.applicationId,
                            accept: accept,
                            remarks: remarks,
                            license: application.authentication.license,
                            driver: application.authentication.driver
                        }));
                },

                updateUserLicense: function (authenticationId, authentication) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/authentication/' + authenticationId + '/update?userId='
                        + authService.getUser().userId + '&token=' + authService.getUser().token,
                        JSON.stringify({
                            license: authentication.license,
                            driver: authentication.driver
                        }))
                }

            }
        }]
)
