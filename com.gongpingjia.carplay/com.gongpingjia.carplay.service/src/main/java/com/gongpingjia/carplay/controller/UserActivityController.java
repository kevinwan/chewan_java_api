package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.service.UserActivityService;


/**
 * @author zhou shuofu
 * 
 * 2.21,2.22,2.23
 * 活动的发布，关注，参与
 * */
@RestController
public class UserActivityController {
	// 2.21 我(TA)的发布
	/*
	 * req,res $userId1,$userId2,$token,$ignore,$limit ResponseDO
	 * getUserPost(1,2,3,4,5); 参数是否正确 ResponseDO
	 * service.UserActivityService.getUserPost(1,2,3,4,5);
	 * 
	 * 校验token
	 * 
	 * 查询此人发布的活动(activityId,publishTime,start,introduction,location,pay) select
	 * id as activityId, createTime as publishTime, ifnull(start, 0) as start,
	 * ifnull(description, "") as introduction, location, paymentType as pay
	 * from activity where organizer ='da51944f-ab85-4296-83f9-02603cb6937f'
	 * order by publishTime desc limit 0,10
	 * 
	 * 转日期时间(publishDate,startDate)
	 * 
	 * 查询活动成员(members{userId,nickname,photo}) select user.id as userId,
	 * user.nickname, concat('getAssetUrl()', user.photo) as photo from
	 * activity_member, user where
	 * activity_member.activityId='00ec5ff7-eaef-4ef1-a603-c6fb921cc49d' and
	 * activity_member.userId = user.id
	 * 
	 * 查询活动图片(url) select concat('getAssetUrl() +', url) as url from
	 * activity_cover where activityId='00ec5ff7-eaef-4ef1-a603-c6fb921cc49d'
	 * order by uploadTime 转为缩放图(cover{thumbnail_pic})
	 * 
	 * 转为map
	 * 
	 * 返回responseDO
	 */
	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private UserActivityService userActivityService;

	/**
	 * 2.21 我(TA)的发布
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动发布列表信息
	 */
	@RequestMapping(value = "/user/{userId1}/post", method = RequestMethod.GET)
	public ResponseDo getUserPost(@PathVariable String userId1, @RequestParam(value = "userId") String userId2,
			@RequestParam(value = "token") String token,
			@RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {

		LOG.info("=> getUserPost");
		
		if (!CodeGenerator.isUUID(userId1) || !CodeGenerator.isUUID(userId2) || !CodeGenerator.isUUID(token)) {
			LOG.error("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			return userActivityService.getUserPost(userId1, userId2, token, ignore, limit);
		} catch (ApiException e) {
			LOG.error("getUserPost(): ");
			return ResponseDo.buildFailureResponse("获取发布列表失败");
		}
	}

}
