package org.sid.calculator.controller;


import org.sid.calculator.plugin.Plugin;
import org.sid.calculator.request.PluginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    // Gérer l'upload du fichier Java
    String targetDir = "src/main/resources/uploads/";

    @PostMapping("/upload")
    public String uploadPlugin(@RequestParam("file") MultipartFile file) {

        try {
            // Créer le répertoire s'il n'existe pas
            File directory = new File(targetDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Crée le répertoire et ses sous-répertoires si nécessaire
            }

            // Créer un chemin vers le fichier dans le répertoire 'uploads'
            Path targetPath = Paths.get(targetDir, file.getOriginalFilename());

            // Sauvegarder le fichier dans le répertoire cible
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(file.getBytes());
            }

            // Compiler le fichier Java
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int result = compiler.run(null, null, null, targetPath.toString());

            if (result == 0) {
                // Si la compilation réussit, charger et exécuter la classe compilée
                String className = file.getOriginalFilename().replace(".java", ""); // Utilisation du nom original du fichier
                executePlugin(className); // Exécution du plugin
                return "{\"pluginName\": \"" + className + "\"}"; // Retourne le nom du plugin en réponse JSON
            } else {
                return "Erreur de compilation.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur lors de l'upload ou de la compilation : " + e.getMessage();
        }
    }

    // Méthode pour charger et exécuter la classe compilée
    private void executePlugin(String className) {
        try {
            File pluginDir = new File(targetDir);
            URL[] urls = {pluginDir.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);
            Class<?> loadedClass = classLoader.loadClass(className);

            if (Plugin.class.isAssignableFrom(loadedClass)) {
                Plugin plugin = (Plugin) loadedClass.getDeclaredConstructor().newInstance();
                System.out.println("Exécution du plugin " + plugin.getName() + " avec le résultat : " + plugin.execute(Math.PI / 2));
            } else {
                System.out.println("La classe ne respecte pas l'interface Plugin.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Répertoire où les plugins sont stockés, en supposant qu'il soit sous /resources/uploads/
    private static final String PLUGIN_DIR = "src/main/resources/uploads/";

    // Méthode GET pour récupérer les noms des plugins disponibles
    @GetMapping("/plugins")
    public List<String> getAvailablePlugins() {
        List<String> pluginNames = new ArrayList<>();
        File pluginDir = new File(PLUGIN_DIR);

        // Vérifier si le répertoire existe
        if (pluginDir.exists() && pluginDir.isDirectory()) {
            try {
                // Récupérer tous les fichiers .class dans le répertoire des plugins
                File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".class"));

                if (files != null) {
                    // Créer un class loader pour charger les plugins depuis le répertoire
                    URL[] urls = {pluginDir.toURI().toURL()};
                    URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

                    // Parcourir tous les fichiers .class
                    for (File file : files) {
                        String className = file.getName().replace(".class", "");

                        try {
                            // Charger la classe
                            Class<?> loadedClass = classLoader.loadClass(className);

                            // Vérifier si la classe implémente l'interface Plugin
                            if (Plugin.class.isAssignableFrom(loadedClass)) {
                                Plugin plugin = (Plugin) loadedClass.getDeclaredConstructor().newInstance();
                                pluginNames.add(plugin.getName()); // Ajouter le nom du plugin à la liste
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // Gérer les erreurs de chargement de classe
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Gérer les erreurs d'accès au répertoire
            }
        }

        return pluginNames;
    }


    // Méthode pour exécuter un plugin spécifique
    @PostMapping("/execute-plugin")
    public ResponseEntity<?> execute(@RequestBody PluginRequest pluginRequest) {
        String name=pluginRequest.getName();
        System.out.println(name);
        try {
            // Chargez et exécutez le plugin en fonction du nom
            Plugin plugin = loadPluginByName(name);
            System.out.println(plugin);


            if (plugin != null) {

                // Exécutez le plugin avec Math.PI comme entrée et retourner le résultat

                Double result = plugin.execute(pluginRequest.getValue());

                return ResponseEntity.ok(result);  // Retourne le résultat du plugin (Double)
            } else {
                // Retourner une réponse d'erreur avec un code 404 si le plugin n'est pas trouvé
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Plugin non trouvé");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Retourner une réponse d'erreur avec un code 500 en cas d'exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'exécution du plugin " +name+ ": " + e.getMessage());
        }
    }


    private Plugin loadPluginByName(String name) {
        try {
            // Chemin du répertoire des plugins
            File pluginDir = new File("src/main/resources/uploads/");

            // Vérifier si le répertoire existe
            if (!pluginDir.exists() || !pluginDir.isDirectory()) {
                System.out.println("Le répertoire des plugins n'existe pas ou n'est pas valide.");
                return null;
            }

            // Construire le chemin complet du fichier .class
            File pluginClassFile = new File(pluginDir, name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + "Plugin.class");

            // Vérifier si le fichier existe
            if (!pluginClassFile.exists()) {
                System.out.println("Le fichier " + pluginClassFile.getAbsolutePath() + " n'existe pas.");
                return null;
            }

            // Créer un URLClassLoader pour charger la classe depuis le répertoire des plugins
            URL[] urls = { pluginDir.toURI().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader()); // Utilise le ClassLoader parent


            // Charger dynamiquement la classe à partir du nom du plugin
            Class<?> clazz = classLoader.loadClass(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase()+"Plugin");
            System.out.println("ClassLoader de l'interface Plugin : " + Plugin.class.getClassLoader());
            System.out.println("ClassLoader de la classe CosPlugin : " + clazz.getClassLoader());

            // Vérifier si la classe implémente l'interface Plugin
            if (Plugin.class.isAssignableFrom(clazz)) {
                // Créer une nouvelle instance du plugin
                return (Plugin) clazz.getDeclaredConstructor().newInstance();
            } else {
                // Si la classe ne respecte pas l'interface Plugin
                System.out.println("La classe " + name + " ne respecte pas l'interface Plugin.");
                return null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Plugin " + name + " non trouvé.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}