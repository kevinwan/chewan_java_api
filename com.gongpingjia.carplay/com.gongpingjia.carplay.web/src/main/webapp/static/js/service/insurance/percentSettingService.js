'use strict';

/**
 * Percent setting service
 * 
 * @constructor
 */
	
gpjApp.factory('percentSettingService', ['restProxyService', 'InsuranceApiHost', 'InsuranceApiPort', 'InsuranceApiPrefix', 'authService',
    function(restProxyService, InsuranceApiHost, InsuranceApiPort, InsuranceApiPrefix, authService){

	var setting;
	var flag;

	return{
		getFlag : function(){
			return flag;
		},
		setFlag : function(aFlag){
			flag = aFlag;
		},
		getSetting : function(){
			return setting;
		},
		setSetting : function(aSetting){
			setting = aSetting;
		},
		searchCompany : function (){
			return restProxyService.sendHttpGet(InsuranceApiHost + ':' + InsuranceApiPort , InsuranceApiPrefix + '/company/percent/list?access_token='
			+ authService.getUser().token + '&username=' + authService.getUser().name);
		},
		updateCompanyPercent : function (){
	    	return restProxyService.sendHttpPost(InsuranceApiHost + ':' + InsuranceApiPort , InsuranceApiPrefix + '/company/percent?access_token='
			+ authService.getUser().token + '&username=' + authService.getUser().name, setting);
	    },
		changeCompanyStatus : function (){
			return restProxyService.sendHttpPost(InsuranceApiHost + ':' + InsuranceApiPort , InsuranceApiPrefix + '/company/status?company='
			+ setting.company + '&invalid=' + ((setting.invalid + 1) % 2) + '&access_token='
			+ authService.getUser().token + '&username=' + authService.getUser().name);
		}
	  }
}]);