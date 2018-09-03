package com.aaron.service.impl;

import com.aaron.mapper.UsersFansMapper;
import com.aaron.mapper.UsersLikeVideosMapper;
import com.aaron.mapper.UsersMapper;
import com.aaron.mapper.UsersReportMapper;
import com.aaron.pojo.Users;
import com.aaron.pojo.UsersFans;
import com.aaron.pojo.UsersLikeVideos;
import com.aaron.pojo.UsersReport;
import com.aaron.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result == null ? false : true;
    }



    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Override
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(example);
        return result;
    }

    @Override
    public void updateUserInfo(Users users) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",users.getId());
        usersMapper.updateByExampleSelective(users,example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user = usersMapper.selectOneByExample(example);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);
        if(usersLikeVideos != null && usersLikeVideos.size() > 0){
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        String relId = sid.nextShort();
        UsersFans userFan = new UsersFans();
        userFan.setId(relId);
        userFan.setUserId(userId);
        userFan.setFanId(fanId);
        usersFansMapper.insert(userFan);
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);

    }



    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        usersFansMapper.deleteByExample(example);
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);
    }

    @Override
    public boolean queryIsFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(example);
        if(list != null && !list.isEmpty() && list.size() > 0){
            return  true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport userReport) {

        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);
    }
}
