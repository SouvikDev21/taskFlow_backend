package com.example.demo.Entity;

import com.example.demo.utils.TodoStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="todos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Todo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 100)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private TodoStatus status;
    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @Column
    private LocalDate dueDate;
    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
    @Column
    private Boolean isDeleted=false;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;
}
