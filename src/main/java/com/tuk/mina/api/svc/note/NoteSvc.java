package com.tuk.mina.api.svc.note;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuk.mina.dao.note.TbNoteDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.record.TbNoteVo;
import java.util.Optional;

@Service
public class NoteSvc {

    @Autowired
    private TbNoteDao noteDao;

    @Autowired
    private SecurityUtil securityUtil;

    public List<TbNoteVo> getNote(TbNoteVo param) {
        if (param == null) param = new TbNoteVo();
        
        Optional<String> userIdOpt = SecurityUtil.getAuthUserId();
        if (userIdOpt.isEmpty()) {
            System.out.println("DEBUG: Unauthenticated access attempt to notes");
            return java.util.Collections.emptyList();
        }
        
        String currentUserId = userIdOpt.get();
        System.out.println("DEBUG: Fetching notes for user: " + currentUserId);
        param.setCreatedUserId(currentUserId);
        List<TbNoteVo> notes = noteDao.getNote(param);
        System.out.println("DEBUG: Found " + notes.size() + " notes");
        return notes;
    }

    public Integer newNote(TbNoteVo param) {
        Optional<String> userIdOpt = SecurityUtil.getAuthUserId();
        if (userIdOpt.isPresent()) {
            param.setCreatedUserId(userIdOpt.get());
        }
        noteDao.newNote(param);
        return param.getNoteId();
    }

    public void putNote(TbNoteVo param) {
        noteDao.putNote(param);
    }

    public void delNote(Integer noteId) {
        noteDao.delNote(noteId);
    }
}
