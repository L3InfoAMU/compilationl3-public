import sc.analysis.*;
import sc.node.*;
import sa.*;

public class Sc2sa extends DepthFirstAdapter{

    private SaNode returnValue;

    public void caseAListeOptdeclaravarProgramme(AListeOptdeclaravarProgramme node){
        node.getListeOptdeclaravar().apply(this);
        SaLDec listeOptdeclaravar = (SaLDec) this.returnValue;

        node.getListeDeclarafonc().apply(this);
        SaLDec listeDeclarafonc = (SaLDec) this.returnValue;

        this.returnValue = new SaProg(listeOptdeclaravar, listeDeclarafonc);
    }

    public void caseAListeDeclarafoncProgramme(AListeDeclarafoncProgramme node){
        node.getListeDeclarafonc().apply(this);
        SaLDec listeDeclarafonc = (SaLDec) this.returnValue;
        this.returnValue = new SaProg(null, listeDeclarafonc);
    }

    public void caseAListeOptdeclaravar(AListeOptdeclaravar node){
        node.getListeDeclaravar().apply(this);
    }

    public void caseAOneormoreListeDeclarafonc(AOneormoreListeDeclarafonc node){
        node.getDeclarafonc().apply(this);
        SaDecFonc declarafonc = (SaDecFonc) this.returnValue;

        node.getListeDeclarafonc().apply(this);
        SaLDec listeDeclarafonc = (SaLDec) this.returnValue;

        this.returnValue = new SaLDec(declarafonc, listeDeclarafonc);
    }

    public void caseALastListeDeclarafonc(ALastListeDeclarafonc node){
        this.returnValue = null;
    }

    public void caseAWithvardecDeclarafonc(AWithvardecDeclarafonc node){
        String id = node.getId().getText();

        node.getListeparam().apply(this);
        SaLDec listeparam = (SaLDec) this.returnValue;

        node.getListeOptdeclaravar().apply(this);
        SaLDec listeOptdeclaravar = (SaLDec) this.returnValue;

        node.getInstrbloc().apply(this);
        SaInst instrbloc = (SaInst) this.returnValue;

        this.returnValue = new SaDecFonc(id, listeparam, listeOptdeclaravar, instrbloc);
    }

    public void caseANovardecDeclarafonc(ANovardecDeclarafonc node){
        String id = node.getId().getText();

        node.getListeparam().apply(this);
        SaLDec listeparam = (SaLDec) this.returnValue;

        node.getInstrbloc().apply(this);
        SaInst instrbloc = (SaInst) this.returnValue;

        this.returnValue = new SaDecFonc(id, listeparam, null, instrbloc);
    }

    public void caseAWithparamListeparam(AWithparamListeparam node){
        node.getListeDeclaravar().apply(this);
    }

    public void caseAWithoutparamListeparam(AWithoutparamListeparam node){
        this.returnValue = null;
    }

    public void caseAMorethanoneListeDeclaravar(AMorethanoneListeDeclaravar node){
        node.getDeclaravar().apply(this);
        SaDec declaravar = (SaDec) this.returnValue;

        node.getListeDeclaravar2().apply(this);
        SaLDec listeDeclaravar2 = (SaLDec) this.returnValue;

        this.returnValue = new SaLDec(declaravar, listeDeclaravar2);
    }

    public void caseAOneListeDeclaravar(AOneListeDeclaravar node){
        node.getDeclaravar().apply(this);
        SaDec declaravar = (SaDec) this.returnValue;
        this.returnValue = new SaLDec(declaravar, null);
    }

    public void caseAMoreListeDeclaravar2(AMoreListeDeclaravar2 node){
        node.getDeclaravar().apply(this);
        SaDec declaravar = (SaDec) this.returnValue;

        node.getListeDeclaravar2().apply(this);
        SaLDec listeDeclaravar2 = (SaLDec) this.returnValue;

        this.returnValue = new SaLDec(declaravar, listeDeclaravar2);
    }

    public void caseALastListeDeclaravar2(ALastListeDeclaravar2 node){
        node.getDeclaravar().apply(this);
        SaDec declaravar = (SaDec) this.returnValue;
        this.returnValue = new SaLDec(declaravar, null);
    }

    public void caseAEntDeclaravar(AEntDeclaravar node){
        String id = node.getId().getText();
        this.returnValue = new SaDecVar(id);
    }

    public void caseAInttableDeclaravar(AInttableDeclaravar node){
        String id = node.getId().getText();
        int nombre = Integer.parseInt(node.getNb().getText());
        this.returnValue = new SaDecTab(id, nombre);
    }

    public void caseAVarVar(AVarVar node){
        String id = node.getId().getText();
        this.returnValue = new SaVarSimple(id);
    }

    public void caseATableVar(ATableVar node){
        String id = node.getId().getText();
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        this.returnValue = new SaVarIndicee(id, exp);
    }

    public void caseAOrExp(AOrExp node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        node.getExp1().apply(this);
        SaExp exp1 = (SaExp) this.returnValue;

        this.returnValue = new SaExpOr(exp, exp1);
    }

    public void caseAExp1Exp(AExp1Exp node){
        node.getExp1().apply(this);
    }

    public void caseAEtExp1(AEtExp1 node){
        node.getExp1().apply(this);
        SaExp exp1 = (SaExp) this.returnValue;

        node.getExp2().apply(this);
        SaExp exp2 = (SaExp) this.returnValue;

        this.returnValue = new SaExpAnd(exp1, exp2);
    }

    public void caseAExp2Exp1(AExp2Exp1 node){
        node.getExp2().apply(this);
    }

    public void caseAEqualsExp2(AEqualsExp2 node){
        node.getExp2().apply(this);
        SaExp exp2 = (SaExp) this.returnValue;

        node.getExp3().apply(this);
        SaExp exp3 = (SaExp) this.returnValue;

        this.returnValue = new SaExpEqual(exp2, exp3);
    }

    public void caseAInfExp2(AInfExp2 node){
        node.getExp2().apply(this);
        SaExp exp2 = (SaExp) this.returnValue;

        node.getExp3().apply(this);
        SaExp exp3 = (SaExp) this.returnValue;

        this.returnValue = new SaExpInf(exp2, exp3);
    }

    public void caseAExp3Exp2(AExp3Exp2 node){
        node.getExp3().apply(this);
    }

    public void caseAPlusExp3(APlusExp3 node){
        node.getExp3().apply(this);
        SaExp exp3 = (SaExp) this.returnValue;

        node.getExp4().apply(this);
        SaExp exp4 = (SaExp) this.returnValue;

        this.returnValue = new SaExpAdd(exp3, exp4);
    }

    public void caseAMoinsExp3(AMoinsExp3 node){
        node.getExp3().apply(this);
        SaExp exp3 = (SaExp) this.returnValue;

        node.getExp4().apply(this);
        SaExp exp4 = (SaExp) this.returnValue;

        this.returnValue = new SaExpSub(exp3, exp4);
    }

    public void caseAExp4Exp3(AExp4Exp3 node){
        node.getExp4().apply(this);
    }

    public void caseAMultExp4(AMultExp4 node){
        node.getExp4().apply(this);
        SaExp exp4 = (SaExp) this.returnValue;

        node.getExp5().apply(this);
        SaExp exp5 = (SaExp) this.returnValue;

        this.returnValue = new SaExpMult(exp4, exp5);
    }

    public void caseADivExp4(ADivExp4 node){
        node.getExp4().apply(this);
        SaExp exp4 = (SaExp) this.returnValue;

        node.getExp5().apply(this);
        SaExp exp5 = (SaExp) this.returnValue;

        this.returnValue = new SaExpDiv(exp4, exp5);
    }

    public void caseAExp5Exp4(AExp5Exp4 node){
        node.getExp5().apply(this);
    }

    public void caseANegExp5(ANegExp5 node){
        node.getExp5().apply(this);
        SaExp exp5 = (SaExp) this.returnValue;
        this.returnValue = new SaExpNot(exp5);
    }

    public void caseAParenthesisExp5(AParenthesisExp5 node){
        node.getExp().apply(this);
    }

    public void caseANbExp5(ANbExp5 node){
        int nombre = Integer.parseInt(node.getNb().getText());

        this.returnValue = new SaExpInt(nombre);
    }

    public void caseACallExp5(ACallExp5 node){
        node.getCall().apply(this);
        SaAppel call = (SaAppel) this.returnValue;

        this.returnValue = new SaExpAppel(call);
    }

    public void caseAVarExp5(AVarExp5 node){
        node.getVar().apply(this);
        SaVar var = (SaVar) this.returnValue;
        this.returnValue = new SaExpVar(var);
    }

    public void caseAReadExp5(AReadExp5 node){
        this.returnValue = new SaExpLire();
    }

    public void caseAMorethanoneListeExp(AMorethanoneListeExp node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        node.getListeExp2().apply(this);
        SaLExp listeExp2 = (SaLExp) this.returnValue;

        this.returnValue = new SaLExp(exp, listeExp2);
    }

    public void caseANoneListeExp(ANoneListeExp node){
        this.returnValue = null;
    }

    public void caseAMoreListeExp2(AMoreListeExp2 node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        node.getListeExp2().apply(this);
        SaLExp listeExp2 = (SaLExp) this.returnValue;

        this.returnValue = new SaLExp(exp, listeExp2);
    }

    public void caseALastListeExp2(ALastListeExp2 node){
        this.returnValue = null;
    }

    public void caseAAffinstrInstr(AAffinstrInstr node){
        node.getAffinstr().apply(this);
    }

    public void caseAIfinstrInstr(AIfinstrInstr node){
        node.getIfinstr().apply(this);
    }

    public void caseAWhileinstrInstr(AWhileinstrInstr node){
        node.getWhileinstr().apply(this);
    }

    public void caseACallinstrInstr(ACallinstrInstr node){
        node.getCallinstr().apply(this);
    }

    public void caseARetinstrInstr(ARetinstrInstr node){
        node.getRetinstr().apply(this);
    }

    public void caseAWriteinstrInstr(AWriteinstrInstr node){
        node.getWriteinstr().apply(this);
    }

    public void caseAEmptyinstrInstr(AEmptyinstrInstr node){
        node.getEmptyinstr().apply(this);
    }

    public void caseAInstrblocInstr(AInstrblocInstr node){
        node.getInstrbloc().apply(this);
    }

    public void caseAAffinstr(AAffinstr node){
        node.getVar().apply(this);
        SaVar var = (SaVar) this.returnValue;

        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        this.returnValue = new SaInstAffect(var, exp);
    }

    public void caseAIfthenIfinstr(AIfthenIfinstr node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        node.getInstrbloc().apply(this);
        SaInst instr = (SaInst) this.returnValue;

        this.returnValue = new SaInstSi(exp, instr, null);
    }

    public void caseAIfthenelseIfinstr(AIfthenelseIfinstr node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        node.getInstrbloc().apply(this);
        SaInst instr = (SaInst) this.returnValue;

        node.getElseinstr().apply(this);
        SaInst elseInstr = (SaInst) this.returnValue;

        this.returnValue = new SaInstSi(exp, instr, elseInstr);
    }

    public void caseAElseinstr(AElseinstr node){
        node.getInstrbloc().apply(this);
    }

    public void caseAWhileinstr(AWhileinstr node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;

        node.getInstrbloc().apply(this);
        SaInst instr = (SaInst) this.returnValue;

        this.returnValue = new SaInstTantQue(exp, instr);
    }

    public void caseACallinstr(ACallinstr node){
        node.getCall().apply(this);
    }

    public void caseACall(ACall node){
        String id = node.getId().getText();
        node.getListeExp().apply(this);
        SaLExp listeExp = (SaLExp) this.returnValue;

        this.returnValue = new SaAppel(id, listeExp);
    }

    public void caseARetinstr(ARetinstr node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;
        this.returnValue = new SaInstRetour(exp);
    }

    public void caseAWriteinstr(AWriteinstr node){
        node.getExp().apply(this);
        SaExp exp = (SaExp) this.returnValue;
        this.returnValue = new SaInstEcriture(exp);
    }

    public void caseAEmptyinstr(AEmptyinstr node){
        this.returnValue = null;
    }

    public void caseAInstrbloc(AInstrbloc node){
        node.getInstrbloc2().apply(this);
        SaLInst instrbloc2 = (SaLInst) this.returnValue;
        this.returnValue = new SaInstBloc(instrbloc2);
    }

    public void caseAInstrInstrbloc2(AInstrInstrbloc2 node){
        node.getInstr().apply(this);
        SaInst instr = (SaInst) this.returnValue;

        node.getInstrbloc2().apply(this);
        SaLInst instrbloc2 = (SaLInst) this.returnValue;

        this.returnValue = new SaLInst(instr, instrbloc2);
    }

    public void caseANoinstrInstrbloc2(ANoinstrInstrbloc2 node){
        this.returnValue = null;
    }

    public SaNode getRoot(){ return returnValue; }

}