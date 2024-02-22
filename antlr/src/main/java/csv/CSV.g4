grammar CSV;

//与row+  row row+ 对比
file : hdr row+ ;
hdr : row ;

row : field (',' field)* '\r'? '\n' ;  //如果有分隔符,这种处理是最好的了
//为什么可以有,, 看最后面一行,细节
field
    :   TEXT
    |   STRING
    |
    ;
//必须要有内容
TEXT : ~[,\n\r"]+ ;
STRING : '"' ('""'|~'"')* '"' ; // quote-quote is an escaped quote
