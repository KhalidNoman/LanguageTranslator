import java.util.*;
import java.io.*;

public class LexicalAnalyzer2 {

    static class token {
        String item;
        String type;

        public String toString() {
            String format = String.format("\n%-30s\t %-20s", item, type);
            return format;
        }
    }

    static class Symbol {
        String item;
        String type;
        String value;
        int address;
        boolean segment;

        public String toString() {
            String format = String.format("\n%-30s\t%-20s\t%-20s\t%-20s\t%-20s",
                    item, type, value == null? "-":value, address, segment? "CS":"DS");
            return format;
        }
    }


    public static void main(String[] args) throws Exception {
        //Get reserved word list
        ArrayList<String> reserved = new ArrayList<String>();
        Scanner sc = new Scanner(new File("reserved.txt"));
        while(sc.hasNext())
            reserved.add(sc.next());

        //Scanner and tokenization
        sc = new Scanner(new File("code.txt"));
        ArrayList<token> tokens = new ArrayList<token>();
        while (sc.hasNext()) {
            token temp = new token();
            temp.item = sc.next();
            Character first = temp.item.charAt(0);
            if (Character.isDigit(first)) {
                temp.type = "numLit";
                token delim = new token();
                for (int j = 1; j < temp.item.length(); j++) {
                    if (!Character.isDigit(temp.item.charAt(j))) {
                        if (temp.item.charAt(j) != '(') {
                            delim.item = Character.toString(temp.item.charAt(j));
                            delim.type = "#" + delim.item;
                        }
                        temp.item = temp.item.substring(0, j);
                    }
                }
                tokens.add(temp);
                if (delim.type != null)
                    tokens.add(delim);
            } else if (Character.isLetter(first)) {
                temp.type = "ident";
                token delim = new token();

                for (int j = 1; j < temp.item.length(); j++) {
                    if (!Character.isLetter(temp.item.charAt(j)) && !Character.isDigit(temp.item.charAt(j))) {
                        if (temp.item.charAt(j) != '(') {
                            delim.item = Character.toString(temp.item.charAt(j));
                            delim.type = "#" + delim.item;
                        }
                        delim.item = temp.item.substring(j, temp.item.length());

                        temp.item = temp.item.substring(0, j);
                    }
                }
                if (reserved.contains(temp.item))
                    temp.type = "$" + temp.item;
                tokens.add(temp);
                if (delim.item != null) {
                    if (delim.type == null) {
                        for (int x = 0; x < delim.item.length(); x++) {
                            if (delim.item.charAt(x) == '{' || delim.item.charAt(x) == '(' || delim.item.charAt(x) == ')') {
                                token temp2 = new token();
                                temp2.item = Character.toString(delim.item.charAt(x));
                                temp2.type = "#" + temp2.item;
                                tokens.add(temp2);
                            }
                        }
                    } else {
                        tokens.add(delim);
                    }

                }
            } else if (first.equals('=')) {
                temp.type = "&assign";
                if(temp.item.length() > 1)
                    temp.type = "&relop";
                tokens.add(temp);
            } else if(first.equals('+') || first.equals('–') ){
                temp.type = "&addop";
                tokens.add(temp);
            } else if(first.equals('*') || (first.equals('/') && temp.item.length() > 2)){
                temp.type = "&mop";
                tokens.add(temp);
            } else if(first.equals('<') || first.equals('>') || first.equals('!')){
                temp.type = "&relop";
                tokens.add(temp);
            }else if(first.equals('}')) {
                temp.type = "#}";
                tokens.add(temp);
            } else if(first.equals('{')) {
                temp.type = "#{";
                tokens.add(temp);
            } else if (temp.item.equals("/*")) {
                temp.type = "comment";
                String next = sc.next();
                while (!next.equals("*/")) {
                    temp.item = temp.item + " " + next;
                    next = sc.next();
                }
                temp.item = temp.item + " " + next;
                tokens.add(temp);
            }


        }
        System.out.println(tokens.toString());


        //Get transition table for table driven problem solving
        sc = new Scanner(new File("transitions.txt"));
        int rows = sc.nextInt(), cols = sc.nextInt();
        int[][] tTable = new int[rows][cols];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                tTable[i][j] = sc.nextInt();
            }
        }

        //Create symbol table
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        int currentState = 0, iteration = 0;
        int codeSeg = 0, dataSeg = 0;
        int inputType;

        while(currentState != 12){
            Symbol curr = new Symbol();
            curr.item = tokens.get(iteration).item;
            String valT = tokens.get(iteration).type;
            inputType = 8;
            if(valT.equals("$CLASS")){
                inputType = 0;
            } else if(valT.equals("ident")) {
                inputType = 1;
            } else if(valT.equals("#{")) {
                inputType = 2;
            } else if(valT.equals("$CONST")) {
                inputType = 3;
            } else if(valT.equals("$VAR")) {
                inputType = 4;
            } else if(valT.equals("&assign")) {
                inputType = 5;
            } else if(valT.equals("numLit")) {
                inputType = 6;
            } else if(valT.equals("#,")) {
                inputType = 7;
            } else if(valT.equals("#;")) {
                inputType = 8;
            } else if(valT.charAt(0) == '$') {
                inputType = 9;
            } else if(iteration == tokens.size()-1){
                currentState = 12;
                break;
            }
            int oldState = currentState;


            currentState = tTable[currentState][inputType];
            if(oldState == 1){
                curr.segment = true;
                curr.type = "$PROGRAMNAME";
                curr.address = codeSeg + 1;
                codeSeg++;
                symbols.add(curr);
            }
            if(oldState == 7){
                curr.segment = false;
                curr.type = "CONSTVAR";
                curr.item = tokens.get(iteration-3).item;
                curr.address = dataSeg + 1;
                curr.value = tokens.get(iteration-1).item;
                dataSeg++;
                symbols.add(curr);
            }
            if(oldState == 9){
                curr.segment = false;
                curr.type = "VAR";
                curr.item = tokens.get(iteration-1).item;
                curr.address = dataSeg + 1;
                dataSeg++;
                symbols.add(curr);
            }
            if(currentState == 11){
                curr.value = curr.item;
                boolean there = false;
                for(int x = 0; x < symbols.size(); x++){
                    if(symbols.get(x).type.equals("numLiteral")){
                        if( symbols.get(x).value.equals(curr.value))
                            there = true;
                    }
                }
                if(!there){
                    curr.segment = false;
                    curr.type = "numLiteral";
                    curr.address = dataSeg + 1;
                    dataSeg++;
                    symbols.add(curr);
                }
            }
            if(currentState == -1) {
                System.out.println("SOMETHING IS NOT PROFESSIONAL GRADE!");
                break;
            }

            iteration++;
        }
        System.out.println(symbols.toString());

        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");

        writer.println("Classification Table");
        writer.println(String.format("%-30s\t %-20s", "Token", "Classification"));
        writer.print("------------------------------------------------");
        for(int i = 0; i < tokens.size(); i++){
            writer.print(tokens.get(i));
        }

        writer.println("\n!END!\n\n\nSymbol Table");
        writer.println(String.format("%-30s\t%-20s\t%-20s\t%-20s\t%-20s",
                "Token", "Class", "Value", "Address", "Segment"));
        writer.print("--------------------------------------------------------------" +
                "--------------------------------------------------");
        for(int i = 0; i < symbols.size(); i++){
            writer.print(symbols.get(i));
        }
        writer.println("\n!END!");
        writer.close();

        QuadsGenerator.generateQuads();

    }
}
