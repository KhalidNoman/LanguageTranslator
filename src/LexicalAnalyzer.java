import java.util.*;
import java.io.*;

public class LexicalAnalyzer {

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
        sc = new Scanner(new File("tokenTransitions.txt"));
        ArrayList<token> tokens = new ArrayList<token>();
        int[][] tokenTransitions = new int[17][17];
        ArrayList<String> headers = new ArrayList<String>();
        for(int i = 0; i < 16; i++){
            headers.add(sc.next());
        }
        for(int i = 0; i < 17; i++){
            for(int j = 0; j < 17; j++){
                tokenTransitions[i][j] = sc.nextInt();
            }
        }


        sc = new Scanner(new File("code.txt"));
        while (sc.hasNext()) {
            int currentState = 0, iteration = 0;
            int inputType, oldstate;
            boolean comm =false;
            while(sc.hasNext()){
                LexicalAnalyzer.token temp = new LexicalAnalyzer.token();
                temp.item = sc.next();
                Character chr = temp.item.charAt(0);
                for(int i = 0; i <= temp.item.length(); i++){
                    if(i == temp.item.length()){
                        inputType = 16;
                        chr = ' ';
                    }


                    else{
                        chr = temp.item.charAt(i);
                        if(Character.isLetter(chr)){
                            inputType = 0;
                        }
                        else if (Character.isDigit(chr)){
                            inputType = 1;
                        }
                        else {
                            inputType = headers.indexOf(Character.toString(chr));
                        }
                    }


                    oldstate = currentState;
                    if(currentState == -1){
                        System.out.println(tokens.toString());
                        break;
                    }
                    currentState = tokenTransitions[currentState][inputType];

                    token temp2 = new token();
                    String delim = "";
                    switch (currentState){
                        case 2:
                            temp.type = "$assign";
                            tokens.add(temp);
                            break;
                        case 3:
                            temp.type = "ident";
                            delim = temp.item.substring(i, temp.item.length());
                            temp.item = temp.item.substring(0, i);
                            if(reserved.contains(temp.item))
                                temp.type = "$"  +temp.item;
                            tokens.add(temp);
                            if(delim.length() > 0){
                                for(int x = 0; x < delim.length(); x++){
                                    temp2 = new token();
                                    temp2.item = ""+ delim.charAt(x);
                                    temp2.type = "#" + temp2.item;
                                    tokens.add(temp2);
                                }
                            }
                            break;
                        case 4:
                        case 15:
                            temp.type = "#" + temp.item;
                            tokens.add(temp);
                            break;
                        case 6:
                            temp.type = "numLit";
                            delim = temp.item.substring(i, temp.item.length());
                            temp.item = temp.item.substring(0, i);
                            tokens.add(temp);
                            if(delim.length() > 0) {
                                for (int x = 0; x < delim.length(); x++) {
                                    temp2 = new token();
                                    temp2.item = "" + delim.charAt(x);
                                    temp2.type = "#" + temp2.item;
                                    tokens.add(temp2);
                                }
                            }
                            break;
                        case 8:
                            temp.type = "&relop";
                            tokens.add(temp);
                            break;
                        case 10:
                            do{
                                temp.item = temp.item + sc.next();
                            }while(!temp.item.endsWith("*/"));
                            temp.type = "comment";
                            comm = true;
                            tokens.add(temp);
                            break;
                        case 13:
                            temp.type = "&addop";
                            tokens.add(temp);
                            break;
                        case 14:
                            temp.type = "&mulop";
                            tokens.add(temp);
                            break;
                    }

                    if(i == temp.item.length() ){
                        inputType = 17;
                    }
                    if(comm){
                        comm = false;
                        break;
                    }
                }
                currentState = 0;
            }

        }
        //System.out.println(tokens.toString());


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
            } else if(valT.equals("$assign")) {
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
                curr.address = codeSeg;
                codeSeg += 2;
                symbols.add(curr);
            }
            if(oldState == 7){
                curr.segment = false;
                curr.type = "CONSTVAR";
                curr.item = tokens.get(iteration-3).item;
                curr.address = dataSeg;
                curr.value = tokens.get(iteration-1).item;
                dataSeg += 2;
                symbols.add(curr);
            }
            if(oldState == 9){
                curr.segment = false;
                curr.type = "VAR";
                curr.item = tokens.get(iteration-1).item;
                curr.address = dataSeg;
                dataSeg += 2;
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
                    curr.address = dataSeg;
                    dataSeg += 2;
                    symbols.add(curr);
                }
            }

            if(currentState == -1) {
                System.out.println("At " + oldState + " GOT " + valT + " " + inputType);
                System.out.println("SOMETHING IS NOT PROFESSIONAL GRADE!");
                break;
            }

            iteration++;
        }
        //System.out.println(symbols.toString());

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
