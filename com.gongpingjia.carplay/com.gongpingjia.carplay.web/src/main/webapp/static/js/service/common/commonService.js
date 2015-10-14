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

        transferDateStringToLong: function (dateTime) {
            if (dateTime == undefined) {
                return 0;
            }
            //var regexp = new RegExp("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}", "g");
            //if (regexp.test(dateTime)) {
            return Date.parse(dateTime.replace(/-/g, "/"));
            //}
            //return 1;
        }
    }
}]);