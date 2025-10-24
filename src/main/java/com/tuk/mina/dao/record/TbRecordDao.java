package com.tuk.mina.dao.record;

import com.tuk.mina.vo.record.TbRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbRecordDao {

    // 단건 조회
    TbRecordVo selectRecordById(@Param("recordId") String recordId);

    // 전체 리스트 조회 (필요 시 조건부)
    List<TbRecordVo> selectRecordList();

    // 등록
    int insertRecord(TbRecordVo recordVo);

    // 수정
    int updateRecord(TbRecordVo recordVo);

    // 삭제
    int deleteRecord(@Param("recordId") String recordId);
}
