grammar ICSS;
//--- LEXER: ---
// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';


//--- PARSER: ---
stylesheet: variableAssignment * stylerule  * EOF;

stylerule: selector OPEN_BRACE (declaration | ifClause | variableAssignment)* CLOSE_BRACE ;
declaration: propertyName COLON expresion SEMICOLON ;
variableAssignment: variableReference ASSIGNMENT_OPERATOR (expresion | boolLiteral) SEMICOLON;
propertyName: LOWER_IDENT;

tagSelector: LOWER_IDENT;
idSelector :ID_IDENT | COLOR;
classSelector: CLASS_IDENT;
selector: tagSelector | idSelector | classSelector;

expresion: expresion MUL expresion | expresion (PLUS | MIN) expresion | literals;

variableReference: CAPITAL_IDENT;
colorLiteral:COLOR;
pixelLiteral: PIXELSIZE;
boolLiteral: TRUE | FALSE ;
scalarLiteral: SCALAR;
percentageLiteral: PERCENTAGE;
literals: variableReference | colorLiteral | pixelLiteral | boolLiteral | scalarLiteral | percentageLiteral;

ifClause: IF BOX_BRACKET_OPEN (variableReference | boolLiteral | ) BOX_BRACKET_CLOSE OPEN_BRACE (declaration | ifClause | variableAssignment)* CLOSE_BRACE elseClause*;
elseClause: ELSE OPEN_BRACE (declaration | ifClause | variableAssignment)* CLOSE_BRACE;
