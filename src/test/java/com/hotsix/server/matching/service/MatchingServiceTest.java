//package com.hotsix.server.matching.service;
//
//import com.hotsix.server.matching.dto.MatchingRequest;
//import com.hotsix.server.matching.dto.MatchingResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentMatchers;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//class MatchingServiceTest {
//
//    private MatchingService matchingService;
//
//    private HttpHeaders httpHeaders;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        httpHeaders = new HttpHeaders();
//        httpHeaders.set("Authorization", "Bearer test-key");
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        matchingService = new MatchingService(restTemplate);
//    }
//
//    @Test
//    void testGetAiRecommendations() {
//        MatchingRequest request = new MatchingRequest();
//        request.setBudget("20-30");
//        request.setDuration("50");
//
//        String aiResponseJson = "[{\"title\":\"프로젝트1\",\"description\":\"설명1\",\"imageUrl\":\"/img1.jpg\"}," +
//                "{\"title\":\"프로젝트2\",\"description\":\"설명2\",\"imageUrl\":\"/img2.jpg\"}," +
//                "{\"title\":\"프로젝트3\",\"description\":\"설명3\",\"imageUrl\":\"/img3.jpg\"}]";
//
//        Map<String, Object> messageMap = Map.of("content", aiResponseJson);
//        Map<String, Object> choiceMap = Map.of("message", messageMap);
//        Map<String, Object> responseMap = Map.of("choices", List.of(choiceMap));
//
//        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseMap, HttpStatus.OK);
//
//        when(restTemplate.exchange(
//                ArgumentMatchers.anyString(),
//                ArgumentMatchers.any(HttpMethod.class),
//                ArgumentMatchers.<HttpEntity<?>>any(),
//                ArgumentMatchers.<Class<Map>>any()
//        )).thenReturn(responseEntity);
//
//        List<MatchingResponse> result = matchingService.getAiRecommendations(request);
//
//        assertEquals(3, result.size());
//        assertEquals("프로젝트1", result.get(0).getTitle());
//        assertEquals("프로젝트2", result.get(1).getTitle());
//        assertEquals("프로젝트3", result.get(2).getTitle());
//    }
//}