package com.hotsix.server.message.controller;

import com.hotsix.server.message.service.SseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    //produces = MediaType.TEXT_EVENT_STREAM_VALUE => pring MVC가 응답을 text/event-stream 형식으로 보내고 HTTP 연결을 끊지 않은 채 계속 유지하게 된다.
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @RequestParam Long chatRoomId
    ) {
        //1. 새로운 SseEmitter(HTTP 스트림 하나를 관리하는 객체) 객체를 생성
        //2. emitter를 ChatRoomEmitterRepository에 저장
        //3. emitter의 timeout/completion 시 정리 로직 등록
        //4. 최초 연결 확인용 이벤트(connect) 전송 -> “connected : userId=1” 같은 데이터
        return sseService.connect(chatRoomId);
    }
}

//<프런트 SSE 연결 요청 코드>
//const eventSource = new EventSource(`/api/sse/connect?chatRoomtId=1`);