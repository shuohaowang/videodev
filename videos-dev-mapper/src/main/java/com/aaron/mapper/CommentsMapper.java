package com.aaron.mapper;

import com.aaron.pojo.Comments;
import com.aaron.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommentsMapper extends MyMapper<Comments> {
}