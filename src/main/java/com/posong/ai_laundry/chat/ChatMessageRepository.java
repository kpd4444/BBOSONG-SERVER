package com.posong.ai_laundry.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);

    long countByChatRoom(ChatRoom chatRoom);

    @Modifying
    @Query(value = """
            delete from chat_message
            where id in (
                select id from (
                    select id
                    from chat_message
                    where chat_room_id = :chatRoomId
                    order by created_at desc
                    limit 18446744073709551615 offset :offset
                ) old_messages
            )
            """, nativeQuery = true)
    void deleteMessagesExceedingRetention(@Param("chatRoomId") Long chatRoomId, @Param("offset") long offset);
}
