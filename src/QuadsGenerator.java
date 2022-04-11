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


    }
}
