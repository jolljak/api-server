package com.tuk.mina.dao.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.service.TbServiceVo;

@Mapper
public interface TbServiceDao {
    
    public List<TbServiceVo>    getService(TbServiceVo param);
    public void                 newService(TbServiceVo param);
    public void                 putService(TbServiceVo param);
    public void                 delService(String param); 

}
