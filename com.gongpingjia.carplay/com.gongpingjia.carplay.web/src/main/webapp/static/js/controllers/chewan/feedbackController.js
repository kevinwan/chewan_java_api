'use strict';

/**
 * Feedback controller
 *
 * @constructor
 */
gpjApp.controller('feedbackController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'chewanService',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, chewanService, moment) {

        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [DTColumnDefBuilder.newColumnDef(0),
            DTColumnDefBuilder.newColumnDef(1),
            DTColumnDefBuilder.newColumnDef(2),
            DTColumnDefBuilder.newColumnDef(3),
            DTColumnDefBuilder.newColumnDef(4).notSortable(),
            DTColumnDefBuilder.newColumnDef(5)];

        /**
         * Data table row click handler
         */
        var rowClickHandler = function (info) {
        };

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap().withOption('rowCallback',
            function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                $('td', nRow).unbind('click');
                $('td', nRow).bind('click', function () {
                    $scope.$apply(function () {
                        rowClickHandler(aData);
                    });
                });
                return nRow;
            }).withOption('order', [0, 'desc']);

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            var today = '';
            $scope.criteria = {status: '0', startDate: today, endDate: today};
        };

        /**
         * Search feedbacks based on criteria
         */
        $scope.searchFeedbacks = function (criteria) {
            //alert(JSON.stringify(criteria));

            $rootScope.loadingPromise = chewanService.getFeedbackList(criteria).success(function (result) {
                $scope.feedbacks = (result.result === 0 ? result.data : undefined);
            });
        };

        /**
         * View feedback detail info
         */
        $scope.viewFeedback = function (feedbackId) {
            chewanService.setFeedback(feedbackId);
            var modalInstance = $modal.open({
                templateUrl: 'views/chewan/feedback_info_modal.html',
                controller: 'feedbackInfoModalController'
            });

            return modalInstance.result.then(function (reply) {
                $scope.searchFeedbacks($scope.criteria);
            });
        };

        /**
         * Initialize component status
         */
        $scope.resetCriteria();
    }]);