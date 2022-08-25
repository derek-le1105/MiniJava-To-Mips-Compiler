import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;

import java.util.*;

public class Printer {
    public String dataSeg = "";
    public String dataFunc = "";
    public String ending = "";
    private static final String IDENT = "  ";
    private String ident = "";
    //handles the extra like .data, class with its methods or dataSegments
    public String printDataSeg(VDataSegment[] cov){
        dataSeg += ".data\n\n";
        increaseIdent();
        for(int i = 0; i < cov.length; i++){
            dataSeg += cov[i].ident + ":\n";
            for(int k = 0; k < cov[i].values.length; k++){
                dataSeg += ident + cov[i].values[k].toString().substring(1) + "\n";
            }
            dataSeg += "\n";
        }
        decreaseIdent();
        dataSeg += "\n" + ident + ".text\n\n";
        increaseIdent();
        dataSeg += ident + "jal Main\n";
        dataSeg += ident + "li $v0 10\n";
        dataSeg += ident + "syscall\n\n";
        decreaseIdent();
        return dataSeg;
    }

    public String printFunction(VFunction func){
        dataFunc = "";
        VInstr[] funcBody = func.body;
        Visitor visit = new Visitor();
        //System.out.println(func.ident + ":\n");
        dataFunc += func.ident + ":\n";
        increaseIdent();

        dataFunc += ident + "sw $fp -8($sp)\n";
        dataFunc += ident + "move $fp $sp\n";
        dataFunc += ident + "subu $sp $sp " + Integer.toString((func.stack.out + func.stack.local + 2)*4) + "\n";
        dataFunc += ident + "sw $ra -4($fp)\n";
        for(VCodeLabel label : func.labels){
            System.out.println(label.instrIndex + ": " + label.ident);
        }

        decreaseIdent();//inside Visitor class it does ident already
        int labelCount = 0;
        for(int i = 0; i < funcBody.length; i++){
            VInstr node = funcBody[i];
            if(labelCount < func.labels.length ) {
                if (func.labels[labelCount].instrIndex == i - 1) {
                    dataFunc += func.labels[labelCount].ident + ":\n";
                    labelCount++;
                }
            }
            dataFunc += node.accept(visit);

            //System.out.println(node);
        }
        increaseIdent();

        dataFunc += ident + "lw $ra -4($fp)\n";
        dataFunc += ident + "lw $fp -8($fp)\n";
        dataFunc += ident + "addu $sp $sp " + Integer.toString((func.stack.out + func.stack.local + 2)*4) + "\n";
        dataFunc += ident + "jr $ra\n";
        decreaseIdent();

        dataFunc += "\n";
        return dataFunc;
    }

    public void increaseIdent(){ident += IDENT;}
    public void decreaseIdent(){ident = ident.substring(0, ident.length() - IDENT.length());}

    public String printEnd(){
        ending += "_print:\n";
        increaseIdent();
        ending += ident + "li $v0 1   # syscall: print integer\n";
        ending += ident + "syscall\n";
        ending += ident + "la $a0 _newline\n";
        ending += ident + "li $v0 4   # syscallL print string\n";
        ending += ident + "syscall\n";
        ending += ident + "jr $ra\n\n";

        ending += "_error:\n";
        ending += ident + "li $v0 4   # syscall: print string\n";
        ending += ident + "syscall\n";
        ending += ident + "li $v0 10   # syscall: exit\n";
        ending += ident + "syscall\n\n";

        ending += "_heapAlloc:\n";
        ending += ident + "li $v0 9   # syscall: sbrk\n";
        ending += ident + "syscall\n";
        ending += ident + "jr $ra\n\n";

        ending += ".data\n.align 0\n_newline: .asciiz \"\\n\"\n_str0: .asciiz \"null pointer\\n\"";
        return ending;
    }
}
