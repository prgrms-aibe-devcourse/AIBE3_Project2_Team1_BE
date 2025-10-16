package com.hotsix.server.matching.controller;

import com.hotsix.server.matching.dto.MatchingRequest;
import com.hotsix.server.matching.dto.MatchingResponse;
import com.hotsix.server.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matching")
@Tag(name = "Matching-Controller", description = "AI 매칭 api")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(summary = "AI 매칭", description = "프로젝트 추천")
    @PostMapping("/ai")
    public List<MatchingResponse> getAiRecommendations(@RequestBody MatchingRequest request) {
        return matchingService.getAiRecommendations(request);
    }
}
