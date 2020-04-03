package fg;
import util.graph.*;
import nasm.*;
import util.intset.*;
import java.io.*;
import java.util.*;

public class FgSolution {
    int iterNum;
    public Nasm nasm;
    Fg fg;

    public Map< NasmInst, IntSet> use;
    public Map< NasmInst, IntSet> def;
    public Map< NasmInst, IntSet> in;
    public Map< NasmInst, IntSet> out;

    public FgSolution(Nasm nasm, Fg fg){
        this.nasm = nasm;
        this.fg = fg;
        this.use = new HashMap< NasmInst, IntSet>();
        this.def = new HashMap< NasmInst, IntSet>();
        this.in =  new HashMap< NasmInst, IntSet>();
        this.out = new HashMap< NasmInst, IntSet>();

        initialisation();
    }

    private void initialisation(){
        for(NasmInst nasmInst : nasm.listeInst) {
            initDefAndUse(nasmInst);
        }
        iterNum = 0;
        initInAndOut();
    }

    private void initDefAndUse(NasmInst nasmInst) {
        IntSet def = new IntSet(10);
        IntSet use = new IntSet(10);

        if(nasmInst.source != null) {
            if (nasmInst.srcUse && nasmInst.source.isGeneralRegister()) use.add(((NasmRegister) nasmInst.source).val);
            //NasmAddress
            if (nasmInst.source.getClass().equals(NasmAddress.class)) {
                NasmAddress nasmAddress = (NasmAddress) nasmInst.source;

                if (nasmAddress.base != null && nasmAddress.base.isGeneralRegister()) use.add(((NasmRegister) nasmAddress.base).val);

                if (nasmAddress.offset != null && nasmAddress.offset.isGeneralRegister()) use.add(((NasmRegister) nasmAddress.offset).val);
            }
        }

        if(nasmInst.destination != null) {
            if (nasmInst.destination.isGeneralRegister() && nasmInst.destUse) { use.add(((NasmRegister) nasmInst.destination).val); }

            if (nasmInst.destination.isGeneralRegister() && nasmInst.destDef) def.add(((NasmRegister) nasmInst.destination).val);
        }
        this.use.put(nasmInst, use);
        this.def.put(nasmInst, def);
    }

    private void initInAndOut() {
        Map< NasmInst, IntSet> inPrime = new HashMap< NasmInst, IntSet>();
        Map< NasmInst, IntSet> outPrime = new HashMap< NasmInst, IntSet>();
        boolean test;

        for(NasmInst nasmInst : nasm.listeInst) {
            // On initialise
            in.put(nasmInst, new IntSet(10));
            inPrime.put(nasmInst, new IntSet(10));
            out.put(nasmInst, new IntSet(10));
            outPrime.put(nasmInst, new IntSet(10));
        }
        do {
            for(NasmInst s : nasm.listeInst) {
                inPrime.replace(s, in.get(s));
                outPrime.replace(s, out.get(s));

                IntSet inS = use.get(s).copy();
                IntSet outS = out.get(s).copy();

                outS.minus(def.get(s));
                inS.union(outS);
                in.replace(s, inS);

                Node nodeS = fg.inst2Node.get(s);
                NodeList nodePred = nodeS.pred();   //predecesseur

                while(nodePred != null) {
                    NasmInst nasmInstHead = fg.node2Inst.get(nodePred.head);

                    out.replace(nasmInstHead, out.get(nasmInstHead).union(in.get(s)));
                    nodePred = nodePred.tail;
                }
            }
            test = true;
            for(NasmInst s : nasm.listeInst) {
                if(!(in.get(s).equal(inPrime.get(s)))) test = false;
                if(!(out.get(s).equal(outPrime.get(s)))) test = false;
            }
            this.iterNum++;

        } while(!test);

        this.iterNum--;
    }

    public void affiche(String baseFileName){
        String fileName;
        PrintStream out = System.out;

        if (baseFileName != null){
            try {
                baseFileName = baseFileName;
                fileName = baseFileName + ".fgs";
                out = new PrintStream(fileName);
            }

            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        out.println("iter num = " + iterNum);
        for(NasmInst nasmInst : this.nasm.listeInst){
            out.println("use = "+ this.use.get(nasmInst) + " def = "+ this.def.get(nasmInst) + "\tin = " + this.in.get(nasmInst) + "\t \tout = " + this.out.get(nasmInst) + "\t \t" + nasmInst);
        }
    }
}
