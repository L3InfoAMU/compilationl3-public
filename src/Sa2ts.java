import sa.*;
import ts.Ts;

public class Sa2ts extends SaDepthFirstVisitor <Void> {

    Ts tableGlobale;
    String nomFctCourrante;
    boolean contextIsParam;

    public Sa2ts(SaNode saRoot) {
        tableGlobale = new Ts();
        nomFctCourrante = null;
        contextIsParam = false;
        saRoot.accept(this);
    }

    public Void visit(SaDecVar node){
        //System.out.println("SaDecVar\nNom du node :" + node.getNom());

        if (nomFctCourrante == null){               //Context GLOBAL
            if (tableGlobale.getVar(node.getNom()) == null){
                node.tsItem = tableGlobale.addVar(node.getNom(), 1);
            }
            else {
                System.out.println("/!\\ Error : variable has been yet declared.");
                System.exit(0);
            }
        }
        else {
            if (contextIsParam){                      //Context PARAM
                if(tableGlobale.getTableLocale(nomFctCourrante).getVar(node.getNom()) == null){
                    node.tsItem = tableGlobale.getTableLocale(nomFctCourrante).addParam(node.getNom());
                }
                else {
                    System.out.println("/!\\ Error : parameter has been yet declared.");
                    System.exit(0);
                }
            }
            else {                                  //Context LOCAL
                if(tableGlobale.getTableLocale(nomFctCourrante).getVar(node.getNom()) == null){
                    node.tsItem = tableGlobale.getTableLocale(nomFctCourrante).addVar(node.getNom(), 1);
                }
                else {
                    System.out.println("/!\\ Error : variable has been yet declared.");
                    System.exit(0);
                }
            }
        }
        return null;
    }

    public Void visit(SaDecTab node){
        //System.out.println("SaDecTab\nNom du node :" + node.getNom());

        if (nomFctCourrante == null){
            if (tableGlobale.getVar(node.getNom()) == null) node.tsItem = tableGlobale.addVar(node.getNom(), node.getTaille());
            else {
                System.out.println("/!\\ Error : variable has been yet declared.");
                System.exit(0);
            }
        }
        else {
            System.out.println("/!\\ Error : array must be declared in a global context.");
            System.exit(0);
        }
        return null;
    }

    public Void visit(SaDecFonc node){
        Ts newTs = new Ts();
        //System.out.println("SaDecFonc\nNom du node :" + node.getNom());

        if (nomFctCourrante != null){
            System.out.println("/!\\ Error : method declared in an other one.");
            System.exit(0);
        }

        if (tableGlobale.getFct(node.getNom()) != null){
            System.out.println("/!\\ Error : method has been yet declared.");
            System.exit(0);
        }
        else
            node.tsItem = tableGlobale.addFct(node.getNom(), node.getParametres() == null ? 0 : node.getParametres().length(), newTs, node);

        nomFctCourrante = node.getNom();

        //System.out.println("---param : ");
        if (node.getParametres() != null){
            contextIsParam = true;
            node.getParametres().accept(this);
        }

        //System.out.println("---var : ");
        if (node.getVariable() != null){
            node.getVariable().accept(this);
        }

        contextIsParam = false;
        //System.out.println("---body : ");
        node.getCorps().accept(this);

        //System.out.println("-----------");
        nomFctCourrante = null;
        return null;
    }


    public Void visit(SaVarSimple node){
        //System.out.println("SaVarSimple\nNom du node :" + node.getNom());

        if (nomFctCourrante != null && tableGlobale.getTableLocale(nomFctCourrante).getVar(node.getNom()) == null && tableGlobale.getVar(node.getNom()) == null) {
            System.out.println("/!\\ Error : Variable used but not declared !");
            System.exit(0);
        }
        else if (nomFctCourrante != null && tableGlobale.getTableLocale(nomFctCourrante).getVar(node.getNom()) != null){
            node.tsItem = tableGlobale.getTableLocale(nomFctCourrante).getVar(node.getNom());
        }
        else if (nomFctCourrante != null && tableGlobale.getVar(node.getNom()) != null){
            node.tsItem = tableGlobale.getVar(node.getNom());
        }

        if (tableGlobale.getVar(node.getNom()) == null && nomFctCourrante == null){
            System.out.println("/!\\ Error : Variable used but not declared !");
            System.exit(0);
        }
        else if (tableGlobale.getVar(node.getNom()) != null && nomFctCourrante == null){
            node.tsItem = tableGlobale.getVar(node.getNom());
        }
        return null;
    }

    public Void visit(SaVarIndicee node){
        //System.out.println("SaVarIndicee\nNom du node :" + node.getNom());

        if (tableGlobale.getVar(node.getNom()) == null) {
            System.out.println("/!\\ ERROR : The variable has not been declared.");
            System.exit(0);
        }
        else if (tableGlobale.getVar(node.getNom()).getTaille() == 1){
            System.out.println("/!\\ Error : Variable must be not indexed !");
            System.exit(0);
        }
        else {
            node.tsItem = tableGlobale.getVar(node.getNom());
        }
        return null;
    }

    public Void visit(SaAppel node) {
        //System.out.println("SaAppel\nNom du node :" + node.getNom());

        if (tableGlobale.getFct(node.getNom()) == null){
            System.out.println("/!\\ Error : Method called but not exists !");
            System.exit(0);
        }
        if (node.getArguments() != null){
            //System.out.println("nb args : " + node.getArguments().length());

            if (tableGlobale.getFct(node.getNom()).getNbArgs() < node.getArguments().length()){
                System.out.println("/!\\ Error : Method called with too much arguments !");
                System.exit(0);
            }
            if (tableGlobale.getFct(node.getNom()).getNbArgs() > node.getArguments().length()){
                System.out.println("/!\\ Error : Method called with not enough arguments !");
                System.exit(0);
            }

            node.getArguments().accept(this);
        }
        node.tsItem = tableGlobale.getFct(node.getNom());
        return null;
    }

    public Ts getTableGlobale() { return tableGlobale; }

}
