grammar CSV;

file : hdr row+ ;
hdr : row ;

row : field (',' field)* '\r'? '\n' ;
//为什么传入 ,, 这里也能正确的展示
field
    :   TEXT
    |   STRING
    |
    ;

TEXT : ~[,\n\r"]+ ;
STRING : '"' ('""'|~'"')* '"' ; // quote-quote is an escaped quote
