'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('gpjService', ['restProxyService', 'GpjApiProvider', 'GpjApiEndPoint',
    function (restProxyService, GpjApiProvider, GpjApiEndPoint) {

        var noDealerTrade;
        var dealerTrade;
        var dealer;
        var dealerPublish;
        var exchange;
        var c2cExchange;
        var appointment;
        var deal;
        var clientPublish;
        var userAppointment;

        return {
            getNoDealerTrade: function () {
                return noDealerTrade;
            },
            setNoDealerTrade: function (aDealerTrade) {
                noDealerTrade = aDealerTrade;
            },
            getDealerTrade: function () {
                return dealerTrade;
            },
            setDealerTrade: function (aDealerTrade) {
                dealerTrade = aDealerTrade;
            },
            getDealer: function () {
                return dealer;
            },
            setDealer: function (aDealer) {
                dealer = aDealer;
            },
            getDealerPublishInfo: function () {
                return dealerPublish;
            },
            setDealerPublishInfo: function (aDealerPublish) {
                dealerPublish = aDealerPublish;
            },
            getExchange: function () {
                return exchange;
            },
            setExchange: function (aExchange) {
                exchange = aExchange;
            },
            getC2CExchange: function () {
                return c2cExchange;
            },
            setC2CExchange: function (aExchange) {
                c2cExchange = aExchange;
            },
            getAppointment: function () {
                return appointment;
            },
            setAppointment: function (aAppointment) {
                appointment = aAppointment;
            },
            getDeal: function () {
                return deal;
            },
            setDeal: function (aDeal) {
                deal = aDeal;
            },
            getClientPublishInfo: function () {
                return clientPublish;
            },
            setClientPublishInfo: function (aClientPublish) {
                clientPublish = aClientPublish;
            },
            getUserAppointment: function () {
                return userAppointment;
            },
            setUserAppointment: function (aUserAppointment) {
                userAppointment = aUserAppointment;
            },
            getCities: function () {
                return restProxyService.sendHttpGet('resource/cities.json', '');
            },
            getStatusField: function (statusName) {
                if (statusName === '已完成')
                    return 'done';
                else if (statusName === '处理中')
                    return 'processing';
                else
                    return 'pending';
            },
            getStatusName: function (statusField) {
                if (statusField === 'done')
                    return '已完成';
                else if (statusField === 'processing')
                    return '处理中';
                else
                    return '待处理';
            },
            getNoDealerWantCars: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-no-dealer-want-cars/', params);
            },
            getDealerWantCars: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-dealer-want-cars/', params);
            },
            getAllDealers: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-all-dealers/', params);
            },
            getSellCars: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-sell-cars/', params);
            },
            addDealer: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/create-dealer/', data);
            },
            updateDealer: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/modify-dealer-message/', data);
            },
            deleteDealer: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-delete-dealer/', data);
            },
            updateRemarksForOpenSell: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-trade-car-note/', data);
            },
            getDealerPublish: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-dealer-cars-info/', params);
            },
            getPromocarAppointments: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-promocar-appointments/', params);
            },
            getDealerDeals: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-dealer-want-cars-detail/', params);
            },
            getCarExchanges: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-trade-in-history/', params);
            },
            updateRemarksForCarExchange: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-trade-car-note/', data);
            },
            getC2CExchanges: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-auction-car/', params);
            },
            updateRemarksForC2CExchange: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-sell-service-note/', data);
            },
            postPromoNote: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-promo-note/', data);
            },
            postPromoAppointmentNote: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-promo-appoint-note/', data);
            },
            postTradeCarNote: function (data) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-trade-car-note/', data);
            },
            getUserPromoCarAppointment: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-user-promocar-appointment/', params);
            },
            getScanDownloadRecords: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-scan-download-records/', params);
            },
            getPromoRank: function (params) {
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-promoter-rank/', params);
            }
        }
    }]);