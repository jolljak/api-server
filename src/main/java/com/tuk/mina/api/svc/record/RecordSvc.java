package com.tuk.mina.api.svc.record;

import com.tuk.mina.dao.record.TbRecordDao;
import com.tuk.mina.vo.record.TbRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RecordSvc {

    @Autowired
    private TbRecordDao tbRecordDao;

    @Transactional
    public void saveRecordResult(int fileId, String language, String memo, int projectId, String createUserId) {
        TbRecordVo vo = new TbRecordVo();
        vo.setRecordFileId(fileId);
        vo.setRecordlanguage(language);
        vo.setRecordMemo(memo);
        vo.setProjectId(projectId);
        vo.setCreateUserId(createUserId);

        tbRecordDao.insertRecord(vo);
        log.info("Record 저장 완료: {}", vo);
    }
}
