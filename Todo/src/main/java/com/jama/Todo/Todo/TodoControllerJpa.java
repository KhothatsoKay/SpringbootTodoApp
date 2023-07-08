package com.jama.Todo.Todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("name")
public class TodoControllerJpa {

    public TodoControllerJpa(TodoRespository todoRespository) {
        super();

        this.todoRespository = todoRespository;
    }

    private TodoRespository todoRespository;

    @RequestMapping("list-todos")
    public String listTodos(ModelMap model) {
        String username = getLoggedinUsername(model);
        List<Todo> todos = todoRespository.findByUsername(username);
        model.addAttribute("todos", todos);
        return "todos";
    }

    @RequestMapping(value = "add-todo", method = RequestMethod.GET)
    public String ShowNewTodoPage(ModelMap model) {
        String username = getLoggedinUsername(model);

        Todo todo = new Todo(0, username, "Default Desc", LocalDate.now().plusYears(1), false);
        model.put("todo", todo);
        return "todo";
    }

    @RequestMapping(value = "add-todo", method = RequestMethod.POST)
    public String AddNewTodoPage(ModelMap model, @Valid Todo todo, BindingResult result) {
        if (result.hasErrors()) {
            return "todo";
        }
        String username = getLoggedinUsername(model);
        todo.setUsername(username);
        todoRespository.save(todo);
        // todoService.addTodo(username, todo.getDescription(),
        // todo.getTargetDate(),todo.getIsDone());
        return "redirect:list-todos";
    }

    @RequestMapping("delete-todo")
    public String DeleteTodo(@RequestParam int id) {
        todoRespository.deleteById(id);
        return "redirect:list-todos";
    }

    @RequestMapping(value = "update-todo", method = RequestMethod.GET)
    public String ShowUpdateTodo(@RequestParam int id, ModelMap model) {
        Todo todo = todoRespository.findById(id).get();
        model.addAttribute("todo", todo);
        return "todo";
    }

    @RequestMapping(value = "update-todo", method = RequestMethod.POST)
    public String UpdateTodo(ModelMap model, @Valid Todo todo, BindingResult result) {
        if (result.hasErrors()) {
            return "todo";
        }
        String username = getLoggedinUsername(model);
        todo.setUsername(username);
        todoRespository.save(todo);
        // todoService.updateTodo(todo);
        return "redirect:list-todos";
    }

    private String getLoggedinUsername(ModelMap model) {
        Authentication Authentication = SecurityContextHolder.getContext().getAuthentication();
        return Authentication.getName();
    }

}
