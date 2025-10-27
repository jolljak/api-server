package com.tuk.mina.api.svc.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.tuk.mina.dao.file.TbFileDao;
import com.tuk.mina.vo.file.TbFileVo;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class fileSvc {

    private final AmazonS3 amazonS3;

    private final TbFileDao fileDao;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // public fileSvc(AmazonS3 amazonS3) {
    //     this.amazonS3 = amazonS3;
    // }

    public String upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        TbFileVo fileParam = new TbFileVo();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);

        fileParam.setFileName(fileName);
        fileParam.setFileSize(file.getSize());
        fileParam.setFileExt(file.getContentType());
        fileDao.newFile(fileParam);

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, amazonS3.getRegionName(), fileName);
    }

    public byte[] downloadFile(String fileName) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucket, fileName);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            return IOUtils.toByteArray(inputStream);
        }
    }
    
}
