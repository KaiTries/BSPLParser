import java.util.HashSet;
import java.util.Set;


public class BSPLValidatorListener extends BSPLBaseListener {
    private final Set<String> definedRoles = new HashSet<>();

    @Override
    public void exitParams(BSPLParser.ParamsContext ctx) {
        boolean hasKey = false;
        for (BSPLParser.ParamContext param : ctx.param()) {
            if (param.getText().contains("key")) {
                hasKey = true;
            }
        }
        if (!hasKey) {
            System.err.println("Error: 'key' parameter is missing in the parameters list.");
        }
    }

    @Override
    public void enterRoles(BSPLParser.RolesContext ctx) {
        for (BSPLParser.RoleContext role : ctx.role()) {
            String roleName = role.name.getText();
            if (definedRoles.contains(roleName)) {
                System.err.println("Error: Role '" + roleName + "' is already defined.");
            } else {
                definedRoles.add(roleName);
            }
        }
    }

    @Override
    public void enterMessage(BSPLParser.MessageContext ctx) {
        String sender = ctx.sender.getText();
        String recipient = ctx.recipient.getText();

        if (!definedRoles.contains(sender)) {
            System.err.println("Error: Sender role '" + sender + "' is not defined.");
        }
        if (!definedRoles.contains(recipient)) {
            System.err.println("Error: Recipient role '" + recipient + "' is not defined.");
        }
    }
}
