package com.tuk.mina.api.svc.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuk.mina.dao.project.TbProjServiceInfoDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.project.TbProjServiceInfoVo;

@Service
public class ProjServiceInfoSvc {

    @Autowired
    private TbProjServiceInfoDao projServiceInfoDao;

    @Autowired
    private SecurityUtil securityUtil;

    public List<TbProjServiceInfoVo> getProjServiceInfo(TbProjServiceInfoVo param) {
        return projServiceInfoDao.getProjServiceInfo(param);
    }

    public void newProjServiceInfo(List<TbProjServiceInfoVo> param) {
        for (TbProjServiceInfoVo vo : param) {
            vo.setCreatedUserId(securityUtil.getAuthUserId().get());
            projServiceInfoDao.newProjServiceInfo(vo);
        }
    }

    public void putProjServiceInfo(TbProjServiceInfoVo param) {
        projServiceInfoDao.putProjServiceInfo(param);
    }

    public void delProjServiceInfo(TbProjServiceInfoVo param) {
        projServiceInfoDao.delProjServiceInfo(param);
    }
}
