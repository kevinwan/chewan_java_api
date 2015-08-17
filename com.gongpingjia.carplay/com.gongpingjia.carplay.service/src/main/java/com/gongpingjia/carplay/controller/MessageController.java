package com.gongpingjia.carplay.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.MessageService;
import com.gongpingjia.carplay.service.impl.ParameterCheck;
/**
 * 
 * */
@RestController
public class MessageController {

	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping(value="/user/{userId}/application/list" ,method=RequestMethod.GET)
	public ResponseDo getApplicationList(@PathVariable("userId") String userId,  HttpServletRequest  req){
		
		LOG.debug("'=> getApplicationList");
		String token=req.getParameter("token");
		int ignore=req.getParameter("ignore")==null?0:Integer.valueOf(req.getParameter("ignore"));
		int limit=req.getParameter("limit")==null?10:Integer.valueOf(req.getParameter("limit"));
		
		try {
			ParameterCheck.getInstance().checkUserInfo(userId, token);
			return messageService.getApplicationList(userId, token, ignore, limit);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
		
	}

}
