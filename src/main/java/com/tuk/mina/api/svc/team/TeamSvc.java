package com.tuk.mina.api.svc.team;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuk.mina.dao.team.TbTeamDao;
import com.tuk.mina.dao.team.TbTeamUserMapDao;
import org.springframework.transaction.annotation.Transactional;
import com.tuk.mina.util.SecurityUtil;
import com.tuk.mina.vo.team.TbTeamUserMapVo;
import com.tuk.mina.vo.team.TbTeamVo;

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

    public List<TbTeamUserMapVo> getTeamUserMap(TbTeamUserMapVo param) {
        return teamUserMapDao.getTeamUserMap(param);
    }

    @Transactional
    public void createNewTeam(TbTeamVo param) {
        // 1. Create Team
        teamDao.newTeam(param);

        // [추가] 만약 teamId가 null인 경우 (일부 MSSQL 드라이버/설정 문제) 재조회 시도
        if (param.getTeamId() == null) {
            List<TbTeamVo> list = teamDao.getTeam(param);
            if (list != null && !list.isEmpty()) {
                param.setTeamId(list.get(0).getTeamId());
            }
        }

        // 2. Map User to Team (as Leader)
        TbTeamUserMapVo mapParam = new TbTeamUserMapVo();
        mapParam.setTeamId(param.getTeamId());
        mapParam.setUserId(param.getCreatedUserId());
        mapParam.setMemberRole("Leader");
        mapParam.setMemberStatus("Active");
        mapParam.setMemberNick(param.getCreatedUserId()); // Default nickname
        
        teamUserMapDao.newTeamUserMap(mapParam);
    }

    public void delTeamMember(TbTeamUserMapVo param) {
        teamUserMapDao.delTeamUserMap(param);
    }
}
