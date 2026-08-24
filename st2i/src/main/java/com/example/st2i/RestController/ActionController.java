package com.example.st2i.RestController;

import com.example.st2i.DTO.ActionRequest;
import com.example.st2i.Entities.Action;
import com.example.st2i.Services.ActionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@CrossOrigin("*")
public class ActionController {

    @Autowired
    private ActionService actionService;

    @PostMapping
    public Action create(@Valid @RequestBody ActionRequest req) {
        return actionService.create(req);
    }

    @GetMapping("/projet/{id}")
    public List<Action> getByProjet(@PathVariable Long id) {
        return actionService.getByProjet(id);
    }

    @PutMapping("/{id}")
    public Action update(@PathVariable Long id,
                         @Valid @RequestBody ActionRequest req) {
        return actionService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        actionService.delete(id);
    }
}