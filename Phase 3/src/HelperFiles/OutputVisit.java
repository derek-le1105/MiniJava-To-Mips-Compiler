package HelperFiles;

import cs132.vapor.ast.*;

public class OutputVisit extends VInstr.VisitorR<String, RuntimeException> {

    public AllocationMap map;
    public String tab;
    public int local;

    public OutputVisit(AllocationMap m, String indent, int l){
        map = m;
        tab = indent;
        local = l;
    }

    @Override
    public String visit(VAssign vAssign) throws RuntimeException {

        Register varReg = map.registerHashMap.get(vAssign.dest.toString());
        String LHS = varReg.register;
        String RHS = (vAssign.source instanceof  VVarRef) ? map.registerHashMap.get(vAssign.source.toString()).register : vAssign.source.toString();

        return LHS + " = " + RHS;
    }

    @Override
    public String visit(VCall vCall) throws RuntimeException {
        String output = "";
        String[] aRegisters = {"$a0", "$a1", "$a2", "$a3"};
        for(int i = 0; i < vCall.args.length; ++i){
            if(i < 4){  //store parameters in $a registers up to 4
                if(i != 0)
                    output += tab;
                if(map.registerHashMap.containsKey(vCall.args[i].toString())){
                    output += aRegisters[i] + " = " + map.registerHashMap.get(vCall.args[i].toString()).register + "\n";
                }
                else{
                    output += aRegisters[i] + " = " + vCall.args[i].toString() + "\n";
                }
            }
            else {  //when # of parameters > 4
                output += tab + "out[" + Integer.toString(i - 4) + "] = ";
                if(map.registerHashMap.containsKey(vCall.args[i].toString()))
                    output += map.registerHashMap.get(vCall.args[i].toString()).register + "\n";
                else
                    output += vCall.args[i].toString() + "\n";
            }
        }

        String labelInstanceOf = (vCall.addr instanceof VAddr.Label) ? vCall.addr.toString() :
                map.registerHashMap.get(vCall.addr.toString()).register;

        output += tab + "call " + labelInstanceOf + "\n";

        String getter = map.registerHashMap.get(vCall.dest.toString()).register;
        if(getter != null){
            output += tab + getter + " = $v0";
        }

        //output += tab + map.registerHashMap.get(vCall.dest.toString()).register + " = $v0"; //fix
        return output;
    }

    @Override
    public String visit(VBuiltIn vBuiltIn) throws RuntimeException {
        if(vBuiltIn.dest != null) { //null when Error or no LHS
            String LHS = "";
            String RHS = "";
            Register varReg = map.registerHashMap.get(vBuiltIn.dest.toString());
            LHS = varReg.register;
            RHS = vBuiltIn.op.name + "(";
            for(VOperand arg: vBuiltIn.args){
                if(map.registerHashMap.containsKey(arg.toString())){
                    RHS += map.registerHashMap.get(arg.toString()).register;
                }
                else
                    RHS += arg.toString();
                if(arg != vBuiltIn.args[vBuiltIn.args.length-1])
                    RHS += " ";
            }
            RHS += ")";
            return LHS + " = " + RHS;
        }
        else{
            String operand = vBuiltIn.args[0].toString();
            String arg = (map.registerHashMap.containsKey(operand)) ? map.registerHashMap.get(operand).register : operand;
            String errorMsg = String.format(vBuiltIn.op.name + "(%s)", arg);
            return errorMsg;
        }
    }

    @Override
    public String visit(VMemWrite vMemWrite) throws RuntimeException {
        String LHS = "";
        String RHS = "";
        Register dest = map.registerHashMap.get(((VMemRef.Global)vMemWrite.dest).base.toString());
        int offset = ((VMemRef.Global)vMemWrite.dest).byteOffset;
        if(offset > 0){
            LHS = "[" + dest.register + "+" + offset + "]";
        }
        else LHS = String.format("[%s]", dest.register);

        //LHS = String.format("[%s]", dest.register);
        if(map.registerHashMap.containsKey(vMemWrite.source.toString()))
            RHS = map.registerHashMap.get(vMemWrite.source.toString()).register;
        else
            RHS = vMemWrite.source.toString();
        return LHS + " = " + RHS;
    }

    @Override
    public String visit(VMemRead vMemRead) throws RuntimeException {
        Register dest = map.registerHashMap.get(vMemRead.dest.toString());
        Register src = map.registerHashMap.get(((VMemRef.Global)vMemRead.source).base.toString());
        String LHS = dest.register;
        String RHS = String.format("[%s", src.register);
        int offset = ((VMemRef.Global)vMemRead.source).byteOffset;
        if(offset > 0){
            RHS += "+" + offset + "]";
        }
        else{RHS += "]";}
        return LHS + " = " + RHS;
    }

    @Override
    public String visit(VBranch vBranch) throws RuntimeException {
        String ifValue = map.registerHashMap.get(vBranch.value.toString()).register;
        String ifTarget = vBranch.target.toString();
        String ifType = (vBranch.positive) ? "if" : "if0";
        return ifType + " " + ifValue + " goto " + ifTarget;
    }

    @Override
    public String visit(VGoto vGoto) throws RuntimeException {
        return "goto " + vGoto.target.toString();
    }

    @Override
    public String visit(VReturn vReturn) throws RuntimeException {
        String output = "";
        if(vReturn.value != null){
            String retValue = (map.registerHashMap.containsKey(vReturn.value.toString())) ?
                            map.registerHashMap.get(vReturn.value.toString()).register :
                            vReturn.value.toString();
            output += "$v0 = " + retValue + "\n" + tab;
        }

        int i = 0;

        for(i = 0; i < local; i++)
        {
            output += "$s" + i + " = local[" + i + "]\n" + tab;
        }
        return output + "ret";
    }
}
