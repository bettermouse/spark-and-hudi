package antlr.listener;

import antlr.generate.SqlBaseBaseListener;
import antlr.generate.SqlBaseParser;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 *  ID   DATE               PERSON             REASON
 *  1   2022/3/18 16:25     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public class Create2SelectWithReg extends SqlBaseBaseListener {
    public String table ="";
    public List<SqlBaseParser.ColTypeContext> fields;
    public List<SqlBaseParser.ColTypeContext> partitionFields = new ArrayList<SqlBaseParser.ColTypeContext>();

    @Override
    public void enterCreateTableHeader(SqlBaseParser.CreateTableHeaderContext ctx) {
        table = ctx.multipartIdentifier().getText();
        super.enterCreateTableHeader(ctx);
    }


    @Override
    public void enterColTypeList(SqlBaseParser.ColTypeListContext ctx) {
        fields = ctx.colType();
        super.enterColTypeList(ctx);
    }

    @Override
    public void enterPartitionColumn(SqlBaseParser.PartitionColumnContext ctx) {
        partitionFields.add(ctx.colType());
        super.enterPartitionColumn(ctx);
    }

    public static String getRegexpField(SqlBaseParser.ColTypeContext colType){
        String selectField="";
        String colName = colType.colName.identifier().getText();
        String dataType = colType.dataType().getText();
        String text = colType.commentSpec().STRING().getText();
        if(text !=null){
          //  colName=text;
        }
        if(dataType.equals("string")){
            selectField=" regexp_replace("+colName+",'\\n|\\t|\\r|\\,|\"','') as "+ text +" ";
        }else{
            selectField=colName ;
        }
        return selectField;
    }

    public String getSelectRegexp(){
        StringBuilder sb = new StringBuilder("select ");
        List<SqlBaseParser.ColTypeContext> allFields =new ArrayList<SqlBaseParser.ColTypeContext>();
        allFields.addAll(partitionFields);
        allFields.addAll(fields);
        int size = allFields.size();
        for(int i= 0;i<size;i++){
            SqlBaseParser.ColTypeContext colTypeContext = allFields.get(i);
            if(i>0){
                sb.append(","+getRegexpField(colTypeContext));
            }else {
                sb.append(getRegexpField(colTypeContext));
            }
        }

        sb.append("from ").append(table);
        return sb.toString();
    }
}
