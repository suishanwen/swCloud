package com.sw.note.service;

import com.sw.note.model.BusinessException;
import com.sw.note.mapper.NoteMapper;
import com.sw.note.model.entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoteSerivce {

    @Autowired
    NoteMapper noteMapper;

    public List<Note> getAllPost() {
        return noteMapper.getAll();
    }

    public List<Note> getRecommend() {
        return noteMapper.getRecommend();
    }

    public Note get(Integer id) {
        return noteMapper.selectByPrimaryKey(id);
    }

    public Note getByTitle(String title) {
        return noteMapper.getByTitle(title);
    }

    public Note add(Note note) {
        note.setPostTime(new Date());
        noteMapper.insertSelective(note);
        return getByTitle(note.getTitle());
    }

    public Note edit(Note note) {
        note.setEditTime(new Date());
        noteMapper.updateByPrimaryKeySelective(note);
        return get(note.getId());
    }

    public void delete(Integer id) {
        noteMapper.deleteByPrimaryKey(id);
    }

}
