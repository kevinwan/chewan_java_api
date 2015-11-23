'use strict';


gpjApp.controller('activityAddController', ['$scope', '$rootScope', '$location', 'activityService', 'moment', '$window', 'commonService', '$q',
    function ($scope, $rootScope, $location, activityService, moment, $window, commonService, $q) {
        $scope.phoneArr = [];
        for (var phoneNum = 10012340001; phoneNum < 10012340063; phoneNum++) {
            $scope.phoneArr.push(phoneNum);
        }
        ;

        function randomInt(from, to) {
            return from + Math.floor(Math.random() * (to - from + 1));
        };

        function randomArr(arr) {
            return arr[randomInt(0, arr.length - 1)];
        };

        $scope.payOptions = ['不限', 'AA', '请我吧', '我请客'];

        $scope.transferOptions = [{'name': '是', 'value': true}, {'name': '否', 'value': false}];

        $scope.randomAll = function () {
            $scope.phone = randomArr($scope.phoneArr);
            //$scope.activity.majorType = $scope.allTypeOptions[randomInt(0, $scope.allTypeOptions.length - 1)].majorType;
            $scope.activity.majorType = randomArr($scope.allTypeOptions).majorType;
            for (var index in $scope.allTypeOptions) {
                if ($scope.allTypeOptions[index].majorType === $scope.activity.majorType) {
                    $scope.typeOptions = $scope.allTypeOptions[index].type;
                }
            }
            $scope.activity.type = randomArr($scope.typeOptions);
            $scope.activity.pay = randomArr($scope.payOptions);
            $scope.activity.transfer = randomArr($scope.transferOptions).value;
            $scope.randomEstablish();
        };

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

        $scope.getUserInfo = function (phone) {
            $rootScope.loadingPromise = activityService.getUserInfo(phone).success(function (result) {
                if (result.result == 0) {
                    $scope.nickname = result.data.nickname;
                    $scope.activity.userId = result.data.userId;
                } else {
                    alert(result.errmsg);
                }
            });
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
                        if ($scope.activity.destPoint === undefined) {
                            $scope.activity.destPoint = {};
                        }
                        $scope.activity.destPoint.longitude = longitude;
                        $scope.activity.destPoint.latitude = latitude;
                    } else {
                        if ($scope.activity.estabPoint === undefined) {
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
            $scope.destination = {};
            $scope.establish = {};
            $scope.getProvinceOptions();
        };
        $scope.initData();

        $scope.addActivity = function () {
            if (validateAll()) {
                if (!commonService.isNull($scope.destination) && !commonService.isNull($scope.destination.province)) {
                    $scope.activity.destination = {};
                    $scope.activity.destination.province = $scope.destination.province.name;
                    $scope.activity.destination.city = $scope.destination.city.name;
                    $scope.activity.destination.district = $scope.destination.district.name;
                    $scope.activity.destination.street = $scope.destination.street.name;
                }
                $scope.activity.establish = {};
                $scope.activity.establish.province = $scope.establish.province.name;
                $scope.activity.establish.city = $scope.establish.city.name;
                $scope.activity.establish.district = $scope.establish.district.name;
                $scope.activity.establish.street = $scope.establish.street.name;
                var postData = {'phone': $scope.phone, 'activity': $scope.activity};
                $rootScope.loadingPromise = activityService.saveActivity(postData).success(function (result) {
                    if (result.result === 0) {
                        alert("发布活动成功");
                        $location.path('/activity/list');
                    } else {
                        alert("发布活动失败" + result.errmsg);
                    }
                }).error(function () {
                    alert("网络异常")
                });
            }
        };


        $scope.randomEstablish = function () {
            $scope.establish.province = randomArr($scope.provinceOptions);
            $scope.initChildArea($scope.establish.province, 'estabCityOptions').then(function () {
                $scope.establish.city = randomArr($scope.estabCityOptions);
                return $scope.initChildArea($scope.establish.city, 'estabDistrictOptions');
            }).then(function () {
                $scope.establish.district = randomArr($scope.estabDistrictOptions);
                return $scope.initChildArea($scope.establish.district, 'estabStreetOptions');
            }).then(function () {
                $scope.establish.street = randomArr($scope.estabStreetOptions);
                $scope.randomPoint($scope.establish.street.code, 'establish');
            });
        }

    }
]);

