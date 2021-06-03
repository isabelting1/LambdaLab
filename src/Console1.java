import java.util.ArrayList;
import java.util.Scanner;

public class Console1 {
    public static ArrayList<Expression> expressions = new ArrayList<Expression>();
    public static void main(String[] args) {
        System.out.printf("> ");
        Scanner s = new Scanner(System.in);
        String input = s.nextLine();
        ArrayList<String> tokens;
        ArrayList<Variable> vars = new ArrayList<Variable>();
        ArrayList<String> varNames = new ArrayList<String>();
        ArrayList<Expression> values = new ArrayList<Expression>();
        
        while (!input.equals("exit")) {
            if (!input.equals("")) {
                tokens = Lexer.cleanUp(input);

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
                            Expression e = ((Function) ((Application) expression).getLeft()).getEx();
                            Expression v = ((Function) ((Application) expression).getLeft()).getVar();
                            if(e instanceof Variable){
                                runProduct = ((Application) expression).getRight();
                            }
                            else if(e instanceof Application){
                                String checkForVar = e.toString();
                                for(int i = 0; i<checkForVar.length(); i++){
                                    //find the variable
                                }
                                System.out.println("heyy");
                            }
                            else{
                                //function
                                System.out.println("function");
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
                else {
                    if (tokens.contains("=")) {
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
                    } else if (varNames.contains(tokens.get(0))) {
                        System.out.println(values.get(varNames.indexOf(tokens.get(0))));
                    } else {
                        System.out.println(parse(tokens));
                    }
                }
            }
            expressions.clear();
            System.out.print("> ");
            input = s.nextLine();
        }
        System.out.printf("Goodbye!");

    }

    public static Expression parse (ArrayList<String> tokens) {
        ArrayList<Expression> exArr = new ArrayList<Expression>();
        for (int i = 0; i < tokens.size(); i ++) {
            if (tokens.get(0).equals("\\")) {
                ArrayList<String> ex = new ArrayList<String>();
                Variable v = new Variable(tokens.get(1));
                for (int j = 3; j < tokens.size(); j++) {
                    ex.add(tokens.get(j));
                }
                tokens.clear();
                exArr.add(new Function(v,parse(ex)));

            }
            else if (tokens.get(0).equals("(")) {
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
            }
            else {
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
            expressions.addAll(exArr);
            return new Application(parseEx(front), exArr.get(exArr.size()-1));
        }
    }
}