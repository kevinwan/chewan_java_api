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
            if (longTime == undefined || longTime == 0) {
                return "";
            }

            var birthdayDate = new Date();
            birthdayDate.setTime(longTime);
            //format  YYYY-MM-DD
            return birthdayDate.getFullYear() + "-" + (birthdayDate.getMonth() + 1) + "-" + birthdayDate.getDate();
        },

        transferDateStringToLong: function (dateTime) {
            if (dateTime == undefined) {
                return 0;
            }
            var regexp = new RegExp("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}", "g");
            if (regexp.test(dateTime)) {
                return Date.parse(dateTime.replace(/-/g, "/"));
            }
            return 1;
        }
    }
}]);