package com.tuk.mina.vo.file;
import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("TbFileVo")
public class TbFileVo {

    private Integer fileId;
    private String filePath;
    private Integer fileSize;
    private String fileName;
    private String fileExt;
    private String createUserId;
    private String createDttm;
    private String updatedDttm;

}
