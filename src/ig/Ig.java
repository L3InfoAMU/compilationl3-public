package ig;

import fg.*;
import nasm.*;
import util.graph.*;
import util.intset.*;

import java.awt.*;
import java.util.*;
import java.io.*;

public class Ig {
    public Graph graph;
    public FgSolution fgs;
    public int regNb;
    public Nasm nasm;
    public Node int2Node[];

    public ColorGraph colorGraph;
    public int couleur[];

    public Ig(FgSolution fgs){
		this.fgs = fgs;
		this.graph = new Graph();
		this.nasm = fgs.nasm;
		this.regNb = this.nasm.getTempCounter();
		this.int2Node = new Node[regNb];

		this.build();

		couleur = getPrecoloredTemporaries();
		colorGraph = new ColorGraph(graph, regNb, couleur);

		for (int i=0; i<regNb; i++) int2Node[i] = graph.newNode();

		allocateRegisters();
    }

    public void processRegisterCaseInMember(IntSet in, int index){
    	for (int i=index+1; i< regNb; i++){
    		if (in.isMember(i)) graph.addNOEdge(int2Node[index], int2Node[i]);
		}
	}

	public void processRegisterCaseOutMember(IntSet out, int index){
		for (int i=index+1; i< regNb; i++){
			if (out.isMember(i)){
				graph.addEdge(int2Node[index], int2Node[i]);
				graph.addEdge(int2Node[i], int2Node[index]);
			}
		}
	}

    public void processRegister(IntSet in, IntSet out){
		for (int i=0; i< regNb; i++){//registres
			if (in.isMember(i)) processRegisterCaseInMember(in, i);

			if (out.isMember(i)) processRegisterCaseOutMember(out, i);
		}
	}

    public void build(){
    	for (NasmInst nasmInst : nasm.listeInst){//sommets
    		IntSet in = fgs.in.get(nasmInst);
    		IntSet out = fgs.out.get(nasmInst);

    		processRegister(in, out);
		}
    }

    public int[] getPrecoloredTemporaries() {
		int[] couleur = new int[regNb];

		for (int i=0; i<regNb; i++) couleur[i] = -1;

		for (NasmInst nasmInst : nasm.listeInst){

			if (nasmInst.source != null && nasmInst.source.isGeneralRegister()){
				NasmRegister nasmRegister = (NasmRegister) nasmInst.source;

				if (nasmRegister.color != Nasm.REG_UNK)
					couleur[nasmRegister.val] = nasmRegister.color;
			}
			if (nasmInst.destination != null && nasmInst.destination.isGeneralRegister()){
				NasmRegister nasmRegister = (NasmRegister) nasmInst.destination;

				if (nasmRegister.color != Nasm.REG_UNK)
					couleur[nasmRegister.val] = nasmRegister.color;
			}
		}
		return couleur;
    }


    public void allocateRegisters(){
    	for (NasmInst nasmInst : nasm.listeInst){

			if (nasmInst.source != null && nasmInst.source.isGeneralRegister()){
				NasmRegister nasmRegister = (NasmRegister) nasmInst.source;

				if (nasmRegister.color != Nasm.REG_UNK)
					nasmRegister.colorRegister(couleur[nasmRegister.val]);
			}
			if (nasmInst.destination != null && nasmInst.destination.isGeneralRegister()){
				NasmRegister nasmRegister = (NasmRegister) nasmInst.destination;

				if (nasmRegister.color != Nasm.REG_UNK)
					nasmRegister.colorRegister(couleur[nasmRegister.val]);
			}
		}



    }


    public void affiche(String baseFileName){
	String fileName;
	PrintStream out = System.out;
	
	if (baseFileName != null){
	    try {
		baseFileName = baseFileName;
		fileName = baseFileName + ".ig";
		out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
	
	for(int i = 0; i < regNb; i++){
	    Node n = this.int2Node[i];
	    out.print(n + " : ( ");
	    for(NodeList q=n.succ(); q!=null; q=q.tail) {
		out.print(q.head.toString());
		out.print(" ");
	    }
	    out.println(")");
	}
    }
}
	    
    

    
    
