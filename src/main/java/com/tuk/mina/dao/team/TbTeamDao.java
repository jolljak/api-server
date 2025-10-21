package com.tuk.mina.dao.team;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.team.TbTeamVo;

@Mapper
public interface TbTeamDao {

    public List<TbTeamVo>    getTeam(TbTeamVo param);
    public void                 newTeam(TbTeamVo param);
    public void                 putTeam(TbTeamVo param);
    public void                 delTeam(Integer teamId);

}
