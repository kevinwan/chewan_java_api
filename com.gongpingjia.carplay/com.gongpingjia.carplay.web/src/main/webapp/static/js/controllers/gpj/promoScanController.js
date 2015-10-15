'use strict';

/**
 * promoScanController
 *
 * @constructor
 */
gpjApp.controller('promoScanController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', '$compile', 'DTInstances',
    'gpjService', 'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, $compile, DTInstances, gpjService, moment) {

        $scope.records = {};

        $scope.dateOptions = {
            changeYear: false,
            changeMonth: false,
            yearRange: '1900:-0'
        };

        /**
         * Watch type changes
         */
        $scope.$watch('criteria.type', function () {
            $scope.searchRequirements();
        });

        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).withOption('data', 'create_time').notSortable(),
            DTColumnDefBuilder.newColumnDef(1).withOption('data', 'code').notSortable(),
            DTColumnDefBuilder.newColumnDef(2).withOption('data', 'name').notSortable(),
            DTColumnDefBuilder.newColumnDef(3).withOption('data', 'phone').notSortable(),
            DTColumnDefBuilder.newColumnDef(4).withOption('data', 'city').notSortable().withOption('width', '15%'),
            DTColumnDefBuilder.newColumnDef(5).withOption('data', 'platform').notSortable(),
            DTColumnDefBuilder.newColumnDef(6).withOption('data', 'id').notSortable().notVisible()
        ];

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
            .withOption('ajax', function (data, callback, settings) {

                $rootScope.loadingPromise = gpjService.getScanDownloadRecords({
                    draw: data.draw,
                    length: data.length,
                    start: data.start,

                    type: $scope.criteria.type,
                    name: $scope.criteria.name ? $scope.criteria.name : '',
                    code: $scope.criteria.code ? $scope.criteria.code : '',
                    city: $scope.criteria.city ? $scope.criteria.city : '',
                    start_time: $scope.criteria.startDate ? $scope.criteria.startDate : '',
                    end_time: $scope.criteria.endDate ? $scope.criteria.endDate : '',
                    platform: $scope.criteria.platform ? $scope.criteria.platform : ''

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
            $scope.records[data['open_sell_id']] = data;
            $compile(angular.element(row).contents())($scope);
        }

        /**
         * Search requirements
         *
         * @param criteria
         */
        $scope.searchRequirements = function (criteria) {
            $scope.dtInstance.rerender();
        };

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.criteria = {
                type: 'scan',
                name: '',
                code: '',
                city: '',
                startDate: moment().startOf('month').format('YYYY-MM-DD'),
                endDate: moment().format('YYYY-MM-DD'),
                platform: ''
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