//package com.hotsix.server.message.repository;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class EmitterRepository {
//
//    // (userId or projectId) → emitter 매핑
//    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
//
//    public SseEmitter save(Long key, SseEmitter emitter) {
//        emitters.put(key, emitter);
//        return emitter;
//    }
//
//    public void delete(Long key) {
//        emitters.remove(key);
//    }
//
//    public SseEmitter get(Long key) {
//        return emitters.get(key);
//    }
//}