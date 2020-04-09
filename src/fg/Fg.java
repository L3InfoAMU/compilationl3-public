package fg;
import nasm.*;
import util.graph.*;
import java.util.*;
import java.io.*;

public class Fg implements NasmVisitor <Void> {
    public Nasm nasm;
    public Graph graph;
    Map< NasmInst, Node> inst2Node;
    Map< Node, NasmInst> node2Inst;
    Map< String, NasmInst> label2Inst;

    public Fg(Nasm nasm){
        this.nasm = nasm;
        this.graph = new Graph();

        this.inst2Node = new HashMap< NasmInst, Node>();
        this.node2Inst = new HashMap< Node, NasmInst>();
        this.label2Inst = new HashMap< String, NasmInst>();

        for(NasmInst nasmInst : nasm.listeInst) {
            Node node = graph.newNode();

            inst2Node.put(nasmInst, node);
            node2Inst.put(node, nasmInst);

            if(nasmInst.label != null) label2Inst.put(nasmInst.label.toString(), nasmInst);
        }

        for(NasmInst nasmInst : nasm.listeInst) nasmInst.accept(this);
    }

    private Node getNextNode(NasmInst nasmInst){
        Node node = graph.nodes().head;
        NodeList nodeList = graph.nodes().tail;

        //On cherche sa place
        while (inst2Node.get(nasmInst) != node){
            node = nodeList.head;
            nodeList = nodeList.tail;
        }
        if (nodeList != null) return nodeList.head; //Alors on a un nextNode
        return null;
    }

    /**Créé un arc dans le graphe
     *
     * Si inst est de valeur true, alors il est peut être suivit d'une instruction
     * sin label est de valeur true, alors il est peut être suivit d'une instruction
     * pointée par un label
     * */
    private void createArc(NasmInst nasmInst, boolean inst, boolean label){
        Node from = inst2Node.get(nasmInst);

        if (inst && !label){
            Node to = getNextNode(nasmInst);
            if (to != null) graph.addEdge(from, to);
        }
        else if (!inst && label){
            if (label2Inst.containsKey(nasmInst.address.toString())) {
                Node to = inst2Node.get(label2Inst.get(nasmInst.address.toString()));
                graph.addEdge(from, to);
            }
            else createArc(nasmInst, true, false);
        }
        else {
            createArc(nasmInst, true, false);

            createArc(nasmInst, false, true);
        }
    }

    public void affiche(String baseFileName){
        String fileName;
        PrintStream out = System.out;

        if (baseFileName != null){
            try {
                baseFileName = baseFileName;
                fileName = baseFileName + ".fg";
                out = new PrintStream(fileName);
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        for(NasmInst nasmInst : nasm.listeInst){
            Node n = this.inst2Node.get(nasmInst);
            out.print(n + " : ( ");
            for(NodeList q=n.succ(); q!=null; q=q.tail) {
                out.print(q.head.toString());
                out.print(" ");
            }
            out.println(")\t" + nasmInst);
        }
    }

    public Void visit(NasmAdd inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmCall inst){
        createArc(inst, false, true);
        return null;
    }

    public Void visit(NasmDiv inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmJe inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmJle inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmJne inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmMul inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmOr inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmCmp inst){
        createArc(inst, true,false);
        return null;
    }

    public Void visit(NasmInst inst){//rien
        return null;
    }

    public Void visit(NasmJge inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmJl inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmNot inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmPop inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmRet inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmXor inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmAnd inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmJg inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmJmp inst){
        //Peut être créé grâce à un label ou non
        createArc(inst, true, true);
        return null;
    }

    public Void visit(NasmMov inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmPush inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmSub inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmEmpty inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmInt inst){
        createArc(inst, true, false);
        return null;
    }

    public Void visit(NasmAddress operand){ return null; }

    public Void visit(NasmConstant operand){ return null; }

    public Void visit(NasmLabel operand){ return null; }

    public Void visit(NasmRegister operand){ return null; }

}