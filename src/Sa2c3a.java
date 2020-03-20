import c3a.*;
import sa.SaAppel;
import sa.SaDepthFirstVisitor;
import sa.SaNode;
import sa.SaProg;
import ts.Ts;

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




    public C3a getC3a() { return c3a; }
}
