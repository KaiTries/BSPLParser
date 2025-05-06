package tokens;

import java.util.List;

public record BSPLProtocol(BSPLName name, List<BSPLRole> roles, List<BSPLParameter> parameters,
                           List<BSPLMessage> messages, List<BSPLPrivateParameter> privates) {
}
