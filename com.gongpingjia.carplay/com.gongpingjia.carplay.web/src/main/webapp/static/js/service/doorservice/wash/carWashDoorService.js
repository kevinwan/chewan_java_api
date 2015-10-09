'use strict';

/**
 * Car wash door service
 *
 * @constructor
 */

gpjApp.factory('carWashDoorService', ['restProxyService', 'authService', 'DoorServiceApiHost', 'DoorServiceApiPort',
    function (restProxyService, authService, DoorServiceApiHost, DoorServiceApiPort) {

        var order;
        var servicePoint;

        return {
            getOrder: function () {
                return order;
            },
            setOrder: function (aOrder) {
                order = aOrder;
            },
            getServicePoint: function () {
                return servicePoint;
            },
            setServicePoint: function (aServicePoint) {
                servicePoint = aServicePoint;
            },
            getOrderList: function (criteria) {
                return restProxyService.sendHttpGet(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/orderList?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name + (criteria.phone ? '&phone=' + criteria.phone : '')
                +  (criteria.plate ? ('&plate=' + criteria.plate) : '') +  (criteria.status ? ('&status=' + criteria.status) : '')
                +  (criteria.servicePointId ? ('&servicePointId=' + criteria.servicePointId) : '') +  (criteria.startDate ? ('&startDate=' + criteria.startDate) : '')
                +  (criteria.endDate ? ('&endDate=' + criteria.endDate) : ''));
            },
            getOrderInfo: function(){
                return restProxyService.sendHttpGet(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/order/' + order.id
                + '/info?access_token=' + authService.getUser().token + '&username=' + authService.getUser().name);
            },
            cancelOrder: function(){
                return restProxyService.sendHttpPost(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/order/' + order.id
                + '/cancel?access_token=' + authService.getUser().token + '&username=' + authService.getUser().name, '');
            },
            fillOrderInfo: function(carInfo){
                return restProxyService.sendHttpPost(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/order/' + order.id
                + '/carinfo?access_token=' + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify(carInfo));
            },
            getReservationStatus: function(orderId, targetDate){
                return restProxyService.sendHttpGet(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/reservation/status?'
                + 'access_token=' + authService.getUser().token + '&username=' + authService.getUser().name + '&orderId=' + orderId + '&targetDate=' + targetDate);
            },
            alterReservation: function(orderId, targetDate, targetSlot){
                return restProxyService.sendHttpPost(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/reservation/alter?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify({orderId:orderId, targetDate:targetDate, targetSlot:targetSlot}));
            },
            closeReservation: function(day, time, servicePointId, reason){
                return restProxyService.sendHttpPost(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/reservation/close?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify({day:day, time:time, servicePointId:servicePointId, reason:reason}));
            },
            getServicePointList: function (criteria) {
                return restProxyService.sendHttpGet(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/servicePointList?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name + (criteria.name ? '&name=' + criteria.name : '')
                +  (criteria.city ? ('&city=' + criteria.city) : '') +  (criteria.invalid ? ('&invalid=' + criteria.invalid) : ''));
            },
            addServicePoint: function (servicePoint) {
                return restProxyService.sendHttpPost(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/servicePoint/add?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify(servicePoint));
            },
            alterServicePoint: function (servicePoint) {
                return restProxyService.sendHttpPost(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/servicePoint/alter?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify(servicePoint));
            },
            getServicePointInfo: function (servicePointId) {
                return restProxyService.sendHttpGet(DoorServiceApiHost + ':' + DoorServiceApiPort, '/yryjapi/doorservice/wash/v1/customerservice/servicePoint/' + servicePointId
                + '/info?access_token=' + authService.getUser().token + '&username=' + authService.getUser().name);
            }
        }
    }]);