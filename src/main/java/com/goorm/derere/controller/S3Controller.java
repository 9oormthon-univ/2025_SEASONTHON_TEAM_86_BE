package com.goorm.derere.controller;

import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import com.goorm.derere.service.S3Service;
import com.goorm.derere.service.S3Service.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이미지 업로드 API", description = "S3를 활용한 이미지 업로드 관련 API입니다.")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {

    private final S3Service s3Service;
    private final OAuthRepository oAuthRepository;

    // 이미지 업로드를 위한 Presigned URL 생성
    @Operation(summary = "이미지 업로드 URL 생성 API",
            description = "이미지 업로드를 위한 Presigned URL을 생성합니다. imageType은 'restaurant' 또는 'menu'입니다.")
    @PostMapping("/upload-url")
    public ResponseEntity<PresignedUrlResponse> generateUploadUrl(
            @RequestParam String imageType,
            @RequestParam String fileName,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        // OAuth2 인증 확인
        if (oauthUser == null) {
            log.warn("이미지 업로드 URL 생성 요청 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 사용자 정보 확인
        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("이미지 업로드 URL 생성 요청 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        // 이미지 타입 검증
        if (!imageType.equals("restaurant") && !imageType.equals("menu")) {
            throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다. (restaurant, menu만 허용)");
        }

        log.info("이미지 업로드 URL 생성 요청 - 사용자ID: {}, 타입: {}, 파일명: {}",
                user.getUserid(), imageType, fileName);

        try {
            PresignedUrlResponse response = s3Service.generatePresignedUrl(imageType, fileName);
            log.info("이미지 업로드 URL 생성 완료 - 사용자ID: {}, 생성된 파일명: {}",
                    user.getUserid(), response.getFileName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("이미지 업로드 URL 생성 실패 - 사용자ID: {}, 사유: {}",
                    user.getUserid(), e.getMessage());
            throw e;
        }
    }

    // 개발용 임시 API (OAuth2 인증 없이 테스트)
    @Operation(summary = "이미지 업로드 URL 생성 API (개발용)",
            description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @PostMapping("/upload-url/temp")
    public ResponseEntity<PresignedUrlResponse> generateUploadUrlTemp(
            @RequestParam String imageType,
            @RequestParam String fileName,
            @RequestParam Long userId) {

        // 이미지 타입 검증
        if (!imageType.equals("restaurant") && !imageType.equals("menu")) {
            throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다. (restaurant, menu만 허용)");
        }

        log.info("이미지 업로드 URL 생성 임시 요청 - 사용자ID: {}, 타입: {}, 파일명: {}",
                userId, imageType, fileName);

        try {
            PresignedUrlResponse response = s3Service.generatePresignedUrl(imageType, fileName);
            log.info("이미지 업로드 URL 생성 임시 완료 - 사용자ID: {}, 생성된 파일명: {}",
                    userId, response.getFileName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("이미지 업로드 URL 생성 임시 실패 - 사용자ID: {}, 사유: {}",
                    userId, e.getMessage());
            throw e;
        }
    }

    // 이미지 삭제
    @Operation(summary = "이미지 삭제 API",
            description = "S3에서 이미지를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            @RequestParam String imageUrl,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        // OAuth2 인증 확인
        if (oauthUser == null) {
            log.warn("이미지 삭제 요청 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("이미지 삭제 요청 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        log.info("이미지 삭제 요청 - 사용자ID: {}, 이미지URL: {}", user.getUserid(), imageUrl);

        try {
            s3Service.deleteImage(imageUrl);
            log.info("이미지 삭제 완료 - 사용자ID: {}", user.getUserid());

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("이미지 삭제 실패 - 사용자ID: {}, 사유: {}", user.getUserid(), e.getMessage());
            throw e;
        }
    }

    // 개발용 임시 API (OAuth2 인증 없이 테스트)
    @Operation(summary = "이미지 삭제 API (개발용)",
            description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @DeleteMapping("/temp")
    public ResponseEntity<String> deleteImageTemp(
            @RequestParam String imageUrl,
            @RequestParam Long userId) {

        log.info("임시 삭제 API 호출 - imageUrl: {}, userId: {}", imageUrl, userId);

        try {
            s3Service.deleteImage(imageUrl);
            log.info("이미지 삭제 완료 - 사용자ID: {}", userId);
            return ResponseEntity.ok("이미지 삭제 완료");

        } catch (Exception e) {
            log.error("이미지 삭제 실패 - 사유: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }
}