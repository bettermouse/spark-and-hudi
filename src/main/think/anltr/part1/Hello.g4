grammar Hello;            // Define a grammar called Hello,与文件名同名
r  : x;
x :d;
d:'hello' ID  #sex
    | ID     #SEX2   // match keyword hello followed by an identifier,规则(语法)小写
    ;
ID : [a-z]+ ;             // match lower-case identifiers 词法大写
DABIAN : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines, \r (Windows)