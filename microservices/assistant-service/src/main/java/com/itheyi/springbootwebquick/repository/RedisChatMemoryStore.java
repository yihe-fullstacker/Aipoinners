package com.itheyi.springbootwebquick.repository;
/* 对话记忆存储层，负责保存和管理 AI 与用户之间的对话历史记录。*/
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {

    // Using an in-memory map instead of Redis to avoid "Unable to connect to Redis" errors
    private final Map<String, List<ChatMessage>> store = new ConcurrentHashMap<>();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMessage> messages = store.get(memoryId.toString());
        return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        store.put(memoryId.toString(), new ArrayList<>(list));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        store.remove(memoryId.toString());
    }
}
