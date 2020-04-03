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

        // Pour chaque instruction nasm, on créer un sommet dans le graph d'analyse
        for(NasmInst nasmInst : nasm.listeInst) {
            // Création du sommet dans le graphe
            Node node = graph.newNode();
            inst2Node.put(nasmInst, node);
            node2Inst.put(node, nasmInst);
            // Si l'instruction est étiquetée
            if(nasmInst.label != null) {
                label2Inst.put(nasmInst.label.toString(), nasmInst);
            }
        }

        // Pour chaque instruction nasm, on créer les arcs dans le graph d'analyse
        for(NasmInst nasmInst : nasm.listeInst) {
            nasmInst.accept(this);
        }
    }

    // Pour une instruction donnée, renvoie le sommet qui suit l'instruction dans le graphe
    private Node getNodeSuccessor(NasmInst inst) {
        // Sommet de l'instruction donnée
        Node fromNode = inst2Node.get(inst);

        // On cherche le sommet de l'instruction suivante
        Node toNode = graph.nodes().head;
        NodeList toNodeSuccessors = graph.nodes().tail;
        // On cherche le sommet courrant dans la liste de sommets
        while(fromNode != toNode) {
            toNode = toNodeSuccessors.head;
            toNodeSuccessors = toNodeSuccessors.tail;
        }

        // Si ce n'est pas la dernière instruction
        if(toNodeSuccessors != null) {
            // Lorsqu'on l'a trouvé, on sait que le sommet suivant correspond à l'instruction suivante
            toNode = toNodeSuccessors.head;
            // On le renvoie
            return toNode;
        }

        // L'instruction n'a pas de successeur
        return null;
    }

    // Crée un arc entre le sommet de l'instruction donnée et le sommet suivant
    private void createArcNextNode(NasmInst inst) {
        // Sommet de l'instruction donnée
        Node fromNode = inst2Node.get(inst);
        // Sommet de l'instruction suivante
        Node toNode = getNodeSuccessor(inst);

        // Si le successeur n'est pas null, on créer un arc
        if(toNode != null) {
            // On crée un arc dans le graphe
            graph.addEdge(fromNode, toNode);
        }
    }

    // Crée un arc entre le sommet de l'instruction donnée et le sommet vers lequel son étiquette pointe
    private void createArcLabeledNode(NasmInst inst) {
        // Sommet de l'instruction donnée
        Node fromNode = inst2Node.get(inst);

        // Si l'étiquette existe dans le code (si l'appel n'est pas à iprintLF)
        if(label2Inst.containsKey(inst.address.toString())) {
            // On récupère l'instruction liée à l'étiquette
            Node toNode = inst2Node.get(label2Inst.get(inst.address.toString()));
            // On crée un arc dans le graphe
            graph.addEdge(fromNode, toNode);
        }
        // Sinon, dans le cas d'un appel système comme iprintLF, on créer un arc avec le sommet suivant
        else {
            createArcNextNode(inst);
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

    // Instructions

    public Void visit(NasmAdd inst){
        // Une instruction add ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmAnd inst){
        // Une instruction and ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmCall inst){
        // Une instruction call ne peut qu'être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmCmp inst){
        // Une instruction cmp ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmDiv inst){
        // Une instruction div ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmEmpty inst){
        // Une instruction empty ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmInst inst){
        return null;
    }

    public Void visit(NasmInt inst){
        // Une instruction int ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmJe inst){
        // Une instruction je peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmJg inst){
        // Une instruction jg peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmJge inst){
        // Une instruction jge peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmJl inst){
        // Une instruction jl peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmJle inst){
        // Une instruction jle peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmJmp inst){
        // Une instruction jmp peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmJne inst){
        // Une instruction jne peut être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        // Elle peut aussi être suivie par l'instruction pointé par l'étiquette
        createArcLabeledNode(inst);

        return null;
    }

    public Void visit(NasmMov inst){
        // Une instruction mov ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmMul inst){
        // Une instruction mul ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmNot inst){
        // Une instruction not ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmOr inst){
        // Une instruction or ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmPop inst){
        // Une instruction pop ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmPush inst){
        // Une instruction push ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmRet inst){
        // Une instruction ret ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmSub inst){
        // Une instruction sub ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    public Void visit(NasmXor inst){
        // Une instruction xor ne peut qu'être suivie par l'instruction d'après,
        // c'est-à-dire le sommet suivant
        createArcNextNode(inst);

        return null;
    }

    // Opérandes

    public Void visit(NasmAddress operand){
        return null;
    }

    public Void visit(NasmConstant operand){
        return null;
    }

    public Void visit(NasmLabel operand){
        return null;
    }

    public Void visit(NasmRegister operand){
        return null;
    }


}