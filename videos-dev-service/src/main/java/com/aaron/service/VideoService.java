package com.aaron.service;

import com.aaron.pojo.Bgm;
import com.aaron.pojo.Comments;
import com.aaron.pojo.Videos;
import com.aaron.utils.PagedResult;

import java.util.List;

public interface VideoService {




    /**
     * 根据Id查询
     * @param
     * @return
     */
    public String saveVideo(Videos video);


    /**
     * 修改视频封面
     * @param videoId
     * @param coverPath
     * @return
     */
    public void updateVideo(String videoId,String coverPath);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);

    /**
     * 热搜词
     * @return
     */
    public List<String> getHotWords();

    /**
     *
     */
    public void userLikeVideo(String userId,String videoId,String videoCreateId);

    /**
     *
     */
    public void userUnLikeVideo(String userId,String videoId,String videoCreateId);

    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);


    //我关注的人发的视频
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);


    public void saveComment(Comments comment);

    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
