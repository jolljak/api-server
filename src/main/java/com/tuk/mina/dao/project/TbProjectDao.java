package com.tuk.mina.dao.project;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.project.TbProjectVo;

@Mapper
public interface TbProjectDao {

    public List<TbProjectVo>    getProject(TbProjectVo param);
    public void                 newProject(TbProjectVo param);
    public void                 putProject(TbProjectVo param);
    public void                 delProject(Integer projectId);

}
