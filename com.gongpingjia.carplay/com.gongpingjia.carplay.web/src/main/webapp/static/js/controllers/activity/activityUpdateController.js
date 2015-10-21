'use strict';


gpjApp.controller('activityUpdateController', ['$scope', '$rootScope', '$location', 'activityService', 'moment', '$window', 'commonService', '$q','$routeParams',
    function ($scope, $rootScope, $location, activityService, moment, $window, commonService, $q,$routeParams) {


        /**
         * Cancel button click handler
         */
        $scope.close = function () {
            $location.path('/activity/list');
        };


        $scope.getProvinceOptions = function () {

            var deferred = $q.defer();
            //获取省份信息
            $rootScope.loadingPromise = activityService.getAreaInfo(0).success(function (result) {
                //更新成功
                if (result.result === 0) {
                    $scope.provinceOptions = result.data;
                    deferred.resolve();
                } else {
                    $window.alert("请检查网络状态");
                }
            });

            return deferred.promise;
        };


        $scope.initChildArea = function (code, type) {
            var deferred = $q.defer();

            $rootScope.loadingPromise = activityService.getAreaInfo(code).success(function (result) {
                //更新成功
                if (result.result === 0) {
                    $scope[type] = result.data;
                    deferred.resolve();
                } else {
                    $window.alert("请检查网络状态");
                }
            });

            return deferred.promise;
        };

        $scope.initData = function () {
            var activityId = $routeParams.id;
            $scope.destCityOptions = [];
            $scope.destDistrictOptions = [];
            $scope.destStreetOptions = [];

            $scope.estabCityOptions = [];
            $scope.estabDistrictOptions = [];
            $scope.estabStreetOptions = [];

            if (activityId === '') {
                return;
            }


            $rootScope.loadingPromise = activityService.viewActivity(activityId).success(function (result) {
                //获取数据成功
                if (result.result === 0) {
                    //初始化时间
                    $scope.activity = result.data;
                    $scope.activity.transfer = $scope.activity.transfer ? 'true' : 'false';

                    $scope.getProvinceOptions().then(function () {
                        //初始化 两个 地址信息
                        $scope.destProvince = searchFromAreaArr($scope.provinceOptions, $scope.activity.destination.province);
                        $scope.initChildArea($scope.destProvince.code, 'destCityOptions').then(function () {
                            $scope.destCity = searchFromAreaArr($scope.destCityOptions, $scope.activity.destination.city);
                            return $scope.initChildArea($scope.destCity.code, 'destDistrictOptions');
                        }).then(function () {
                            $scope.destDistrict = searchFromAreaArr($scope.destDistrictOptions, $scope.activity.destination.district);
                            return $scope.initChildArea($scope.destDistrict.code, 'destStreetOptions');
                        }).then(function () {
                            $scope.destStreet = searchFromAreaArr($scope.destStreetOptions, $scope.activity.destination.street);
                        });

                        $scope.estabProvince = searchFromAreaArr($scope.provinceOptions, $scope.activity.establish.province);
                        $scope.initChildArea($scope.estabProvince.code, 'estabCityOptions').then(function () {
                            $scope.estabCity = searchFromAreaArr($scope.estabCityOptions, $scope.activity.establish.city);
                            return $scope.initChildArea($scope.estabCity.code, 'estabDistrictOptions');
                        }).then(function () {
                            $scope.estabDistrict = searchFromAreaArr($scope.estabDistrictOptions, $scope.activity.establish.district);
                            return $scope.initChildArea($scope.estabDistrict.code, 'estabStreetOptions');
                        }).then(function () {
                            $scope.estabStreet = searchFromAreaArr($scope.estabStreetOptions, $scope.activity.establish.street);
                        });
                    });

                } else {
                    $window.alert("获取数据失败");
                }
                var name = 1;
            });
        };

        $scope.updateActivity = function () {
            // 将地址信息装换;
            $scope.activity.destination.province = $scope.destProvince.name;
            $scope.activity.destination.city = $scope.destCity.name;
            $scope.activity.destination.district = $scope.destDistrict.name;
            $scope.activity.destination.street = $scope.destStreet.name;

            $scope.activity.establish.province = $scope.estabProvince.name;
            $scope.activity.establish.city = $scope.estabCity.name;
            $scope.activity.establish.district = $scope.estabDistrict.name;
            $scope.activity.establish.street = $scope.estabStreet.name;


            $rootScope.loadingPromise = activityService.updateActivity($scope.activity, $scope.activity.activityId).success(function (result) {
                //更新成功
                if (result.result === 0) {
                    $window.alert("更新成功");
                    $location.path("/activity/list");
                } else {
                    $window.alert("更新失败" + result.errmsg);
                }
            });
        };

        function searchFromAreaArr(arr, placeName) {
            for (var index in arr) {
                if (arr[index].name === placeName) {
                    return arr[index];
                }
            }
            return null;
        }


        $scope.initData();
    }
]);
