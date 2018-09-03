package com.aaron.controller;

import com.aaron.pojo.Users;
import com.aaron.pojo.UsersReport;
import com.aaron.pojo.vo.PublisherVideo;
import com.aaron.pojo.vo.UsersVO;
import com.aaron.service.UserService;
import com.aaron.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@Api(value = "用户相关业务接口",tags = {"注册和登陆的Controller"})
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;

	@ApiOperation(value = "用户上传头像",notes = "用户头像接口")
	@ApiImplicitParam(name = "userId",value = "用户id",required = true,
			dataType = "String",paramType = "query")
	@PostMapping("/uploadFace")
	public IMoocJSONResult uploadFace(String userId,@RequestParam("file") MultipartFile[] files) throws Exception {
       if(StringUtils.isBlank(userId)){
           return IMoocJSONResult.errorMsg("用户Id不能为空");
       }

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        //文件保存空间
        String fileSpace = "D:/videos_dev";
        //数据库路径
        String uploadPathDB = "/" + userId + "/face";
	    try {


            if(files != null && files.length > 0){

                String fileName = files[0].getOriginalFilename();
                if(StringUtils.isNotBlank(fileName)){
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    //数据库保存路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalFacePath);
                    if(outFile.getParentFile() != null || outFile.getParentFile().isDirectory()){
                        //创建父目录
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }else {
                    return IMoocJSONResult.errorMsg("上传出错");
                }
            }
        }catch (Exception e){
		    e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错");
        }finally {
	        if(fileOutputStream != null){
	            fileOutputStream.flush();
	            fileOutputStream.close();
            }
        }
        Users user = new Users();
	    user.setId(userId);
	    user.setFaceImage(uploadPathDB);
	    userService.updateUserInfo(user);
		return IMoocJSONResult.ok(uploadPathDB);
	}



    @ApiOperation(value = "查询用户信息",notes = "查询用户信息接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,
            dataType = "String",paramType = "query")
    @PostMapping("/query")
    public IMoocJSONResult query(String userId,String fanId) throws Exception {
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo,usersVO);
        usersVO.setFollow(userService.queryIsFollow(userId,fanId));
        return IMoocJSONResult.ok(usersVO);
    }


    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,
                                          String publisherUserId) throws Exception {
        if(StringUtils.isBlank(publisherUserId)){
            return IMoocJSONResult.errorMsg("");
        }
        //1.查询视频发布者信息
        Users userInfo = userService.queryUserInfo(publisherUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo,publisher);
        //2.查询当前登陆者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);
        return IMoocJSONResult.ok(bean);
    }

    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId,String fanId) throws Exception {
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.saveUserFanRelation(userId,fanId);
        System.out.println("关注成功");
        return IMoocJSONResult.ok("关注成功...");
    }

    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyourfans(String userId,String fanId) throws Exception {
        System.out.println("进入dontbeyourfans");
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.deleteUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("取消关注成功...");
    }


    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {

        // 保存举报信息
        userService.reportUser(usersReport);

        return IMoocJSONResult.errorMsg("举报成功");
    }








}
