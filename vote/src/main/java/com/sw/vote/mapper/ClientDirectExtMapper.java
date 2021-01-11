package com.sw.vote.mapper;

import com.sw.vote.model.entity.ClientDirectExt;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ClientDirectExtMapper extends tk.mybatis.mapper.common.Mapper<ClientDirectExt> {

    void insertClient(@Param("id") String id, @Param("userId") String userId, @Param("sortNo") int sortNo);

    void batchUpdate(@Param("list") List<ClientDirectExt> list);
}
