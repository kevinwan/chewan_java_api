/**
 * Created by licheng on 2015/11/18.
 * 用户相册审核
 */

'use strict';

gpjApp.controller('userAlbumAuthenticationController', function ($scope, $rootScope, authenticationService, moment) {
        var DEFAULT_REMARKS = '照片违反车玩规定，给予删除';

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.criteria = {
                phone: "",
                startDate: moment().subtract(2, 'day').format('YYYY-MM-DD'),
                endDate: moment().format('YYYY-MM-DD'),
                checked: 0,
                remark: ""
            };
        };

        $scope.searchUserUncheckedPhotos = function () {
            authenticationService.getUserUncheckedPhotos($scope.criteria).success(function (response) {
                if (response && response.result == 0 && response.data) {
                    $scope.photos = response.data;
                }
            });
        }

        $scope.approvedPhotos = function () {
            authenticationService.processUserAlbumPhotos($scope.photos, $scope.criteria.remark).success(function (response) {
                if (response && response.result == 0 && response.data) {
                    alert("审核成功");
                } else {
                    alert("审核失败");
                }
            });
        }

        $scope.resetCriteria();

        $rootScope.loadingPromise = $scope.searchUserUncheckedPhotos();
    }
);
