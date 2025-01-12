package org.sid.calculator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private String directoryPath="C:\\Users\\aliat\\Desktop\\REACT 18\\CalculPlugins\\calculatorBack\\public";

    @GetMapping
    public List<String> listFiles() {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);

        // Vérifie si le dossier existe et est un dossier
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;  // Retourne la liste des noms de fichiers
    }
}
