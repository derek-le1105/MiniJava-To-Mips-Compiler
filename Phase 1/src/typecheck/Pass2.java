package typecheck;
import syntaxtree.*;
import visitor.Visitor;
import typecheck.Types.*;

import java.util.*;
import visitor.*;

public class Pass2 extends GJNoArguDepthFirst<Scope> {
    Map<String,String> map = new HashMap<>();
    Map<String, Holder> phase1;

    String key; //store the current class
    String currMethod; //store the current method
    
    
    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */

    public Pass2(Map<String, Holder> phase12, Goal goal){
        phase1 = phase12;

        System.out.println("In Pass2");
        goal.f0.accept(this);
        goal.f1.accept(this);


        /* for(String key1: phase1.keySet()){
            System.out.println(key1+"=>\n" + phase1.get(key1).printEverything());
          } */
    }


    /**
 * Grammar production:
 * f0 -> MainClass()
 * f1 -> ( TypeDeclaration() )*
 * f2 -> <EOF>
 */
    public Scope visit(Goal goal)//  Goal() :
    { 
        System.out.println("Inside goal");
        return Int.instance(); //just removing the reds
    }
    
    
    /**
 * Grammar production:
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> "public"
 * f4 -> "static"
 * f5 -> "void"
 * f6 -> "main"
 * f7 -> "("
 * f8 -> "String"
 * f9 -> "["
 * f10 -> "]"
 * f11 -> Identifier()
 * f12 -> ")"
 * f13 -> "{"
 * f14 -> ( VarDeclaration() )*
 * f15 -> ( Statement() )*
 * f16 -> "}"
 * f17 -> "}"
 */
    
    @Override
    public Scope visit(MainClass mainClass)//  MainClass() :
    {
        System.out.println("Inside mainclass");
        System.out.println(mainClass.f0);
        System.out.println(mainClass.f1 + "---");
        Scope type1 = mainClass.f1.accept(this);
        Scope type2 = mainClass.f15.accept(this);
        System.out.println(mainClass.f15);
        mainClass.f11.accept(this);
        mainClass.f14.accept(this);
        mainClass.f15.accept(this);
        System.out.println("finished with mainClass");
        return Int.instance(); //just removing the reds
    }
    
    
    /**
 * Grammar production:
 * f0 -> ClassDeclaration()
 *       | ClassExtendsDeclaration()
 */
    public Scope visit(TypeDeclaration typeDec)//  TypeDeclaration() :  
    {
        System.out.println("Inside typeDec");
        typeDec.f0.accept(this);
        return Int.instance(); //just removing the reds
    }
    
    
    /**
 * Grammar production:
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> ( VarDeclaration() )*
 * f4 -> ( MethodDeclaration() )*
 * f5 -> "}"
 */
    public Scope visit(ClassDeclaration classDec)//  ClassDeclaration() :
    {
        System.out.println("inside classDec ");
        Scope type1 = classDec.f1.accept(this);
        key = type1.name();
        //classDec.f3.accept(this); //remove this because pass1 already goes thru the varDeclaration
        classDec.f4.accept(this);
        return Int.instance(); //just removing the reds
    }
   
    /**
 * Grammar production:
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "extends"
 * f3 -> Identifier()
 * f4 -> "{"
 * f5 -> ( VarDeclaration() )*
 * f6 -> ( MethodDeclaration() )*
 * f7 -> "}"
 */
    public Scope visit(ClassExtendsDeclaration classExtend)//  ClassExtendsDeclaration() :
    {
        System.out.println("inside classExtend");
        return Int.instance(); //just removing the reds
    }
    
    
    /**
 * Grammar production:
 * f0 -> Type()
 * f1 -> Identifier()
 * f2 -> ";"
 */
    public Scope visit(VarDeclaration varDec)//  VarDeclaration() :  
    {
      System.out.println("VarDeclaration; type: " + varDec.f0);
      Scope type1 = varDec.f0.accept(this); //to get the type
      Scope type2 = varDec.f1.accept(this);
      System.out.println("inside VarDec type1 string: " + type1.name());
      System.out.println("inside VarDec type2 string: " + type2.name());
      if(isDistinct(type2.name())){
        map.put(type2.name(), type1.name());
      }
      
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "public"
 * f1 -> Type()
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( FormalParameterList() )?
 * f5 -> ")"
 * f6 -> "{"
 * f7 -> ( VarDeclaration() )*
 * f8 -> ( Statement() )*
 * f9 -> "return"
 * f10 -> Expression()
 * f11 -> ";"
 * f12 -> "}"
 */
    public Scope visit(MethodDeclaration mDec)//  MethodDeclaration() :  f1: Type; f2: identifier; f9: "return"
    {
      System.out.println("MethodDeclaration" );
      Scope type1 = mDec.f1.accept(this);
      Scope type2 = mDec.f2.accept(this);
      map.put(type2.name(), type1.name());
      Scope hold = mDec.f4.accept(this);
      mDec.f7.accept(this);
      mDec.f8.accept(this);
      System.out.println("+++++" );
      Scope type3 = mDec.f10.accept(this); //x is here which is int [basic]
      if(map.get(type3.name()) != type1.name()){
          throw new Error("wrong return type");
      }
        return Int.instance(); //just removing the reds
    }


    /**
 * Grammar production:
 * f0 -> FormalParameter()
 * f1 -> ( FormalParameterRest() )*
 */
    public Scope visit(FormalParameterList FPL)// FormalParameterList() :
    {
      System.out.println("FormalParameterList");
        return Int.instance(); //just removing the reds
    }
    
    /**
 * Grammar production:
 * f0 -> Type()
 * f1 -> Identifier()
 */
    public Scope visit(FormalParameter formalParam)// FormalParameter() :
    {
      System.out.println("FormalParameter");
        return Int.instance(); //just removing the reds
    }
    
    /**
 * Grammar production:
 * f0 -> ","
 * f1 -> FormalParameter()
 */
    public Scope visit(FormalParameterRest FPR)// FormalParameterRest() :
    {
      System.out.println("FormalParameterRest");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> ArrayType()
 *       | BooleanType()
 *       | IntegerType()
 *       | Identifier()
 */
    public Scope visit(Type theType)// Type() :
    {
      System.out.println("Type theType " );
      Scope type1 = theType.f0.accept(this);
      System.out.println("theType: " );
      if(type1.typeName() =="Id"){
        return type1;
      }
      else if(type1.name() == "boolean"){return type1;}
      else if(type1 == Arr.instance()){return Arr.instance();}
      else if(type1.name() == "int"){return type1;}//Int.instance()
      else{throw new Error("Non-integer left operand for \"Type\".");}
      
    }
    

    /**
 * Grammar production:
 * f0 -> "int" -> array
 * f1 -> "["
 * f2 -> "]"
 */
    public Scope visit(ArrayType arrayType)// ArrayType() :
    {
      Scope type1 = arrayType.f0.accept(this);
      if(!(type1 == Int.instance())){throw new Error("Non-integer left operand for \"ArrayType\".");}
      return Int.instance();
    }
    

    /**
 * Grammar production:
 * f0 -> "boolean"
 */
    public Scope visit(BooleanType BType)// BooleanType() :
    {
      Scope type1 = BType.f0.accept(this);
      Scope type2 = new Bool();
      if(!(BType.f0.tokenImage == "boolean")){throw new Error("Non-integer left operand for \"BooleanType\".");}
      return type2;
    }
    

    /**
 * Grammar production:
 * f0 -> "int"
 */
    @Override
    public Scope visit(IntegerType IType)// IntegerType() :
    {
      Scope type1 = IType.f0.accept(this);
      Scope type2 = new Int();
      System.out.println("checking the type1 under IntegerType " + IType.f0.tokenImage);
      if(!(IType.f0.tokenImage == "int")){throw new Error("Non-integer left operand for \"IntegerType\".");} //after testing using token image gives out the type
      return type2;
    }
    

    /**
 * Grammar production:
 * f0 -> Block()
 *       | AssignmentStatement()
 *       | ArrayAssignmentStatement()
 *       | IfStatement()
 *       | WhileStatement()
 *       | PrintStatement()
 */
    public Scope visit(Statement state)// Statement() :
    {
      System.out.println("Statement ");
      System.out.println(state.f0.which);
      System.out.println(state.f0);
      state.f0.accept(this);
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "{"
 * f1 -> ( Statement() )*
 * f2 -> "}"
 */
    public Scope visit(Block block)// Block() :
    {
        System.out.println("Block");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> Identifier()
 * f1 -> "="
 * f2 -> Expression()
 * f3 -> ";"
 */
    public Scope visit(AssignmentStatement assign)// AssignmentStatement() : f0: identifier; f2: expression
    {
      System.out.println("AssignmentStatement");
      Scope type1 = assign.f0.accept(this);
      Scope type2 = assign.f2.accept(this);
      System.out.println("Type2 in AssignState: " + type2.name()); //+ type2.name()
      System.out.println("map for type1: " + map.get(type1.name()));
      System.out.println("typeName for type2: " + type2.typeName());
    
      if(map.get(type1.name()) == null)
      {
        throw new Error("not in the map");
      }
    
      if( map.get(type1.name()) != type2.typeName()){
         throw new Error("Types not similar");
       }
      return type1;
      // removed the return statement: if needed check the left side
    }
    

    /**
 * Grammar production:
 * f0 -> Identifier()
 * f1 -> "["
 * f2 -> Expression()
 * f3 -> "]"
 * f4 -> "="
 * f5 -> Expression()
 * f6 -> ";"
 */
    public Scope visit(ArrayAssignmentStatement arrayAssign)// ArrayAssignmentStatement() :
    {
      System.out.println("ArrayAssignmentStatement");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "if"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 * f5 -> "else"
 * f6 -> Statement()
 */
    public Scope visit(IfStatement theIf)// IfStatement() : boolean
    {
      System.out.println("IfStatement");
      return Bool.instance();
    }
    

    /**
 * Grammar production:
 * f0 -> "while"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 */
    public Scope visit(WhileStatement theWhile)// WhileStatement() :
    {
        System.out.println("WhileStatement");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "System.out.println"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> ";"
 */
    public Scope visit(PrintStatement print)// PrintStatement() : return int
    {
        System.out.println("PrintStatement");
        Scope type1 = print.f2.accept(this);
        if(type1.name() != "int"){throw new Error("Type mismatch");}
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> AndExpression()
 *       | CompareExpression()
 *       | PlusExpression()
 *       | MinusExpression()
 *       | TimesExpression()
 *       | ArrayLookup()
 *       | ArrayLength()
 *       | MessageSend()
 *       | PrimaryExpression()
 */
    public Scope visit(Expression express)// Expression() :
    {
        System.out.println("Expression");
        Scope type1 = express.f0.accept(this);
        return type1; //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "&&"
 * f2 -> PrimaryExpression()
 */
    public Scope visit(AndExpression andExpress)// AndExpression() :
    {
        System.out.println("And");
      Scope type1 = andExpress.f0.accept(this);
      if(!(type1 == Bool.instance())){
        //if(!(type1 instanceof IntegerType)){ /*Int.instance() */
            throw new Error("Non-integer left operand for \"+\".");
        }
        Scope type2 = andExpress.f2.accept(this); 
        if(!(type2 == Bool.instance())){
            throw new Error("Non-integer left operand for \"+\".");
        }
        return Bool.instance();
    } 
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "<"
 * f2 -> PrimaryExpression()
 */
    public Scope visit(CompareExpression compExpress)// CompareExpression() :
    {
      System.out.println("Compare");
      Scope type1 = compExpress.f0.accept(this);
            if(!(type1 == Int.instance())){
                throw new Error("Non-integer left operand1 for \"<\".");
            }
            Scope type2 = compExpress.f2.accept(this);
            if(!(type2 == Int.instance())){
                throw new Error("Non-integer right operand2 for \"<\".");
            }
            return Bool.instance();
    } 
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "+"
 * f2 -> PrimaryExpression()
 */
    @Override
    public Scope visit(PlusExpression plusExpress)// PlusExpression() :
    {
        System.out.println("Plus");
      Scope type1 = plusExpress.f0.accept(this);//
        if(!(type1 == Int.instance())){
        //if(!(type1 instanceof IntegerType)){ /*Int.instance() */
            throw new Error("Non-integer left operand for \"+\".");
        }
        Scope type2 = plusExpress.f2.accept(this); 
        if(!(type2 == Int.instance())){
            throw new Error("Non-integer left operand for \"+\".");
        }
        return Int.instance();
    }
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "-"
 * f2 -> PrimaryExpression()
 */
    public Scope visit(MinusExpression minExpress)// MinusExpression() :
    {
        System.out.println("Minus");
      Scope type1 = minExpress.f0.accept(this);//
        //if(!(type1 == Int.instance())){
        if(!(type1 == Int.instance())){ /*Int.instance() */
            throw new Error("Non-integer left operand for \"-\".");
        }
        Scope type2 = minExpress.f2.accept(this); 
        if(!(type2 == Int.instance())){
            throw new Error("Non-integer left operand for \"-\"."); /*TypeMismatchExpression */
        }
        return Int.instance();
    }
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "*"
 * f2 -> PrimaryExpression()
 */
    public Scope visit(TimesExpression timeExpress)// TimesExpression() :
    {
        System.out.println("Times");
      Scope type1 = timeExpress.f0.accept(this);//
        //if(!(type1 == Int.instance())){
        if(!(type1 == Int.instance())){
            throw new Error("Non-integer left operand for \"-\"."); /*TypeMismatchExpression */
        }
        Scope type2 = timeExpress.f2.accept(this); 
        if(!(type2 == Int.instance())){
            throw new Error("Non-integer left operand for \"-\"."); /*TypeMismatchExpression */
        }
        return Int.instance();
    }
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "["
 * f2 -> PrimaryExpression()
 * f3 -> "]"
 */
    public Scope visit(ArrayLookup arrayLook)// ArrayLookup() :
    {
        System.out.println("arrayLookup");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> "length"
 */
    public Scope visit(ArrayLength arrLen)// ArrayLength() :
    { return Int.instance();}
    

    /**
 * Grammar production:
 * f0 -> PrimaryExpression()
 * f1 -> "."
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( ExpressionList() )?
 * f5 -> ")"
 */
    public Scope visit(MessageSend mSend)// MessageSend() :
    {
        System.out.println("MessageSend");
        Scope type1 = mSend.f0.accept(this);
        System.out.println("/////// " + type1.name());
        Scope type2 = mSend.f2.accept(this);
        Scope type3 = mSend.f4.accept(this);

        System.out.println("--- type1: " + type1.name());
        System.out.println("--- type2: " + type2.name());

        if(phase1.get(type1.name()).get_met(type2.name()) == null){throw new Error("method has not been made");}
        //System.out.println("--- type3: " + type3.name());
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> Expression()
 * f1 -> ( ExpressionRest() )*
 */
    public Scope visit(ExpressionList exList)//  ExpressionList() :
    {
        System.out.println("ExpressionList");
        return Int.instance(); //just removing the reds
    }
    
    /**
 * Grammar production:
 * f0 -> ","
 * f1 -> Expression()
 */
    public Scope visit(ExpressionRest exRest)//  ExpressionRest() :
    {
        System.out.println("ExpressionRest");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> IntegerLiteral()
 *       | TrueLiteral()
 *       | FalseLiteral()
 *       | Identifier()
 *       | ThisExpression()
 *       | ArrayAllocationExpression()
 *       | AllocationExpression()
 *       | NotExpression()
 *       | BracketExpression()
 */
    public Scope visit(PrimaryExpression primaryExpress)//  PrimaryExpression() :
    {
        System.out.println("PrimaryExpression");
        Scope type1 = primaryExpress.f0.accept(this);
        System.out.println("Fin");
        return type1; //just removing the reds
    }
    


    /**
 * Grammar production:
 * f0 -> <INTEGER_LITERAL>
 */
    public Scope visit(IntegerLiteral intLit)//  IntegerLiteral() :
    {
      System.out.println("IntegerLiteral " + intLit.f0.toString());
      Scope type1 = intLit.f0.accept(this);
      Scope type2 = new Id(intLit.f0.toString(), "int");
      //if(!(type1 == Int.instance())){throw new Error("Non-integer left operand for \"IntegerLit\".");}
      return type2;
    }
    
    /**
 * Grammar production:
 * f0 -> "true"
 */
    public Scope visit(TrueLiteral TLit)//  TrueLiteral() :
    {
      System.out.println("TrueLiteral");
      Scope type1 = TLit.f0.accept(this);
      Scope type2 = new Bool();
      //if(!(type1 == Bool.instance())){throw new Error("Non-integer left operand for \"TrueLit\".");}
      return type2;
    }
    
    /**
 * Grammar production:
 * f0 -> "false"
 */
    public Scope visit(FalseLiteral FLit)//  FalseLiteral() :
    {
      System.out.println("FalseLiteral");
      Scope type1 = FLit.f0.accept(this);
      Scope type2 = new Bool();
      //if(!(type1 == Bool.instance())){throw new Error("Non-integer left operand for \"FalseLit\".");}
      return type2;
    }
    
    /**
 * Grammar production:
 * f0 -> <IDENTIFIER>
 */
    public Scope visit(Identifier identifier)//  Identifier() :
    { 
      System.out.println("Identifier here1");
      System.out.println("***" + identifier.f0);
      System.out.println("here2");
      Scope type1 = identifier.f0.accept(this);
      //type1.nameAdd(identifier.f0);
      Scope type2 = new Id((String)identifier.f0.tokenImage, "Id");
      System.out.println("Testing the type1 under Identifier: " + type2.name());
      //if(!(type1 == Id.instance())){throw new Error("Non-integer left operand for \"TrueLit\".");}
      return type2;
    }
    
    /**
 * Grammar production:
 * f0 -> "this"
 */
    public Scope visit(ThisExpression tExpress)//  ThisExpression() :
    {
        System.out.println("ThisExpression");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "new"
 * f1 -> "int"
 * f2 -> "["
 * f3 -> Expression()
 * f4 -> "]"
 */
    public Scope visit(ArrayAllocationExpression arrAllExpress)//  ArrayAllocationExpression() :
    {
      System.out.println("ArrayAllocationExpression");
        return Int.instance(); //just removing the reds
    }
    
    /**
 * Grammar production:
 * f0 -> "new"
 * f1 -> Identifier()
 * f2 -> "("
 * f3 -> ")"
 */
    public Scope visit(AllocationExpression AllocExpress)//  AllocationExpression() :
    {
      System.out.println("AllocationExpression");
      Scope type1 = AllocExpress.f1.accept(this);
      System.out.println("+++++++++++++ " + type1.name());
        return type1; //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "!"
 * f1 -> Expression()
 */
    public Scope visit(NotExpression notExpress)//  NotExpression() :
    {
        System.out.println("NotExpression");
        return Int.instance(); //just removing the reds
    }
    

    /**
 * Grammar production:
 * f0 -> "("
 * f1 -> Expression()
 * f2 -> ")"
 */
    public Scope visit(BracketExpression bracket)//  BracketExpression() :
    {
      System.out.println("BracketExpression");
        return Int.instance(); //just removing the reds
    }
    
    
    //static class TypleList implements Type{} 
    
    public static boolean isInt(String str) {
        
        try {
            @SuppressWarnings("unused")
          int x = Integer.parseInt(str);
            return true; //String is an Integer
      } catch (NumberFormatException e) {
          return false; //String is not an Integer
      }
    }
    @Override
    public Scope visit(NodeList n) {
      // TODO Auto-generated method stub
      return null;
    }
    @Override
    public Scope visit(NodeListOptional n) {
      // TODO Auto-generated method stub
      System.out.println("NodeListOptional --; present: " + n.present());
      System.out.println("size of nodeListOptional: " + n.size());
       /* if ( n.present() )
             for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
                e.nextElement().accept(this);  */
       
       for(int i = 0; i < n.nodes.size(); ++i){
        System.out.println("nodeListOptional[" + i + "]: " + n.nodes.elementAt(i));
        n.nodes.elementAt(i).accept(this);
      } 
      return null;
    }
    @Override
    public Scope visit(NodeOptional n) {
      // TODO Auto-generated method stub
      System.out.println("NodeOptional");
      return null;
    }
    @Override
    public Scope visit(NodeSequence n) {
      // TODO Auto-generated method stub
      System.out.println("NodeSequence");
      return null;
    }
    @Override
    public Scope visit(NodeToken n) {
      // TODO Auto-generated method stub
      System.out.println("NodeToken " + n.kind + "; image: " + n.tokenImage);//t.beginLine, t.beginColumn, t.endLine, t.endColumn
      System.out.println(n.toString());
      return null;
    }
    
    //added this
    public Scope visit(NodeChoice n){
      System.out.println("NodeChoice");
    
      return null;
    }
    
    public boolean isDistinct(String a){ // return true if a if its not in the map else return false
      if(map.get(a) == null){
        return true;
      }
      //might want to throw error here!!!
      return false;
    }
}
/* class Holder1{
    //superClass, fields, methods
    String SC;
    Map<String, String> fields = new HashMap<>();
    Map<String, String> methods = new HashMap<>();
    Map<String, Map<String, String>> hold = new HashMap<>(); //Change < p1, int>>
    
    public Holder1(){
        
    }
    
    public void SC_input(String str1){
        SC = str1;
    }
    
    public void field_input(String str1, String str2){
        fields.put(str1, str2);
    }
    
    public void met_input(String str1, String str2){
        methods.put(str1, str2);
    }
  
    public void hold_input(String str1, String str2, String str3){
      Map<String, String> inner = hold.get(str1);
      if(inner == null){
          inner = new HashMap<>();
      hold.put(str1, inner);
      }
      inner.put(str2, str3);
    }
    
    public String get_SC(String str){
        return SC;
    }
  
    public String get_hold(String str1, String str2){
      return hold.get(str1).get(str2);
    }
    
    public String get_field(String str){
        return fields.get(str);
    }
    
    public String get_met(String str){
        return methods.get(str);
    }
  
    public int hold_size(String str){
      return hold.get(str).size();
    }
  
    public String printEverything(){
      String output = "    SC: " + this.SC + '\n' + 
                      "    Fields:" + this.fields + '\n' +
                      "    Methods: " + this.methods + '\n' +
                      "    Passer: ";
  
                      for (String temp : hold.keySet()) {
                        for (String c : hold.get(temp).keySet()) {
                            System.out.println("key--" + c + "--value--" + hold.get(temp).get(c));
                        }
                        System.out.println("-------------");
                    }
      return output;
    }
  } */