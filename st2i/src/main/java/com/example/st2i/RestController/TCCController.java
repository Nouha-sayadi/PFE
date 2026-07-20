package com.example.st2i.RestController;

import com.example.st2i.DTO.TCCRequest;
import com.example.st2i.DTO.TCCResponse;
import com.example.st2i.Services.TCCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tcc")
@CrossOrigin("*")
public class TCCController {

    @Autowired
    private TCCService tccService;

    @PostMapping
    public TCCResponse create(@RequestBody TCCRequest request) {
        return tccService.create(request);
    }

    @GetMapping
    public List<TCCResponse> getAll() {
        return tccService.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<TCCResponse> getByUser(@PathVariable Long userId) {
        return tccService.getByUser(userId);
    }

    @PutMapping("/{id}")
    public TCCResponse update(@PathVariable Long id, @RequestBody TCCRequest request) {
        return tccService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tccService.delete(id);
    }
}