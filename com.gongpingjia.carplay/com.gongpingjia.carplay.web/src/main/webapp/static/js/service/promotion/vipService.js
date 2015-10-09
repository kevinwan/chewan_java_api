'use strict';

/**
 * vip service
 *
 * @constructor
 */

gpjApp.factory('vipService', ['restProxyService', 'PromotionApiPrefix', 'authService',
    function (restProxyService, PromotionApiPrefix, authService) {
        var vip;

        return {
            getVip: function () {
                return vip;
            },
            setVip: function (aVip) {
                vip = aVip;
            },
            grantVip: function (vip) {
                return restProxyService.sendHttpPost(PromotionApiPrefix, '/vip?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, vip);
            },
            getVipList: function (criteria) {
                return restProxyService.sendHttpGet(PromotionApiPrefix, '/vip/list?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name
                + '&name=' + criteria.name + '&phone=' + criteria.phone + '&plate=' + criteria.plate
                + '&type=' + criteria.type);
            }
        }
    }]);