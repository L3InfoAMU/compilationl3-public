import org.sablecc.sablecc.analysis.DepthFirstAdapter;
import sc.analysis.*;
import sc.node.*;
import sa.*;

public class Sc2sa extends DepthFirstAdapter{

    private SaNode returnValue;

    public void caseAPlusExp3(APlusExp3 node){

        SaExp op1 =null;
        SaExp op2 =null;

        node.getExp3().apply(this);
        op1 = (SaExp)this.returnValue;

        node.getExp4().apply(this);
        op2 = (SaExp)this.returnValue;

        this.returnValue =newSaExpAdd(op1, op2);
    }

    public SaNode getRoot(){ return returnValue; }


}