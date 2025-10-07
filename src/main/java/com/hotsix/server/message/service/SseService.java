package com.hotsix.server.message.service;

import com.hotsix.server.message.sse.ProjectEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SseService {

    private final ProjectEmitterRepository emitterRepository;

    public SseEmitter connect(Long projectId, Long userId) {

        //이 emitter가 '이 클라이언트의 실시간 통신 라인' 이 된다.
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 유지
        emitterRepository.save(projectId, emitter);

        // 연결 종료 / 타임아웃 처리
        emitter.onCompletion(() -> emitterRepository.remove(projectId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(projectId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected : userId=" + userId));
        } catch (IOException e) {
            emitterRepository.remove(projectId, emitter);
        }

        return emitter;
    }

    //실시간 메시지를 broadcast 하는 시점에서 호출됨
    public List<SseEmitter> getEmitters(Long projectId) {
        return emitterRepository.findAllByProjectId(projectId);
    }

    public void removeEmitter(Long projectId, SseEmitter emitter) {
        emitterRepository.remove(projectId, emitter);
    }
}
