'use strict';

/**
 * carExchangeController
 *
 * @constructor
 */
gpjApp.controller('carExchangeController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', '$compile', 'DTInstances',
    'gpjService', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, $compile, DTInstances, gpjService) {

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
            DTColumnDefBuilder.newColumnDef(1).withOption('data', 'name').notSortable().withOption('width', '7%'),
            DTColumnDefBuilder.newColumnDef(2).withOption('data', 'phone').notSortable(),
            DTColumnDefBuilder.newColumnDef(3).withOption('data', 'city').notSortable().withOption('width', '5%'),
            DTColumnDefBuilder.newColumnDef(4).withOption('data', 'brand').notSortable().withOption('width', '6%'),
            DTColumnDefBuilder.newColumnDef(5).withOption('data', 'model').notSortable().withOption('width', '15%'),
            DTColumnDefBuilder.newColumnDef(6).withOption('data', 'mile').notSortable(),
            DTColumnDefBuilder.newColumnDef(7).withOption('data', 'year').notSortable(),
            DTColumnDefBuilder.newColumnDef(8).withOption('data', 'eval_price').notSortable(),
            DTColumnDefBuilder.newColumnDef(9).withOption('data', 'target_brand').notSortable().withOption('width', '6%'),
            DTColumnDefBuilder.newColumnDef(10).withOption('data', 'target_model').notSortable().withOption('width', '10%'),
            DTColumnDefBuilder.newColumnDef(11).withOption('data', 'source').notSortable(),
            DTColumnDefBuilder.newColumnDef(12).withOption('data', 'process_status').notSortable(),
            DTColumnDefBuilder.newColumnDef(13).withOption('data', null).renderWith(actionsHtml).notSortable(),
            DTColumnDefBuilder.newColumnDef(14).withOption('data', 'publish_id').notVisible().notSortable()
        ];

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
            return '<a href="" ng-click="viewItem(' + data.publish_id + ')">查看详情</a>';
        }

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
            .withOption('ajax', function (data, callback, settings) {

                $rootScope.loadingPromise = gpjService.getCarExchanges({
                    draw: data.draw,
                    length: data.length,
                    start: data.start,
                    phone: $scope.criteria.phone ? $scope.criteria.phone : '',
                    name: $scope.criteria.name ? $scope.criteria.name : '',
                    process_status: $scope.criteria.process_status ? $scope.criteria.process_status : '',
                    source: $scope.criteria.source ? $scope.criteria.source : '',
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
            $scope.records[data['publish_id']] = data;
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
            gpjService.setExchange($scope.records[itemId]);

            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/car_exchange_modal.html',
                controller: 'carExchangeModalController'
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
        $scope.searchExchanges = function (criteria) {
            $scope.dtInstance.rerender();
        };

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.criteria = {
                phone: '',
                name: '',
                process_status: '',
                source: '',
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