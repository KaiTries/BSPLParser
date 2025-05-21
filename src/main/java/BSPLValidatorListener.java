import java.util.HashSet;
import java.util.Set;


public class BSPLValidatorListener extends BSPLBaseListener {
    private final Set<String> definedRoles = new HashSet<>();
    private final Set<String> definedParams = new HashSet<>();


    /**
     * Checks that atleast one of the parameters is a key.
     *
     * @param ctx the parse tree
     */
    @Override
    public void exitParams(BSPLParser.ParamsContext ctx) {
        boolean hasKey = false;
        for (BSPLParser.ParamContext param : ctx.param()) {
            if (param.getText().contains("key")) {
                hasKey = true;
            }
            definedParams.add(param.name.getText());
        }
        if (!hasKey) {
            System.err.println("Error: 'key' parameter is missing in the parameters list.");
        }
    }

    /**
     * Checks that the roles are defined and not duplicated.
     *
     * @param ctx the parse tree
     */
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

    /**
     * Checks that the message sender and recipient are defined roles,
     * and that parameters exist.
     *
     *
     * @param ctx the parse tree
     */
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

        for (BSPLParser.ParamContext param : ctx.params().param()) {
            if (!definedParams.contains(param.name.getText())) {
                System.err.println("Error: Parameter '" + param.getText() + "' is not defined.");
            }
        }


    }
}
