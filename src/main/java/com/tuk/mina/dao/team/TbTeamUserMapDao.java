package com.tuk.mina.dao.team;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.team.TbTeamUserMapVo;

@Mapper
public interface TbTeamUserMapDao {

    public List<TbTeamUserMapVo>    getTeamUserMap(TbTeamUserMapVo param);
    public void                     newTeamUserMap(TbTeamUserMapVo param);
    public void                     putTeamUserMap(TbTeamUserMapVo param);
    public void                     delTeamUserMap(TbTeamUserMapVo param);

}
