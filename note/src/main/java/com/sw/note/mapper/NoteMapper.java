package com.sw.note.mapper;

import com.sw.note.model.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface NoteMapper extends tk.mybatis.mapper.common.Mapper<Note> {

    @Select("<script> \n" +
            "SELECT\n" +
            "    id,\n" +
            "    title,\n" +
            "    poster,\n" +
            "    summary,\n" +
            "    tag,\n" +
            "    CASE\n" +
            "        WHEN edit_time IS NULL THEN post_time\n" +
            "        ELSE edit_time\n" +
            "    END post_time\n" +
            "FROM\n" +
            "    note\n" +
            "ORDER BY\n" +
            "    CASE\n" +
            "        WHEN edit_time IS NULL THEN post_time\n" +
            "        ELSE edit_time\n" +
            "    END\n" +
            "DESC\n" +
            "</script>")
    List<Note> getAll();

    @Select("<script> \n" +
            "SELECT\n" +
            "    id,\n" +
            "    title,\n" +
            "    poster,\n" +
            "    summary,\n" +
            "    tag,\n" +
            "    post_time\n" +
            "FROM\n" +
            "    note\n" +
            "WHERE \n" +
            "    recommend = 1\n" +
            "ORDER BY post_time DESC\n" +
            "LIMIT 6\n" +
            "</script>")
    List<Note> getRecommend();

    @Select("<script> \n" +
            "SELECT\n" +
            "   n.*\n" +
            "FROM\n" +
            "    note n\n" +
            "WHERE \n" +
            "    n.title = #{title}\n" +
            "</script>")
    Note getByTitle(@Param("title") String title);

}

