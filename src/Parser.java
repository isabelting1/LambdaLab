import java.util.ArrayList;

public class Parser {
    public static Expression parse (ArrayList<String> tokens) {
            ArrayList<Expression> exArr = new ArrayList<Expression>();
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(0).equals("\\")) {
                    ArrayList<String> ex = new ArrayList<String>();
                    Variable v = new Variable(tokens.get(1));
                    for (int j = 3; j < tokens.size(); j++) {
                        ex.add(tokens.get(j));
                    }
                    tokens.clear();
                    exArr.add(new Function(v, parse(ex)));

                } else if (tokens.get(0).equals("(")) {
                    int parens = 1;
                    int pIndex = -1;
                    ArrayList<String> in = new ArrayList<String>();
                    for (int p = 1; p < tokens.size(); p++) {
                        if (tokens.get(p).equals("(")) {
                            parens++;
                        }
                        if (parens != 0 && tokens.get(p).equals(")")) {
                            parens--;
                            if (parens == 0) {
                                pIndex = p;
                                for (int j = 1; j < pIndex; j++) {
                                    in.add(tokens.get(j));
                                }
                                for (int j = 1; j < pIndex + 2; j++) {
                                    tokens.remove(0);
                                }
                                exArr.add(parse(in));
                            }
                        }
                    }
                } else {
                    exArr.add(new Variable(tokens.get(0)));
                    tokens.remove(0);
                }
                i--;
            }
            return parseEx(exArr);
    }

    public static Expression parseEx(ArrayList<Expression> exArr) {
        if (exArr.size() == 1) {
            return exArr.get(0);
        }
        else {
            ArrayList<Expression> front = new ArrayList<Expression>();
            for (int i = 0; i < exArr.size() -1; i++) {
                front.add(exArr.get(i));
            }
            return new Application(parseEx(front), exArr.get(exArr.size()-1));
        }
    }

    public static void runAndStore(ArrayList<String> tokens, ArrayList<Variable> vars, ArrayList<String> varNames, ArrayList<Expression> values){
        //running
        if (tokens.contains("run")){

            ArrayList<String> a = new ArrayList<String>();
            for(int i = tokens.indexOf("run") + 1; i < tokens.size(); i++){
                a.add(tokens.get(i));
            }

            Expression expression = parse(a);

            Expression runProduct = null;

            if(expression.getClass() == Application.class){
                if(((Application) expression).getLeft() instanceof Function){
                    Integer posOfRight = ((Application) expression).toString().indexOf(((Application) expression).getRight().toString());
                    Expression e = ((Function) ((Application) expression).getLeft()).getEx();
                    Expression v = ((Function) ((Application) expression).getLeft()).getVar();
                    if(e instanceof Variable){
                        if(e.toString().equals(v.toString())){
                            ((Function) ((Application) expression).getLeft()).setEx(((Application) expression).getRight());
                            //runProduct = ((Application) expression).getRight();
                        }
                        runProduct = ((Function) ((Application) expression).getLeft()).getEx();
                        /*else{
                            runProduct = e;
                        }*/
                    }
                    else if(e instanceof Application){
                        if(((Application) e).getLeft().toString().equals(v.toString())){
                            ((Application) e).setLeft(((Application) expression).getRight());
                            if(((Application) expression).getRight() instanceof Function){
                                //if application right is a function, run the function with the right side of function expression.
                                runAndStore(Lexer.cleanUp("run " + e.toString()), vars, varNames, values);
                            }
                        }
                        else if(((Application) e).getRight().toString().equals(v.toString())){
                            ((Application) e).setRight(((Application) expression).getRight());
                        }
                        runProduct = e;
                    }

                    if(e instanceof Function){
                        //function
                        System.out.println("function");
                    }
                    else{
                        //look for the bound variable and replace with right side
                        //if not there then return expression
                    }
                }
                else {
                    runProduct = expression;
                }
            }
            else{
                runProduct = expression;
            }
            if (tokens.contains("=")) {
                if (varNames.contains(tokens.get(0))) {
                    System.out.println(tokens.get(0) + " is already defined.");
                }
                else {
                    vars.add(new Variable(tokens.get(0)));
                    varNames.add(tokens.get(0));
                    values.add(runProduct);
                    System.out.println("Added " + values.get(values.size() - 1) + " as " + vars.get(values.size() - 1));
                }
            }
            else if (varNames.contains(tokens.get(0))) {
                System.out.println(values.get(varNames.indexOf(tokens.get(0))));
            }
            else {
                System.out.println(runProduct);
            }
        }

        //adding only
        else if (tokens.contains("=")) {
            if (varNames.contains(tokens.get(0))) {
                System.out.println(tokens.get(0) + " is already defined.");
            } else {
                ArrayList<String> equalEx = new ArrayList<String>();
                vars.add(new Variable(tokens.get(0)));
                varNames.add(tokens.get(0));
                for (int i = tokens.indexOf("=") + 1; i < tokens.size(); i++) {
                    equalEx.add(tokens.get(i));
                }
                for (int i = 0; i < varNames.size(); i++) {
                    String var = varNames.get(i);
                    for (int j = 0; j < equalEx.size(); j++) {
                        if (equalEx.get(j).equals(var)) {
                            equalEx.set(j, values.get(i).toString());
                        }
                    }
                }
                values.add(parse(equalEx));
                System.out.println("Added " + values.get(values.size() - 1) + " as " + vars.get(values.size() - 1));
            }
        }
        else if (varNames.contains(tokens.get(0))) {
            System.out.println(values.get(varNames.indexOf(tokens.get(0))));
        }
        else{
            System.out.println(parse(tokens));
        }
    }
   /* public static Expression run(){

    }*/
}
