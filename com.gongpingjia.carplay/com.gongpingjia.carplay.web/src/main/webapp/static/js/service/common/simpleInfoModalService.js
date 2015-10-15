'use strict';

/**
 * simpleInfoModalService
 *
 * @constructor
 */

gpjApp.factory('simpleInfoModalService', function () {
        var info;
        return {
            getInfo: function () {
                return info;
            },
            setInfo: function(aInfo){
                info = aInfo;
            }
        }
    });