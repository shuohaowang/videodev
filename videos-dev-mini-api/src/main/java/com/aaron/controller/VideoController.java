package com.aaron.controller;

import com.aaron.enums.VideoStatusEnum;
import com.aaron.pojo.Bgm;
import com.aaron.pojo.Comments;
import com.aaron.pojo.Videos;
import com.aaron.service.BgmService;
import com.aaron.service.VideoService;
import com.aaron.utils.FetchVideoCover;
import com.aaron.utils.IMoocJSONResult;
import com.aaron.utils.MergeVideoMp3;
import com.aaron.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/video")
@Api(value = "视频相关业务",tags = {"视频业务业务的Controller"})
public class VideoController extends BasicController{


	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideoService videoService;


	@ApiOperation(value = "用户上传视频",notes = "用户上传视频接口")
	@ApiImplicitParams({@ApiImplicitParam(name = "userId",value = "用户id",required = true,
			dataType = "String",paramType = "form"),
			@ApiImplicitParam(name = "bgmId",value = "背景音乐id",required = false,
					dataType = "String",paramType = "form"),
			@ApiImplicitParam(name = "videoSeconds",value = "背景音乐播放长度",required = true,
					dataType = "String",paramType = "form"),
			@ApiImplicitParam(name = "videoWidth",value = "视频宽度",required = true,
					dataType = "String",paramType = "form"),
			@ApiImplicitParam(name = "videoHeight",value = "视频高度",required = true,
					dataType = "String",paramType = "form"),
			@ApiImplicitParam(name = "desc",value = "视频描述",required = false,
					dataType = "String",paramType = "form")})
	@PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
	public IMoocJSONResult uploadFace(String userId,String bgmId,
									  double videoSeconds, int videoWidth, int videoHeight,
									  String desc,
									  @ApiParam(value = "短视频",required = true) MultipartFile file)
			throws Exception {
		if(StringUtils.isBlank(userId)){
			return IMoocJSONResult.errorMsg("用户Id不能为空");
		}

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		//数据库路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";
		String finalVideoPath = "";
		try {

			if(file != null && file.getSize() > 0){
				String fileName = file.getOriginalFilename();
				//
				String fileNamePrefix = fileName.substring(0,fileName.lastIndexOf("."));

				if(StringUtils.isNotBlank(fileName)){
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
					//数据库保存路径
					uploadPathDB += ("/" + fileName);
					coverPathDB =  coverPathDB + "/" + fileNamePrefix + ".jpg";
					File outFile = new File(finalVideoPath);
					if(outFile.getParentFile() != null || outFile.getParentFile().isDirectory()){
						//创建父目录
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream,fileOutputStream);
				}else {
					return IMoocJSONResult.errorMsg("上传出错");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错");
		}

		finally {
			if(fileOutputStream != null){
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		//判断bgm是否为空，如果不是空就查询bgm信息,并且合并视频，生成新的视频
		if(StringUtils.isNotBlank(bgmId)){
			Bgm bgm = bgmService.queryBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();
			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
			finalVideoPath = FILE_SPACE + uploadPathDB;
			try {
				tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
			}catch (IOException e){
				e.printStackTrace();
			}

		}

		//对视频截图
		FetchVideoCover videoCover = new FetchVideoCover(FFMPEG_EXE);
		videoCover.getCover(finalVideoPath,FILE_SPACE + coverPathDB);
		System.out.println(coverPathDB);

		//保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoWidth(videoWidth);
		video.setVideoHeight(videoHeight);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());
		String videoId = videoService.saveVideo(video);
		return IMoocJSONResult.ok(videoId);
	}


	@ApiOperation(value = "用户上传封面",notes = "用户上传封面接口")
	@ApiImplicitParams({@ApiImplicitParam(name = "userId",value = "用户Id",required = true,
			dataType = "String",paramType = "form"),
			@ApiImplicitParam(name = "videoId",value = "视频主键id",required = true,
			dataType = "String",paramType = "form")})
	@PostMapping(value = "/uploadCover",headers = "content-type=multipart/form-data")
	public IMoocJSONResult uploadCover(String userId,String videoId,
									  @ApiParam(value = "视频封面",required = true) MultipartFile file)
			throws Exception {
		if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
			return IMoocJSONResult.errorMsg("视频主键Id和用户Id不能为空");
		}

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		//数据库路径
		String uploadPathDB = "/" + userId + "/video";
		String finalCoverPath = "";
		try {

			if(file != null && file.getSize() > 0){
				String fileName = file.getOriginalFilename();
				if(StringUtils.isNotBlank(fileName)){
					finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
					//数据库保存路径
					uploadPathDB += ("/" + fileName);
					File outFile = new File(finalCoverPath);
					if(outFile.getParentFile() != null || outFile.getParentFile().isDirectory()){
						//创建父目录
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream,fileOutputStream);
				}else {
					return IMoocJSONResult.errorMsg("上传出错");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错");
		}
		finally {
			if(fileOutputStream != null){
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		videoService.updateVideo(videoId,uploadPathDB);
		return IMoocJSONResult.ok();
	}


	/**
	 * 分页和搜索查询
	 *
	 * @param video
	 * @param isSaveRecord  1-需要保存 0-不需要
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/showAll")
	public IMoocJSONResult showAll(@RequestBody Videos video,
								   Integer isSaveRecord,Integer page,Integer pageSize) throws Exception{

		if(page == null){
			page = 1;
		}
		if(pageSize == null){
			pageSize = PAGE_SIZE;
		}
		PagedResult result = videoService.getAllVideos(video,isSaveRecord,page, pageSize);
		return  IMoocJSONResult.ok(result);
	}

	@PostMapping(value = "/hot")
	public IMoocJSONResult hot() throws Exception{
		return  IMoocJSONResult.ok(videoService.getHotWords());
	}

	@PostMapping(value = "/userLike")
	public IMoocJSONResult userLike(String userId, String videoId, String videoCreateId) throws Exception{
		videoService.userLikeVideo(userId,videoId,videoCreateId);
		return  IMoocJSONResult.ok();
	}

	@PostMapping(value = "/userUnlike")
	public IMoocJSONResult userUnlike(String userId, String videoId, String videoCreateId) throws Exception{
		videoService.userUnLikeVideo(userId,videoId,videoCreateId);
		return  IMoocJSONResult.ok();
	}

	/**
	 * @Description: 我收藏(点赞)过的视频列表
	 */
	@PostMapping("/showMyLike")
	public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.ok();
		}

		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}

		PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

		return IMoocJSONResult.ok(videosList);
	}


	@PostMapping("/showMyFollow")
	public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.ok();
		}

		if (page == null) {
			page = 1;
		}

		int pageSize = 6;

		PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

		return IMoocJSONResult.ok(videosList);
	}


	@PostMapping("/saveComment")
	public IMoocJSONResult saveComment(@RequestBody Comments comment,String fatherCommentId,
									   String toUserId) throws Exception {

		if(fatherCommentId != null && fatherCommentId != ""){
			comment.setFatherCommentId(fatherCommentId);
			comment.setToUserId(toUserId);
		}
		videoService.saveComment(comment);
		return IMoocJSONResult.ok();
	}


	@PostMapping("/getVideoComments")
	public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {
		if (StringUtils.isBlank(videoId)) {
			return IMoocJSONResult.ok();
		}
		// 分页查询视频列表，时间顺序倒序排序
		if (page == null) {
			page = 1;
		}
		if (pageSize == null) {
			pageSize = 10;
		}

		PagedResult list = videoService.getAllComments(videoId, page, pageSize);

		return IMoocJSONResult.ok(list);
	}






}
