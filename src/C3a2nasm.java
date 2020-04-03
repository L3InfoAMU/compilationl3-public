import c3a.*;
import nasm.*;
import ts.*;

public class C3a2nasm implements C3aVisitor<NasmOperand> {

    private Nasm nasm;
    private Ts table;
    String nameFct;

    //pointer
    private NasmRegister ebp = new NasmRegister(Nasm.REG_EBP);
    //sommet
    private NasmRegister esp = new NasmRegister(Nasm.REG_ESP);

    public C3a2nasm(C3a c3a, Ts table){
        nasm = new Nasm(table);
        this.table = table;
        ebp.colorRegister(Nasm.REG_EBP);
        esp.colorRegister(Nasm.REG_ESP);

        //On reprend les temporaires de c3a
        nasm.setTempCounter(c3a.getTempCounter());

        initialisation();

        for(C3aInst inst : c3a.listeInst) {
            inst.accept(this);
        }
    }

    public void initialisation(){
        //Temporaires minimum
        NasmRegister eax = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);

        NasmRegister ebx = nasm.newRegister();
        ebx.colorRegister(Nasm.REG_EBX);

        //On commence toujours par le main
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("main"), ""));
        nasm.ajouteInst(new NasmMov(null, ebx, new NasmConstant(0), "valeur de retour du programme"));
        nasm.ajouteInst(new NasmMov(null, eax, new NasmConstant(1), ""));
        nasm.ajouteInst(new NasmInt(null, ""));
    }


    @Override
    public NasmOperand visit(C3aInstAdd inst) {
        NasmOperand label = (inst.label !=null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);

        NasmOperand result =  inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, result, op1, ""));
        nasm.ajouteInst(new NasmAdd(null, result, op2, ""));

        return null;
    }

    @Override
    public NasmOperand visit(C3aInstCall inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmSub(label, esp, new NasmConstant(4), "allocation mémoire pour la valeur de retour"));
        nasm.ajouteInst(new NasmCall(null, inst.op1.accept(this), ""));
        nasm.ajouteInst(new NasmPop(null, inst.result.accept(this), "récupération de la valeur de retour"));

        if(table.getFct(inst.op1.toString()).nbArgs > 0) {
            // Les paramètres ne doivent pas occuper de mémoire
            nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(table.getTableLocale(nameFct).nbArg()*4), "désallocation des arguments"));
        }
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFBegin inst) {
        NasmOperand label = new NasmLabel(inst.val.identif);
        nameFct = inst.val.identif;

        nasm.ajouteInst(new NasmPush(label, ebp, "sauvegarde la valeur de ebp"));
        nasm.ajouteInst(new NasmMov(null, ebp, esp, "nouvelle valeur de ebp"));
        nasm.ajouteInst(new NasmSub(null, esp, new NasmConstant(4 * table.getTableLocale(nameFct).nbVar()), "allocation des variables locales"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInst inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfLess inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmCmp(label, inst.op1.accept(this), inst.op2.accept(this), "Alors"));
        nasm.ajouteInst(new NasmJl(null, inst.result.accept(this), "On saute le  bloc si la condition n'est pas réalisée"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstMult inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);
        NasmOperand result = inst.result.accept(this);

        nasm.ajouteInst(new NasmMov(label, result, op1, ""));
        nasm.ajouteInst(new NasmMul(null, result, op2, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstRead inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand result = inst.result.accept(this);

        NasmRegister eax = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);

        // On donne l'adresse des lignes à lire à eax
        nasm.ajouteInst(new NasmMov(label, eax, new NasmConstant(2), ""));

        // readline lit les lignes
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("readline"), ""));

        // atoi remplace la valeur dans eax par l'entier obtenu
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("atoi"), ""));

        nasm.ajouteInst(new NasmMov(null, result, eax, "Résultat dans result"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstSub inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);
        NasmOperand result = inst.result.accept(this);

        // On met la première opérante dans result pour soustraire ensuite
        nasm.ajouteInst(new NasmMov(label, result, op1, ""));
        nasm.ajouteInst(new NasmSub(null, result, op2, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstAffect inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        nasm.ajouteInst(new NasmMov(label, inst.result.accept(this), inst.op1.accept(this), "Affect"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstDiv inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmOperand op1 = inst.op1.accept(this);
        NasmOperand op2 = inst.op2.accept(this);
        NasmOperand result = inst.result.accept(this);

        NasmRegister eax = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);

        nasm.ajouteInst(new NasmMov(label, eax, op1, ""));

        if(op2.getClass().equals(NasmConstant.class)) {
            //Si c'est une constante on la place d'abord dans un temporaire pour pouvoir la diviser
            NasmRegister ecx = nasm.newRegister();
            nasm.ajouteInst(new NasmMov(null, ecx, op2, ""));
            // div : eax par ebx
            nasm.ajouteInst(new NasmDiv(null, ecx, ""));
        }
        else nasm.ajouteInst(new NasmDiv(null, op2, ""));

        nasm.ajouteInst(new NasmMov(null, result, eax, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFEnd inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmAdd(label, esp, new NasmConstant(4 *table.getTableLocale(nameFct).nbVar()), "désallocation des variables locales"));
        nasm.ajouteInst(new NasmPop(null, ebp, "restaure la valeur de ebp"));

        // On retourne la dernière valeur
        nasm.ajouteInst(new NasmRet(null, ""));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfEqual inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmCmp(label, inst.op1.accept(this), inst.op2.accept(this), "JumpIfEqual 1"));
        nasm.ajouteInst(new NasmJe(null, inst.result.accept(this), "JumpIfEqual 2"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfNotEqual inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmCmp(label, inst.op1.accept(this), inst.op2.accept(this), "jumpIfNotEqual 1"));
        nasm.ajouteInst(new NasmJne(null, inst.result.accept(this), "jumpIfNotEqual 2"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJump inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmJmp(label, inst.result.accept(this), "Jump"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstParam inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        nasm.ajouteInst(new NasmPush(label, inst.op1.accept(this), "Param"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstReturn inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;

        NasmAddress nasmAddress = new NasmAddress(ebp, '+', new NasmConstant(2));
        nasm.ajouteInst(new NasmMov(label, nasmAddress, inst.op1.accept(this), "ecriture de la valeur de retour"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstWrite inst) {
        NasmOperand label = (inst.label != null) ? inst.label.accept(this) : null;
        NasmRegister eax = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);

        // On déplace ce qu'on veut écrire dans le registre eax
        nasm.ajouteInst(new NasmMov(label, eax, inst.op1.accept(this), "Write 1"));
        // iprintLF affiche eax
        nasm.ajouteInst(new NasmCall(null, new NasmLabel("iprintLF"), "Write 2"));
        return null;
    }

    @Override
    public NasmOperand visit(C3aConstant oper) { return new NasmConstant(oper.val); }

    @Override
    public NasmOperand visit(C3aLabel oper) { return new NasmLabel(oper.toString()); }

    @Override
    public NasmOperand visit(C3aTemp oper) { return new NasmRegister(oper.num); }

    @Override
    public NasmOperand visit(C3aVar oper) {
        NasmOperand base;
        NasmConstant offset;
        char direction;

        if(oper.item.portee == table) {//GLOBALE
            // On associe une étiquette à la variable dans ce cas là
            base = new NasmLabel(oper.item.identif);
            if(oper.item.getTaille() == 1) return new NasmAddress(base);
        }
        else base = ebp;//LOCALE

        // Une var locale est positionnée dans les adresses inférieures à ebp
        // et une globale dans les adresses supérieurs à ebp
        if(!(oper.item.portee == table || oper.item.isParam)) direction = '-';
        else direction = '+';

        if(oper.item.getTaille() > 1) offset = new NasmConstant(((C3aConstant) oper.index).val);

        else {//PARAM
            if(oper.item.isParam) offset = new NasmConstant(2 + oper.item.portee.nbArg() - oper.item.getAdresse());
            //LOCALE
            else offset = new NasmConstant(1 + oper.item.getAdresse());
        }
        return new NasmAddress(base, direction, offset);
    }

    @Override
    public NasmOperand visit(C3aFunction oper) {
        return new NasmLabel(oper.toString());
    }

    public Nasm getNasm() {
        return nasm;
    }

}
