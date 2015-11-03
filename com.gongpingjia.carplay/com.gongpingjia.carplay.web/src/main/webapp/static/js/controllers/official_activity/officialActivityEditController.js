'use strict';


gpjApp.controller('officialActivityEditController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window', 'commonSer' +
'vice', '$timeout', '$routeParams',
    function ($scope, $rootScope, $location, officialActivityService, moment, $window, commonService, $timeout, $routeParams) {
        /**
         * Cancel button click handler
         */
        $scope.close = function () {
            $location.path('/officialActivity/list');
        };

        $scope.showMeridian = false;

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


        $scope.changeFree = function(){
            if($scope.freeFlag) {
                //免费 price
                document.getElementById("price").disabled = true;
                document.getElementById("subsidyPrice").disabled = true;

                $scope.activity.price = 0;
                $scope.activity.subsidyPrice = 0;
            }else{
                document.getElementById("price").disabled = false;
                document.getElementById("subsidyPrice").disabled = false;
            }
        };

        $scope.initData = function () {
            var officialActivityId = $routeParams.id;
            if (officialActivityId === undefined || officialActivityId === '') {
                //增加
                $scope.activity = {destination: {}};
                $scope.activity.limitType = 0;

                $scope.freeFlag = false;
            } else {
                $rootScope.loadingPromise = officialActivityService.getOfficialActivity(officialActivityId).success(function (result) {
                    //获取数据成功
                    if (result.result === 0) {
                        $scope.activity = result.data;

                        //上下架
                        $scope.activity.onFlag = $scope.activity.onFlag ? 'true' : 'false';

                        //是否免费
                        if($scope.activity.price == 0) {
                            $scope.freeFlag = true;
                        }else{
                            $scope.freeFlag = false;
                        }

                        //人数限制类型
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
                        //document.getElementById("startTime").value = commonService.transferLongToTimeString($scope.activity.start);
                        if ($scope.startTime === undefined) {
                            $scope.startTime = {};
                        }
                        if ($scope.endTime === undefined) {
                            $scope.endTime = {};
                        }
                        $scope.startTime.date = commonService.transferLongToDateString($scope.activity.start);
                        $scope.startTime.time = new Date($scope.activity.start);
                        //结束时间可能不存在
                        if ($scope.activity.end != undefined && $scope.activity.end != null && $scope.activity.end !== "") {
                            document.getElementById("endDate").value = commonService.transferLongToDateString($scope.activity.end);
                            $scope.endTime.date = commonService.transferLongToDateString($scope.activity.end);
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
            if (commonService.isNull($scope.startTime)) {
                $window.alert("请选择开始时间");
                return false;
            }
            if (commonService.isStrEmpty($scope.startTime.date) || commonService.isStrEmpty($scope.startTime.time)) {
                $window.alert("请选择开始时间");
                return false;
            }


            //初始化绑定参数时间
            var startStr = $scope.startTime.date + " " + $scope.startTime.time.getHours() + ":" + $scope.startTime.time.getMinutes();
            $scope.activity.start = new Date(startStr).getTime();
            if ($scope.activity.start < new Date().getTime()) {
                $window.alert("开始时间必须大于当前时间");
                return false;
            }

            if (!commonService.isNull($scope.endTime) && $scope.endTime.date !== undefined && $scope.endTime.date !== '') {
                var endStr = $scope.endTime.date;
                var tempTimeLong = new Date(endStr).getTime();
                if(isNaN(tempTimeLong)){
                    alert("请选择合法的结束时间");
                    return false;
                }
                $scope.activity.end = tempTimeLong;
                if ($scope.activity.end + 24*60*60*1000  <= $scope.activity.start) {
                    $window.alert("结束时间必须大于开始时间");
                    return false;
                }
            } else {
                $scope.activity.end = null;
            }

            //校验通过
            return true;
        };


        /**
         * 更新官方活动
         */
        $scope.updateOfficialActivity = function () {
            if (checkTime() && validateAll()) {
                if ($scope.activity.end === undefined || $scope.activity.end === null) {
                    $scope.activity.end = '';
                }

                //高德bug
                $scope.activity.destination.detail = document.getElementById('keyword').value;
                if ($scope.activity.onFlag === true || $scope.activity.onFlag === 'true') {
                    if (!confirm("上架以后活动信息将无法修改!请确定是否上架?")) {
                        return;
                    }
                }
                $rootScope.loadingPromise = officialActivityService.updateOfficialActivity($scope.activity.officialActivityId, $scope.activity).success(function (result) {
                    if (result.result == 0) {
                        $window.alert("更新成功");
                        $location.path('/officialActivity/list');
                    } else {
                        $window.alert("更新失败");
                        $location.path('/officialActivity/list');
                    }
                }).error(function(result){
                    alert("更新失败" + result.errmsg);
                });
            }
        };


        /**
         * 上架中 只能更新官方活动的 人数类型   已经 人数限制
         */
        $scope.updateLimit = function () {
            //validator limitType  and limit number;
            var data = {};
            data.limitType = $scope.activity.limitType;
            if ($scope.activity.limitType === 0 || $scope.activity.limitType === '0') {
                //0 无限制
            } else if ($scope.activity.limitType === 1 || $scope.activity.limitType === '1') {
                //1 限制总人数;
                if ($scope.activity.nowJoinNum > $scope.activity.totalLimit) {
                    $window.alert("总人数限制不能小于当前总人数");
                    return;
                }
                data.totalLimit = $scope.activity.totalLimit;
            } else if ($scope.activity.limitType === 2 || $scope.activity.limitType === '2') {
                //2 分别限制 男性 女性 数量；
                if ($scope.activity.maleNum > $scope.activity.maleLimitNum) {
                    $window.alert("男性人数限制必须大于现在参加的男性数目");
                    return;
                }
                if ($scope.activity.femaleNum > $scope.activity.femaleLimit) {
                    $window.alert("女性人数限制必须大于现在参加的女性数目");
                    return;
                }

                data.maleLimit = $scope.activity.maleLimit;
                data.femaleLimit = $scope.activity.femaleLimit;
            }

            $rootScope.loadingPromise = officialActivityService.updateLimit($scope.activity.officialActivityId, data).success(function (result) {
                if (result.result === 0) {
                    $window.alert("设置成功");
                    $location.path('/officialActivity/list');
                } else {
                    $window.alert("设置失败 " + result.errmsg);
                }
            });
        };

        /**
         * register
         * 注册 官方活动;
         */
        $scope.register = function () {
            if (checkTime() && validateAll()) {
                //高德数字bug
                $scope.activity.destination.detail = document.getElementById('keyword').value;
                if ($scope.activity.onFlag === true || $scope.activity.onFlag === 'true') {
                    if (!confirm("上架以后活动信息将无法修改!请确定是否上架?")) {
                        return;
                    }
                }
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
            if ($scope.activity.title.length > 36) {
                $window.alert("标题不能超过36个字符");
                return false;
            }
            if (commonService.isStrEmpty($scope.activity.instruction)) {
                $window.alert("活动介绍不能为空");
                return false;
            }
            if (commonService.isNull($scope.activity.destination) || commonService.isStrEmpty($scope.activity.destination.province)) {
                $window.alert("省不能为空");
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