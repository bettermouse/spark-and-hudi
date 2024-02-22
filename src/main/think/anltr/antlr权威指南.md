### 引言
 参考龙书,编写简单编译器,从头实现一个编译器前端的难度远远超出了一个开发者的能力.编写编译器所需要的理论基础.    
&emsp;&emsp;antlr能使这个过程易如反掌.antlr能够根据用户定义的语法文件自动生成词法分析器和语法分析器,并将输入文本处理为(可视化的)语法分析树.   
&emsp;&emsp;本书没有冗长的理论,从一些具体需求出发,由浅入深地介绍了语言的背景知识,antlr语法的设计方法及基于antlr4实现语言识别程序的详细步骤.  
&emsp;&emsp;hive中用了antlr,hibernate的hql用了antlr.除了这些鼎鼎大名的项目之外,还可以利用antlr构建各种各样的实用工具,如配置文件读取器,遗留代码转换器等   
&emsp;&emsp;一门语言的正式描述称为语法,antlr能够为该语言生成一个语法分析器,并自动建立语法分析树--一种描述语法与输入文本匹配关系的数据结构.antlr也能够自动生成树的遍历器,这样你就可以访问树中的节点,执行自定义的业务逻辑代码.无论多复杂的语法,只要你提供给antlr自动生成语法分析器的输入是合法的,该语法分析器就能够自动识别.当然,你需要自行保证该语法能够准确地描述目标语言.
* 第一部分介绍了antlr,提供了一些与语言相关的背景知识,并展示了antlr一些简单的应用.
+ 第二部分关于设计语法和使用语法来构建语言类应用程序的百科全书.
- 第三部分展示了自定义antlr生成的语法分析器的错误处理机制的方法.

在这种看似矛盾的情形背后存在着一条鸿沟：
大量从事传统行业的人员拥有在本行业中无与伦比的业务知识和经验，
却苦于跟不上现代软件发展的脚步。解决这个问题的根本方法就是DSL（Domain Specific Language）
让传统行业的人员能够用严谨的方式与计算机对话。其实，本质上任何编程语言都是一种DSL，殊途同归。
## 第一部分 antlr和计算机语言简介
### 第一章 初识antlr
#### 1.1
* antlr包含了运行antlr的工具和编译,执行antlr产生的识别程序所依赖的全部运行库.
* antlr工具将语法文件转换成可以识别该语法文件所描述的语言程序.
* 一个复杂的树形结构生成库和StringTemplate,用于生成代码和其它结构化文本的优秀的模板引擎.
* 通过idea,安装antlr插件即可(百度)
#### 1.2
``` antlr
grammar Hello;            // Define a grammar called Hello,与文件名同名
r  : 'hello' ID ;         // match keyword hello followed by an identifier,规则(语法)小写
ID : [a-z]+ ;             // match lower-case identifiers 词法大写
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines, \r (Windows)
```
通过antlr,生成语法生成器,词法生成器.

一条语法规则生成一个visit 方法

如果一个语法规则被拆成了多条,直接生成对应的多条方法
rule d: must label all alternatives or none,一量加别名,所有的方法都要加别名


![image](http://47.75.83.77:8080//markdown/antlr/1.jpg)
![image](http://47.75.83.77:8080//markdown/antlr/2.png)
![image](http://47.75.83.77:8080//markdown/antlr/3.png)
再加上一个main函数就可以运行了(这里没有讲).
* 包含一个TestBig的调试工具.查看可视化的语法分析树,如下.
![image](http://47.75.83.77:8080//markdown/antlr/4.png)
### 第二章 纵观全局
- 学习语言类应用程序相关的重要过程,术语和数据结构.
#### 2.1 从antlr元语言开始
- 语言(language)由一系列有意义的语句组成,语句(sentence)由词组组成,词组(phrase)是由更小的子词组(subphrase)和词汇符号(vocabulary symbol)组成.
- 如果一个程序能够分析计算或者执行语句,我们就称之为解释器,如计算器,python解释器.
- 如果一个程序能够将一门语言转换为另外一门语言的语句,我们称之为翻译器(translator),例如java到c#的转换器和普通的编译器.
- 为了达到预期的目的,解释器或者翻译器需要识别出一门特定语言的所有的有意义的语句,词组和子词组,识别一个词组意味着我们可以将它从众多的组成部分中辨认区分出来.
- 识别语言的程序称为语法分析器(parser)或者句法分析器(syntax analyzer).句法(syntax)是指约束语言中的各个组成部分之间关系的规则.在本书中,我们会通过antlr语法来指定语言的句法.语法是一系列规则的集合,每条规则表述出一种词汇结构,antlr工具能够将其转换为如同经验丰富的开发者的手工构建一般的语法分析器(antlr是一个能够生成其它程序的程序),antlr语法本身又遵循了一种专门用来描述其他语法的语法,我们称之为antlr元语言(antlr 元数据)
- 语法分析的过程分解成两个过程.在我们大脑阅读英文文本的过程中,一:将一个个字符看成单词,二理解句子的意思.
- 将字符聚集为单词或者符号(token)的过程称为词法分析(lexical analysis)或者词法符号化(tokenizing),我们把可以将输入文本转换为词法符号的程序称为词法分析器(lexer),词法分析器可以将相关的词法符号归类,如Int,Id,Float等,语法分析器不关心单个符号,而仅关心符号的类型,词法分析器就需要将词汇符号归类.词法符号包含至少两部分信息:词法符号类型(从而能通过类型来识别词法结构)和该词法符号对应的文本.
- 第二个阶段是实际的语法分析过程,在这个过程中,输入的词法符号被消费以识别语句结构.antlr生成的语法分析树会建造一棵分析树(parse tree)或句法树(syntax tree)的结构
![image](http://47.75.83.77:8080//markdown/antlr/5.png)
- 词法分析树的内部节点是词组名,这些名称用于识别它们的子节点,并将子节点归类.
- 词法分析树的叶子节点永远是输入的词法符号.
- 由于我们使用一系列的规则指定语句的词汇结构,语法分析树的子树的根结点就对应于语法规则的名字.
#### 2.2 实现一个语法分析器
- antlr工具依据类似于我们之前看到的assign的语法规则,产生一个递归下降的语法分析器,递归下降的语法分析器实际上是若干递归方法的集合,每个方法对应一条规则,下降的过程就是从语法分析器的根节点开始,朝着叶节点(词法符号)进行解析的过程.
- 下面是一个antlr根据assign规则生成的方法,用于展示递归下降的语法分析器的实现细节.
![image](http://47.75.83.77:8080//markdown/antlr/6.png)
#### 2.3 你再也不能往核反应堆多加水了
- 语言是有歧义性的.antlr解决歧义问题的方法是:选择所有匹配的备选分支中的第一条.
词法分析器会匹配可能的最长字符串来生成一个词法符号
我们将在5.4节中学习如何隐式地指定表达式中的运算符优先级。
在词法分析器中，ANTLR解决歧义问题的方法是：匹配在语法定义中最靠前的那条词法规则。我们通过编程语言中常见的一种歧义——关键字和标识符规则的冲突——来说明这套机制是如何工作的
#### 2.4 使用语法分析树来构建语言类应用程序
- 为了编写一个语言类应用程序,需要对每个输入的词组或进子词组进行一些适当的操作.进行这项工作最简单的方法是操作语法分析器自动生成语法分析树.
- 认识一下antlr在识别和建立语法分析树的过程中使用的数据结构和类名,熟悉这些数据结构将为我们未来的讨论奠定基础.CharStream,Lexer(词法分析程序),Token,Parser,以及ParsTree.下图.
![image](http://47.75.83.77:8080//markdown/antlr/7.png)
- ParseTree的子类RuleNode和TerminalNode二者分别是子树的根节点和叶子节点.RudeNode并不是确定不变的,为了更好的支持对特定节点的元素的访问,antlr会为每条规则生成一个RudeNode的子类,在我们的例子中,子树根节点的类型实际上是StaContext,AssignContext,ExprContext.
![image](http://47.75.83.77:8080//markdown/antlr/8.png)
-因为这些根节点包含了使用规则识别词组中的全部信息,它们被称为上下文对象,每个上下文对象都知道自己识别出的词组中,开始和结束位置处的词法符号,同时提供访问该词组全部元素的途径,例如,AssignContext类提供了Id()方法,expr()方法来访问标识符节点和代表表达式的子树.
#### 2.5 语法分析树监听器和访问器.
- antlr的运行库提供了两种遍历树的机制.默认情况下,antlr使用内建的遍历器访问生成的语法分析树.
- 并为每个遍历时可能触发的事件生成一个语法分析树监听接口(parse-tree listener interface),监听器非常类似于xml解析器生成sax文档对象,sax监听器接收类似startDocument()和endDocument()事件通知.一个监听器的方法实际上就是回调函数.除了监听器方式,还有一种访问者模式.
1. 语法分析树监听器
- antlr提供了ParseTree-Walker类,可以实现ParseTreeListener接口,实现自己的逻辑代码.
- Antlr为每个语法文件生成一个ParserTreeListener子类,在该类中,语法中的每条规则都有对应的enter方法和exit方法.ParseTreeWaler对语法分析树是深度优先遍历.
- 监听器机制的优秀之处在于,这一切都是自动进行的.我们不需要编写对语法分析树的遍历,也不需要让我们的监听器显式地访问子节点.
2. 语法分析树访问器
- 有时候,我们希望控制遍历语法分析树的过程,通过显式的方法调用来访问子节点.在命令中加入-visitor选项可以指示antlr为一个语法生成访问器接口(visitor interface),语法中的每条规则对应接口中的一个visit方法.
``` java
ParseTree tree = ;
MyVisitor v = new MyVisitor();
v.visit(tree);
```
##### 与语法分析相关的术语
- 语言 一门语言是一个有效语句的集合.语句由词组组成,词组由子词组组成.
- 语法 语法定义了语言的语义规则.语法中每条规则定义了一种词组结构.
- 语义树或语法分析树 代表语句的结构,其中每个子树的根结点都使用一个抽象的名字给其包含的元素命名.即子树的根节点对应了语法规则的名字.树的叶子节点是语句中的符号或者词法符号.
- 词法分析器或者词法符号生成器 将输入的字符序列分解成一系列的词法符号.一个词法分析器负责分析词法.
- 语法分析器 语法分析器通过检查语句中的结构是否符合语法规则的定义来验证该语句在特定语言中是否合法.
- 递归下降的语法分析器
- 向前预测 
- ### 第3章 入门的antlr项目
- 构造语法,识别{1,2,3}和{1,{2,3},4},这种结构可以作为int数组或者c语言中的结构体的初始化语法.工具的工作,如果初始化字节数组中所有的整数都能用一个字节表示,那么将该整数转换为字节数组.
- 通过个个项目.你将会学到
1. 一些antlr语法的语义元素定义
2. antlr根据语法自动生成代码机制
3. 如何将自动生成的语法分析器和java程序集成,以及如何使用语法分析树监听器编写一个代码翻译工具.
#### 3.1 antlr工具,运行库以及自动生成的代码
- antlr的jar包中包含两个关键部分:antlr工具和antlr运行库.当说到"对一个语法运行ANTLR"时,我们指的是运行antlr工具生成一些代码(语法分析器,词法分析器),它们能够识别使用这份语法代表的语言所写成的语句.词法分析器将输入的字符流分解为词法符号序列,然后将它们传递给能够进行语法检查的语法分析器.运行库是一个由若干类和方法组成的库.这些类和方法是自动生成的代码(Parser)运行所必须的.因些我们完成工作的一般步骤是:首先我们对一个语法运行antlr,然后将生成的代码与jar包中运行库一起编译,最后净编译好的代码和运行库放在一起运行.
1. 创建能够描述语言的语法.
``` antlr
/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: ArrayInit.g4
 */
grammar ArrayInit;

/** A rule called init that matches comma-separated values between {...}. */
init  : '{' value (',' value)* '}' ;  // must match at least one value

/** A value can be either a nested array/struct or a simple integer (INT) */
value : init
      | INT
      ;

// parser rules start with lowercase letters, lexer rules with uppercase
INT :   [0-9]+ ;             // Define token INT as one or more digits
WS  :   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
```
2. 生成文件    
   1. ArrayInitParser 语法分析器,每条规则都有对应的方法
   2. ArrayInitLexer 词法分析器,antlr能够自动识别出我们语法中的文法规则和词法规则.
   3. ArrayInit.tokens antlr会给每个我们定义的词法符号指定一个数字形式的类型.
   4. ArrayInitBaseListener,ArrayInitListener 接口和默认空实现.
#### antlr语法比正则表达式更强大
#### 3.2 测试生成的语法分析器
![image](http://47.75.83.77:8080//markdown/antlr/9.png)
#### 3.3 将生成的语法分析器与java程序集成.
- 整合进入main函数
``` java
public class Test {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
	//新建一个charStream,从标准输入读取数据
        ANTLRInputStream input = new ANTLRInputStream("{1,{2,3},4}");

        // create a lexer that feeds off of input CharStream
        ArrayInitLexer lexer = new ArrayInitLexer(input);

	//新建一个词法符号缓冲区,用于存储词法分析器
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        ArrayInitParser parser = new ArrayInitParser(tokens);

        ParseTree tree = parser.init(); // begin parsing at init rule
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    }
}

#### 3.4 构建一个语言类应用程序
``` java
//监听器 
public class ShortToUnicodeString extends ArrayInitBaseListener {
    /**
     * Translate { to "
     */
    @Override
    public void enterInit(ArrayInitParser.InitContext ctx) {
        System.out.print('"');
    }

    /**
     * Translate } to "
     */
    @Override
    public void exitInit(ArrayInitParser.InitContext ctx) {
        System.out.print('"');
    }

    /**
     * Translate integers to 4-digit hexadecimal strings prefixed with \\u
     */
    @Override
    public void enterValue(ArrayInitParser.ValueContext ctx) {
        // Assumes no nested array initializers
        int value = Integer.valueOf(ctx.INT().getText());
        System.out.printf("\\u%04x", value);
    }
}
```
``` java 
public class Translate {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream("{1,2,3}");
        // create a lexer that feeds off of input CharStream
        ArrayInitLexer lexer = new ArrayInitLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        ArrayInitParser parser = new ArrayInitParser(tokens);
        ParseTree tree = parser.init(); // begin parsing at init rule

        // Create a generic parse tree walker that can trigger callbacks
        ParseTreeWalker walker = new ParseTreeWalker();
        // Walk the tree created during the parse, trigger callbacks
        walker.walk(new ShortToUnicodeString(), tree);
        System.out.println(); // print a \n after translation
    }
}

```
### 第4章 快速指南 
#### 4.1 匹配算术表大家式的语言
- 第一个语法构建一个简单的计算器,其对算术表达式的处理具有十分重要的意义.因为它们太常见了.为简单起见,我们只允许+-*/,(),整数及变量的出现.
``` antlr
grammar Expr;

/** The start rule; begin parsing here. */
prog:   stat+ ; 

stat:   expr NEWLINE                
    |   ID '=' expr NEWLINE        
    |   NEWLINE                   
    ;

expr:   expr ('*'|'/') expr   
    |   expr ('+'|'-') expr   
    |   INT                    
    |   ID                    
    |   '(' expr ')'         
    ;

ID  :   [a-zA-Z]+ ;      // match identifiers <label id="code.tour.expr.3"/>
INT :   [0-9]+ ;         // match integers
NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace
```
- 我们使用|来分隔同一个语言规则的若干备选分支,使用()把一些符号组成子规则.
- 词法符号定义中的标记和正则表达式的元字符非常相似.
``` java 
public class ExprJoyRide {
    public static void main(String[] args) throws Exception {
        String inputFile = null; 
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        ANTLRInputStream input = new ANTLRInputStream(is);
        ExprLexer lexer = new ExprLexer(input); 
        CommonTokenStream tokens = new CommonTokenStream(lexer); 
        ExprParser parser = new ExprParser(tokens); 
        ParseTree tree = parser.prog(); // parse; start at prog <label id="code.tour.main.6"/>
        System.out.println(tree.toStringTree(parser)); // print tree as text <label id="code.tour.main.7"/>
    }
}
```
#### 如何将大型的语法维持在可控范围内
1. 语法导入: 将语法拆分成逻辑单元
``` antlr
lexer grammar CommonLexerRules; // note "lexer grammar"

ID  :   [a-zA-Z]+ ;      // match identifiers
INT :   [0-9]+ ;         // match integers
NEWLINE:'\r'? '\n' ;     // return newlines to parser (end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace
```
``` antlr
grammar LibExpr;         // Rename to distinguish from original
import CommonLexerRules; // includes all rules from CommonLexerRules.g4
/** The start rule; begin parsing here. */
prog:   stat+ ; 

stat:   expr NEWLINE                
    |   ID '=' expr NEWLINE        
    |   NEWLINE                   
    ;

expr:   expr ('*'|'/') expr   
    |   expr ('+'|'-') expr  
    |   INT                    
    |   ID                    
    |   '(' expr ')'    
    ;
```
2. 处理有错误的输入
#### 4.2 利用访问器构建一个计算器
- 使用访问者模式,来实现计算器
- 在开始之前,我们需要对语法做少量修改,首先,我们需要给备选分支加上标签(标签可以是任意标识符,只要不与规则名冲突),如果备选分支上没有标签,antlr就只为每条规则生成一个方法,在本例中,我们希望每个备选分支都有不同的访问器方法.在本例中,我们希望每个备选分支都有不同的访问器方法,这样我们就可以对每种输入都获得一个不同的事件,在我们新的语法LabledExpr中,标签以#开头,放置在一个备选分支的右侧.
- 为运算符这样词法符号定义一些名字,这样,在随后的访问器中,我们就可以将这些词法符号的名字当做java常量来引用.
``` java 
grammar LabeledExpr; // rename to distinguish from Expr.g4

prog:   stat+ ;

stat:   expr NEWLINE                # printExpr
    |   ID '=' expr NEWLINE         # assign
    |   NEWLINE                     # blank
    ;

expr:   expr op=('*'|'/') expr      # MulDiv
    |   expr op=('+'|'-') expr      # AddSub
    |   INT                         # int
    |   ID                          # id
    |   '(' expr ')'                # parens
    ;

MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;
ID  :   [a-zA-Z]+ ;      // match identifiers
INT :   [0-9]+ ;         // match integers
NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace
```
- 在新程序中,我们创建的词法分析器对象和语法分析器对象是基于语法LabeledExpr的,我们实例化了一个自定义的访问器EvalVistor,我们调用visit()方法,开始遍历prog()方法返回的语法分析树.
- 首先,antlr自动生成了一个访问器接口,并为其中每个带标签的备选分支生成了一个方法.
``` java
package chapter4.example2;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link LabeledExprVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class LabeledExprBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements LabeledExprVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitProg(LabeledExprParser.ProgContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitPrintExpr(LabeledExprParser.PrintExprContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitAssign(LabeledExprParser.AssignContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitBlank(LabeledExprParser.BlankContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitParens(LabeledExprParser.ParensContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitMulDiv(LabeledExprParser.MulDivContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitAddSub(LabeledExprParser.AddSubContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitId(LabeledExprParser.IdContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitInt(LabeledExprParser.IntContext ctx) { return visitChildren(ctx); }
}
```
- 该接口使用了java的泛型定义,参数化的类型是visit方法的返回值类型.考虑我们的实际情况,表达式计算的结果都是整数.
``` java
public class EvalVisitor extends LabeledExprBaseVisitor<Integer> {
    /** "memory" for our calculator; variable/value pairs go here */
    Map<String, Integer> memory = new HashMap<String, Integer>();

    /** ID '=' expr NEWLINE */
    @Override
    public Integer visitAssign(LabeledExprParser.AssignContext ctx) {
        String id = ctx.ID().getText();  // id is left-hand side of '='
        int value = visit(ctx.expr());   // compute value of expression on right
        memory.put(id, value);           // store it in our memory
        return value;
    }

    /** expr NEWLINE */
    @Override
    public Integer visitPrintExpr(LabeledExprParser.PrintExprContext ctx) {
        Integer value = visit(ctx.expr()); // evaluate the expr child
        System.out.println(value);         // print the result
        return 0;                          // return dummy value
    }

    /** INT */
    @Override
    public Integer visitInt(LabeledExprParser.IntContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    /** ID */
    @Override
    public Integer visitId(LabeledExprParser.IdContext ctx) {
        String id = ctx.ID().getText();
        if ( memory.containsKey(id) ) return memory.get(id);
        return 0;
    }

    /** expr op=('*'|'/') expr */
    @Override
    public Integer visitMulDiv(LabeledExprParser.MulDivContext ctx) {
        int left = visit(ctx.expr(0));  // get value of left subexpression
        int right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == LabeledExprParser.MUL ) return left * right;
        return left / right; // must be DIV
    }

    /** expr op=('+'|'-') expr */
    @Override
    public Integer visitAddSub(LabeledExprParser.AddSubContext ctx) {
        int left = visit(ctx.expr(0));  // get value of left subexpression
        int right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == LabeledExprParser.ADD ) return left + right;
        return left - right; // must be SUB
    }

    /** '(' expr ')' */
    @Override
    public Integer visitParens(LabeledExprParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }
}
```
``` java
public class Calc {
    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        ANTLRInputStream input = new ANTLRInputStream(is);
        LabeledExprLexer lexer = new LabeledExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LabeledExprParser parser = new LabeledExprParser(tokens);
        ParseTree tree = parser.prog(); // parse

        EvalVisitor eval = new EvalVisitor();
        eval.visit(tree);
    }
}
```
- 上述计算器构建过程透露出一个信息.我们不需要像antlr3那样,在语法文件中插入java代码编写的动作(action).语法文件独立于程序,具有编程语言中立性.访问器机制也使得一切语言识别之外的工作在我们所熟悉的Java领域进行.在生成的所需的语法分析器之后,就不需要同antlr语法标记打交道了.
#### 4.3 利用监听器构建一个翻译程序
- 编写一个工具,用来将一个java类中的全部方法抽取出来.同时保留方法签名中的空白字符和注释,我们已经别无选择,我们必须解析java源代码了.如下.
``` java
public class Demo {
	void f(int x, String y) { }
	int[ ] g(/*no args*/) { return null; }
	List<Map<String, Integer>>[] h() { return null; }
}
interface IDemo {
	void f(int x, String y);
	int[ ] g(/*no args*/);
	List<Map<String, Integer>>[] h();
}

```
- 信不信由你,我们能够用大约15行代码解决这个问题.这些代码是通过监听java语法分析树遍历器触发的事件.java语法分析树是由解析java语言的语法分析器生成的.本书包含了java语言的antlr语法,我们将会从类定义中提取类名,用它来命名生成的接口,然后从类的方法定义中获取方法签名.(返回值,方法名,以及参数列表).在本例中,我们需要通过覆盖对应的方法.对三个事件作出响应:遍历器进入和离开类定义时,以及遍历器遇难到方法定义时.
![image](http://47.75.83.77:8080//markdown/antlr/10.png)
- 访问器机制和监听器机制的最大的区别在于,监听器的方法会被antlr提供的遍历器对象自动调用.而在访问器方法中,必须显式调用visit方法来访问子节点.忘记调用visit()后果就是对应的子树将不会被访问.
``` antlr
classDeclaration
    :   'class' Identifier typeParameters? ('extends' type)?
        ('implements' typeList)?
        classBody
    ;
methodDeclaration
    :   type Identifier formalParameters ('[' ']')* methodDeclarationRest
    |   'void' Identifier formalParameters methodDeclarationRest
    ;
```
- 我们无需实现全部接口中的200个方法.
- 我们的基本思想是,在类定义的起始位置打印出接口定义,然后在类定义的结束位置打印出}.遇到每个方法定义时,我们将会抽取出它的签名.
- maint程序
``` java 
public class ExtractInterfaceTool {
    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);

        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit(); // parse

        ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
        ExtractInterfaceListener extractor = new ExtractInterfaceListener(parser);
        walker.walk(extractor, tree); // initiate walk of tree with listener
    }
}
```
- 我们实现这个接口提取器功能并不完整.因为它没有为接口定义添加原有类中的import语句.生成的接口可能引用了这些import语句所对应的类型.如List.使用监听器机制来构建这种提取器或者翻译器是如此的容易,我不甚至不需要知道importDeclarationf规则长什么样子,因为在enterImportDeclaration()方法中,只需要简单地打印出整条规则匹配的文本即可.
``` java
    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        System.out.println(parser.getTokenStream().getText(ctx));
    }
```
- 尽管访问器和监听器机制表现出色,它们使语法分析过程和程序本身调度分离.有些时候,我们还是需要额外的灵活性和可操控性.
#### 4.4 定制语法分析过程 
- 为了极佳的灵活性和可操控性.我们可以直接将代码片段(动作)嵌入语法中.这些动作将被拷贝到antlr自动生成的递归下降语法分析器的代码中.本节中,我们将实现一个简单的程序,读取若干行数据,然后将指定列的值打印出来.之后,我们将会看到如何实现特殊的动作,叫作语义判定.它能够动态地开户或者关闭部分语法.
1. 在语法中嵌入任意动作
``` text
parrt	Terence Parr	101
tombu	Tom Burns	020
bke	Kevin Edgar	008
```
- 列之间用tab符分隔的,每行以一个换行符结尾.匹配这样的输入文件的语法非常简单.
``` java
file: (row NL)+ ;
```
- 当我们加入动作时,上述语法就会变得混乱.我们需要在其中创建一个构造器,这样我们就能传入希望提取的列号(从1开始计数);另外,我们需要在row规则的(...)+循环中放置一些动作.
``` java
grammar Rows;

@parser::members { // add members to generated RowsParser 在生成的rowsParser中添加一些成员
    int col;
    public RowsParser(TokenStream input, int col) { // custom constructor 自定义构造器
        this(input);
        this.col = col;
    }
}

file: (row NL)+ ;

row
locals [int i=0]
    : (   STUFF
          {
          $i++;
          if ( $i == col ) System.out.println($STUFF.text);
          }
      )+
    ;

TAB  :  '\t' -> skip ;   // match but don't pass to the parser
NL   :  '\r'? '\n' ;     // match and pass to the parser
STUFF:  ~[\t\r\n]+ ;     // match any chars except tab, newline
```
- stuff词法规则匹配除tab符和换行符之外的任何字符.这意味着数据中可以包含空格.
- main函数与其它程序不同之处在于,我们给语法分析器的自定义构造器传递了一个列号,并且告诉语法分析树不必建立语法分析树.
``` java
public class Col {
    public static void main(String[] args) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        RowsLexer lexer = new RowsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        int col = Integer.valueOf("1");
        RowsParser parser = new RowsParser(tokens, col); // pass column number!
        parser.setBuildParseTree(false); // don't waste time bulding a tree
        parser.file(); // parse
    }
}

```
- 其中的很多细节我们将在第10章中深入探究.现在看来,动作就是花括号包围的一些代码片段,member动作可以将代码注入到生成的语法分析器类中.使之成为该类的成员.在row规则中的动作访问了$i,它是一个使用locals子句定义的局部变量.row规则也使用了$STUFF.text来获得刚则匹配的STUFF词法符号中包含的文本.
- 这些动作在语法分析器的匹配过程中提取并打印相关的值,但是并不改变语法分析过程本身.除此之外,动作还能改变语法分析器识别输入文本的过程.
2. 使用主义判定改变语法分析过程
- 我们会在第11章中通过一个简单的例子来展示主义判定的威力,在此之前,让我们先来看一个读取一列整数的语法.它耍了一个小把戏,其中的一部分整数指定了接下来的多少个整数分为一组.
```  data
2 9 10 3 1 2 3
```
- 第一个数字2告诉我们,匹配接下来的两个数字9和10,紧接着的数字3告诉我们匹配接下来的三个数字.我们的目标是创建一份名为Data语法,将9和10分为一组,然后1,2,3分为一组.如下图
![image](http://47.75.83.77:8080//markdown/antlr/11.png)
``` antlr
grammar Data;

file : group+ ;

group: INT sequence[$INT.int] ;

sequence[int n]
locals [int i = 1;]
     : ( {$i<=$n}? INT {$i++;} )* // match n integers
     ;
     
INT :   [0-9]+ ;             // match integers
WS  :   [ \t\n\r]+ -> skip ; // toss out all whitespace
```
- 下面Data语法的关键在于一段动作,它的值是布尔类型的,称为一个语义判断,{$i<=$n}?.它的值在匹配到n个输入整数之前保持为true,其中n是sequence语法中的参数.当语义判定的值为false时,对应的备选分支就从语法中消失了.因此,它也就从生成的语法分析器中消失了.在本例中,语义判定的值为false使得循环终止,从sequence规则返回.
![image](http://47.75.83.77:8080//markdown/antlr/12.png)
- 剪刀的虚线显示语义判定会剪掉该路径.让语法分析器只剩下一个可选路径:退出.
- 大多数情况下我们不需要如此精细的操作,不过知道有这样一件处理异常问题的利器总是好的.
- 迄今为止,我们的侧重点都是语法分析的功能,实际上,在词法分析的层面上还有很多的功能等着我们去发现.下面就让我们一起来看一看.
#### 4.5 神奇的词法分析特性
- antlr有三个与词法符号有关非常棒的特性,值得付诸笔墨.首先,我们将会尝试处理xml这样的具有不同词法结构的输入格式(标签内外不同).其次,我们将会学习通过修改输入的词法符号流,在java类中插入一个字段的方法.它将会展示,如何以最低的代价来生成和输入内容相似的输出.最后,我们将会看到antlr语法分析器如何忽略空白字符和注释,同时不丢弃它们.
1. 孤岛语法: 处理相同文件中的不同格式
- 迄今为止,我们看到的样例输入文件都只包含一种语言,但是事实上,有很多常见的文件格式包含多重语言,例如,java文档注释中的@author标签等内容使用的是一种特殊格式的微型语言;,在注释之外的一切内容都是Java代码.类似StringTemplate和Django的模板引擎也存在相似的问题.它们必须将模板语言表达式之外的文本按照不同的方式处理.这样的情况通常称为孤岛语法.
- antlr提供了一个众所周知的词法分析器特性.称为词法分析模式(lexical mode),使我们能够方便地处理混杂着不同格式数据的文件.它的基本思想是,当词法分析器看到一些特殊的哨兵字符序列时,执行不同模式的切换.
- xml是个很好的例子.一个xml解析器将除了标签和实体转义(例如&pound;)之外的东西全部都当作普通文本.当看到<时,语法分析器会切换到"标签内部"模式,当看到>或/>时,它就切换回默认模式.
``` antlr
lexer grammar XMLLexer;

// Default "mode": Everything OUTSIDE of a tag 默认模式,所有在标签之外的东西.
OPEN        :   '<'                 -> pushMode(INSIDE) ;
COMMENT     :   '<!--' .*? '-->'    -> skip ;
EntityRef   :   '&' [a-z]+ ';' ;
TEXT        :   ~('<'|'&')+ ;           // match any 16 bit char minus < and & 匹配任意除<和&之外的16位字符

// ----------------- Everything INSIDE of a tag --------------------- 所有在标签之内的东西
mode INSIDE;

CLOSE       :   '>'                 -> popMode ; // back to default mode 回到默认模式
SLASH_CLOSE :   '/>'                -> popMode ;
EQUALS      :   '=' ;
STRING      :   '"' .*? '"' ;
SlashName   :   '/' Name ;
Name        :   ALPHA (ALPHA|DIGIT)* ;
S           :   [ \t\r\n]           -> skip ;

fragment
ALPHA       :   [a-zA-Z] ;

fragment
DIGIT       :   [0-9] ;
```
```
<tools>
	<tool name="ANTLR">A parser generator</tool>
</tools>
```
![image](http://47.75.83.77:8080//markdown/antlr/13.png)
- 词法分析器将输入文本转换为词法符号的过程.
- 知道词法符号是如何从词法分析器流向语法分析器的是非常有用的.例如,一些与翻译相关的问题实际上就是对输入的修改,有时候,我们可以通过修改原先的词法符号流来达到目的,而不需要产生新的输出.
2. 重写输入流
- 接下来,让我们构建一个小工具,它能够修改Java源代码并插入java.io.Serializable使用的序列化版本标识符serialVersionUID.我们不希望小题大做,所以,在原先的词法符号流中插入一个适当的代表常量字段的词法符号,然后打印出修改后的输入流.对症下药,才能事半功倍.
- main函数如下,当遍历结束后,我们将词法符号流打印出来.
``` java 
public class InsertSerialID {
    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);

        JavaLexer lexer = new JavaLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit(); // parse

        ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
        InsertSerialIDListener extractor = new InsertSerialIDListener(tokens);
        walker.walk(extractor, tree); // initiate walk of tree with listener

        // print back ALTERED stream
        System.out.println(extractor.rewriter.getText());
    }
}

```
- 在监听器中,我们需要在类定义的起始位置触发一个插入操作.
``` java 
public class InsertSerialIDListener extends JavaBaseListener {
    TokenStreamRewriter rewriter;
    public InsertSerialIDListener(TokenStream tokens) {
        rewriter = new TokenStreamRewriter(tokens);
    }

    @Override
    public void enterClassBody(JavaParser.ClassBodyContext ctx) {
        String field = "\n\tpublic static final long serialVersionUID = 1L;";
        rewriter.insertAfter(ctx.start, field);
    }
}
```
- 其中的关键之处在于,TokenStreamRewriter对象实际上修改的是词法符号流的视图,而非词法符号本身.它认为所有对修改方法的调用都只有一个指令.然后将这些修改放入一个队列;在未来词法符号流被重新渲染为文本时,这些修改才会被执行.在每次我们调用getText()时候,rewrite对象都会执行上述队列中的指令.
- 通过数行代码,我们就完成了对java类定义的修改,同时又不影响我们插入位置之外的任何代码.这样的策略在代码插桩,或者重构等场合下是非常有效的.TokenStreamRewriter是够修改词法符号流,是一个非常高效和强大的工具.
3. 将词法符号送入不同的通道
- 之前我们看到的,java接口抽取器魔术般的保留了方法签名中的空白字符和注释.
- 使用传统的方法很难达到这个目的.对于大多数语法,语法分析器是可以忽略空白字符和注释的.忽略却保留注释和空白字符的秘诀是将这些词法符号送入一个"隐藏的通道".语法分析器只处理一个通道,因此我们可以将希望保留的词法符号送往其它通道内.
``` antlr
COMMENT
    :   '/*' .*? '*/'    -> channel(HIDDEN) // match anything between /* and */
    ;
WS  :   [ \r\t\u000C\n]+ -> channel(HIDDEN)
```
- 如同我们之前讨论过的->skip一样,channel(hidden),也是一个词法分析器指令.此处,它设置了这些词法符号的通道号,这样,这些词法符号就会被语法分析器忽略.词法符号流中仍然保存着这些原始的词法符号序列,只不过在向语法分析器提供数据时忽略了那些处于已关闭通道的词法符号.
- 这一章,没有涉及细节.下一章我们将从学习antlr语法标记开始.
## 第二部分 antlr开始语言类应用程序
- 第二部分,我们将学习如何参照编程语言的标准和样例输入来编写语法.之后,通过遍历语法分析树来深入研究构建语言类应用程序的细节.
### 第5章 设计语法
- 在第一部分,我们认识了antlr,也了解了语法和语言类应用程序.现在我们要放慢脚步,学习一些实用的细节,例如构建内部数据结构,提取信息,以及翻译输入内容等.我们的第一步是学习如何编写语法.在本章中,我们将分析编程语言中最常见的语法结构和词法结构,学会如何用antlr标记来表达它们.我们要从一系列有代表性的输入文件中归纳出一门语言的结构.
- 虽然人们在过去的五十上年间发明了多种编程语言,但是,相对而言,基本的语言模式种类并不多.这种情况的出现,是因为人们在设计编程语言时,倾向于将它们设计的和脑海中的自然语言相似.我们期望看到有序的词法符号,也期望看到的词法符号间的依赖关系.除此之外,编程语言通常也因设计者使用了通用的数学符号而显得十分相似.在词法层面上,不同的编程语言也倾向于使用结构.如标识符,整数,字符串.
- 发展成以下四种抽象的计算机语言模式.
   1. 序列:一列元素
   2. 选择
   3. 词法符号依赖
   4. 嵌套结构
- 为了实现以上模式,我们的语法规定只需要可选方案,词法符号引用和规则引用即可(BNF).尽管如此,为方便起见,我们还是将这些元素划分为子规则.子规则是用括号包围的内联规则.
#### 5.1 从编程语言的范例代码中提取语法.
- 编写语法和编写软件很相似,差异是我们处理的是语言规则.而非函数或者过程(procedure),(记住,antlr将会为你的语法中的每条规则生成一个函数.),首先,我们应该讨论语法的整体结构以及如何建立初始的语法框架.
- 语法由一个为该语法命名的头部定义和一系列可以相互引用的语言规则组成.
- 和编写软件一样,我们必须指明我们需要的语言起始规则.
- 为了给某种编程语言编写语法.我们必须要么精通它,要么拥有很多有代表性的,由该语言所编写的样例程序.
- 设计良好的语法反映了编程世界中的功能分解或者自顶向下的设计.这意味着我们对语言结构的辨识是从最粗的粒度开始的.一走进行到最详细的层次,并把它们编写成语法规则.所以我们的第一个任务是找到最粗粒度的语言结构,将它作为我们的起始规则.在英语中,我们可以使用sentence规则作为起始规则.对于一个xml文件,我们可以使用document规则作为起始规则.对于一个Java文件,我们可以使用compliationUnit规则作为起始规则.
- 设计起始规则的内容实际上就是使用英语伪代码来描述输入文本的整体结构.这和我们编写软件的过程有点类似,例如,一个csv文件就是一系列以换行符为终止的行.(a comma-separated-value[CSV] file is a sequence of rows terminated by newlines)其中,is a 左侧的单词file就是规则名.右侧的全部内容就是规则定义中的<<stuff>>
``` engliseh
file : <<sequence of rows terminated by newlines>>
```
- 接着,我们降低一个层次,描述起始规则右侧所指定的那些元素.
- 它右侧的名词通常是词法符号或者尚未定义的规则.其中,词法符号是那些我们大脑能够轻易识别出的单词,标点符号或者运算符.正如英语句子中的单词是最基本的元素一样,词法符号是方法的基本元素.起始规则引用了其他的,需要进一步细化的语言结构.正如上面的例子中的行.
- 再低一层,我们可以说,一行就是一系列由逗号分隔的字段,接下来,一个字段就是一个数字或者字符串,我们的伪代码如下所示.
``` english
file : <<sequence of rows terminated by newlines>>
row  : <<sequence of fields separated by commas>>
field : <<number or string>>
```
- 当我们完成对规则的定义后,我们的语法草稿就成形了.让我们来试着用这种方法描述一下java文件的关键结构.在最粗的粒度上,一个Java的编译单元(complilation unit)由一个可选的包声明语句(package specifier)和一个或多个类定义( class definition)组成。其中,类定义由关键字 class开始,之后是一个标识符、可选的父类名( superclass specifier)、可选的实现语句(implements clause),以及类的定义体( class body)。一个类的定义体就是由花括号包裹的一系列类成员( class member)。一个类成员可以是内部类定义、字段或者方法。然后,我们将会描述字段和方法,接下来是方法中的语句。你应该已经明白了这个过程,从最高的层次开始,逐渐向下进行,将像是Java类定义这样巨大的语言结构分解为若干条稍后定义的规则。我们现在能够写出如下的语法伪代码.
- 如果以现有的语法规范作为参考,那么设计类似Java的大型编程语言的 ANTLR语法就会容易得多。不过,盲目地遵循已经存在的语法规范可能使你误入歧途,我们接下来将会讨论这一点。
#### 5.2 以现有的语法规范为指南
- 一份非ANπLR格式的语法规范能够很好地指导编程者理清该语言的结构。至少,一份已经存在的语法规范给我们提供了一份非常好的、可供参考的规则名列表。不过,还是小心为妙。我不建议从一门语言的参考手册里拷贝语法并粘贴到 ANTLR中,然后调试到它正常工作为止。请把参考手册当作一份指南,而非一份代码。
- 在刚开始的时候,辨识一条语法规则并使用伪代码编写右侧的内容是一项充满挑战的工作,不过,它会随着你为不同语言编写语法的过程变得越来越容易。在学习本书例子的过程中,你将会得到充分的锻炼。
- 一旦我们拥有了伪代码,我们就需要将它翻译为 ANTLR标记,从而得到一个能够正常工作的语法。在下一节里,我们将会定义常见的四种语言模式,研究如何将它们构造成 ANTLR语法。之后,我们将会学习如何定义语法中引用的词法符号,如“整数”和“标识符”。记住,在本章中,我们学习的是语法开发过程中的基础知识,这些知识将会为下一章中对真实世界的编程语言的处理奠定坚实的基础。
#### 5.3使用 ANTLR语法识别常见的语言模式
- 现在,我们已经掌握了一种自顶向下的、草拟一个语法的策略,接下来我们需要关注的是常见的语言模式:序列( sequence)、选择( choice)、词法符号依赖( token dependency),以及嵌套结构( nested phrase)。在之前的章节中,我们见过这些模式的一些例子。随着学习的深入,我们会用正式的语法规则将特定的模式表达出来,通过这种方式,我们就能够掌握基本的 ANTLR标记的用法。下面,让我们开始学习这些最常见的语言模式吧。
1. 序列模式
- 在计算机编程语言中,这种结构最常见的形式是一列元素,就像上文中的类定义中包括一系列方法一样。即使是像HTTP、POP和SMTP这样的简单的“协议语言”中,也能够看到序列模式的身影。协议的输入通常是一列指令。例如,下面是登录一台POP服务器并获取第一条消息的指令序列
USER parrt
PASS secret
RETR 1  
- 接下来让我们看一个任意长度序列的例子,在 Matlabl中,向量是保存在形如[123]的一列整数中的。对于有限长度的序列,我们可以逐个列出其中的元素,但是在这种情况下,我们不能通过INT INT INT INT INT INT INT...方式来列举所有可能的情况。我们使用+字符来处理这种一个或多个元素的情况。例如,(INT)+描述一个任意长度的、整数组成的序列。作为简写,INT+也是可以的。如果这样的序列可以为空,那么我们使用代表“零个或多个元素”的*字符:INT*。上述字符好比是编程语言中的循环,当然, ANTLR自动生成的语法分析器也是通过循环来实现它们的功能的
- 序列模式的变体包括:带终止符的序列模式和带分隔符的序列模式。CsV文件同时使用了这两种模式。下面是我们在先前的章节中使用 ANTLR标记写出的伪代码语法
``` code
file (row '\n')*; //以一个'\n'作为终止符的序列
row :  field (',' field)*; //以一个','作为分隔符的序列
field: INT;//假设字段都是整数
```
- 我们再来看看其他编程语言里的相同结构.例如,下面的语法匹配类似java的,每个语句都是由分号结束的编程语言:
stats :(stat ';')* ; //匹配零个或多个以';'终止的语句
- 与之相似,下面的语法匹配以逗号分隔的多个表达式,我们可以在一次函数调用的参数列表中找到这样的例子:
exprList : expr(',' expr)*;
- 就连antlr元语言也使用了序模式,下面的语法片段显示了antlr是如何使用它自身的句法表达"规则定义"这条句法的:
//匹配这样的结构 '规则名':'后面跟着至少一个备选分支'
//然后是若干条以'|'符号分隔的备选分支,最后是一个';'
rule:ID ':' alternative ('|' alternative)* ';';
- 最后,还有一种特殊的"零个或一个元素的序列",它可以用?字符来指定,用于表达一种可选的结构.在java的语法中,我们能够发现('extends' identifier)?这样的字符串,它用于匹配可选的父类声明.相似地,为了匹配可选的变量初始化语句,我们可以写成('='expr)?.这有点像是在"有和元之间选择".在下一节中我们会看到('='expr)?等价于('='expr|).
2. 选择模式(多个备选分支)
- 如果一门编程语言只有一种语句,那就太无聊了。即使是网络协议这样的最简单的语言,也包含多种有效语句,如POP协议中的USER和RETR指令。这促使我们思考选择模式的必要性。我们已经在Java语法伪代码的 member规则中看到了一个选择模式的实际应<<ested class definition or field or method>>.
- 我们使用符号作为“或者”来表达编程语言中的选择模式,在
ANTLR的规则中,它用于分隔多个可选的语法结构一—称作备选分
支( alternatives)或者可生成的结果( productions)。选择模式在语
法中随处可见。
回到之前的CsV语法,我们可以编写一条更加灵活的field规则,- 在之前的例子中,我们使用NT+来表示Matb的向量中的整数序列[1 2 3]。为了描述向量两侧的方括号,我们需要一种方法来表达对这样的词法符号的依赖。此时,如果我们在某个语句中看到了某个符号,我们就必须在同一个语句中找到和它配对的那个符号。为表达出这种语义,在语法中,我们使用一个序列来指明所有配对的符号,通常这些符号会把其他元素分组或者包裹起来。在上例中,我们可以用下面这种方式指定一个完整的向量
``` antlr
vector : '[' INT+ ']'; [1],[1,2],[1,2,3]
```
- 查看任何一个用你喜欢的编程语言编写的程序,你就会发现,几乎所有的用于分组的符号都是成对出现的:(...),{...}以及[...]。从6.4节的学习中我们能发现,在方法调用的圆括号间,以及用于数组索引的方括号间,词法符号依赖模式都存在。
``` antlr
expr : expr '(' exprList? ')' //类似f(),f(x),f(1,2)的函数词用
     | expr '[' expr']'  //类似a[i], a[i][j]的数组素引
     ...
     ;
```
- 我们也在方法声明中看到左右圆括号之间的词法符号依赖模式。
```
```
4. 嵌套模式
- 嵌套的词组是一种自相似的语言结构,即它的子词组也遵循相同的结构。表达式是一种典型的自相似语言结构,它包含多个嵌套的,以运算符分隔的子表达式。与之相似,一个while循环代码块是一个嵌套在更外层代码块中的代码块。在语法中,我们使用递归规则来表达这种自相似的语言结构。所以,如果一条规则定义中的伪代码引用了它自身,我们就需要一条递归规则(自引用规则)。
- 让我们看一看如何处理“代码块”这样的嵌套结构。一个 while表达式由一个关键字whle开始,后面是一个在括号中的条件表达式,再后面就是一条语句。我们也可以把多个语句放入花括号中,当作一个“代码块语句”使用。对上述规则的语法表述如下所示
```
stat : 'while' '(' expr')' stat //匹配while语句
     | '{' stat*'}' //匹配花括号中若干条语句组成的代码块
     ... //其他种关的语句
     ;
```     
- 其中, while中的stat是一个循环结构,它可以是一个语句或者由花括号包裹的一组语句。因为sta规则在前两个备选分支中引用了自身,我们称它为直接递归( directly recursive)的。如果我们将它的第二个备选分支抽取出来,stat规则和bok规则就会互为间接递归
```
stat :'while' '(' expr')' stat //匹配 WHILE语句
     | block   //匹配一个语句组成的代码块
     ...      //其他种类的语句
     ;
block : '{' stat*'}' //匹配花活号中若干条语句组成的代码块
```
- 大部分编程语言都包含多种形式的自相似结构,这带来的结果是语法中包含很多递归规则。让我们一起来看一门简单的、表达式类型只有三种—一数组索引表达式、括号表达式和整数——的编程语言。下面是用 ANTLR标记书写的语法
```
expr: ID '[' expr']' //a[1].a[b[1]],a[(2*b[1])]
    | '(' expr')'    //(1),(a[1]),(((1))),(2*a[1])
    | INT           //1,45
```
- 其中的递归发生的非常自然。因为一个数组的索引值本身也是一个表达式,所以我们就在对应的备选分支中直接引用了expr。实际上索引值本身也可以是一个数组索引表达式。从这个例子中我们可以看到,语言结构上的递归自然而然地使得语言规则发生了递归。如图5.1所示是两个样例输入对应的语法分析树。
![image](http://47.75.83.77:8080//markdown/antlr/14.png)
- 语法的非叶子节点代表了规则,而叶子节点代表了词法符号.将一个规则节点看作它的后代子树的标签,因为根节点是expr,所以整棵树就是一个表达式(expression).
#### 5.4处理优先级、左递归和结合性
- 在自顶向下的语法和手工编写的递归下降语法分析器中,处理表达式都是一件相当棘手的事情,这首先是因为大多数语法都存在歧义,其次是因为大多数语言的规范使用了一种特殊的递归方式,称为左递归( left recursion)。我们稍后会详细讨论它,现在请记住点,自顶向下的语法和语法分析器的经典形式无法处理左递归。为了阐明这个问题,假设有一种简单的算术表达式语言,它包含乘法和加法运算符,以及整数因子。表达式是自相似的,所以,很自然地,我们说,一个乘法表达式是由*连接的两个子表达式,一个加法表达式是由+连接的两个子表达式。另外单个整数也可以作为简单的表达式。这样写出的就是下列看上去非常合理的规则
```
expr : expr '*' expr //匹配由'*'运算符连接的子表达式
     | expr '+' expr //匹配由"+’运算符连接的子表达式
     | INTo   //匹配简单的整数因子
     ;
```
- 问题在于,对于某些输入文本而言,上面的规则存在歧义。换话说,这条规则可以用不止一种方式匹配某种输入的字符流,正如2.3节中所描述的那样。这个语法在简单的整数表达式和单运算符表达式上工作得很好--例如1+2和1*2--是因为只存在一种方式去匹配它们.对于1+2,上述语法只能用第二个备选分支去匹配,如图5-2左侧的语法分析树所示.
![image](http://47.75.83.77:8080//markdown/antlr/15.png)
- 但是对于1+2*3这样的输入而言,上述规则能够用两种方式解释它,如图5-2中间和右侧的语法分析树所示。它们的差异在于,中间的语法分析树表示将1加到2和3相乘的结果上去,而右侧的语法分析树表示将1和2相加的结果与3相乘.
- 这就是运算符优先级带来的问题,传统的语法无法指定优先级。大多数语法工具,例如 Bison,使用额外的标记来指定运算符优先级
- 与之不同的是, ANTLR通过优先选择位置靠前的备选分支来解决歧义问题,这隐式地允许我们指定运算符优先级。例如,expr规则中,乘法规则在加法规则之前,所以 ANTLR在解决1+2*3的歧义问题时会优先处理乘法。默认情况下, ANTLR按照我们通常对*和+的理解,将运算符从左向右地进行结合.尽管如此,一些运算符--例如指数运算符是从右向左结合的,所以我们需要在这样的运算符上使用assoc选项手工指定结合性.这样输入2^3^4就能够被正确解释为2^(3^4)
```
expr : expr '^' <assoc=right> expr //^运算符是右结合的
     | INT
     ;
```
- 在4.2后,<assoc=right> 要被放到备选分支的最左侧,否则会收到警告.
- 若要将上述三种运算符组合成为同一条规则,我们就必须把^放在最前面,因为它的优先级比*和+都要高(1+2^3的结果是9)。
```
expr:expr"^'< assoc= right:>expr//^运算符是右结合的
    | expr'*'expr//匹配由'*'运算符连接的子表达式
    | expr+ expr //匹配由"+'运算符连接的子表达式
    | INT  //匹配简单的整数因子

```
- 熟悉 ANTLR3的读者可能正在等我指出,和所有传统的自顶向下的语法分析器生成器一样, ANTLR无法处理左递归规则。然而,ANTLR4的一项重大改进就是,它已经可以处理直接左递归了。左递归规则是这样的一种规则:在某个备选分支的最左侧以直接或者间接方式调用了自身。上面的例子中的exp规则是直接左递归的,因为除INT之外的所有备选分支都以expr规则本身开头(它同时也是右递归的)
- 虽然antlr能够处理直接左递归,但无法处理间接左递归.
### 使用优先级上升(precedence climbing)算法解析表达式.
#### 5.5识别常见的词法结构
- 在词法角度上,不同的计算机语言的外观都十分相似。例如,如果我打乱一段输入文本的顺序,然后分别在所有曾经出现过的编程语言中将词法符号)10(重新组合为有效的词组,会发生什么呢?五十年前,我们在LSP中看到的是(f10),在Ago中看到的是f(10)。实际上,f(10)在从 Prolog到Java再到Go语言山的几乎所有编程语言中都是有效的。在词法角度上,不论是函数式、过程式声明式,还是面向对象的编程语言,看上去都是大同小异的。这一点令人惊讶。
- 这是一件好事,因为我们只需描述标识符和整数一次,然后稍加改动,就可以将它们应用于大多数的编程语言中。和语法分析器一样,词法分析器也使用规则来描述种类繁多的语言结构。在 ANTLR中,我们使用的是几乎完全相同的标记。唯一的差别在于,语法分析器通过输入的词法符号流来识别特定的语言结构,而词法分析器通过输入的字符流来识别特定的语言结构。
- 由于词法规则和文法规则的结构相似, ANTLR允许二者在同个语法文件中同时存在。不过,由于词法分析和语法分析是语言识别过程中的两个不同阶段,我们必须告诉 ANTLR每条规则对应的阶段。它是通过这种方式完成的:词法规则以大写字母开头,而文法规则以小写字母开头。例如,ID是一个词法规则名,而exp是一个文法规则名。
- 当开始编写一个新语法的时候,我通常从一个已有的语法(例如Java语法)中复制一些常见的词法结构对应的规则:标识符、数字、字符串、注释,以及空白字符。几乎所有的语言,哪怕是XML和JSON这样的非编程类的语言,都包含这些词法符号的变体。例如,尽管二者的语法差异巨大,C语言的词法分析器还是能够毫无问题地对下面的JSON字符流进行词法分析。
```
{
	title": Cat wrestling",
	chapters":[I"Intro":"."},...]
}
```
- 另外一个例子是多行注释。在Java中,多行注释使用/*...*/,而在XML中,多行注释使用的是<!--....-->,除了开始和结束的字符不同之外,二者的词法结构几乎完全相同。
- 对于关键字、运算符和标点符号,我们无须声明词法规则,只需要在文法规则中直接使用单引号将它们括起来即可,例如'whie'、'*',以及'++'。有些开发者更愿意使用类似MULT的词法规则来引用,以避免对其的直接使用。这样,在改变乘法运算符的时候,它们只需修改MULT规则,而无须逐个修改引用了MULT的文法规则
- 为了展示词法规则,一起来构造一些描述常见词法符号的词法规则的简化版本.
1. 匹配标识符
-  一个基本的标识符就是一个由大小写字母组成的序列.
```
ID : ('a'...'z'|'A'...'Z')+; //匹配1个或多个大小写字母,如果需要使用unicode字符,就必须写成'\uxxx'
ID : [a-zA-Z]+; 
```
- 类似的ID规则有时候会和其他词法规则或者字符串常量产生冲突,例如'enum'
- ID规则也能够匹配类似enum和for关键字,这意味着存在不止种规则可以匹配相同的输入字符串。要弄清此事,我们需要了解ANTLR对这种混合了词法规则和文法规则的语法文件的处理机制。首先, ANTLR从文法规则中筛选出所有的字符串常量,并将它们和词法规则放在一起。enum这样的字符串常量被隐式定义为词法规则,然后放置在文法规则之后、显式定义的词法规则之前。 ANTLR词法分析器解决歧义问题的方法是优先使用位置靠前的词法规则。这意味着,ID规则必须定义在所有的关键字规则之后,在上面的例子中,它在FOR规则之后。 ANTLR将为字符串常量隐式生成的词法规则放在显式定义的词法规则之前,所以它们总是拥有最高的优先级。因此,在本例中,'enum'被自动赋予了比D更高的优先级。
2. 匹配数字
描述10这样的数字非常容易,它不过是一列数字而已。
```
INT:θ"..."9"+;//匹配1个或多个数字
IMT:[0-9]+;//匹配1个或多个数字
//浮点数
FLOAT: DIGIT+ '.'  DIGIT* //匹配1.39.3.14159等
     | '.'  DIGIT+
     ;
fragment
 DIGIT :[0-9]; //匹配单个数字
```
- 在这里,我们使用了一条辅助规则DGT,这样就不用重复书写[0-9]了。将一条规则声明为 fragment可以告诉 ANTLR,该规则本身不是一个词法符号,它只会被其他的词法规则使用。这意味着我们不能在文法规则中引用DGT。
3. 匹配字符串常量
- 另外一种计算机语言共有的词法符号是类似"hello"的字符串常量。大多数语言中的字符串常量使用双引号,部分语言使用单引号或者同时使用单引号和双引号( Python)。不论哪种分界符,我们都使用同一种规则来匹配字符串常量:识别分界符之间的全部内容。
- 用语法伪代码表示,一个字符串就是两个双引号之间的任意字符序列。
``` 
STRING : '"' .*? '"' //匹配"..."间的任意文本
```
- 其中,点号通配符匹配任意的单个字符。因此,就是一个循环,它匹配零个或多个字符组成的任意字符序列。显然,它可以一直匹配到文件结束,但这没有任何意义。为解决这个问题, ANTLR通过标准正则表达式的标记?后缀)提供了对非贪婪匹配子规则( nongreedy subrule)的支持。非贪婪匹配的基本含义是:“获取一些字符,直到发现匹配后续子规则的字符为止”。更准确的描述是,在保证整个父规则完成匹配的前提下,非贪婪的子规则匹配数量最少的字符。有关非贪婪匹配的更多细节,请参阅15.6节。与之相反,*是贪婪的,因为它贪婪地消费掉一切匹配的字符(在本例中就是匹配通配符.的字符.
- 我们的 STRING规则还不够完善,因为它不允许其中出现双引号。为了解决这个问题,很多语言都定义了以\开头的转义序列。在这些语言中,如果希望在一个被双引号包围的字符串中使用双引号,我们就需要使用。下列规则能够支持常见的转义字符
```
STRING  : '"' (ESC|.) *?'"'
fragment
ESC :   '\\\"'|'\\\\\';/双字符序列\"和\\
```
- 其中, ANTLR语法本身需要对转义字符进行转义,因此我们需要\\来表示单个反斜杠字符。现在, STRING规则中的循环既能通过ESC片段规则( fragment rue)来匹配转义字符序列,也能通过通配符来匹配任意的单个字符。*?运算符会使(ESC|.)*?循环在看到后续子规则,即一个未转义的双引号时终止。
#### 5.6 划定词法分析器和语法分析器的界线
- 由于 ANTLR的词法规则可以包含递归,从技术角度上看,词法分析器变得和语法分析器一样强大。这意味着我们可以甚至可以在词法分析器中匹配语法结构。或者,另外一种极端是,我们可以把字符看作词法符号,然后用语法分析器来分析字符流的语法结构(这种情况称为无扫描器的语法分析器( scannerless parser),参阅code extras/CSQL.g4,匹配一门小型的C和SoL的混合语言的语法)。
- 划定词法分析器和语法分析器的界线位置不仅是语言的职责,更是语言编写的应用程序的职责。幸运的是,我们可以得到一些经验法则的指导。
- 在词法分析器中匹配并丢弃任何语法分析器无须知晓的东西。对于编程语言来说,要识别并丢弃的就是类似注释和空白字符的东西。否则,语法分析器就需要频繁检查它们是否存在于词法符号之间。
- 由词法分析器来匹配类似标识符、关键字、字符串和数字的常见词法符号。语法分析器的层级更高,所以我们不应当让它处理将数字组合成整数这样的事情,这会加重它的负担。
- 将语法分析器无须区分的词法结构归为同一个词法符号类型例如,如果我们的程序对待整数和浮点数的方式是一致的,那就把它们都归为 NUMBER类型的词法符号。没必要传给语法分析器不同的类型。
- 将任何语法分析器可以以相同方式处理的实体归为一类。例如,如果语法分析器不关心XML标签的内容,词法分析器就可以将尖括号中的所有内容归为一个名为TAG的词法符号类型
- 另一方面,如果语法分析器需要把一种类型的文本拆开处理,那么词法分析器就应该将它的各组成部分作为独立的词法符号输送给语法分析器。例如,如果语法分析器需要处理IP地址中的元素,那么词法分析器就应该把|P地址的各组成部分(整数和点)作为独立的词法符号送入语法分析器。
- 当我们说语法分析器无须区分特定的词法结构或者无须关心某个词法结构的内容时,实际上的意思是我们编写的程序不关心它们。我们编写的程序对这些词法结构进行的处理和翻译工作与语法分析器相为了展示最终的程序对我们构建词法分析器和语法分析器过程的影响,想象一个场景,我们在处理一个网络服务器上的日志文件,日志文件的每行包含一条记录。我们将逐渐增加程序的需求,在这个过程中分析词法分析器和语法分析器之间的界线是如何移动的。首先,假设每行都有一个IP地址、一个HTTP的请求方法,以及一个HTTP的状态码.
- 在本章中,我们学习了如何根据一份语言的样例代码或者文档,来构造语法的伪代码,然后使用 ANTLR标记构造出一个正式的语法。我们也学到了通用的语言模式:序列、选择、词法符号依赖和嵌套结构。在词法分析领域中,我们了解了最常见的词法符号的实现方法:标识符、数字、字符串、注释,以及空白字符。现在,是时候将这些知识应用于实践了,我们将会尝试构造一些真实世界中语言的语法
###　第６章　探索真实的语法世界
#### 6.1 解析csv
```
grammar CSV;

file : hdr row+ ;
hdr : row ;

row : field (',' field)* '\r'? '\n' ;

field
    :   TEXT
    |   STRING
    |
    ;

TEXT : ~[,\n\r"]+ ;
STRING : '"' ('""'|~'"')* '"' ; // quote-quote is an escaped quote

```
#### 6.2 解析json
### 第7章 将语法和程序的逻辑代码解耦
- 监听器和访问器机制的最大区别在于,监听器方法不负责显式调用子节点的访问方法,而访问器必须显式触发对子节点的访问以便树的遍历过程能够正常进行(正如我们在25节中看到的那样)。因为访问器机制需要显式调用方法来访问子节点,所以它能够控制遍历过程中的访问顺序,以及节点被访问的次数。为简便起见,在下文中我会用术语“事件方法”( event method)来代替监听器的回调方法和访问器方法。
- 本章中,我们的目标是准确理解 ANTLR自动生成的语法分析树遍历机制的工作方式和原理。我们将首先了解监听器机制的起源,以及如何使用监听器和访问器机制来使得程序逻辑代码与语法分离。随后,我们将会学习如何令 ANTLR产生更加精确的事件一一为规则的每个备选分支都生成一个事件。在深入了解 ANTLR的语法分析树遍历机制后,我们将阅读三个计算器的实现代码,它们展示了传递子表达式结果的不同方法。最后,我们将会讨论三种方法的优缺点。到那时,我们就能胸有成竹地处理下一章中的真实语言了。
#### 7.1 从内嵌动作到监听器的演进
- 如果你曾经使用过 ANTLR的早期版本或者其他能够自动生成语法分析器的工具,你会惊讶于这一事实:我们构建语言类应用程序时可以不在语法中内嵌动作(代码)。监听器和访问器机制能够将语法和程序逻辑代码解耦,从而大有裨益。这样旳解耦将程序封装起来,避免了杂乱无章地分散在语法中。如果语法中没有内嵌动作,我们就可以在多个程序中复用同一个语法,而无须为每个目标语法分析器重新编译一次。
- 本节主要研究从包含内嵌动作的语法到完全与动作解耦的语法的演进过程。下列语法用于读取属性文件,这些文件的每行都是一个赋值语句,其中<<.>>是内嵌动作的概要。类似<< start file>的标记代表一段恰当的Java代码。
- 更好的方案是从antlr自动生成的语法分析器PropertyFileParser派生出一个子类,然后将内嵌动作转换为方法.
- @member 语法分析器parser
7.2 使用语法分析树监听器编写程序


# 命令
java -cp D:\Users\chen\.m2\repository\org\antlr\antlr4\4.5.3;%CLASSPATH% org.antlr.v4.Tool


java -cp E:\programer\gitRepository\learnantrl\antlr-4.0-complete.jar;%CLASSPATH% org.antlr.v4.runtime.misc.TestRig

    参考龙书,编写简单编译器,从头实现一个编译器前端的难度远远超出了一个开发者的能力.编写编译器所需要的理论基础,
技术功底和精力都远非普通软件可比.
    antlr能使这个过程易如反掌.antlr能够根据用户定义的语法文件自动生成词法分析器和语法分析器,并将输入文本处理为(可视化的)
语法分析树.这一切都是自动进行的,只需要一份描述该语言的语法文件.
    语言由语句组成,语句由词组组成.词组由更小的词组和词汇符号组成.
    将字符聚集为单词或符号 token,过程称为词法分析.词法符号包含至少两部分信息.词法符号的类型,该词法符号对应的文本.
    第二个阶段是语法分析过程.
    语法分析树的内部节点是词组名,这些名字用于识别它们的子节点,并将子节点归类.
    通过语法分析树这种方便的数据结构.语法分析器就能将诸如符号是如何构成词组这样的完整信息传达给
    程序的其余部分.树结构不仅在后续的步骤中易于处理.而且也是一种为开发者所熟知的数据结构.


  我们使用一系列的规则指定语句的词汇结构.语法分析树的子树根节点就是对应语法规则的名子.
  递归下降语法分析器.
  递归下降语法分析器最神奇的地方在于,通过方法调用路线
  为了编写一个语言类程序,必须对每个输入的词组或子词组执行一些适当的操作.最简单的方式是操作语法
  分析器自动生成语法分析树
  词法分析器处理字符序列,并将生成的词法符号提供给语法分析器.语法分析器随即根据这些信息来检查语法的正确性并建造出一棵
语法分析树.
通过这个入门的项目示例,antlr语法的语义元素定义,antlr根据语法自动生成代码的机制,如何将自动生成的语法分析器和java程序集成,以及如何
使用语法分析树监听器编写一个代码翻译工具.
antlr的jar包中存在两个关键部分.antlr工具和antlr运行库.

ArrayInitParser.java 语法分析器的定义.
ArrayInitLexer.java 自动识别出我们的语法中的文法规则和词法规则.
用自然语言来说,我们的表达式语言组成的程序就是一系列语句,每个语句都由换行符终止.
一个语句可以是表达式,一个赋值语句或者是一个空行.

不过多地深究细节,让我们看一看antlr语法基本标记包含哪些元素.
语法包含一系列描述语言结构的规则,这些规则既包括类似stat和expr的描述语法结构的规则.
也包括描述标识符和整数之类的词汇符号.
语法分析器的规则以小写字母开头
词法分析器的规则以大写字母开头


## EOF
如果不加EOF 先面的匹配了就结束了,如果加了必须要将一行匹配完