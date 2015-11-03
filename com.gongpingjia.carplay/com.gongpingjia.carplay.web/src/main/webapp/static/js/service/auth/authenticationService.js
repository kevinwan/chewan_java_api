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
                    param += (criteria.phone ? ("&phone=" + criteria.phone) : "")
                    param += (criteria.status ? ("&status=" + criteria.status) : "");
                    param += (criteria.type ? ("&type=" + criteria.type) : "");
                    param += (criteria.startDate ? ("&start=" + commonService.transferDateStringToLong(criteria.startDate)) : "");
                    param += (criteria.endDate ? ("&end=" + commonService.transferDateStringToLong(criteria.endDate)) : "");

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

                    if (driver) {
                        driver.birthday = commonService.transferLongToDateString(driver.birthday);
                        driver.issueDate = commonService.transferLongToDateString(driver.issueDate);
                        driver.validFrom = commonService.transferLongToDateString(driver.validFrom);
                    }

                    var license = authentication.license;
                    if (license) {
                        license.registerTime = commonService.transferLongToDateString(license.registerTime);
                        license.issueTime = commonService.transferLongToDateString(license.issueTime);
                    }
                },

                buildLicense: function (authentication) {
                    var license = new Object();
                    license.address = authentication.license.address;
                    license.engineNumber = authentication.license.engineNumber;
                    license.issueTime = commonService.transferDateStringToLong(authentication.license.issueTime);
                    license.model = authentication.license.model;
                    license.name = authentication.license.name;
                    license.plate = authentication.license.plate;
                    license.registerTime = commonService.transferDateStringToLong(authentication.license.registerTime);
                    license.vehicleNumber = authentication.license.vehicleNumber;
                    license.vehicleType = authentication.license.vehicleType;
                    return license;
                },

                buildDriver: function (authentication) {
                    var driver = new Object();
                    driver.address = authentication.driver.address;
                    driver.birthday = commonService.transferDateStringToLong(authentication.driver.birthday);
                    driver.code = authentication.driver.code;
                    driver.drivingClass = authentication.driver.drivingClass;
                    driver.gender = authentication.driver.gender;
                    driver.issueDate = commonService.transferDateStringToLong(authentication.driver.issueDate);
                    driver.name = authentication.driver.name;
                    driver.nationality = authentication.driver.nationality;
                    driver.police = authentication.driver.police;
                    driver.validFor = authentication.driver.validFor;
                    driver.validFrom = commonService.transferDateStringToLong(authentication.driver.validFrom);
                    return driver;
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

                processApplication: function (accept, application) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/official/approve/driving?token='
                        + authService.getUser().token + '&userId=' + authService.getUser().userId,
                        JSON.stringify({
                            applicationId: application.applicationId,
                            accept: accept,
                            remarks: application.remarks,
                            license: this.buildLicense(application.authentication),
                            driver: this.buildDriver(application.authentication)
                        }));
                },

                updateUserLicense: function (authenticationId, authentication) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/authentication/' + authenticationId + '/update?userId='
                        + authService.getUser().userId + '&token=' + authService.getUser().token,
                        JSON.stringify({
                            license: this.buildLicense(authentication),
                            driver: this.buildDriver(authentication)
                        }))
                },

                processUserPhotoApplication: function (accept, remarks, applicationId) {
                    return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, "/official/approve/userPhoto?userId="
                        + authService.getUser().userId + '&token=' + authService.getUser().token,
                        JSON.stringify({
                            applicationId: applicationId,
                            accept: accept,
                            remarks: remarks
                        }))
                },

                getAuthHistorys: function () {
                    return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/authentication/" + applicationId + "/historys?userId="
                    + authService.getUser().userId + '&token=' + authService.getUser().token);
                }
            }
        }]
)
