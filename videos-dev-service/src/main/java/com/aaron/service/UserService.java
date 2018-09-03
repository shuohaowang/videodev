package com.aaron.service;

import com.aaron.pojo.Users;
import com.aaron.pojo.UsersReport;

public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 保存用户
     * @param users
     */
    public void saveUser(Users users);

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username,String password);

    /**
     *   用户修改信息
     * @param users
     */
    public void updateUserInfo(Users users);

    /**
     * 查询用户Id
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 查询用户是否喜欢点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    public boolean isUserLikeVideo(String userId,String videoId);


    //增加关系
    public void saveUserFanRelation(String userId,String fanId);

    //删除关系
    public void deleteUserFanRelation(String userId,String fanId);

    public boolean queryIsFollow(String userId,String fanId);

    public void reportUser(UsersReport userReport);





}
