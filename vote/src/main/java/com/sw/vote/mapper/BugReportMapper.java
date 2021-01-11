package com.sw.vote.mapper;

import com.sw.vote.model.entity.BugReport;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface BugReportMapper extends tk.mybatis.mapper.common.Mapper<BugReport> {


}
