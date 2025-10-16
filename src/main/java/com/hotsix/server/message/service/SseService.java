package com.hotsix.server.message.service;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.message.sse.ChatRoomEmitterRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SseService {

    private final ChatRoomEmitterRepository emitterRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final Rq rq;

    @Transactional
    public SseEmitter connect(Long chatRoomId, String token) {
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.USER_NOT_FOUND));

        // ✅ 3. emitter 생성 및 등록
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitterRepository.save(chatRoomId, emitter);

        emitter.onCompletion(() -> emitterRepository.remove(chatRoomId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(chatRoomId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected : userId=" + user.getUserId()));
        } catch (IOException e) {
            emitterRepository.remove(chatRoomId, emitter);
        }

        return emitter;
    }

    @Transactional
    public void removeEmitter(Long chatRoomId, SseEmitter emitter) {
        emitterRepository.remove(chatRoomId, emitter);
    }
}
