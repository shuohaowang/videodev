package com.aaron.controller;

import com.aaron.service.BgmService;
import com.aaron.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bgm")
@Api(value = "背景音乐接口",tags = {"背景音乐业务的Controller"})
public class BgmController {

	@Autowired
	private BgmService bgmService;

	@ApiOperation(value = "获取背景音乐列表",notes = "获取背景音乐列表接口")
	@PostMapping("/list")
	public IMoocJSONResult Hello() {
		return IMoocJSONResult.ok(bgmService.queryBgmList());
	}
	
}
