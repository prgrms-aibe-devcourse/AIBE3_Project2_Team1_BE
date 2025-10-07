package com.hotsix.server.message.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class ChatRoomEmitterRepository {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long chatRoomId, SseEmitter emitter) {
        emitters.computeIfAbsent(chatRoomId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    public void remove(Long chatRoomId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(chatRoomId);
        if (list != null) list.remove(emitter);
    }

    public List<SseEmitter> findAllByChatRoomId(Long chatRoomId) {
        return emitters.getOrDefault(chatRoomId, new CopyOnWriteArrayList<>());
    }

}
