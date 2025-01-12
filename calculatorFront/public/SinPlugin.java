import org.sid.calculator.plugin.Plugin;

// SinPlugin.java
public class SinPlugin implements Plugin {

    @Override
    public String getName() {
        return "sin"; // Nom du plugin
    }

    @Override
    public double execute(double input) {
        return Math.sin(Math.toRadians(input)); // Calcul du sinus
    }
}
