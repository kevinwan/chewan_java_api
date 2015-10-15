'use strict';

/**
 * User controller
 *
 * @constructor
 */
gpjApp.controller('userAppointmentController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', '$compile',
    'DTInstances', 'gpjService', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, $compile, DTInstances, gpjService) {

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
            DTColumnDefBuilder.newColumnDef(0).withOption('data', 'process_time').notSortable(),
            DTColumnDefBuilder.newColumnDef(1).withOption('data', 'name').notSortable(),
            DTColumnDefBuilder.newColumnDef(2).withOption('data', 'phone').notSortable(),
            DTColumnDefBuilder.newColumnDef(3).withOption('data', 'book_time').notSortable(),
            DTColumnDefBuilder.newColumnDef(4).withOption('data', 'dealer_name').notSortable().withOption('width', '5%'),
            DTColumnDefBuilder.newColumnDef(5).withOption('data', 'dealer_phone').notSortable(),
            DTColumnDefBuilder.newColumnDef(6).withOption('data', 'brand').notSortable(),
            DTColumnDefBuilder.newColumnDef(7).withOption('data', 'model').notSortable().withOption('width', '15%'),
            DTColumnDefBuilder.newColumnDef(8).withOption('data', 'mile').notSortable(),
            DTColumnDefBuilder.newColumnDef(9).withOption('data', 'year').notSortable(),
            DTColumnDefBuilder.newColumnDef(10).withOption('data', 'status').notSortable(),
            DTColumnDefBuilder.newColumnDef(11).withOption('data', 'process_status').notSortable(),
            DTColumnDefBuilder.newColumnDef(12).withOption('data', null).renderWith(actionsHtml).notSortable(),
            DTColumnDefBuilder.newColumnDef(13).withOption('data', 'book_id').notVisible().notSortable()
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
            $scope.criteria.city = '';
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
            return '<a href="" ng-click="viewRemarks(' + data.book_id + ')">客服备注</a>';
        }

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
            .withOption('ajax', function (data, callback, settings) {

                if (($scope.provinceIndex !== '') && ($scope.criteria.city === ''))
                    return alert('请选择城市');

                $rootScope.loadingPromise = gpjService.getUserPromoCarAppointment({
                    draw: data.draw,
                    length: data.length,
                    start: data.start,
                    name: $scope.criteria.name ? $scope.criteria.name : '',
                    phone: $scope.criteria.phone ? $scope.criteria.phone : '',
                    city: $scope.criteria.city ? $scope.criteria.city : '',
                    process_status: $scope.criteria.process_status ? $scope.criteria.process_status : '',
                    start_time: $scope.criteria.startDate ? $scope.criteria.startDate : '',
                    end_time: $scope.criteria.endDate ? $scope.criteria.endDate : '',
                    order_by: 'process_time',
                    asc: 0

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
            $scope.records[data['book_id']] = data;
            //console.dir(data);
            $compile(angular.element(row).contents())($scope);
        }

        /**
         * View item info
         *
         * @param itemId
         */
        $scope.viewRemarks = function (itemId) {
            //alert('itemId = ' + itemId);
            //alert(JSON.stringify($scope.records[itemId]));

            gpjService.setUserAppointment($scope.records[itemId]);

            var modalInstance = $modal.open({
                templateUrl: 'views/gpj/user_appointment_remarks_modal.html',
                controller: 'userAppointmentRemarksModalController'
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
        $scope.searchAppointments = function (criteria) {
            $scope.dtInstance.rerender();
        };

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.provinceIndex = '';
            $scope.criteria = {
                name: '',
                phone: '',
                city: '',
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