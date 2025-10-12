package com.tuk.mina.dao.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tuk.mina.vo.user.TbUserVo;

@Mapper
public interface TbUserDao {

    public List<TbUserVo>   getUser(TbUserVo tbUserVo);
    public void             newUser(TbUserVo tbUserVo);
    public void             putUser(TbUserVo tbUserVo);
    public void             delUser(TbUserVo tbUserVo);
}