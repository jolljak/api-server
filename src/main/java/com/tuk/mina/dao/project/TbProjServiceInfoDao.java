package com.tuk.mina.dao.project;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.project.TbProjServiceInfoVo;

@Mapper
public interface TbProjServiceInfoDao {

    public List<TbProjServiceInfoVo>    getProjServiceInfo(TbProjServiceInfoVo param);
    public void                         newProjServiceInfo(TbProjServiceInfoVo param);
    public void                         putProjServiceInfo(TbProjServiceInfoVo param);
    public void                         delProjServiceInfo(TbProjServiceInfoVo param);
    
    // 서비스 마스터 목록 조회 (projectId가 NULL이고 serviceField가 'service_master'인 데이터)
    public List<TbProjServiceInfoVo>    getServiceMasterList();

}
