package com.goorm.derere.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;


    // Presigned URL 생성 (업로드용)
    // @param imageType 이미지 타입 (restaurant, menu)
    // @param originalFileName 원본 파일명
    // @return Presigned URL과 최종 파일명을 포함한 응답
    public PresignedUrlResponse generatePresignedUrl(String imageType, String originalFileName) {
        try {
            // 파일 확장자 검증
            validateImageFile(originalFileName);

            // 고유한 파일명 생성
            String fileName = generateUniqueFileName(imageType, originalFileName);

            // S3 객체 키 생성
            String objectKey = "images/" + imageType + "/" + fileName;

            // PutObjectRequest 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(getContentType(originalFileName))
                    .build();

            // Presigned URL 요청 생성 (15분 유효)
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            // Presigned URL 생성
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            // 최종 이미지 URL 생성
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, objectKey);

            log.info("Presigned URL 생성 완료 - 파일: {}, URL: {}", fileName, presignedUrl);

            return new PresignedUrlResponse(presignedUrl, imageUrl, fileName);

        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드 URL 생성에 실패했습니다.", e);
        }
    }

    // S3에서 이미지 삭제
    // @param imageUrl 삭제할 이미지 URL
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return;
            }

            // URL에서 객체 키 추출
            String objectKey = extractObjectKey(imageUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("S3 이미지 삭제 완료: {}", objectKey);

        } catch (Exception e) {
            log.error("S3 이미지 삭제 실패: {}", e.getMessage());
            // 이미지 삭제 실패는 전체 작업을 중단시키지 않음
        }
    }

    // 이미지 파일 유효성 검증
    private void validateImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 필요합니다.");
        }

        String lowercaseFileName = fileName.toLowerCase();
        if (!lowercaseFileName.endsWith(".jpg") &&
                !lowercaseFileName.endsWith(".jpeg") &&
                !lowercaseFileName.endsWith(".png") &&
                !lowercaseFileName.endsWith(".gif")) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (jpg, jpeg, png, gif만 허용)");
        }

        // 파일명 길이 제한
        if (fileName.length() > 100) {
            throw new IllegalArgumentException("파일명이 너무 깁니다. (최대 100자)");
        }
    }

    // 고유한 파일명 생성
    private String generateUniqueFileName(String imageType, String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return imageType + "_" + System.currentTimeMillis() + "_" + uuid + extension;
    }

    // 파일 확장자에 따른 Content-Type 반환
    private String getContentType(String fileName) {
        String lowercaseFileName = fileName.toLowerCase();
        if (lowercaseFileName.endsWith(".jpg") || lowercaseFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowercaseFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowercaseFileName.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg"; // 기본값
    }

    // 이미지 URL에서 S3 객체 키 추출
    private String extractObjectKey(String imageUrl) {
        try {
            // https://bucket-name.s3.region.amazonaws.com/images/restaurant/filename.jpg
            String baseUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
            if (imageUrl.startsWith(baseUrl)) {
                return imageUrl.substring(baseUrl.length());
            }
            throw new IllegalArgumentException("잘못된 이미지 URL 형식입니다.");
        } catch (Exception e) {
            throw new IllegalArgumentException("이미지 URL에서 키를 추출할 수 없습니다.");
        }
    }

    // Presigned URL 응답 클래스
    public static class PresignedUrlResponse {
        private final String presignedUrl;  // 업로드용 URL
        private final String imageUrl;      // 최종 이미지 접근 URL
        private final String fileName;      // 생성된 파일명

        public PresignedUrlResponse(String presignedUrl, String imageUrl, String fileName) {
            this.presignedUrl = presignedUrl;
            this.imageUrl = imageUrl;
            this.fileName = fileName;
        }

        public String getPresignedUrl() { return presignedUrl; }
        public String getImageUrl() { return imageUrl; }
        public String getFileName() { return fileName; }
    }
}