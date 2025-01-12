import org.sid.calculator.plugin.Plugin;

// SinPlugin.java
public class TanPlugin implements Plugin {

    @Override
    public String getName() {
        return "tan"; // Nom du plugin
    }

    @Override
    public double execute(double input) {
        return Math.tan(Math.toRadians(input)); // Calcul du sinus
    }
}
