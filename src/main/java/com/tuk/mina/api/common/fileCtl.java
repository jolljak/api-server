package com.tuk.mina.api.common;

import java.io.IOException;
import java.util.List;

import com.tuk.mina.api.svc.file.fileSvc;
import com.tuk.mina.dao.file.TbFileDao;
import com.tuk.mina.vo.file.TbFileVo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/file")
@Tag(name = "File Controller", description = "파일 관련 API")
@RequiredArgsConstructor
public class fileCtl {

    private final fileSvc fileService;
    private final TbFileDao fileDao;


    @PostMapping("/upload")
    @Operation(summary = "file upload", description = "S3 파일 업로드")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.upload(file);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download")
    @Operation(summary = "file download", description = "S3 파일 다운로드")
    public ResponseEntity<byte[]> download(@RequestParam("fileName") String fileName) {
        try {
            byte[] bytes = fileService.downloadFile(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // headers.setContentDispositionFormData("attachment", fileName); // Forces download
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/metadata")
    public ResponseEntity<List<TbFileVo>> getFileMetadata(TbFileVo param) {
        return ResponseEntity.ok(fileDao.getFile(param));
    }
    
}
