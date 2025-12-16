package com.krillinator.spring_security.todo;

import com.krillinator.spring_security.user.CustomUser;
import jakarta.persistence.*;

@Entity
public class Todo {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    public Todo() {}

    public Todo(String title, CustomUser user) {
        this.title = title;
        this.user = user;
        this.completed = false;
    }

    // getters & setters
}
