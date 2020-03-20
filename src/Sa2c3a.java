import c3a.*;
import sa.*;
import ts.Ts;
import ts.TsItemFct;
import ts.TsItemVar;

public class Sa2c3a extends SaDepthFirstVisitor <C3aOperand> {

    private C3a c3a;
    private Ts table;

    public Sa2c3a(SaNode saRoot, Ts table) {
        c3a = new C3a();
        this.table = table;
        //saRoot.accept(this);
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
        TsItemFct fct = table.getFct(node.getNom());
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







    public C3a getC3a() { return c3a; }
}
