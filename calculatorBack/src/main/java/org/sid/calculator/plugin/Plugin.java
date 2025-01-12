package org.sid.calculator.plugin;

public interface Plugin {
     String getName();          // Retourne le nom du plugin (ex: "sin")
     double execute(double input); // Exécute le calcul avec un input
}
