package com.posong.ai_laundry.chat;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConversationStore {
    // 매우 단순한 메모리 저장소 (배포 전에는 Redis/DB 고려)
    private final Map<String, Deque<String>> history = new HashMap<>();
    private static final int MAX_TURNS = 12; // 마지막 12턴만 유지

    public String start() {
        String id = UUID.randomUUID().toString();
        history.put(id, new ArrayDeque<>());
        return id;
    }

    public void append(String conversationId, String role, String content) {
        var deque = history.getOrDefault(conversationId, new ArrayDeque<>());
        deque.addLast(role + ":" + content);
        while (deque.size() > MAX_TURNS) deque.removeFirst();
        history.put(conversationId, deque);

    }

    public List<String> get(String conversationId) {
        return List.copyOf(history.getOrDefault(conversationId, new ArrayDeque<>()));
    }
}
