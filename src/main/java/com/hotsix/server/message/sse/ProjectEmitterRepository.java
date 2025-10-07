package com.hotsix.server.message.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProjectEmitterRepository {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long projectId, SseEmitter emitter) {
        emitters.computeIfAbsent(projectId, k -> new ArrayList<>()).add(emitter);
        return emitter;
    }

    public void remove(Long projectId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(projectId);
        if (list != null) list.remove(emitter);
    }

    public List<SseEmitter> findAllByProjectId(Long projectId) {
        return emitters.getOrDefault(projectId, Collections.emptyList());
    }
}
