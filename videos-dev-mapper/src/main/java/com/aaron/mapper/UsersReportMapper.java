package com.aaron.mapper;

import com.aaron.pojo.UsersReport;
import com.aaron.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UsersReportMapper extends MyMapper<UsersReport> {
}