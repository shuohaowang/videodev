package com.aaron.mapper;


import com.aaron.pojo.Bgm;
import com.aaron.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BgmMapper extends MyMapper<Bgm> {
}