package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.message.sse.ChatRoomEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SseService {

    private final ChatRoomEmitterRepository emitterRepository;
    private final Rq rq;

    public SseEmitter connect(Long chatRoomId) {

        //이 emitter가 '이 클라이언트의 실시간 통신 라인' 이 된다.
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 유지
        emitterRepository.save(chatRoomId, emitter);

        // 연결 종료 / 타임아웃 처리
        emitter.onCompletion(() -> emitterRepository.remove(chatRoomId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(chatRoomId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected : userId=" + rq.getUser()));
        } catch (IOException e) {
            emitterRepository.remove(chatRoomId, emitter);
        }

        return emitter;
    }

    public void removeEmitter(Long chatRoomId, SseEmitter emitter) {
        emitterRepository.remove(chatRoomId, emitter);
    }
}
