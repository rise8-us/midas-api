grammar RestQuery;

input
   : query EOF
   ;

query
   : left=query logicalOp=(AND | OR) right=query #opQuery
   | LPAREN query RPAREN                         #priorityQuery
   | criteria                                    #atomQuery
   ;

criteria
   : key op value
   ;

key
   : IDENTIFIER
   ;

value
   : IDENTIFIER
   | STRING
   | ENCODED_STRING
   | NUMBER
   | BOOL
   | NULL
   ;

op
   : EQ
   | GT
   | GT_EQ
   | LT
   | LT_EQ
   | NOT_EQ
   | INCLUDES
   ;

BOOL
    : 'true'
    | 'false'
    ;
NULL
    : 'null'
    | 'NULL'
    ;

STRING
 : '"' DoubleStringCharacter* '"'
 | '\'' SingleStringCharacter* '\''
 ;

fragment DoubleStringCharacter
   : ~["\\\r\n]
   | '\\' EscapeSequence
   | LineContinuation
   ;
fragment SingleStringCharacter
    : ~['\\\r\n]
    | '\\' EscapeSequence
    | LineContinuation
    ;
fragment EscapeSequence
    : CharacterEscapeSequence
    | HexEscapeSequence
    | UnicodeEscapeSequence
    ;
fragment CharacterEscapeSequence
 : SingleEscapeCharacter
 | NonEscapeCharacter
 ;
fragment HexEscapeSequence
 : 'x' HexDigit HexDigit
 ;
 
fragment UnicodeEscapeSequence
 : 'u' HexDigit HexDigit HexDigit HexDigit
 ;
fragment SingleEscapeCharacter
 : ['"\\bfnrtv]
 ;

fragment NonEscapeCharacter
 : ~['"\\bfnrtv0-9xu\r\n]
 ;
fragment EscapeCharacter
 : SingleEscapeCharacter
 | DecimalDigit
 | [xu]
 ;
fragment LineContinuation
 : '\\' LineTerminatorSequence
 ;
fragment LineTerminatorSequence
 : '\r\n'
 | LineTerminator
 ;
fragment DecimalDigit
 : [0-9]
 ;
fragment HexDigit
 : [0-9a-fA-F]
 ;
fragment OctalDigit
 : [0-7]
 ;
AND
   : 'AND'
   ;
OR
   : 'OR'
   ;
NUMBER
   : ('0' .. '9') ('0' .. '9')* POINT? ('0' .. '9')*
   ;
LPAREN
   : '('
   ;
RPAREN
   : ')'
   ;
GT
   : '>'
   ;
GT_EQ
   : '>='
   ;
LT
   : '<'
   ;
LT_EQ
   : '<='
   ;
INCLUDES
   : '::'
   ;
EQ
   : ':'
   ;
NOT_EQ
   : '!'
   ;
fragment POINT
   : '.'
   ;
IDENTIFIER
   : [A-Za-z0-9.]+
   ;
ENCODED_STRING
   : ~([ :<>!()])+
   ;
LineTerminator
: [\r\n\u2028\u2029] -> channel(HIDDEN)
;
WS
   : [ \t\r\n]+ -> skip
   ;