package com.aaron.mapper;

import com.aaron.pojo.UsersLikeVideos;
import com.aaron.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UsersLikeVideosMapper extends MyMapper<UsersLikeVideos> {
}