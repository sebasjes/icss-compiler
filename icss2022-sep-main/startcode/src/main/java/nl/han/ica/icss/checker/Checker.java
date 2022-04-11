package nl.han.ica.icss.checker;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.*;

import java.util.HashMap;


public class Checker {

    public HashMap<String, ExpressionType> variableTypes = new HashMap<>();

    public void check(AST ast) {
        check(ast.root);
        this.checkStylesheetExist(ast.root);
    }

    public void check(ASTNode node) {
        if (node instanceof VariableAssignment) {
            addVariableAssignmentToTypeHashmap((VariableAssignment) node);
        }
        if (node instanceof VariableReference) {
            checkVariableReference((VariableReference) node);
        }
        if (node.getChildren().size() > 0) {
            for (var astNode : node.getChildren()) {
                this.check(astNode);
            }
        }
    }

    // Haalt de variableAssignment en zet deze in de hashmap.
    private void addVariableAssignmentToTypeHashmap(VariableAssignment node) {
        var expression = node.expression;
        var name = node.name.name;
        var expressionType = getExpressionType(expression);
        variableTypes.put(name, expressionType);
    }

    //kijkt of een variable wel gedefinieerd is.
    private void checkVariableReference(VariableReference variableReference) {
        if (variableTypes.get(variableReference.name) == null) {
            variableReference.setError("Variable not declared.");
        }
    }

    // kijkt of er een variableAssignment of styleRule in de stylesheet staan.
    public void checkStylesheetExist(Stylesheet stylesheet) {
        for (var child : stylesheet.getChildren()) {
            if (child instanceof Stylerule) {
                checkStyleruleExist((Stylerule) child);
            }
        }
    }

    // kijkt of er een declaration of variableAssignment in de styleRule staat.
    private void checkStyleruleExist(Stylerule stylerule) {
        for (var child : stylerule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    // kijkt of declaration een expression heeft en, of de variable in scope staat.
    private void checkDeclaration(Declaration declaration) {
        if (checkExpression(declaration.expression) == null) {
            declaration.setError("Can not find variable in scope");
        }
        for (var child : declaration.getChildren()) {
            if (child instanceof Expression) {
                checkExpression((Expression) child);
            }
        }
    }

    //Kijkt of er een operation in de expression staat, zo niet kijkt hij meteen welk type de expression is.
    private ExpressionType checkExpression(Expression expression) {
        if (expression instanceof Operation) {
            return SplitOperation((Operation) expression);
        }
        return getExpressionType(expression);
    }

    // Deelt de operation volledig op.
    private ExpressionType SplitOperation(Operation operation) {
        ExpressionType left;
        ExpressionType right;

        if (operation.lhs instanceof Operation) {
            left = SplitOperation((Operation) operation.lhs);
        } else {
            left = getExpressionType(operation.lhs);
        }
        if (operation.rhs instanceof Operation) {
            right = SplitOperation((Operation) operation.rhs);
        } else {
            right = getExpressionType(operation.rhs);
        }
        return checkOperation(operation, left, right);
    }

    // Kijkt op de operation wel uitgevoerd mag worden.
    private ExpressionType checkOperation(Operation operation, ExpressionType left, ExpressionType right) {
        if (left == ExpressionType.COLOR || right == ExpressionType.COLOR || left == ExpressionType.BOOL || right == ExpressionType.BOOL) {
            operation.setError("These expressions are not allowed in operations.");
            return ExpressionType.UNDEFINED;
        }

        if (operation instanceof MultiplyOperation) {
            if (left != ExpressionType.SCALAR && right != ExpressionType.SCALAR) {
                operation.setError("Can not multiply without scalar literal.");
                return ExpressionType.UNDEFINED;
            }
            if (left == ExpressionType.SCALAR) {
                return right;
            }
            return left;

        } else if ((operation instanceof SubtractOperation || operation instanceof AddOperation) && right != left) {
            operation.setError("Can not add or subtract without the same literal.");
            return ExpressionType.UNDEFINED;
        }
        return left;
    }

    //Geeft het type van een expression terug
    public ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof VariableReference) {
            var varRef = (VariableReference) expression;
            return variableTypes.get(varRef.name);
        }
        if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else {
            return ExpressionType.UNDEFINED;
        }
    }

}
