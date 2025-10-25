package com.tuk.mina.api.svc.team;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuk.mina.dao.team.TbTeamDao;
import com.tuk.mina.dao.team.TbTeamUserMapDao;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.team.TbTeamUserMapVo;

@Service
public class TeamSvc {

    @Autowired
    private TbTeamDao teamDao;

    @Autowired
    private TbTeamUserMapDao teamUserMapDao;

    @Autowired
    private SecurityUtil securityUtil;

    public void newTeamUserMap(List<TbTeamUserMapVo> param) {
        for (TbTeamUserMapVo teamUserMap : param) {
            teamUserMapDao.newTeamUserMap(teamUserMap);   
        }
    }

}
