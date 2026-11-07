package com.chat.repository;

import com.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByRoomIdOrderBySentAtDesc(Long roomId, Pageable pageable);
    int countByRoomId(Long roomId);
}
