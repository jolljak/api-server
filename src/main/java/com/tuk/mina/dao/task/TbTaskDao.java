package com.tuk.mina.dao.task;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.task.TbTaskVo;

@Mapper
public interface TbTaskDao {

    public List<TbTaskVo>    getTask(TbTaskVo param);
    public void                 newTask(TbTaskVo param);
    public void                 putTask(TbTaskVo param);
    public void                 delTask(String taskId);

}
