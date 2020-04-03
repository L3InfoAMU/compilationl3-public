import c3a.*;
import sa.*;
import ts.Ts;
import ts.TsItemFct;
import ts.TsItemVar;

public class Sa2c3a extends SaDepthFirstVisitor <C3aOperand> {

    private C3a c3a;

    public Sa2c3a(SaNode saRoot) {
        c3a = new C3a();
        saRoot.accept(this);
    }

    public C3aOperand visit(SaProg node) {
        node.getFonctions().accept(this);
        return null;
    }

    public C3aOperand visit(SaAppel node) {
        C3aFunction c3aFunction = new C3aFunction(node.tsItem);

        if(node.getArguments() != null) {
            node.getArguments().accept(this);
        }
        c3a.ajouteInst(new C3aInstCall(c3aFunction, null, ""));
        return c3aFunction;
    }

    public C3aOperand visit(SaDecFonc node) {
        TsItemFct fct = node.tsItem;
        c3a.ajouteInst(new C3aInstFBegin(fct, ""));

        node.getCorps().accept(this);

        c3a.ajouteInst(new C3aInstFEnd(""));
        return null;
    }

    //On ne se préoccupe pas des déclarations de variable
    public C3aOperand visit(SaDecTab node) {
        return null;
    }

    public C3aOperand visit(SaDecVar node) {
        return null;
    }

    public C3aOperand visit(SaVarIndicee node) {
        C3aOperand indice = node.getIndice().accept(this);
        C3aVar var;

        if(!(indice.getClass().equals(C3aConstant.class) || indice.getClass().equals(C3aTemp.class))) {
            C3aTemp c3aTemp = c3a.newTemp();

            c3a.ajouteInst(new C3aInstAffect(indice, c3aTemp, ""));

            var = new C3aVar((TsItemVar) node.tsItem, c3aTemp);
        }
        else {
            var = new C3aVar((TsItemVar) node.tsItem, indice);
        }
        return var;
    }

    public C3aOperand visit(SaVarSimple node) {
        return new C3aVar((TsItemVar) node.tsItem, null);
    }

    public C3aOperand visit(SaExpInt node) { return new C3aConstant(node.getVal()); }

    public C3aOperand visit(SaExpVar node) { return node.getVar().accept(this); }

    public C3aOperand visit(SaExpAdd node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstAdd(op1, op2, c3aTemp, ""));
        return c3aTemp;
    }

    public C3aOperand visit(SaExpSub node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstSub(op1, op2, c3aTemp, ""));
        return c3aTemp;
    }

    public C3aOperand visit(SaExpMult node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstMult(op1, op2, c3aTemp, ""));
        return c3aTemp;
    }

    public C3aOperand visit(SaExpDiv node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstDiv(op1, op2, c3aTemp, ""));
        return c3aTemp;
    }

    public C3aOperand visit(SaExpAnd node) {
        C3aLabel label = c3a.newAutoLabel();
        C3aLabel label2 = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, c3a.False, label, ""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op2, c3a.False, label, ""));
        c3a.ajouteInst(new C3aInstAffect(c3a.True, c3aTemp, ""));
        c3a.ajouteInst(new C3aInstJump(label2, ""));
        c3a.addLabelToNextInst(label);
        c3a.ajouteInst(new C3aInstAffect(c3a.False, c3aTemp, ""));
        c3a.addLabelToNextInst(label2);

        return c3aTemp;
    }

    public C3aOperand visit(SaExpOr node) {
        C3aLabel label = c3a.newAutoLabel();
        C3aLabel label2 = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstJumpIfNotEqual(op1, c3a.False, label, ""));
        c3a.ajouteInst(new C3aInstJumpIfNotEqual(op2, c3a.False, label, ""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False, c3aTemp, ""));
        c3a.ajouteInst(new C3aInstJump(label2, ""));
        c3a.addLabelToNextInst(label);
        c3a.ajouteInst(new C3aInstAffect(c3a.True, c3aTemp, ""));
        c3a.addLabelToNextInst(label2);

        return c3aTemp;
    }

    public C3aOperand visit(SaExpEqual node) {
        C3aLabel label = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstAffect(c3a.True, c3aTemp, ""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, op2, label, ""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False, c3aTemp, ""));
        c3a.addLabelToNextInst(label);

        return c3aTemp;
    }

    public C3aOperand visit(SaExpInf node) {
        C3aLabel label = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstAffect(c3a.True, c3aTemp, ""));
        c3a.ajouteInst(new C3aInstJumpIfLess(op1, op2, label, ""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False, c3aTemp, ""));
        c3a.addLabelToNextInst(label);

        return c3aTemp;
    }

    public C3aOperand visit(SaExpNot node) {
        C3aLabel label = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aTemp c3aTemp = c3a.newTemp();

        c3a.ajouteInst(new C3aInstAffect(c3a.True, c3aTemp, ""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, c3a.False, label, ""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False, c3aTemp, ""));
        c3a.addLabelToNextInst(label);

        return c3aTemp;
    }

    public C3aOperand visit(SaExpAppel node) {
        C3aTemp c3aTemp = c3a.newTemp();
        C3aFunction function = new C3aFunction(node.getVal().tsItem);

        if(node.getVal().getArguments() != null) {
            node.getVal().getArguments().accept(this);
        }
        c3a.ajouteInst(new C3aInstCall(function, c3aTemp, ""));
        return c3aTemp;
    }

    public C3aOperand visit(SaExpLire node) {
        C3aTemp c3aTemp = c3a.newTemp();
        c3a.ajouteInst(new C3aInstRead(c3aTemp, ""));
        return c3aTemp;
    }

    public C3aOperand visit(SaInstBloc node) {
        node.getVal().accept(this);
        return null;
    }

    public C3aOperand visit(SaInstAffect node) {
        c3a.ajouteInst(new C3aInstAffect(node.getRhs().accept(this), node.getLhs().accept(this), ""));
        return null;
    }

    public C3aOperand visit(SaInstEcriture node) {
        c3a.ajouteInst(new C3aInstWrite(node.getArg().accept(this), ""));
        return null;
    }

    public C3aOperand visit(SaInstRetour node) {
        C3aOperand val = node.getVal().accept(this);
        c3a.ajouteInst(new C3aInstReturn(val, ""));

        return val;
    }

    public C3aOperand visit(SaInstSi node) {
        C3aOperand toTest = node.getTest().accept(this);
        C3aLabel label = c3a.newAutoLabel();
        C3aLabel label2 = c3a.newAutoLabel();

        c3a.ajouteInst(new C3aInstJumpIfEqual(toTest, c3a.False, label, ""));
        node.getAlors().accept(this);
        //Sinon
        if(node.getSinon() != null) {
            c3a.ajouteInst(new C3aInstJump(label2, ""));
        }
        // Pour label:
        c3a.addLabelToNextInst(label);
        if(node.getSinon() != null) {
            node.getSinon().accept(this);
            c3a.addLabelToNextInst(label2);
        }
        return null;
    }

    public C3aOperand visit(SaInstTantQue node) {
        C3aLabel label = c3a.newAutoLabel();
        C3aLabel label2 = c3a.newAutoLabel();

        c3a.addLabelToNextInst(label);

        C3aOperand toTest = node.getTest().accept(this);

        c3a.ajouteInst(new C3aInstJumpIfEqual(toTest, c3a.False, label2, ""));
        node.getFaire().accept(this);

        c3a.ajouteInst(new C3aInstJump(label, ""));
        c3a.addLabelToNextInst(label2);
        return null;
    }

    public C3aOperand visit(SaLDec node) {
        node.getTete().accept(this);

        if(node.getQueue() != null) {
            node.getQueue().accept(this);
        }
        return null;
    }

    public C3aOperand visit(SaLExp node) {
        c3a.ajouteInst(new C3aInstParam(node.getTete().accept(this), ""));

        if(node.getQueue() != null) {
            node.getQueue().accept(this);
        }
        return null;
    }

    public C3aOperand visit(SaLInst node) {
        node.getTete().accept(this);

        if(node.getQueue() != null) {
            node.getQueue().accept(this);
        }
        return null;
    }

    public C3a getC3a() { return c3a; }
}
