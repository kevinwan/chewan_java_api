'use strict';

/**
 * User controller
 *
 * @constructor
 */
gpjApp.controller('dealerPublishController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', '$compile',
    'DTInstances', 'gpjService', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, $compile, DTInstances, gpjService) {

        var STATUS_PROMO = '特卖中';

        $scope.records = {};

        $scope.dateOptions = {
            changeYear: false,
            changeMonth: false,
            yearRange: '1900:-0'
        };

        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).withOption('data', 'publish_time').notSortable(),
            DTColumnDefBuilder.newColumnDef(1).withOption('data', 'brand').notSortable().withOption('width', '5%'),
            DTColumnDefBuilder.newColumnDef(2).withOption('data', 'model').notSortable().withOption('width', '15%'),
            DTColumnDefBuilder.newColumnDef(3).withOption('data', 'mile').notSortable(),
            DTColumnDefBuilder.newColumnDef(4).withOption('data', 'year').notSortable(),
            DTColumnDefBuilder.newColumnDef(5).withOption('data', 'dealer_name').notSortable(),
            DTColumnDefBuilder.newColumnDef(6).withOption('data', 'dealer_phone').notSortable(),
            DTColumnDefBuilder.newColumnDef(7).withOption('data', 'dealer_city').notSortable(),
            DTColumnDefBuilder.newColumnDef(8).withOption('data', 'original_price').notSortable(),
            DTColumnDefBuilder.newColumnDef(9).withOption('data', 'promotion_price').notSortable(),
            DTColumnDefBuilder.newColumnDef(10).withOption('data', 'order_count').notSortable(),
            DTColumnDefBuilder.newColumnDef(11).withOption('data', 'status').notSortable(),
            DTColumnDefBuilder.newColumnDef(12).withOption('data', 'process_status').notSortable(),
            DTColumnDefBuilder.newColumnDef(13).withOption('data', null).renderWith(actionsHtml).notSortable(),
            DTColumnDefBuilder.newColumnDef(14).withOption('data', 'publish_id').notVisible().notSortable()
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
            if (provinceOption && provinceOption.cities)
                $scope.cityOptions = provinceOption.cities;
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

            if (data.status === STATUS_PROMO)
                return '<a href="" ng-click="viewItem(' + data.publish_id + ')">查看预约</a>' +
                    '&nbsp;&nbsp;&nbsp;<a href="" ng-click="viewRemarks(' + data.publish_id + ')">客服备注</a>';
            else
                return ' - ';
        }

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
            .withOption('ajax', function (data, callback, settings) {

                if (($scope.provinceIndex !== '') && ($scope.criteria.dealer_city === ''))
                    return alert('请选择城市');

                $rootScope.loadingPromise = gpjService.getDealerPublish({
                    draw: data.draw,
                    length: data.length,
                    start: data.start,
                    dealer_name: $scope.criteria.dealer_name ? $scope.criteria.dealer_name : '',
                    dealer_phone: $scope.criteria.dealer_phone ? $scope.criteria.dealer_phone : '',
                    dealer_city: $scope.criteria.dealer_city ? $scope.criteria.dealer_city : '',
                    status: $scope.criteria.status ? $scope.criteria.status : '',
                    process_status: ($scope.criteria.process_status && $scope.criteria.status === 'promo') ? $scope.criteria.process_status : '',
                    start_time: $scope.criteria.startDate ? $scope.criteria.startDate : '',
                    end_time: $scope.criteria.endDate ? $scope.criteria.endDate : ''

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
            $scope.records[data['publish_id']] = data;
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

            gpjService.setDealerPublishInfo($scope.records[itemId]);

            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/view_appointments_modal.html',
                controller: 'viewAppointmentsModalController'
            });

            return modalInstance.result.then(function (reply) {
                //alert(JSON.stringify(reply));
                $scope.dtInstance.rerender();
            });
        };

        /**
         * View item info
         *
         * @param itemId
         */
        $scope.viewRemarks = function (itemId) {
            //alert('itemId = ' + itemId);
            //alert(JSON.stringify($scope.records[itemId]));

            gpjService.setDealerPublishInfo($scope.records[itemId]);

            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/dealer_publish_remarks_modal.html',
                controller: 'dealerPublishRemarksModalController'
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
        $scope.searchDealerPublish = function (criteria) {
            $scope.dtInstance.rerender();
        };

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.provinceIndex = '';
            $scope.criteria = {
                dealer_name: '',
                dealer_phone: '',
                dealer_city: '',
                status: 'promo',
                process_status: '',
                startDate: moment().format('YYYY-MM-DD'),
                endDate: moment().format('YYYY-MM-DD')
            };
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