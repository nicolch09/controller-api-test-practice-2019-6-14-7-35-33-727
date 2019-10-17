package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {
    @MockBean
    TodoRepository todoRepository;

    @Autowired
    private MockMvc mvc;

    @Test
    void should_receive_data_when_todo_has_values() throws Exception {
        List<Todo> todos = new ArrayList<>();
        Todo todo = new Todo("title", true);
        todos.add(todo);
        when(todoRepository.getAll()).thenReturn(todos);

        ResultActions result = mvc.perform(get("/todos"));

        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].completed", is(true)))
                .andExpect(jsonPath("$[0].title").value("title"));
    }

    @Test
    void should_return_data_when_id_is_5() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        when(todoRepository.findById(5)).thenReturn(todo);

        ResultActions result = mvc.perform(get("/todos/{todo-id}", 5L));

        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.completed", is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order").value(10));
    }

    @Test
    void should_not_return_data_when_id_is_3() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        when(todoRepository.findById(5)).thenReturn(todo);

        ResultActions result = mvc.perform(get("/todos/{todo-id}", 3L));

        result.andExpect(status().isNotFound());
    }

    @Test
    void should_create_the_data() throws Exception {
        Todo todo = new Todo(5, "Test", true, 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(todo);

        ResultActions result = mvc.perform(post("/todos")
                .content(jsonValue)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated()).andReturn();
    }

    @Test
    void should_delete_the_data_when_id_is_5() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        when(todoRepository.findById(5)).thenReturn(todo);

        ResultActions result = mvc.perform(delete("/todos/{todo-id}", 5L ));

        result.andExpect(status().isOk());
    }

    @Test
    void should_not_delete_the_data_when_id_is_3() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        when(todoRepository.findById(5)).thenReturn(todo);

        ResultActions result = mvc.perform(delete("/todos/{todo-id}", 3L ));

        result.andExpect(status().isNotFound());
    }

    @Test
    void should_update_the_data_when_id_is_5() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        Optional<Todo> updated = Optional.of(new Todo(5, "title", true, 10));
        when(todoRepository.findById(5)).thenReturn(todo);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(updated);

        ResultActions result = mvc.perform(patch("/todos/{todo-id}", 5L )
                .content(jsonValue)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    void should_not_update_the_data_when_id_is_3() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        Optional<Todo> updated = Optional.of(new Todo(5, "title", true, 10));
        when(todoRepository.findById(5)).thenReturn(todo);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(updated);

        ResultActions result = mvc.perform(patch("/todos/{todo-id}", 3L )
                .content(jsonValue)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    void should_not_update_the_data_when_url_is_invalid() throws Exception {
        Optional<Todo> todo = Optional.of(new Todo(5, "Test", true, 10));
        Optional<Todo> updated = Optional.of(null);
        when(todoRepository.findById(5)).thenReturn(todo);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(updated);

        ResultActions result = mvc.perform(patch("/todos/{todo-id}", 5L )
                .content(jsonValue)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }
}