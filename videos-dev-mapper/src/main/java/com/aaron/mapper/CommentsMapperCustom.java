package com.aaron.mapper;


import com.aaron.pojo.Comments;
import com.aaron.pojo.vo.CommentsVO;
import com.aaron.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}