package com.krillinator.spring_security.todo;

import com.krillinator.spring_security.user.CustomUser;
import com.krillinator.spring_security.user.CustomUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository todoRepository;
    private final CustomUserRepository userRepository;

    public TodoController(TodoRepository todoRepository, CustomUserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ')")
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE')")
    public Todo createTodo(
            @RequestBody String title,
            Authentication authentication
    ) {
        CustomUser user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        return todoRepository.save(new Todo(title, user));
    }
}
