import java.util.*;
import java.io.*;

public class LexicalAnalyzer {

    enum classification{
        CLASS$,
        var,
        LB$,
        RB$,
        assign$,
        integer,
        comma$,
        semi$,
        var$,
        const$,
        procedure$,
        while$,
        do$,
        relop,
        if$,
        then$,
        else$,
        mop,
        addop,
        call$,
        LP$,
        RP$,
        LComment$,
        RComment$
    }

    enum Type{
        identifier,
        integer,
        comment
    }

    static class Symbol{
        String item;
        String type;
        Object value;
        int address;
        boolean codeSegment;

    }

    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(new File("C:\\Users\\Khalid\\Documents\\code.txt"));
        ArrayList<String> items = new ArrayList<String>();
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        while(sc.hasNext()){
            items.add(sc.next());
        }

        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < items.get(i).length(); j++){
                Type type;
                Character temp = items.get(i).charAt(0);
                if(Character.isDigit(temp)){
                    type = Type.integer;
                } else if(Character.isLetter(temp)){
                    type = Type.identifier;
                }

            }
        }
        System.out.println(symbols.toString());
        System.out.println(items.toString());
    }
}
