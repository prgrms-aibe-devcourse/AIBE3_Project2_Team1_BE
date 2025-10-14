package com.hotsix.server.matching.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsix.server.matching.dto.MatchingRequest;
import com.hotsix.server.matching.dto.MatchingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RestTemplate restTemplate;

    public List<MatchingResponse> getAiRecommendations(MatchingRequest request) {
        String prompt = String.format(
                "당신은 프리랜서 프로젝트 추천 도우미입니다. " +
                        "예산은 '%s', 기간은 '%s'입니다. " +
                        "이 조건에 맞는 프로젝트 아이디어 3개를 JSON 배열로 만들어주세요. " +
                        "각 항목은 {\"title\": \"프로젝트명\", \"description\": \"간단한 설명\", \"imageUrl\": \"샘플 이미지 경로\"} 형태로 주세요.",
                request.getBudget(), request.getDuration()
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body);

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
            ObjectMapper mapper = new ObjectMapper();
            MatchingResponse[] responses = mapper.readValue(content, MatchingResponse[].class);
            return List.of(responses);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}