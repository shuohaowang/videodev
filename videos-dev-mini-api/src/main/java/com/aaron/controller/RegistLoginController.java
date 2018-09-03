package com.aaron.controller;

import com.aaron.pojo.Users;
import com.aaron.pojo.vo.UsersVO;
import com.aaron.service.UserService;
import com.aaron.utils.IMoocJSONResult;
import com.aaron.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(value = "用户注册登陆接口",tags = {"注册和登陆的Controller"})
public class RegistLoginController extends BasicController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "用户注册",notes = "用户注册接口")
	@PostMapping("/regist")
	public IMoocJSONResult regist(@RequestBody Users user) throws Exception {

		//1.判断用户名密码不为空
		if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
			return IMoocJSONResult.errorMsg("用户名和密码不能为空");
		}
		//2.判断用户名是否存在
		boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
		//3.保存用户
		if(!usernameIsExist){
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setReceiveLikeCounts(0);
			user.setFollowCounts(0);
			user.setFansCounts(0);
			user.setFaceImage(null);
			userService.saveUser(user);
		}else {
	 		return IMoocJSONResult.errorMsg("用户名已经存在");
		}
		user.setPassword("");
		UsersVO usersVO = setUserRedisSessionToken(user);
		return IMoocJSONResult.ok(usersVO);
	}

	public UsersVO setUserRedisSessionToken(Users userModel){
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION + ":" + userModel.getId(),uniqueToken, 1000 * 60 * 30);
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(userModel,usersVO);
		usersVO.setUserToken(uniqueToken);
		return usersVO;
	}


	@ApiOperation(value = "用户登陆",notes = "用户登陆接口")
	@PostMapping("/login")
	public IMoocJSONResult login(@RequestBody Users user) throws Exception {
		String username = user.getUsername();
		String password = user.getPassword();
		//1.判断用户名密码不为空
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			return IMoocJSONResult.ok("用户名或密码不能为空");
		}
		//2.判断用户是否存在
		Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
			//3.返回
			if(userResult != null){
			userResult.setPassword("");
			UsersVO usersVO = setUserRedisSessionToken(userResult);
			return IMoocJSONResult.ok(usersVO);
		}else {
			return IMoocJSONResult.errorMsg("用户名或者密码不正确，请重试！");
		}
	}


	@ApiOperation(value = "用户注销",notes = "用户注销接口")
	@ApiImplicitParam(name = "userId",value = "用户id",required = true,
			dataType = "String",paramType = "query")
	@PostMapping("/logout")
	public IMoocJSONResult logout(String userId) throws Exception {
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return IMoocJSONResult.ok("注销成功");
	}


}
