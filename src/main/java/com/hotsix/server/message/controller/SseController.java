package com.hotsix.server.message.controller;

import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.message.service.SseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
@Tag(name = "SseController", description = "SSE 컨트롤러")
public class SseController {

    private final SseService sseService;
    private final JwtTokenProvider jwtTokenProvider;


    //produces = MediaType.TEXT_EVENT_STREAM_VALUE => pring MVC가 응답을 text/event-stream 형식으로 보내고 HTTP 연결을 끊지 않은 채 계속 유지하게 된다.
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @RequestParam Long chatRoomId,
            @RequestParam(required = false) String token,
            HttpServletResponse response
    ) {
        if (token != null && !token.isBlank()) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        SseEmitter emitter = sseService.connect(chatRoomId);

        response.setHeader("Access-Control-Allow-Origin", "https://pickplezone.vercel.app");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        //1. 새로운 SseEmitter(HTTP 스트림 하나를 관리하는 객체) 객체를 생성
        //2. emitter를 ChatRoomEmitterRepository에 저장
        //3. emitter의 timeout/completion 시 정리 로직 등록
        //4. 최초 연결 확인용 이벤트(connect) 전송 -> “connected : userId=1” 같은 데이터

        return sseService.connect(chatRoomId);
    }
}

//<프런트 SSE 연결 요청 코드>
//const eventSource = new EventSource(`/api/v1/sse/connect?chatRoomId=1`);