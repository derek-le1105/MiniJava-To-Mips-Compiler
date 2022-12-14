import cs132.util.ProblemException;
import cs132.vapor.ast.VFunction;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

import java.io.*;
import java.util.*;

public class VM2M {
    public static String fileOutput = "";
    public static void main(String[] args) {
        processStream(System.in);
    }

    public static void processStream(InputStream var0) {
        try {
            VaporProgram program = parseVapor(System.in, System.err);
            Printer print = new Printer();

            fileOutput += print.printDataSeg(program.dataSegments);
            for (VFunction func : program.functions) {
                fileOutput += print.printFunction(func);
            }
            fileOutput += print.printEnd();
            printFile(fileOutput);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void printFile(String s){
        try{
            FileWriter writer = new FileWriter("Phase 4\\src\\P.txt");
            writer.write(s);
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
        Op[] ops = {
                Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
                Op.PrintIntS, Op.HeapAllocZ, Op.Error,
        };

        boolean allowLocals = false;
        String[] registers = {
                "v0", "v1",
                "a0", "a1", "a2", "a3",
                "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
                "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
                "t8",
        };
        boolean allowStack = true;

        VaporProgram tree;
        try {
            tree = VaporParser.run(new InputStreamReader(in), 1, 1,
                    java.util.Arrays.asList(ops),
                    allowLocals, registers, allowStack);
        }
        catch (ProblemException ex) {
            err.println(ex.getMessage());
            return null;
        }

        return tree;
    }
}
