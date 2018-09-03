package com.aaron.mapper;

import com.aaron.pojo.Videos;
import com.aaron.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface VideosMapper extends MyMapper<Videos> {
}