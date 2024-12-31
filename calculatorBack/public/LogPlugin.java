import org.sid.calculator.plugin.Plugin;

public class LogPlugin implements Plugin {

    @Override
    public String getName() {
        return "log"; // Nom du plugin
    }

    @Override
    public double execute(double input) {
        return Math.log(input); // Calcul du sinus
    }
}
