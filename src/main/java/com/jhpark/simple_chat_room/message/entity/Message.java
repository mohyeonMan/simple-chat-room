package com.jhpark.simple_chat_room.message.entity;

import java.time.LocalDateTime;

import com.jhpark.simple_chat_room.room.entity.Room;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "user_id", nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String content;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public void deleteMessage(){
        this.deletedAt = LocalDateTime.now();
    }

}