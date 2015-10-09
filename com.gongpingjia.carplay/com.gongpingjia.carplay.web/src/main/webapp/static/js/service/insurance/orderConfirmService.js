'use strict';

/**
 * Order confirm service
 *
 * @constructor
 */

gpjApp.factory('orderConfirmService', ['restProxyService', 'InsuranceApiHost', 'InsuranceApiPort', 'InsuranceApiPrefix', 'authService',
    function (restProxyService, InsuranceApiHost, InsuranceApiPort, InsuranceApiPrefix, authService) {

        var token;
        var insurance;

        return {
            getToken: function () {
                return token;
            },
            setToken: function (aToken) {
                token = aToken;
            },
            getInsurance: function () {
                return insurance;
            },
            setInsurance: function (aInsurance) {
                insurance = aInsurance;
            },
            confirmOrder: function (insurance) {
                return restProxyService.sendHttpPost(InsuranceApiHost + ':' + InsuranceApiPort, InsuranceApiPrefix + '/order/confirm?token=' + token
                + '&access_token=' + authService.getUser().token + '&username=' + authService.getUser().name, insurance);
            },
            changeStatus: function (status) {
                return restProxyService.sendHttpPost(InsuranceApiHost + ':' + InsuranceApiPort, InsuranceApiPrefix + '/order/status?token=' + token
                + '&access_token=' + authService.getUser().token + '&username=' + authService.getUser().name, status);
            }
        }
    }]);