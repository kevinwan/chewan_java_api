/**
 * Created by 123 on 2015/10/13.
 */
'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('commonService', ['restProxyService', 'ChewanOfficialApiEndPoint', function (restProxyService, ChewanOfficialApiEndPoint) {

    return {
        transferLongToDateString: function (longTime) {
            if (longTime == undefined || longTime <= 1) {
                return "";
            }

            var date = new Date();
            date.setTime(longTime);
            //format  YYYY-MM-DD
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
        },

        transferLongToDateTimeString: function (longTime) {
            if (longTime == undefined || longTime <= 1) {
                return "";
            }

            var date = new Date();
            date.setTime(longTime);
            //format  YYYY-MM-DD HH24:MM

            var hourStr = date.getHours() >= 10 ? date.getHours() : '0' + date.getHours();
            var minuteStr = date.getMinutes() >= 10 ? date.getMinutes() : '0' + date.getMinutes();

            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " "
                + hourStr + ":" + minuteStr;
        },

        transferLongToTimeString: function (longTime) {
            if (longTime == undefined || longTime <= 1) {
                return "";
            }

            var date = new Date();
            date.setTime(longTime);
            //format  YYYY-MM-DD HH24:MM
            var hourStr = date.getHours();
            if (hourStr < 10) {
                hourStr = "0" + hourStr;
            }
            var minuteStr = date.getMinutes();
            if (minuteStr < 10) {
                minuteStr = "0" + minuteStr;
            }
            return hourStr + ":" + minuteStr;
        },


        transferDateStringToLong: function (dateTime) {
            if (dateTime == undefined) {
                return 0;
            }
            return Date.parse(dateTime.replace(/-/g, "/"));
        },

        transferDateTimeStringToLong: function (dateTime) {
            if (dateTime == undefined) {
                return 0;
            }

            var data = dateTime.split(" ");
            var hour = data[1].substring(0, data[1].indexOf(":"));
            var minute = data[1].substring(hour.length + 1);

            var milliseconds = this.transferDateStringToLong(data[0]);
            milliseconds += parseInt(hour) * (60 * 60 * 1000);
            milliseconds += parseInt(minute) * (60 * 1000);

            return milliseconds;
        },

        getCities: function () {
            return restProxyService.sendHttpGet('resource/cities.json', '');
        },
        getActivityTypes: function () {
            return restProxyService.sendHttpGet('resource/type.json', '');
        },
        isDefined: function (str) {
            return (str && (str !== 'undefined'));
        },
        isNull: function (source) {
            return (source === undefined || source === null);
        },
        isStrEmpty: function (str) {
            return (str === undefined || str === null || str === "");
        },
        transferIllegalToEmpty: function (str) {
            if (str === undefined || str === null) {
                return "";
            } else {
                return str;
            }
        },
        getAreaRangeInfo: function (code) {
            return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, '/areaRange/info?code=' + code)
        }
    }
}]);