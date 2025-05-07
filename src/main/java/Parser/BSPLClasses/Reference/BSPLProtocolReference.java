package Parser.BSPLClasses.Reference;

import Parser.BSPLClasses.BSPLParameter;
import Parser.BSPLClasses.BSPLRole;
import java.util.List;

public record BSPLProtocolReference(String protocolName, List<BSPLRole> roles,
                                    List<BSPLParameter> parameters) implements BSPLReference {
}
