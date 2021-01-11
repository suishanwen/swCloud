package com.sw.vote.mapper;

import com.sw.vote.model.entity.CtrlClient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface VoteMapper extends tk.mybatis.mapper.common.Mapper<CtrlClient> {
    @Select("<script> \n" +
            "SELECT\n" +
            "    *\n" +
            "FROM\n" +
            "    ctrl_client\n" +
            "WHERE\n" +
            "   user = #{user}\n" +
            "and state=1\n" +
            "ORDER BY sort\n" +
            "asc\n" +
            "</script>")
    List<CtrlClient> queryByUser(@Param("user") String user);


    @Select("<script> \n" +
            "SELECT\n" +
            "    *\n" +
            "FROM\n" +
            "    ctrl_client\n" +
            "WHERE\n" +
            "   identity = #{identity}\n" +
            "</script>")
    CtrlClient queryByIdentity(@Param("identity") String identity);
}
