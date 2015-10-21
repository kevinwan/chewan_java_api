'use strict';


gpjApp.controller('officialActivityEditController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window', 'commonService', '$timeout',
    function ($scope, $rootScope, $location, officialActivityService, moment, $window, commonService, $timeout) {

        //$scope.items = [
        //    { id: 1, name: 'foo' },
        //    { id: 2, name: 'bar' },
        //    { id: 3, name: 'blah' }
        //];
        //
        //$scope.selectedItem = 2;


        /**
         * Cancel button click handler
         */
        $scope.close = function () {
            $location.path('/officialActivity/list');
        };

        /**
         * Get province and city info
         */
        commonService.getCities().success(function (data) {
            $scope.provinceOptions = data;
        });

        /**
         * Trigger by province change
         */
        $scope.changeProvince = function (province) {
            $scope.activity.destination.city = '';
            $scope.cityOptions = [];

            //找出 province 对应的 cityOptions
            for (var index in $scope.provinceOptions) {
                if ($scope.provinceOptions[index].province === province) {
                    $scope.cityOptions = $scope.provinceOptions[index].cities;
                    //$scope.$apply();
                    break;
                }
            }
        };

        $scope.initData = function () {
            var officialActivityId = officialActivityService.getOfficialActivityId();
            //var officialActivityId = $location.search()['id'];
            if (officialActivityId === '') {
                //增加
                $scope.activity = {destination: {}};
                $scope.activity.limitType = 0;
                return;
            } else {
                $rootScope.loadingPromise = officialActivityService.getOfficialActivity(officialActivityId).success(function (result) {
                    //获取数据成功
                    if (result.result === 0) {
                        //初始化时间
                        $scope.activity = result.data;
                        $scope.activity.onFlag = $scope.activity.onFlag ? 'true' : 'false';

                        //初始化 人数限制类型
                        var limitType = $scope.activity.limitType;
                        document.getElementById("limitType-" + limitType).checked = true;

                        //初始化省市 信息；
                        $scope.cityOptions = [];
                        for (var index in $scope.provinceOptions) {
                            if ($scope.provinceOptions[index].province === $scope.activity.destination.province) {
                                $scope.cityOptions = $scope.provinceOptions[index].cities;
                                break;
                            }
                        }

                        //初始化封面信息；
                        $scope.photoUrl = $scope.activity.cover.url;

                        //初始化 开始 结束 时间信息；
                        document.getElementById("startDate").value = commonService.transferLongToDateString($scope.activity.start);
                        document.getElementById("startTime").value = commonService.transferLongToTimeString($scope.activity.start);
                        //结束时间可能不存在
                        if ($scope.activity.end != undefined && $scope.activity.end != null && $scope.activity.end !== "") {
                            document.getElementById("endDate").value = commonService.transferLongToDateString($scope.activity.end);
                            document.getElementById("endTime").value = commonService.transferLongToTimeString($scope.activity.end);
                        }
                    }
                });
            }
        };

        /**
         * 检查时间 并且初始化时间
         * @returns {boolean}
         */
        function checkTime() {
            var startDate = document.getElementById("startDate").value;
            var startTime = document.getElementById("startTime").value;
            var endDate = document.getElementById("endDate").value;
            var endTime = document.getElementById("endTime").value;
            if (startDate == undefined || startDate == null || startDate == "") {
                $window.alert("请选择开始时间");
                return false;
            }
            if (startTime == undefined || startTime == null || startTime == "") {
                $window.alert("请选择开始时间");
                return false;
            }
            if (endDate != undefined && endDate != null && endDate != "") {
                if (endTime == undefined || endTime == null || endTime == "") {
                    $window.alert("请选择结束时间");
                    return false;
                }
            }
            if (endTime != undefined && endTime != null && endTime != "") {
                if (endDate == undefined || endDate == null || endDate == "") {
                    $window.alert("请选择结束时间");
                    return false;
                }
            }
            var startStr = startDate + " " + startTime;
            var endStr = endDate + " " + endTime;

            var start = new Date(startStr);
            var end = new Date(endStr);
            $scope.activity.start = start.getTime();

            if (endStr !== " ") {
                $scope.activity.end = end.getTime();
            } else {
                $scope.activity.end = null;
            }
            return true;
        };


        /**
         * 更新官方活动
         */
        $scope.updateOfficialActivity = function () {
            if (checkTime() && validateAll()) {
                $rootScope.loadingPromise = officialActivityService.updateOfficialActivity($scope.activity.officialActivityId, $scope.activity).success(function (result) {
                    if (result.result == 0) {
                        $window.alert("更新成功");
                        $location.path('/officialActivity/list');
                    } else {
                        $window.alert("更新失败");
                        $location.path('/officialActivity/list');
                    }
                });
            }
        };

        /**
         * register
         * 注册 官方活动;
         */
        $scope.register = function () {
            if (checkTime() && validateAll()) {
                //初始化 省份信息 angular上绑定的 provinceIndex 信息；
                //$scope.activity.destination.province = $scope.provinceOptions[$scope.provinceIndex].province;
                $rootScope.loadingPromise = officialActivityService.saveOfficialActivity($scope.activity).success(function (data) {
                    if (data.result == 0) {
                        $window.alert("创建成功");
                        $location.path('/officialActivity/list');
                    } else {
                        $window.alert("创建失败 请检查参数");
                    }
                });
            }
        };


        /**
         * 上传图片 并返回一个 cover 对象；
         * @param data
         */
        $scope.uploadFile = function (data) {
            var formData = new FormData();
            formData.append('attach', data.files[0]);

            $rootScope.loadingPromise = officialActivityService.uploadFile(formData).success(function (result) {
                if (result.result === 0) {
                    var cover = {};
                    cover.id = result.data.photoId;
                    cover.photoUrl = result.data.photoUrl;
                    cover.key = result.data.photoKey;
                    $scope.activity.cover = cover;
                    $scope.photoUrl = cover.photoUrl;
                }
            });
        };


        /**
         * 修改 人数限制类型 并且对应着不同的 样式 表单
         * @param data
         */
        $scope.changeLimitType = function (data) {
            $scope.activity.limitType = parseInt(data.value);
            $scope.$apply();
        };

        /**
         * 判断 上架 上架中 下架状态
         * @param onFlag
         * @param end
         * @returns {number}
         */
        $scope.checkOnItemStatus = function (onFlag, end) {
            if (onFlag == false) {
                //未上架
                return 0;
            } else {
                //上架中
                if (end == undefined || end == null || end == "") {
                    return 1;
                } else {
                    var nowTime = new Date().getTime();
                    //当前时间大于 活动 截止时间 活动处于下架状态
                    if (nowTime > end) {
                        return 2;
                    } else {
                        //活动没有到截止时间 处于 上架中
                        return 1;
                    }
                }
            }
        };


        /**
         * 校验表单 参数
         * @returns {boolean}
         */
        function validateAll() {
            if (commonService.isStrEmpty($scope.activity.title)) {
                $window.alert("活动标题不能为空");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.instruction)) {
                $window.alert("活动标题不能为空");
                return false;
            }
            if (commonService.isNull($scope.activity.destination) || commonService.isStrEmpty($scope.activity.destination.city)) {
                $window.alert("城市不能为空");
                return false;
            }
            if (commonService.isNull($scope.activity.destination) || commonService.isStrEmpty($scope.activity.destination.detail)) {
                $window.alert("目的地不能为空");
                return false;
            }
            if (commonService.isNull($scope.activity.cover)) {
                $window.alert("封面不能为空");
                return false;
            }
            var intRegex = /^[0-9]*$/;
            if ($scope.activity.limitType == 1) {
                //限制总人数
                //if($scope.activity.totalLimit === undefined || $scope.activity.totalLimit === ''){
                //    $window.alert("总设置人数不能为空");
                //}
                if (!intRegex.test($scope.activity.totalLimit)) {
                    $window.alert("请输入合法的总人数");
                    return false;
                }

                var totalLimit = parseInt($scope.activity.totalLimit)
                if (isNaN(totalLimit) || totalLimit <= 0) {
                    $window.alert("总人数必须大于0");
                    return false;
                }
            }
            if ($scope.activity.limitType == 2) {
                //分别检查男女参数
                if (!intRegex.test($scope.activity.maleLimit)) {
                    $window.alert("请输入合法的男性数目");
                    return false;
                }
                if (!intRegex.test($scope.activity.femaleLimit)) {
                    $window.alert("请输入合法的女性数目");
                    return false;
                }
                var maleLimit = parseInt($scope.activity.maleLimit)
                if (isNaN(maleLimit) || maleLimit < 0) {
                    $window.alert("男性数目必须大于大于0");
                    return false;
                }
                var femaleLimit = parseInt($scope.activity.femaleLimit)
                if (isNaN(femaleLimit) || femaleLimit < 0) {
                    $window.alert("女性数目必须大于大于0");
                    return false;
                }
                if (femaleLimit == 0 && maleLimit == 0) {
                    $window.alert("男女人数不能同时为0");
                    return false;
                }
            }
            var priceReg = /^[0-9]+\.{0,1}[0-9]{0,2}$/;
            if (!priceReg.test($scope.activity.price)) {
                $window.alert("请输入合法的价格");
                return false;
            }

            if (!commonService.isStrEmpty($scope.activity.subsidyPrice)) {
                if (!priceReg.test($scope.activity.subsidyPrice)) {
                    $window.alert("请输入合法的补贴价格");
                    return false;
                }
            }
            //参数合法
            return true;
        };

        //初始化相关参数；
        $scope.initData();
    }
]);