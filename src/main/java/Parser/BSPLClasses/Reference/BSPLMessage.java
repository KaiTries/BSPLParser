package Parser.BSPLClasses.Reference;

import Parser.BSPLClasses.BSPLParameter;
import java.util.List;

public record BSPLMessage(String sender, String recipient, String name,
                          List<BSPLParameter> parameters) implements BSPLReference {
}
