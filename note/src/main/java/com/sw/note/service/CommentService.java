package com.sw.note.service;

import com.sw.note.model.BusinessException;
import com.sw.note.mapper.CommentMapper;
import com.sw.note.model.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> get(Integer thread, String ip) {
        return commentMapper.get(thread, ip);
    }

    public void add(Comment comment) {
        comment.setCommentTime(new Date());
        commentMapper.insert(comment);
    }


    public void edit(Comment comment) {
        comment.setEditTime(new Date());
        commentMapper.updateByPrimaryKeySelective(comment);
    }

    public void delete(Integer id) {
        commentMapper.deleteByPrimaryKey(id);
    }
}
