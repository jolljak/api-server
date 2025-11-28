package com.tuk.mina.dao.note;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.tuk.mina.vo.record.TbNoteVo;

@Mapper
public interface TbNoteDao {

    public List<TbNoteVo> getNote(TbNoteVo param);
    public void newNote(TbNoteVo param);
    public void putNote(TbNoteVo param);
    public void delNote(Integer noteId);

}
