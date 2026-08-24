package com.example.st2i.RestController;

import com.example.st2i.DTO.ChatRequest;
import com.example.st2i.Services.RagService;
import com.example.st2i.Services.SyntheseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "http://localhost:4200")
public class IaController {

    private final SyntheseService syntheseService;
    private final RagService ragService;

    public IaController(SyntheseService syntheseService, RagService ragService) {
        this.syntheseService = syntheseService;
        this.ragService = ragService;
    }

    @GetMapping("/synthese/{projetId}")
    public String synthese(@PathVariable Long projetId) {
        return syntheseService.genererSynthese(projetId);
    }

    @PostMapping("/chat")
    public String chat(@Valid @RequestBody ChatRequest req) {
        return ragService.chat(req.getQuestion());
    }
}