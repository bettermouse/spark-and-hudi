#### 创建表
createTable->colTypeList->colType

colType
    : colName=errorCapturingIdentifier dataType (NOT NULL)? commentSpec?
    ;
    
#### 表名称