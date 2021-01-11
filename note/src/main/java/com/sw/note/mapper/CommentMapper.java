package com.sw.note.mapper;

import com.sw.note.model.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CommentMapper extends tk.mybatis.mapper.common.Mapper<Comment> {


    @Select("<script> \n" +
            "SELECT\n" +
            "   c.*,\n" +
            "   c.ip = #{ip} operable\n" +
            "FROM\n" +
            "    comment c\n" +
            "WHERE \n" +
            "    c.thread = #{thread}\n" +
            "ORDER BY\n" +
            "    comment_time\n" +
            "</script>")
    List<Comment> get(@Param("thread") Integer thread, @Param("ip") String ip);

}

