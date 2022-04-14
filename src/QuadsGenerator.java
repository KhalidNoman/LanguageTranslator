import java.io.*;
import java.util.*;

public class QuadsGenerator {
    public static void generateQuads() throws Exception{
        ArrayList<LexicalAnalyzer.token> tokens = new ArrayList<LexicalAnalyzer.token>();
        ArrayList<LexicalAnalyzer.token> myStack = new ArrayList<LexicalAnalyzer.token>();
        Scanner sc = new Scanner(new File("output.txt"));
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
            if(!headers.contains(tokens.get(i).item) && !tokens.get(i).item.equals(",")){
                myStack.add(tokens.get(i));
            } else {
                if(lastOp != 0){
                    //System.out.println("CHECKING: " + myStack.get(lastOp).item  + " WITH " + tokens.get(i).item );
                    if((myStack.get(lastOp).item.equals(";") && tokens.get(i).item.equals(",")) || (myStack.get(lastOp).item.equals(",") && tokens.get(i).item.equals(";"))
                    || (myStack.get(lastOp).item.equals(";") && tokens.get(i).item.equals(";")) || (myStack.get(lastOp).item.equals(",") && tokens.get(i).item.equals(","))){
                        i++;
                        myStack.remove(lastOp);
                        for(int x = myStack.size() -1; x > -1; x--){
                            if(headers.contains(myStack.get(x).item)){
                                lastOp = x;
                                break;
                            }
                        }
                    } else
                    if(pTable[headers.indexOf(myStack.get(lastOp).item)][tokens.get(i).item.equals(",")? 0:headers.indexOf(tokens.get(i).item)] == 2){
                        System.out.println("REDUCE " + myStack.get(lastOp).item + " takes " + tokens.get(i).item);

                        if(myStack.get(lastOp).type.equals("&mulop") || myStack.get(lastOp).type.equals("&addop")){
                            for(int x = 0; x < 4; x++){
                                System.out.println("REMOVE " + myStack.remove(myStack.size()-1));
                            }
                        } else if(myStack.get(lastOp).type.equals("&relop") || myStack.get(lastOp).type.equals("$assign")){
                            for(int x = 0; x < 3; x++){
                                System.out.println("REMOVE " + myStack.remove(myStack.size()-1));
                            }
                        } else if(myStack.get(lastOp).item.equals("$CALL")){
                            for(int x = 0; x < 2; x++){
                                System.out.println("REMOVE " + myStack.remove(myStack.size()-1));
                            }
                        } else if(myStack.get(lastOp).item.equals(")")){
                            for(int x = 0; x < 2; x++){
                                System.out.println("REMOVE " + myStack.remove(myStack.size()-1));
                            }
                        }

                        for(int x = myStack.size() -1; x > -1; x--){
                            if(headers.contains(myStack.get(x).item)){
                                lastOp = x;
                                System.out.println("NEW LAST: "  + myStack.get(lastOp));
                                break;
                            }
                        }
                        myStack.add(tokens.get(i));
                        if(headers.contains(tokens.get(i).item))
                            lastOp = myStack.size()-1;
                    } else {
                        myStack.add(tokens.get(i));
                        lastOp = myStack.size() - 1;
                    }
                }else {
                    myStack.add(tokens.get(i));
                    lastOp = myStack.size() - 1;
                }
            }
        }

        System.out.println(myStack.toString());


    }
}
