'use strict';


gpjApp.controller('activityUpdateController', ['$scope', '$rootScope', '$location', 'activityService', 'moment', '$window', 'commonService', '$q', '$routeParams',
    function ($scope, $rootScope, $location, activityService, moment, $window, commonService, $q, $routeParams) {


        $scope.close = function () {
            $location.path('/activity/list');
        };

        commonService.getActivityTypes().success(function (data) {
            $scope.allTypeOptions = data;
        });

        $scope.changeMajorType = function (majorType) {
            if (majorType === undefined || majorType === null || majorType === "") {
                return;
            }
            for (var index in $scope.allTypeOptions) {
                if ($scope.allTypeOptions[index].majorType === majorType) {
                    $scope.typeOptions = $scope.allTypeOptions[index].type;
                }
            }
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

        $scope.initChildArea = function (item, type) {
            var deferred = $q.defer();
            if (item === undefined || item === null) {
                deferred.reject('error');
                return deferred.promise;
            }

            //排除 直辖市的划分  11北京 12 天津 31 上海 50 重庆
            if (type.indexOf('CityOptions') != -1) {
                if (item.code == 11 || item.code == 12 || item.code == 31 || item.code == 50) {
                    var tempAreaList = [];
                    tempAreaList.push(item);
                    $scope[type] = tempAreaList;
                    deferred.resolve();
                    return deferred.promise;
                }
            }
            $rootScope.loadingPromise = activityService.getAreaInfo(item.code).success(function (result) {
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

        $scope.randomPoint = function (code, type) {
            $rootScope.loadingPromise = commonService.getAreaRangeInfo(code).success(function (result) {
                if (result.result === 0) {
                    var maxLongitude = result.data.maxLongitude;
                    var minLongitude = result.data.minLongitude;
                    var maxLatitude = result.data.maxLatitude;
                    var minLatitude = result.data.minLatitude;
                    var longitude = minLongitude + (maxLongitude - minLongitude) * Math.random();
                    var latitude = minLatitude + (maxLatitude - minLatitude) * Math.random();
                    if ($scope.activity === undefined) {
                        $scope.activity = {};
                    }
                    if (type === 'destination') {
                        if (commonService.isEmpty($scope.activity.destPoint)) {
                            $scope.activity.destPoint = {};
                        }
                        $scope.activity.destPoint.longitude = longitude;
                        $scope.activity.destPoint.latitude = latitude;
                    } else {
                        if ($scope.activity.estabPoint === undefined ) {
                            $scope.activity.estabPoint = {};
                        }
                        $scope.activity.estabPoint.longitude = longitude;
                        $scope.activity.estabPoint.latitude = latitude;
                    }
                } else {
                    $scope[item] = false;
                    alert("该区域无法获取经纬度范围");

                }
            }).error(function () {
                alert("服务器获取数据失败，请检查网络状况")
            });
        };

        function validateAll() {
            if (commonService.isNull($scope.activity)) {
                alert("活动类型不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.majorType)) {
                alert("活动类型不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.type)) {
                alert("活动子类型不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.pay)) {
                alert("活动子类型不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.transfer)) {
                alert("包接送不能为空");
                return false;
            }
            if (commonService.isNull($scope.establish)) {
                alert("活动建立地不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.establish.province)) {
                alert("活动建立地省不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.establish.city)) {
                alert("活动建立地城市不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.establish.district)) {
                alert("活动建立地区不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.establish.street)) {
                alert("活动建立地街道不能为空");
                return false;
            }
            if (commonService.isNull($scope.activity.estabPoint)) {
                alert("活动建立地经纬度不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.estabPoint.longitude)) {
                alert("活动建立地 经度不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.estabPoint.latitude)) {
                alert("活动建立地 纬度不能为空");
                return false;
            }

            return true;
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

                    for (var index in $scope.allTypeOptions) {
                        if ($scope.allTypeOptions[index].majorType === $scope.activity.majorType) {
                            $scope.typeOptions = $scope.allTypeOptions[index].type;
                        }
                    }

                    $scope.getProvinceOptions().then(function () {
                        //初始化 两个 地址信息
                        if ($scope.activity.destination != null && $scope.activity.destination.province != null) {
                            $scope.destination = {};
                            $scope.destination.province = searchFromAreaArr($scope.provinceOptions, $scope.activity.destination.province);
                            $scope.initChildArea($scope.destination.province, 'destCityOptions').then(function () {
                                $scope.destination.city = searchFromAreaArr($scope.destCityOptions, $scope.activity.destination.city);
                                return $scope.initChildArea($scope.destination.city, 'destDistrictOptions');
                            }).then(function () {
                                $scope.destination.district = searchFromAreaArr($scope.destDistrictOptions, $scope.activity.destination.district);
                                return $scope.initChildArea($scope.destination.district, 'destStreetOptions');
                            }).then(function () {
                                $scope.destination.street = searchFromAreaArr($scope.destStreetOptions, $scope.activity.destination.street);
                            }).catch(function (err) {
                                console.dir(err);
                            });
                        }

                        $scope.establish = {};
                        $scope.establish.province = searchFromAreaArr($scope.provinceOptions, $scope.activity.establish.province);
                        $scope.initChildArea($scope.establish.province, 'estabCityOptions').then(function () {
                            $scope.establish.city = searchFromAreaArr($scope.estabCityOptions, $scope.activity.establish.city);
                            return $scope.initChildArea($scope.establish.city, 'estabDistrictOptions');
                        }).then(function () {
                            $scope.establish.district = searchFromAreaArr($scope.estabDistrictOptions, $scope.activity.establish.district);
                            return $scope.initChildArea($scope.establish.district, 'estabStreetOptions');
                        }).then(function () {
                            $scope.establish.street = searchFromAreaArr($scope.estabStreetOptions, $scope.activity.establish.street);
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
            if (!commonService.isNull($scope. destination)) {
                $scope.activity.destination.province = $scope.destination.province.name;
                $scope.activity.destination.city = $scope.destination.city.name;
                $scope.activity.destination.district = $scope.destination.district.name;
                $scope.activity.destination.street = $scope.destination.street.name;
            }
            $scope.activity.establish.province = $scope.establish.province.name;
            $scope.activity.establish.city = $scope.establish.city.name;
            $scope.activity.establish.district = $scope.establish.district.name;
            $scope.activity.establish.street = $scope.establish.street.name;


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
