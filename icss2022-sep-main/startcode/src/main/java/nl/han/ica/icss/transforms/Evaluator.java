package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator implements Transform {

    private final HashMap<String, Literal> variableValues;

    public Evaluator() {
        variableValues = new HashMap<String, Literal>();
    }

    @Override
    public void apply(AST ast) {
        evaluate(ast.root);
    }

    //Evaluate het de tree, Kijkt of het een variableAssignment is of een declaration
    private void evaluate(ASTNode parent) {
        var children = parent.getChildren();

        for (var child : children) {
            if (parent instanceof Declaration) {
                Declaration declaration = (Declaration) parent;
                if (child instanceof Operation) {
                    declaration.expression = getLiteral((Expression) child);
                }
                if (child instanceof VariableReference) {
                    VariableReference variableReference = (VariableReference) child;
                    if (variableValues.containsKey(variableReference.name)) {
                        ((Declaration) parent).expression = variableValues.get(variableReference.name);
                    }
                }
            }
            //Zet variableAssignment in een hashmap
            if (child instanceof VariableAssignment) {
                String name = ((VariableAssignment) child).name.name;
                Literal literal = getLiteral(((VariableAssignment) child).expression);
                variableValues.put(name, literal);
            }
            //kijkt of het een ifclause is
            if (child instanceof IfClause) {
                evaluateIfClause((IfClause) child, parent);
            }
            evaluate(child);
        }
    }

    //haalt de literal op, dit kan de literal, een operation of variable reference zijn.
    private Literal getLiteral(Expression expression) {
        if (expression instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) expression;
            if (variableValues.containsKey(variableReference.name)) {
                return variableValues.get(variableReference.name);
            }
        }
        if (expression instanceof Literal) {
            return (Literal) expression;
        }
        if (expression instanceof Operation) {
            return calculateOperations((Operation) expression);
        }
        return null;
    }

    //berekent de operation.
    private Literal calculateOperations(Operation operation) {
        Expression lhs = operation.lhs;
        Expression rhs = operation.rhs;

        //kijkt of er nog een operation staat in de operation.
        if (rhs instanceof Operation) {
            rhs = calculateOperations((Operation) rhs);
        }
        //kijkt of links of rechts een variable reference is.
        if (lhs instanceof VariableReference) {
            lhs = getLiteral(lhs);
        } else if (rhs instanceof VariableReference) {
            rhs = getLiteral(rhs);
        }

        if (lhs instanceof PercentageLiteral && rhs instanceof PercentageLiteral) {
            if (operation instanceof SubtractOperation) {
                return new PercentageLiteral(((PercentageLiteral) lhs).value - ((PercentageLiteral) rhs).value);
            } else if (operation instanceof AddOperation) {
                return new PercentageLiteral(((PercentageLiteral) lhs).value + ((PercentageLiteral) rhs).value);
            } else if (operation instanceof MultiplyOperation) {
                return new PercentageLiteral(((PercentageLiteral) lhs).value * ((PercentageLiteral) rhs).value);
            }
        } else if (lhs instanceof PixelLiteral && rhs instanceof PixelLiteral) {
            if (operation instanceof SubtractOperation) {
                return new PixelLiteral(((PixelLiteral) lhs).value - ((PixelLiteral) rhs).value);
            } else if (operation instanceof AddOperation) {
                return new PixelLiteral(((PixelLiteral) lhs).value + ((PixelLiteral) rhs).value);
            } else if (operation instanceof MultiplyOperation) {
                return new PixelLiteral(((PixelLiteral) lhs).value * ((PixelLiteral) rhs).value);
            }
        } else if (lhs instanceof ScalarLiteral && rhs instanceof PercentageLiteral) {
            if (operation instanceof SubtractOperation) {
                return new PercentageLiteral(((ScalarLiteral) lhs).value - ((PercentageLiteral) rhs).value);
            } else if (operation instanceof AddOperation) {
                return new PercentageLiteral(((ScalarLiteral) lhs).value + ((PercentageLiteral) rhs).value);
            } else if (operation instanceof MultiplyOperation) {
                return new PercentageLiteral(((ScalarLiteral) lhs).value * ((PercentageLiteral) rhs).value);
            }
        } else if (lhs instanceof ScalarLiteral && rhs instanceof PixelLiteral) {
            if (operation instanceof SubtractOperation) {
                return new PixelLiteral(((ScalarLiteral) lhs).value - ((PixelLiteral) rhs).value);
            } else if (operation instanceof AddOperation) {
                return new PixelLiteral(((ScalarLiteral) lhs).value + ((PixelLiteral) rhs).value);
            } else if (operation instanceof MultiplyOperation) {
                return new PixelLiteral(((ScalarLiteral) lhs).value * ((PixelLiteral) rhs).value);
            }
        }
        return null;
    }

    //Haalt de ifclause weg
    private List<ASTNode> evaluateIfClause(IfClause ifClause, ASTNode parent) {
        ArrayList<ASTNode> values = new ArrayList<>();
        Literal literal = getLiteral(ifClause.conditionalExpression);

        if (literal != null) {
            boolean bool = ((BoolLiteral) literal).value;
            ArrayList<ASTNode> body = ifClause.body;
            ElseClause elseClause = ifClause.elseClause;
            for (ASTNode node : body) {
                if (node instanceof IfClause) {
                    values.addAll(evaluateIfClause((IfClause) node, ifClause));
                } else if (bool) {
                    values.add(node);
                } else {
                    values.addAll(elseClause.body);
                }
            }
        }
        parent.removeChild(ifClause);
        if (!(parent instanceof IfClause)) {
            for (ASTNode node : values) {
                parent.addChild(node);
            }
        }
        return values;
    }
}

