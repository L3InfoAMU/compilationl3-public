import c3a.C3a;
import c3a.C3aEval;
import sc.parser.*;
import sc.lexer.*;
import sc.node.*;
import sa.*;
import ts.*;
import java.io.*;

public class Compiler{

    public static void main(String[] args){

		PushbackReader br = null;
		String baseName = null;

		try {

			if (0 < args.length) {
				br = new PushbackReader(new FileReader(args[0]));
				baseName = removeSuffix(args[0], ".l");
			}
			else{
				System.out.println("il manque un argument");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			Parser p = new Parser(new Lexer(br));
			Start tree = p.parse();
			
			System.out.println("[SC]");
			tree.apply(new Sc2Xml(baseName));

			System.out.println("[SA]");
			Sc2sa sc2sa = new Sc2sa();
			tree.apply(sc2sa);
			SaNode saRoot = sc2sa.getRoot();
			new Sa2Xml(saRoot, baseName);


			System.out.println("[TABLE SYMBOLES]");
			Ts table = new Sa2ts(saRoot).getTableGlobale();
			table.afficheTout(baseName);


			/*
			System.out.print("[C3A] ");
			C3a c3a = new Sa2c3a(saRoot, table).getC3a();
			c3a.affiche(baseName);

			System.out.println("[PRINT C3A OUT]");
			C3aEval c3aEval = new C3aEval(c3a, table);
			c3aEval.affiche(baseName);
			 */

			/*
			System.out.println("[C3A]");
			C3a c3a = new Sa2c3a(saRoot, table).getC3a();
			c3a.affiche(baseName);

			System.out.println("[NASM]");
			Nasm nasm = new C3a2nasm(c3a, table).getNasm();
			nasm.affiche(baseName);

			System.out.println("[FLOW GRAPH]");
			Fg fg = new Fg(nasm);
			fg.affiche(baseName);

			System.out.println("[FLOW GRAPH SOLVE]");
			FgSolution fgSolution = new FgSolution(nasm, fg);
			fgSolution.affiche(baseName);*/
			
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
    }


    public static String removeSuffix(final String s, final String suffix){
		if (s != null && suffix != null && s.endsWith(suffix)){
			return s.substring(0, s.length() - suffix.length());
		}
		return s;
    }
    
}
