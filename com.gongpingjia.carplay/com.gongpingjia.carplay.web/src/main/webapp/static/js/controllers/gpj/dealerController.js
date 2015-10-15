'use strict';

/**
 * User controller
 *
 * @constructor
 */
gpjApp.controller('dealerController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', '$compile',
    'DTInstances', 'gpjService', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, $compile, DTInstances, gpjService) {

        $scope.records = {};

        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).withOption('data', 'dealer_name').notSortable(),
            DTColumnDefBuilder.newColumnDef(1).withOption('data', 'dealer_phone').notSortable(),
            DTColumnDefBuilder.newColumnDef(2).withOption('data', 'dealer_city').notSortable(),
            DTColumnDefBuilder.newColumnDef(3).withOption('data', 'count').notSortable(),
            DTColumnDefBuilder.newColumnDef(4).withOption('data', null).renderWith(actionsHtml).notSortable(),
            DTColumnDefBuilder.newColumnDef(5).withOption('data', 'dealer_id').notVisible().notSortable()
        ];

        /**
         * Get province and city info
         */
        gpjService.getCities().success(function (data) {
            $scope.provinceOptions = data;
        });

        /**
         * Trigger by province change
         */
        $scope.changeProvince = function (provinceOption) {
            $scope.criteria.dealer_city = '';
            $scope.cityOptions = [];
            if (provinceOption && provinceOption.cities){
                $scope.cityOptions = provinceOption.cities;
                if($scope.cityOptions.length === 1)
                    $scope.criteria.dealer_city = $scope.cityOptions[0];
            }
        };

        /**
         * Trigger by city change
         */
        $scope.changeCity = function () {
            //alert($scope.criteria.dealer_city);
        };

        /**
         * Create style for last visible column
         *
         * @param data
         * @param type
         * @param full
         * @param meta
         * @returns {string}
         */
        function actionsHtml(data, type, full, meta) {
            return '<a href="" ng-click="viewItem(' + data.dealer_id + ')">编辑删除</a>' +
                '&nbsp;&nbsp;&nbsp;<a href="" ng-click="viewHistory(' + data.dealer_id + ')">拍车明细</a>';
        }

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
            .withOption('ajax', function (data, callback, settings) {

                if (($scope.provinceIndex !== '') && ($scope.criteria.dealer_city === ''))
                    return alert('请选择城市');

                $rootScope.loadingPromise = gpjService.getAllDealers({
                    draw: data.draw,
                    length: data.length,
                    start: data.start,
                    dealer_name: $scope.criteria.dealer_name ? $scope.criteria.dealer_name : '',
                    dealer_city: $scope.criteria.dealer_city ? $scope.criteria.dealer_city : '',
                    dealer_phone: $scope.criteria.dealer_phone ? $scope.criteria.dealer_phone : ''

                }).success(function (res) {
                    $scope.records = {};
                    $scope.total = res.recordsFiltered;
                    callback(res);
                });
            })
            .withOption('bFilter', false)
            .withDataProp('data')
            .withOption('displayLength', 10)
            .withOption('serverSide', true)
            .withPaginationType('full_numbers')
            .withOption('createdRow', createdRow)
            .withOption('order', []);

        /**
         * Perform while creating row
         *
         * @param row
         * @param data
         * @param dataIndex
         */
        function createdRow(row, data, dataIndex) {
            // Recompiling so we can bind Angular directive to the DT
            $scope.records[data['dealer_id']] = data;
            //console.dir(data);
            $compile(angular.element(row).contents())($scope);
        }

        /**
         * View item info
         *
         * @param itemId
         */
        $scope.viewItem = function (itemId) {
            //alert('itemId = ' + itemId);
            //alert(JSON.stringify($scope.records[itemId]));

            gpjService.setDealer($scope.records[itemId]);

            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/view_dealer_modal.html',
                controller: 'viewDealerModalController'
            });

            return modalInstance.result.then(function (reply) {
                //alert(JSON.stringify(reply));
                $scope.dtInstance.rerender();
            });
        };

        /**
         * View history
         */
        $scope.viewHistory = function (itemId) {
            //alert(JSON.stringify($scope.records[itemId]));

            gpjService.setDealer($scope.records[itemId]);

            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/dealer_deals_modal.html',
                controller: 'dealerDealsModalController'
            });

            return modalInstance.result.then(function (reply) {
                //alert(JSON.stringify(reply));
                $scope.dtInstance.rerender();
            });
        };

        /**
         * Search requirements
         *
         * @param criteria
         */
        $scope.searchDealers = function (criteria) {
            $scope.dtInstance.rerender();
        };

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.provinceIndex = '';
            $scope.criteria = {dealer_name: '', dealer_city: '', dealer_phone: ''};
        };

        /**
         * Create a new dealer
         */
        $scope.createDealer = function () {
            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/add_dealer_modal.html',
                controller: 'addDealerModalController'
            });

            return modalInstance.result.then(function (reply) {
                $scope.dtInstance.rerender();
            });
        };

        /**
         * Initialize component status
         */
        $scope.resetCriteria();

        /**
         * Initialize dtInstance
         */
        DTInstances.getLast().then(function (dtInstance) {
            $scope.dtInstance = dtInstance;
        });
    }]);