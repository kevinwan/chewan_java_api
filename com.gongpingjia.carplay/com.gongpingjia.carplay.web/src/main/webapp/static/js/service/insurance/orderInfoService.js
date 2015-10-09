'use strict';

/**
 * Order confirm service
 *
 * @constructor
 */

gpjApp.factory('orderInfoService', ['restProxyService', 'InsuranceApiHost', 'InsuranceApiPort', 'InsuranceApiPrefix', 'authService',
    function (restProxyService, InsuranceApiHost, InsuranceApiPort, InsuranceApiPrefix, authService) {

        var order;
        return {
            getOrder: function () {
                return order;
            },
            setOrder: function (aOrder) {
                order = aOrder;
            },
            getOrderByToken: function () {
                return restProxyService.sendHttpGet(InsuranceApiHost + ':' + InsuranceApiPort, InsuranceApiPrefix + '/order/list?token=' + order.token
                + '&showReturn=' + (order.status ? 'yes' : 'no') + '&access_token=' + authService.getUser().token
                + (order.status ? '' : '&showFailure=yes') + '&username=' + authService.getUser().name);
            },
            getStoreInfo: function (sno) {
                return restProxyService.sendHttpGet(InsuranceApiHost + ':' +  InsuranceApiPort, InsuranceApiPrefix + '/store/' + sno + '?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name);
            }
        }
    }]);