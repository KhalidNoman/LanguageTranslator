import java.io.*;
import java.util.*;

public class QuadsGenerator {
    static class quads{
        String op;
        String arg1 = "?";
        String arg2 = "?";
        String arg3 = "?";

        public String toString(){
            return String.format("%10s, %10s, %10s, %10s",op, arg1, arg2, arg3);
        }
    }

    public static void generateQuads() throws Exception{
        ArrayList<LexicalAnalyzer.token> tokens = new ArrayList<LexicalAnalyzer.token>();
        ArrayList<LexicalAnalyzer.token> myStack = new ArrayList<LexicalAnalyzer.token>();
        ArrayList<String> fixUp = new ArrayList<>();
        ArrayList<String> whileFix = new ArrayList<>();
        Scanner sc = new Scanner(new File("output.txt"));
        boolean reduced = false;
        int tempUsed = 1, labels = 0, whiles = 0;
        PrintWriter writer = new PrintWriter("partial.txt", "UTF-8");
        for (int i = 0; i < 3; i++)
            sc.nextLine();

        while(sc.hasNext()){
            LexicalAnalyzer.token temp = new LexicalAnalyzer.token();
            temp.item = sc.next();
            if(!temp.item.equals("!END!")){
                if(temp.item.startsWith("/*")){
                    while(!temp.item.endsWith("*/"))
                        temp.item = temp.item + sc.next();
                }
                temp.type = sc.next();
                if(temp.item != null)
                    tokens.add(temp);
            } else break;
        }

        sc = new Scanner(new File("PrecedenceTable.txt"));
        int[][] pTable = new int[22][22];
        ArrayList<String> headers = new ArrayList<>();
        for(int i = 0; i < 22; i++){
            headers.add(sc.next());
        }



        for(int i = 0; i < 22; i++){
            for(int j = 0; j < 22; j++){
                pTable[i][j] = sc.nextInt();
            }
        }

        int lastOp = 0;
        for(int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).item.equals("CONST") || tokens.get(i).item.equals("VAR")){
                for(int j = i; j < tokens.size(); j++){
                    if(tokens.get(j).item.equals(";")){
                        i = j;
                        break;
                    }
                }

            }
            else if(!headers.contains(tokens.get(i).item) && !tokens.get(i).item.equals(",")){
                myStack.add(tokens.get(i));
                if(tokens.get(i).type.equals("$PROCEDURE")){
                    quads qds = new quads();
                    qds.op = "PROCEDURE";
                    qds.arg1 = tokens.get(i+1).item;
                    writer.println(qds);
                }
            } else {
                if(lastOp != 0){
                    //System.out.println("CHECKING: " + myStack.get(lastOp).item  + " WITH " + tokens.get(i).item );
                    do {
                        reduced = false;
                        if((myStack.get(lastOp).item.equals(";") && tokens.get(i).item.equals(",")) || (myStack.get(lastOp).item.equals(",") && tokens.get(i).item.equals(";"))
                                || (myStack.get(lastOp).item.equals(";") && tokens.get(i).item.equals(";")) || (myStack.get(lastOp).item.equals(",") && tokens.get(i).item.equals(","))){
                            myStack.remove(lastOp);
                            for(int x = myStack.size() -1; x > -1; x--){
                                if(headers.contains(myStack.get(x).item)){
                                    lastOp = x;
                                    break;
                                }
                            }
                        } else if(pTable[headers.indexOf(myStack.get(lastOp).item)][tokens.get(i).item.equals(",")?
                                0:headers.indexOf(tokens.get(i).item)] == 2){
                            System.out.println("REDUCE " + myStack.get(lastOp).item + " takes " + tokens.get(i).item);

                            if(myStack.get(lastOp).type.equals("&mulop") || myStack.get(lastOp).type.equals("&addop")){
//                                System.out.println(myStack.get(lastOp).type);
                                String code = "";
                                quads qds = new quads();
                                for(int x = 0; x < 3; x++){
                                    //System.out.println("REMOVE " + myStack.remove(myStack.size()-1));
                                    LexicalAnalyzer.token hold = myStack.remove(myStack.size()-1);
                                    if(hold.type.equals("Temporary")){
                                        tempUsed--;
                                    }
                                    switch (x){
                                        case 1:
                                            qds.op = hold.item;
                                            break;
                                        case 2:
                                            qds.arg1 = hold.item;
                                            break;
                                        case 0:
                                            qds.arg2 = hold.item;
                                            break;
                                    }
                                    qds.arg3 = "T"+tempUsed;
                                    code = hold.item + " " + code;
                                }
                                System.out.println("REDUCTION: " + code);
                                writer.println(qds);
//                                for(int x = myStack.size() -1; x > -1; x--){
//                                if(myStack.get(x).item.equals("=")){
//                                    LexicalAnalyzer.token temp = new LexicalAnalyzer.token();
//                                    temp.item = "T1";
//                                    temp.type = "Temporary";
//                                    myStack.add(temp);
//
//                                }
//                                }
                                LexicalAnalyzer.token temp = new LexicalAnalyzer.token();
                                temp.item = "T"+tempUsed;
                                temp.type = "Temporary";
                                myStack.add(temp);
                                tempUsed++;
                                reduced = true;

                            } else if(myStack.get(lastOp).type.equals("&relop") || myStack.get(lastOp).type.equals("$assign")){
//                                System.out.println(myStack.get(lastOp).type);
                                String code = "";
                                quads qds = new quads();
                                for(int x = 0; x < 3; x++){
                                    LexicalAnalyzer.token hold = myStack.remove(myStack.size()-1);
                                    if(hold.type.equals("Temporary")){
                                        tempUsed--;
                                    }
                                    switch (x){
                                        case 1:
                                            qds.op = hold.item;
                                            break;
                                        case 0:
                                            qds.arg2 = hold.item;
                                            break;
                                        case 2:
                                            qds.arg1 = hold.item;
                                            break;
                                    }

                                    code = hold.item + " " + code;
                                }
                                System.out.println("REDUCTION: " + code);
                                writer.println(qds);
                                reduced = true;

//                            } else if(myStack.get(lastOp).item.equals("$CALL")){
//                                System.out.println(myStack.get(lastOp).type);
//
//                                for(int x = 0; x < 2; x++){
//                                    System.out.println("REMOVE " + myStack.remove(myStack.size()-1));
//                                }
//                                reduced = true;
                            } else if(myStack.get(lastOp).type.equals("#)")){
                                myStack.remove(myStack.size()-1);
                                for(int x = myStack.size()-1; x > -1; x--){
                                    if(myStack.get(x).type.equals("#(")){
                                        myStack.remove(x);
                                        break;
                                    }
                                }
                                if(myStack.get(myStack.size()-2).item.equals("PROCEDURE")){
                                    myStack.remove(myStack.size()-1);
                                    myStack.remove(myStack.size()-1);
                                    quads qds = new quads();
                                    qds.op = "RETURN";
                                    writer.println(qds);
                                }
                                reduced = true;
                            } else if(myStack.get(lastOp).item.equals("CALL")){
                                String code = "";
                                quads qds = new quads();
                                for(int x = 0; x < 2; x++){
                                    LexicalAnalyzer.token hold = myStack.remove(myStack.size()-1);
                                    if(hold.type.equals("Temporary")){
                                        tempUsed--;
                                    }
                                    if(x == 0) qds.arg1 = hold.item;
                                    code = hold.item + " " + code;
                                }
                                qds.op = "CALL";

                                System.out.println("REDUCTION: " + code);
                                writer.println(qds);
                            } else if(myStack.get(lastOp).item.equals("{")){
                                myStack.remove(myStack.size()-1);
//                                LexicalAnalyzer.token temp = new LexicalAnalyzer.token();
//                                temp.item = ";";
//                                temp.type = "#;";
//                                myStack.add(temp);
                                tokens.get(i).item = ";";
                                i--;
                                if(myStack.get(myStack.size()-3).item.equals("CLASS")){
                                    quads qds = new quads();
                                    qds.op= "JUMP";
                                    qds.arg1 = "FINI";
                                    writer.println(qds);
                                }
//                                writer.println("RETURN");
                                reduced = true;
                            } else if(myStack.get(lastOp).item.equals("THEN")){
                                System.out.println("FIXING " + fixUp.toString());
                                for(int x = myStack.size()-1; x > -1; x--){
                                    String check = myStack.remove(myStack.size()-1).item;
                                    System.out.println("IF CHECK: " + check);
                                    if(check.equals("IF")){
                                        break;
                                    }
                                }
//                                System.out.println(myStack.remove(myStack.size()-1));
//                                System.out.println(myStack.remove(myStack.size()-1));
                                quads qds = new quads();
                                qds.op = fixUp.remove(fixUp.size()-1);
                                writer.println(qds);
                                reduced = true;
                            } else if(myStack.get(lastOp).item.equals("DO")){
                                quads qds = new quads();
                                qds.op = "JUMP";
                                qds.arg1 = whileFix.remove(whileFix.size()-1);
                                writer.println(qds);
                                System.out.println("FIXING " + fixUp.toString());
                                for(int x = myStack.size()-1; x > -1; x--){
                                    String check = myStack.remove(myStack.size()-1).item;
                                    System.out.println("WHILE CHECK: " + check);
                                    if(check.equals("WHILE")){
                                        break;
                                    }
                                }
//                                System.out.println(myStack.remove(myStack.size()-1));
//                                System.out.println(myStack.remove(myStack.size()-1));
//                                System.out.println(myStack.remove(myStack.size()-1));
                                qds = new quads();
                                qds.op = fixUp.remove(fixUp.size()-1);
                                writer.println(qds);
                                reduced = true;

                            }

                            for(int x = myStack.size() -1; x > -1; x--){
                                if(headers.contains(myStack.get(x).item)){
                                    lastOp = x;
                                    break;
                                }
                            }
//                            myStack.add(tokens.get(i));
//                            if(headers.contains(tokens.get(i).item))
//                                lastOp = myStack.size()-1;
                        } else {
                            myStack.add(tokens.get(i));
                            lastOp = myStack.size() - 1;
                            quads qds = new quads();
                            if(tokens.get(i).item.equals("WHILE")){
                                whiles++;
                                qds.op = "WHILE";
                                qds.arg1 = "W"+ whiles;
                                whileFix.add(qds.arg1);
                            } else if (tokens.get(i).item.equals("DO")){
                                qds.op = "DO";
                                labels++;
                                fixUp.add("L" + labels);
                                qds.arg1 = fixUp.get(fixUp.size()-1);
                            } else if (tokens.get(i).item.equals("IF")) {
                                qds.op = "IF";
                            } else if (tokens.get(i).item.equals("THEN")){
                                qds.op = "THEN";
                                labels++;
                                fixUp.add("L" + labels);
                                qds.arg1 = fixUp.get(fixUp.size()-1);
                            }
                            if (qds.op != null){
                                writer.println(qds);
                            }
                        }

                    }while(reduced);
                    if(myStack.get(myStack.size()-1).item.equals(";")){
                        myStack.remove(myStack.size()-1);
                        for(int x = myStack.size() -1; x > -1; x--){
                            if(headers.contains(myStack.get(x).item)){
                                lastOp = x;
                                break;
                            }
                        }
                    }
                }else {
                    myStack.add(tokens.get(i));
                    lastOp = myStack.size() - 1;
                }
            }
        }

        System.out.println(myStack.toString());
        writer.close();


    }
}
