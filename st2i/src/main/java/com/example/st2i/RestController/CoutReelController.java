package com.example.st2i.RestController;

import com.example.st2i.DTO.CoutReelRequest;
import com.example.st2i.DTO.CoutReelResponse;
import com.example.st2i.Services.CoutReelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cout-reels")
@CrossOrigin("*")
public class CoutReelController {

    @Autowired
    private CoutReelService coutReelService;



    @GetMapping("/projet/{projetId}")
    public List<CoutReelResponse> getByProjet(@PathVariable Long projetId) {
        return coutReelService.getByProjet(projetId);
    }

    @GetMapping("/projet/{projetId}/total")
    public Double getTotalByProjet(@PathVariable Long projetId) {
        return coutReelService.getTotalByProjet(projetId);
    }

    @PostMapping("/projet/{projetId}/recalculer")
    public void recalculer(@PathVariable Long projetId) {
        coutReelService.recalculerDepuisPointages(projetId);
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        coutReelService.delete(id);
    }
}