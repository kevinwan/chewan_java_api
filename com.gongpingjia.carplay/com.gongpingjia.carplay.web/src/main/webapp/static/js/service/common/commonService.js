/**
 * Created by 123 on 2015/10/13.
 */
'use strict';

/**
 * Chewan Service
 *
 * @constructor
 */

gpjApp.factory('commonService', [function () {

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
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " "
                + date.getHours() + ":" + date.getMinutes();
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
        }

    }
}]);