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
            System.out.println(tokens.get(i) + " " + i);
            if(!headers.contains(tokens.get(i).item)){
                myStack.add(tokens.get(i));
            } else {
                System.out.println("ELSE");
                if(lastOp != 0){

                    if(pTable[headers.indexOf(myStack.get(lastOp).item)][headers.indexOf(tokens.get(i).item)] == 2){
                        System.out.println("REDUCE " + myStack.get(lastOp).item + " > " + tokens.get(i).item);
                    } else {
                        myStack.add(tokens.get(i));
                        lastOp = myStack.size();
                    }
                }else {
                    myStack.add(tokens.get(i));
                    lastOp = myStack.size();
                }
            }
            System.out.println("STACK " + myStack.toString());
        }

        System.out.println(myStack.toString());


    }
}
