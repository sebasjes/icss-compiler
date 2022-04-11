package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;


import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {


    public String generate(AST ast){
        StringBuilder stringBuilder = new StringBuilder();

        for(ASTNode child : ast.root.getChildren()) {
            if (child instanceof Stylerule) {
                generateStyleRule((Stylerule) child, stringBuilder);
            }
        }
        return stringBuilder.toString();
    }

    private void generateStyleRule(Stylerule stylerule, StringBuilder stringBuilder) {
        for (int i = 0; i < stylerule.selectors.size(); i++) {
            stringBuilder.append(stylerule.selectors.get(i))
                    .append("{ \n");
            for (ASTNode body : stylerule.body) {
                if (body instanceof Declaration) {
                    generateDeclaration((Declaration) body, stringBuilder);
                }
            }
            stringBuilder.append("} \n \n");
        }
    }

    private void generateDeclaration(Declaration declaration, StringBuilder stringBuilder) {
        stringBuilder.append("  ")
                .append(declaration.property.name)
                .append(":")
                .append(generateLiteral(declaration.expression))
                .append("; \n");
    }

    private String generateLiteral(Expression literal) {
        if (literal instanceof PercentageLiteral) {
            return ((PercentageLiteral) literal).value + "%";
        }
        if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value + "px";
        }
        if (literal instanceof ColorLiteral) {
            return ((ColorLiteral) literal).value + "";
        }
        return "";
    }
}
