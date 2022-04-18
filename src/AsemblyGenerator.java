import java.io.*;
import java.util.*;

public class AsemblyGenerator{

    static void generateAssembly() throws Exception{
        Scanner sc = new Scanner(new File("tokens.txt"));
        Scanner prelim = new Scanner(new File("IONASM.txt"));
        PrintWriter writer = new PrintWriter("assembly.asm", "UTF-8");

        String[] read = {"","","","",""};
        int endCounts = 0;
        boolean dont = false;

        while(!read[0].equals("STOP")) {
            writer.println(read[0]);
            read[0] = prelim.nextLine();
        }
        read[0] = "";

        while(!read[0].equals("!END!") && endCounts < 1){
            read[0] = sc.nextLine();
            if(read[0].equals("!END!")){
                endCounts++;
            }
        }
        read[0] = "";

        for(int i = 0; i < 6; i++){
            sc.nextLine();
        }

        while(sc.hasNext()){
            read[0] = sc.next();
            if(read[0].equals("!END!")){
                break;
            }
            read[1] = sc.next();
            read[2] = sc.next();
            read[3] = sc.next();
            read[4] = sc.next();
            if(read[1].equals("CONSTVAR") || read[1].equals("VAR")){
                writer.println(String.format("\t%-10s DW %-10s", read[0], read[2].equals("-")? "0":read[2]));
            }
        }
        read[0] = "";
        for(int i = 0; i < 3; i++){
            writer.println(String.format("\t%-10s DW %-10s", "T"+(i+1), "0"));
        }


        while(!read[0].equals("STOP")) {
            writer.println(read[0]);
            read[0] = prelim.nextLine();
        }
        read[0] = "";

        sc.close();
        sc = new Scanner(new File("quads.txt"));
        while(sc.hasNext()){
            if(!dont){
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
            }
            dont = false;

            if(read[0].equals("PROCEDURE")){
                writer.println(read[1] + ": nop");
            } else if(read[0].equals("GET")){
                writer.println("\tcall PrintString");
                writer.println("\tcall GetAnInteger");
                writer.println("\tmov ax, [ReadInt]");
                writer.println("\tmov [" + read[1] + "], ax");
            } else if(read[0].equals("PUT")){
                writer.println("\tmov ax, [" + read[1] + "]");
                writer.println("\tcall ConvertIntegerToString");
                writer.println("\tmov eax, 4");
                writer.println("\tmov ebx, 1");
                writer.println("\tmov ecx, Result");
                writer.println("\tmov edx, ResultEnd");
                writer.println("\tint 80h");
            }else if(read[0].equals("+")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tadd ax, " + read[2] + "");
                else
                    writer.println("\tadd ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tadd ax, [" + read[2] + "]");
                writer.println("\tmov [" + read[3] +  "], ax");
            }else if(read[0].equals("-")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tsub ax, " + read[2] + "");
                else
                    writer.println("\tsub ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tsub ax, [" + read[2] + "]");
                writer.println("\tmov [" + read[3] +  "], ax");
            }else if(read[0].equals("*")){
//                writer.println("\txor edx, edx");
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov al, " + read[1] + "");
                else
                    writer.println("\tmov al, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tmov bl, " + read[2] + "");
                else
                    writer.println("\tmov bl, [" + read[2] + "]");
//                writer.println("\tmov al, [" + read[1] + "]");
//                writer.println("\tmov bl, [" + read[2] + "]");
//                if(Character.isDigit(read[2].charAt(0)))
//                    writer.println("\tmul " + read[2] + "");
//                else
//                    writer.println("\tmul [" + read[2] + "]");
                writer.println("\tmul bl");
                writer.println("\tmov [" + read[3] +  "], al");
            }else if(read[0].equals("/")){
//                writer.println("\txor edx, edx");
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov al, " + read[1] + "");
                else
                    writer.println("\tmov al, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tmov bl, " + read[2] + "");
                else
                    writer.println("\tmov bl,[" + read[2] + "]");
//                writer.println("\tmov al, [" + read[1] + "]");
//                writer.println("\tmov bl, [" + read[2] + "]");
//                if(Character.isDigit(read[2].charAt(0)))
//                    writer.println("\tdiv " + read[2] + "");
//                else
//                    writer.println("\tdiv [" + read[2] + "]");
                writer.println("\tdiv bl");
                writer.println("\tmov [" + read[3] +  "], al");
            }else if(read[0].equals("RETURN")){
                writer.println("\tret");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                if(read[0].equals("PROCEDURE")){
                    writer.println(read[1] + ": nop");
                } else {
                    writer.println("_start: nop");
                    dont = true;
                }
            }else if(read[0].equals("=")){
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tmov ax, " + read[2] + "");
                else
                    writer.println("\tmov ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[2] + "]");
                writer.println("\tmov [" + read[1] +  "], ax");
            }else if(read[0].equals(">")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tcmp ax, " + read[2] + "");
                else
                    writer.println("\tcmp ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[2] + "]");
//                writer.println("\tmov [" + read[1] +  "], ax");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tcmp ax, [" + read[2] + "]");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                writer.println("\tjle " + read[1]);
            }else if(read[0].equals("<")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tcmp ax, " + read[2] + "");
                else
                    writer.println("\tcmp ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tcmp ax, [" + read[2] + "]");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                writer.println("\tjge " + read[1]);
            }else if(read[0].equals(">=")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tcmp ax, " + read[2] + "");
                else
                    writer.println("\tcmp ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tcmp ax, [" + read[2] + "]");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                writer.println("\tjl " + read[1]);
            }else if(read[0].equals("<=")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tcmp ax, " + read[2] + "");
                else
                    writer.println("\tcmp ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tcmp ax, [" + read[2] + "]");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                writer.println("\tjg " + read[1]);
            }else if(read[0].equals("==")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tcmp ax, " + read[2] + "");
                else
                    writer.println("\tcmp ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tcmp ax, [" + read[2] + "]");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                writer.println("\tjne " + read[1]);
            }else if(read[0].equals("!=")){
                if(Character.isDigit(read[1].charAt(0)))
                    writer.println("\tmov ax, " + read[1] + "");
                else
                    writer.println("\tmov ax, [" + read[1] + "]");
                if(Character.isDigit(read[2].charAt(0)))
                    writer.println("\tcmp ax, " + read[2] + "");
                else
                    writer.println("\tcmp ax, [" + read[2] + "]");
//                writer.println("\tmov ax, [" + read[1] + "]");
//                writer.println("\tcmp ax, [" + read[2] + "]");
                read[0] = sc.next();
                read[1] = sc.next();
                read[2] = sc.next();
                read[3] = sc.next();

                read[0] = read[0].substring(0,read[0].length()-1);
                read[1] = read[1].substring(0,read[1].length()-1);
                read[2] = read[2].substring(0,read[2].length()-1);
                read[3] = read[3].substring(0,read[3].length()-1);
                writer.println("\tje " + read[1]);
            }else if(read[0].equals("WHILE")){
                writer.println("\t"+read[1] + ": nop");
            }else if(read[0].equals("JUMP")){
                writer.println("\tjmp " + read[1]);
            }else if(read[0].equals("CALL")){
                writer.println("\tcall " + read[1]);
            }else if(read[0].startsWith("L")){
                writer.println("\t" + read[0] + ":");
            }
        }
        read[0] = "";

        while(!read[0].equals("STOP")) {
            writer.println(read[0]);
            read[0] = prelim.nextLine();
        }
        read[0] = "";

        writer.close();
    }
}
