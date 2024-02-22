package rewrite.ReplaceAnnotation;

import org.antlr.v4.runtime.*;
import rewrite.JavaLexer;

import java.util.List;

import static rewrite.ReplaceAnnotation.SqlBaseParser.SIMPLE_COMMENT;

public class Test {
    public static void main(String[] args) {
        //如果有分号,line 2:0 mismatched input 'SELECT' expecting {<EOF>, ';'}
        String sql = "SELECT * FROM AA --OK;\n" +
                "SELECT * FROM CC --OK;\n";
        ANTLRInputStream input = new ANTLRInputStream(sql);
        // get rewrite token
        SqlBaseLexer lexer = new SqlBaseLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokens);
        parser.singleStatement();
        TokenStreamRewriter tokenStreamRewriter = new TokenStreamRewriter(tokens);


        // 获取所有token
        ANTLRInputStream inputNew = new ANTLRInputStream(sql);
        SqlBaseLexer lexerNew = new SqlBaseLexer(inputNew);
        List<CommonToken> allTokens = (List<CommonToken>) lexerNew.getAllTokens();
        for(int i =0;i<allTokens.size();i++){
            CommonToken commonToken = allTokens.get(i);
            if(commonToken.getType()==SIMPLE_COMMENT){
                String text = commonToken.getText();
                //获取前面的多个-
                int start =0;
                for(int index =0;index<text.length();index++){
                    if(text.charAt(index)=='-'|| text.charAt(index)==' '){
                        start++;
                    }else {
                        break;
                    }
                }
                //后面的\r\n
                text= text.substring(start);
                text= text.replace("\r","");
                text= text.replace("\n","");
                text= text.replace("\r\n","");
                tokenStreamRewriter.replace(i,"/*"+text+"*/"+"\r\n");
                System.out.println(commonToken);
            }
        }
        System.out.println(sql);
        System.out.println(tokenStreamRewriter.getText());

    }
}
