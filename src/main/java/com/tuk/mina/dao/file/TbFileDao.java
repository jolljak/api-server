package com.tuk.mina.dao.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.file.TbFileVo;

@Mapper
public interface TbFileDao {

    public List<TbFileVo>       getFile(TbFileVo param);
    public void                 newFile(TbFileVo param);
    public void                 putFile(TbFileVo param);
    public void                 delFile(Integer fileId);

}
