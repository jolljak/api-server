package com.tuk.mina.api.ctl.record;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuk.mina.api.svc.file.fileSvc;
import com.tuk.mina.api.svc.record.RecordSvc;
import com.tuk.mina.util.MultipartInputStreamFileResource;
import com.tuk.mina.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/record")
@Tag(name = "Record Controller", description = "녹음 파일 처리 API")
public class recordCtl {

    private static final Logger log = LoggerFactory.getLogger(recordCtl.class);

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private RecordSvc recordSvc;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/transcribe-diarize")
    @Operation(
            summary = "Whisper + Pyannote 분석 요청",
            description = """
            - 업로드된 음성 파일을 AWS S3에 저장하고,
            - 동일 파일을 FastAPI로 전송하여 Whisper + Pyannote 분석을 수행합니다.
            - 반환 결과에는 S3 URL과 분석 결과(JSON)가 포함됩니다.
            """
    )
    public ResponseEntity<String> transcribeAndDiarize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", defaultValue = "auto") String language,
            @RequestParam(value = "memo", defaultValue = "") String memo,
            @RequestParam(value = "projectId") int projectId
    ) throws IOException {

        // S3 업로드
//        String fileUrl = fileSvc.upload(file);
//        log.info("파일 업로드 완료 → {}", fileUrl);

        // FastAPI 호출 준비
        String url = aiServerUrl + "/transcribe-diarize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        body.add("language", language);
        body.add("createUserId", securityUtil.getAuthUserId().get());

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("FastAPI 분석 완료, 상태코드: {}", response.getStatusCode());
            log.info("FastAPI 응답 본문: {}", response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());
            String fulltext = jsonNode.path("fulltext").asText(null);
            int fileId = jsonNode.path("fileId").asInt(0);
            recordSvc.saveRecordResult(fileId, language, memo, projectId, fulltext, securityUtil.getAuthUserId().get());

            return ResponseEntity
                    .status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            log.error("FastAPI 호출 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"resultCode\":0,\"message\":\"FastAPI 서버 응답 실패\"}");
        }
    }
}