package com.sw.note.web;

import com.sw.note.model.entity.Comment;
import com.sw.note.service.CommentService;
import com.sw.note.util.IpUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "评论", description = "评论", tags = "2")
@RestController
@RequestMapping(path = "/comment")
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    HttpServletRequest request;

    @ApiOperation(value = "获取", notes = "获取")
    @ApiImplicitParam(name = "thread", paramType = "query", value = "笔记id", required = true, dataType = "int")
    @PostMapping(value = "get")
    public List<Comment> get(@RequestParam("thread") int thread) {
        return commentService.get(thread, IpUtil.getIpAddr(request));
    }

    @ApiOperation(value = "添加评论", notes = "添加评论")
    @PostMapping("/add")
    public void add(@ApiParam(value = "Comment", required = true) @RequestBody Comment comment) {
        comment.setIp(IpUtil.getIpAddr(request));
        commentService.add(comment);
    }

    @ApiOperation(value = "编辑评论", notes = "编辑评论")
    @PostMapping("/edit")
    public void edit(@ApiParam(value = "Comment", required = true) @RequestBody Comment comment) {
        comment.setIp(IpUtil.getIpAddr(request));
        commentService.edit(comment);
    }

    @ApiOperation(value = "删除评论", notes = "删除评论")
    @ApiImplicitParam(name = "id", paramType = "path", value = "id", required = true, dataType = "String")
    @PostMapping("/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        commentService.delete(id);
    }
}
