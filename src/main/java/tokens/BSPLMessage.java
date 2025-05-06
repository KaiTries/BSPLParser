package tokens;

import java.util.List;

public record BSPLMessage(BSPLName sender, BSPLName recipient, BSPLName name,
                          List<BSPLParameter> parameters) {
}
