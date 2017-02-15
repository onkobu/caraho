grammar MetaStructure;

// Reihenfolge ist wichtig, tokens werden mit Priorität ausgewählt und
// damit shortText bspw. kein NAME wird, muss es zuerst gefunden werden/
// ganz oben stehen.
WS : [ \r\t\n]+ -> skip ;
TYPE: 'shortText' | 'number' | 'dateTime' | 'boolean' | 'currency';
AGGREGATE_OPERATOR: 'sum' | 'average';
SCALAR_OPERATOR: 'add' | 'sub' | 'multiply';
INT : [0-9]+ ;
LETTER: [a-zA-Z];
NAME: LETTER [a-zA-Z]*;
REFERENCE: NAME('.'NAME)*;

document: context abstractDef* classDef+;

context: 'context' NAME ;

abstractDef: 'Template' NAME 'has attributes:' attributeList '.';

classDef: 'A' NAME interfaceAlias? 'has attributes:' attributeList ('and functions:' functionList)? '.' belongsTo*;

interfaceAlias: 'is a' NAME;

functionList: function (',' function)*;

function: aggregateFunction | scalarFunction;

aggregateFunction: NAME 'as' AGGREGATE_OPERATOR '(' REFERENCE ')';

scalarFunction: NAME 'as' SCALAR_OPERATOR '(' NAME (',' NAME)* ')';

attributeList: attributeOrSet (',' attributeOrSet)*;

attributeOrSet: attribute | set;

attribute: NAME 'as' TYPE;

set: 'set of' NAME;

belongsTo: 'belongs to ' NAME;