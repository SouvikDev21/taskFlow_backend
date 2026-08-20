package com.example.demo.Service;

import com.example.demo.Entity.Todo;
import com.example.demo.Entity.User;
import com.example.demo.Repository.TodoRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.dto.CreateTodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.updateTodo;
import com.example.demo.utils.TodoStatus;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TodoService {
    private  TodoRepository todoRepository;
    private UserRepository userRepository;

    //Creating newTodo
    public TodoResponse createTodo(CreateTodoRequest req){
        //create todo object
        Todo todo=new Todo();
        //find the user who is creating the todo
        User user=userRepository.findById(req.userId()).orElseThrow(()->new RuntimeException("error in finding user"));
        todo.setTitle(req.title());
        todo.setDescription(req.description());
        todo.setStatus(req.status());
        todo.setDueDate(req.dueDate());
        todo.setUser(user);

        //save the new todo in DB
        todoRepository.save(todo);
        return new TodoResponse(todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getUpdatedAt(),
                todo.getDueDate(),
                todo.getIsDeleted()
        );
    }
    //Get All Todos for a User
    public List<TodoResponse> getAllTodos(Long id){
        //get all Todos for a user from db
        List<Todo>  alltodo= todoRepository.findByUserId(id);
        //cast all todos in todo response to send to user
        List<TodoResponse> todolist=new ArrayList<>();
            for(int i=0;i<alltodo.size();i++){
                TodoResponse response =new TodoResponse(
                    alltodo.get(i).getId(),
                    alltodo.get(i).getTitle(),
                    alltodo.get(i).getDescription(),
                    alltodo.get(i).getStatus(),
                    alltodo.get(i).getCreatedAt(),
                    alltodo.get(i).getUpdatedAt(),
                    alltodo.get(i).getDueDate(),
                    alltodo.get(i).getIsDeleted()
            );
            todolist.add(response);
        }
        //send the todos
        return todolist;
    }
    public TodoResponse updateTodo(updateTodo req){
        Long id=req.id();
        TodoStatus status=req.status();
        //find the todo from DB
        Todo existingTodo=todoRepository.findById(id).orElseThrow(()->new RuntimeException("Todo not found"));
        //change the status
        existingTodo.setStatus(status);
        Todo todo = todoRepository.save(existingTodo);
        System.out.println(todo.toString());
        TodoResponse res= new TodoResponse(
                todo.getId(),todo.getTitle(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getUpdatedAt(),
                todo.getDueDate(),
                todo.getIsDeleted()
        );
        return res;
    }

    public void softDeleteTodo(Long id) {
        Todo todo=todoRepository.findById(id).orElseThrow(()->new RuntimeException("todo not found"));
        todo.setIsDeleted(Boolean.TRUE);
        todoRepository.save(todo);
    }
}
