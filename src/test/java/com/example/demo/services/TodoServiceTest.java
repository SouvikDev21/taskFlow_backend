package com.example.demo.services;

import com.example.demo.Entity.Todo;
import com.example.demo.Entity.User;
import com.example.demo.Repository.TodoRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.TodoService;
import com.example.demo.dto.CreateTodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.updateTodo;
import com.example.demo.utils.TodoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;


    // ---------------------------------------------------------
    // CREATE TODO
    // ---------------------------------------------------------

    @Test
    void createTodo_shouldCreateAndReturnTodo() {

        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        CreateTodoRequest request = new CreateTodoRequest(
                userId,
                "Learn Spring Boot",
                "Learn unit testing with Mockito",
                TodoStatus.PENDING,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(todoRepository.save(any(Todo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TodoResponse response = todoService.createTodo(request);

        // Assert
        assertNotNull(response);

        assertEquals("Learn Spring Boot", response.title());
        assertEquals(
                "Learn unit testing with Mockito",
                response.description()
        );
        assertEquals(TodoStatus.PENDING, response.status());
        assertEquals(false, response.isDeleted());

        verify(userRepository).findById(userId);
        verify(todoRepository).save(any(Todo.class));
    }


    @Test
    void createTodo_shouldThrowException_whenUserDoesNotExist() {

        // Arrange
        Long userId = 100L;

        CreateTodoRequest request = new CreateTodoRequest(
                userId,
                "Learn Spring Boot",
                "Learn unit testing",
                TodoStatus.PENDING,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> todoService.createTodo(request)
        );

        assertEquals(
                "error in finding user",
                exception.getMessage()
        );

        // Todo should never be saved
        verify(todoRepository, never()).save(any(Todo.class));
    }


    @Test
    void createTodo_shouldAssignTodoToCorrectUser() {

        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        CreateTodoRequest request = new CreateTodoRequest(
                userId,
                "Test Todo",
                "Testing user assignment",
                TodoStatus.PENDING,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(todoRepository.save(any(Todo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        todoService.createTodo(request);

        // Capture the Todo passed to repository.save()
        ArgumentCaptor<Todo> captor =
                ArgumentCaptor.forClass(Todo.class);

        verify(todoRepository).save(captor.capture());

        Todo savedTodo = captor.getValue();

        // Assert
        assertEquals(user, savedTodo.getUser());
        assertEquals("Test Todo", savedTodo.getTitle());
        assertEquals(
                "Testing user assignment",
                savedTodo.getDescription()
        );
        assertEquals(TodoStatus.PENDING, savedTodo.getStatus());
    }


    // ---------------------------------------------------------
    // GET ALL TODOS
    // ---------------------------------------------------------

    @Test
    void getAllTodos_shouldReturnTodosForUser() {

        // Arrange
        Long userId = 1L;

        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTitle("Todo 1");
        todo1.setDescription("Description 1");
        todo1.setStatus(TodoStatus.PENDING);
        todo1.setIsDeleted(false);

        Todo todo2 = new Todo();
        todo2.setId(2L);
        todo2.setTitle("Todo 2");
        todo2.setDescription("Description 2");
        todo2.setStatus(TodoStatus.COMPLETED);
        todo2.setIsDeleted(false);

        when(todoRepository.findByUserId(userId))
                .thenReturn(List.of(todo1, todo2));

        // Act
        List<TodoResponse> result =
                todoService.getAllTodos(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("Todo 1", result.get(0).title());
        assertEquals(TodoStatus.PENDING, result.get(0).status());

        assertEquals(2L, result.get(1).id());
        assertEquals("Todo 2", result.get(1).title());
        assertEquals(TodoStatus.COMPLETED, result.get(1).status());

        verify(todoRepository).findByUserId(userId);
    }


    @Test
    void getAllTodos_shouldReturnEmptyList_whenUserHasNoTodos() {

        // Arrange
        Long userId = 1L;

        when(todoRepository.findByUserId(userId))
                .thenReturn(List.of());

        // Act
        List<TodoResponse> result =
                todoService.getAllTodos(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(todoRepository).findByUserId(userId);
    }


    // ---------------------------------------------------------
    // UPDATE TODO
    // ---------------------------------------------------------

    @Test
    void updateTodo_shouldUpdateStatus() {

        // Arrange
        Long todoId = 1L;

        Todo existingTodo = new Todo();
        existingTodo.setId(todoId);
        existingTodo.setTitle("Learn Java");
        existingTodo.setDescription("Study Spring Boot");
        existingTodo.setStatus(TodoStatus.PENDING);
        existingTodo.setIsDeleted(false);

        updateTodo request = new updateTodo(
                todoId,
                TodoStatus.COMPLETED
        );

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.of(existingTodo));

        when(todoRepository.save(any(Todo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TodoResponse response =
                todoService.updateTodo(request);

        // Assert
        assertNotNull(response);

        assertEquals(todoId, response.id());
        assertEquals(TodoStatus.COMPLETED, response.status());

        // Verify the actual entity was changed
        assertEquals(
                TodoStatus.COMPLETED,
                existingTodo.getStatus()
        );

        verify(todoRepository).findById(todoId);
        verify(todoRepository).save(existingTodo);
    }


    @Test
    void updateTodo_shouldThrowException_whenTodoDoesNotExist() {

        // Arrange
        Long todoId = 999L;

        updateTodo request = new updateTodo(
                todoId,
                TodoStatus.COMPLETED
        );

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> todoService.updateTodo(request)
        );

        assertEquals(
                "Todo not found",
                exception.getMessage()
        );

        verify(todoRepository, never())
                .save(any(Todo.class));
    }


    // ---------------------------------------------------------
    // SOFT DELETE
    // ---------------------------------------------------------

    @Test
    void softDeleteTodo_shouldMarkTodoAsDeleted() {

        // Arrange
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setTitle("Delete me");
        todo.setIsDeleted(false);

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.of(todo));

        // Act
        todoService.softDeleteTodo(todoId);

        // Assert
        assertTrue(todo.getIsDeleted());

        verify(todoRepository).findById(todoId);
        verify(todoRepository).save(todo);
    }


    @Test
    void softDeleteTodo_shouldThrowException_whenTodoDoesNotExist() {

        // Arrange
        Long todoId = 999L;

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> todoService.softDeleteTodo(todoId)
        );

        assertEquals(
                "todo not found",
                exception.getMessage()
        );

        verify(todoRepository, never())
                .save(any(Todo.class));
    }
}