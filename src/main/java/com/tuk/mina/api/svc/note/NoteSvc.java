package com.tuk.mina.api.svc.note;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuk.mina.dao.note.TbNoteDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.record.TbNoteVo;

@Service
public class NoteSvc {

    @Autowired
    private TbNoteDao noteDao;

    @Autowired
    private SecurityUtil securityUtil;

    public List<TbNoteVo> getNote(TbNoteVo param) {
        return noteDao.getNote(param);
    }

    public void newNote(TbNoteVo param) {
        param.setCreatedUserId(securityUtil.getAuthUserId().get());
        noteDao.newNote(param);
    }

    public void putNote(TbNoteVo param) {
        noteDao.putNote(param);
    }

    public void delNote(Integer noteId) {
        noteDao.delNote(noteId);
    }
}
