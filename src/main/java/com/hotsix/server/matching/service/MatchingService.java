package com.hotsix.server.matching.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsix.server.matching.dto.MatchingRequest;
import com.hotsix.server.matching.dto.MatchingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RestTemplate restTemplate;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    public List<MatchingResponse> getAiRecommendations(MatchingRequest request) {
        String prompt = String.format(
                """
                당신은 프로젝트 제작 도우미입니다.
                사용자가 원하는 조건은 다음과 같습니다:
                
                - 주제: '%s'
                - 예산: '%s'
                - 마감기간: '%s'
    
                이 조건을 고려하여 적절한 프로젝트 아이디어 3개를 JSON 배열로 생성해주세요.
    
                JSON 외에는 어떤 텍스트도 포함하지 말고, 코드블럭(```) 없이 순수 JSON만 응답해주세요.
    
                각 항목은 다음 필드를 포함해야 합니다:
    
                {
                  "title": "프로젝트명",
                  "description": "프로젝트 설명",
                  "budget": 예산 (숫자),
                  "deadline": "yyyy-MM-dd 형식 마감일",
                  "category": "VIDEO | WRITE | IT | MARKETING | HOBBY | TAX | STARTUP | TRANSLATE 중 하나"
                }
                
                title과 description은 다른 사용자의 관심을 끌 수 있게 만들어주세요.
                
                title과 description은 한글로 적어주세요.
                
                
                """,
                request.getSubject(), request.getBudget(), request.getDuration()
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> response = responseEntity.getBody();
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> firstChoice = (Map<String, Object>) choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String content = (String) message.get("content");

        return parseJsonToList(content);
    }

    private List<MatchingResponse> parseJsonToList(String content) {
        try {
            content = content.replaceAll("(?s)```json\\s*", "");
            content = content.replaceAll("(?s)```\\s*", "");

            ObjectMapper mapper = new ObjectMapper();
            return List.of(mapper.readValue(content.trim(), MatchingResponse[].class));
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}