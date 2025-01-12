package org.sid.calculator.controller;



import org.sid.calculator.request.CalculationRequest;
import org.sid.calculator.response.PluginResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    // Endpoint pour charger un plugin
    @PostMapping("/upload-plugin")
    public PluginResponse uploadPlugin(@RequestParam("file") MultipartFile file) {
        try {
            // Sauvegarder le fichier plugin (dans le dossier plugins)
            Path path = Paths.get("plugins", file.getOriginalFilename());
            Files.write(path, file.getBytes());


            return new PluginResponse(file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout du plugin");
        }
    }




}



