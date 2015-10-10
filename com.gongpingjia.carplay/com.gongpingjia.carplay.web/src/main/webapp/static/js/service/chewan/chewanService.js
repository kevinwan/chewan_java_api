'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('chewanService', ['restProxyService', 'authService', 'ChewanApiProvider', 'ChewanApiEndPoint',
    function (restProxyService, authService, ChewanApiProvider, ChewanApiEndPoint) {

        var application;
        var feedback;
        var user;
        var activity;

        return {
            getApplication: function () {
                return application;
            },

            getFeedback: function () {
                return feedback;
            },
            setFeedback: function (afeedback) {
                feedback = afeedback;
            },
            getUser: function () {
                return user;
            },
            setUser: function (aUser) {
                user = aUser;
            },
            getActivity: function () {
                return activity;
            },
            setActivity: function (aActivity) {
                activity = aActivity;
            },
            processApplication: function (applicationId, action, license, remarks) {
                return restProxyService.sendHttpPost(ChewanApiProvider, ChewanApiEndPoint + '/application/' + application + '/process?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify({
                    action: action,
                    license: license,
                    remarks: remarks
                }));
            },
            getFeedbackList: function (criteria) {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/feedback/list?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name + (criteria.phone ? '&phone=' + criteria.phone : '')
                + (criteria.nickname ? '&nickname=' + criteria.nickname : '') + (criteria.status ? ('&status=' + criteria.status) : '')
                + (criteria.startDate ? ('&startDate=' + criteria.startDate) : '') + (criteria.endDate ? ('&endDate=' + criteria.endDate) : ''));
            },
            getFeedbackInfo: function () {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/feedback/' + feedback + '/info?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name);
            },
            alterFeedbackInfo: function (remarks) {
                return restProxyService.sendHttpPost(ChewanApiProvider, ChewanApiEndPoint + '/feedback/' + feedback + '/info?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name, JSON.stringify({
                    remarks: remarks
                }));
            },
            getUserList: function (criteria) {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/user/list?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name + (criteria.phone ? '&phone=' + criteria.phone : '')
                + (criteria.nickname ? '&nickname=' + criteria.nickname : '') + (criteria.isAuthenticated ? ('&isAuthenticated=' + criteria.isAuthenticated) : ''));
            },
            getUserInfo: function () {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/user/' + user + '/info?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name);
            },
            getActivityList: function (criteria) {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/activity/list?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name + (criteria.city ? '&city=' + criteria.city : '')
                + (criteria.type ? '&type=' + criteria.type : '') + (criteria.key ? '&key=' + criteria.key : ''));
            },
            getActivityInfo: function () {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/activity/' + activity + '/info?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name);
            },
            getCityList: function (criteria) {
                return restProxyService.sendHttpGet(ChewanApiProvider, ChewanApiEndPoint + '/city/list?access_token='
                + authService.getUser().token + '&username=' + authService.getUser().name);
            }
        }
    }]);