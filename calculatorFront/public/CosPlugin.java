import org.sid.calculator.plugin.Plugin;

public class CosPlugin implements Plugin {

    @Override
    public String getName() {
        return "cos"; // Nom du plugin
    }

    @Override
    public double execute(double input) {
        return Math.cos(Math.toRadians(input)); // Calcul du sinus
    }
}
