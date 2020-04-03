package util.graph;

import util.graph.*;
import util.intset.*;
import java.util.*;
import java.io.*;

public class ColorGraph {
    public  Graph          G;
    public  int            R;
    public  int            K;   //nb max color
    private Stack<Integer> pile;
    public  IntSet         removed;
    public  IntSet         spill;
    public  int[]          couleur;
    public  Node[]         int2Node;
    static  int            NOCOLOR = -1;

    public ColorGraph(Graph G, int K, int[] phi){
        this.G       = G;
        this.K       = K;
        pile         = new Stack<Integer>();
        R            = G.nodeCount();
        couleur      = new int[R];
        removed      = new IntSet(R);
        spill        = new IntSet(R);
        int2Node     = G.nodeArray();

        for(int v=0; v < R; v++){
            int preColor = phi[v];
            if(preColor >= 0 && preColor < K) couleur[v] = phi[v];
            else couleur[v] = NOCOLOR;
        }

        simplification();
        selection();
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* associe une couleur à tous les sommets se trouvant dans la pile */
    /*-------------------------------------------------------------------------------------------------------------*/

    public void selection() {
        while (pile.size() != 0){
            int s = pile.pop();
            removed.remove(s);
            if (couleur[s] == NOCOLOR && couleursVoisins(s).getSize() != K)
                couleur[s] = choisisCouleur(couleursVoisins(s));
        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* récupère les couleurs des voisins de t */
    /*-------------------------------------------------------------------------------------------------------------*/

    public IntSet couleursVoisins(int t) {
        IntSet intSet = new IntSet(K);
        Node node = int2Node[t];
        NodeList nodeList = node.pred();

        while (nodeList != null){ //pred to visit
            if (couleur[nodeList.head.mykey] >= 0) intSet.add(couleur[nodeList.head.mykey]);
            nodeList = nodeList.tail;
        }
        return intSet;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* recherche une couleur absente de colorSet */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int choisisCouleur(IntSet colorSet) {
        for (int i=0; i<K; i++)
            if (!(colorSet.isMember(i))) return i;
        return -1;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* calcule le nombre de voisins du sommet t */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int nbVoisins(int t) {//du sommet t
        int nbVoisins = 0;
        Node node = int2Node[t];
        NodeList nodeList = node.pred();

        while (nodeList != null){
            //si le voisin existe toujours
            if (!removed.isMember(nodeList.head.mykey)) nbVoisins++;
            nodeList = nodeList.tail;
        }
        return nbVoisins;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* simplifie le graphe d'interférence g                                                                        */
    /* la simplification consiste à enlever du graphe les temporaires qui ont moins de k voisins                   */
    /* et à les mettre dans une pile                                                                               */
    /* à la fin du processus, le graphe peut ne pas être vide, il s'agit des temporaires qui ont au moins k voisin */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int simplification() {
        boolean bool = true;

        while (bool && pile.size() != R){
            for (int i=0; i < R; i++){
                if (!pile.contains(i) && nbVoisins(i) < K){
                    pile.push(i);
                    removed.add(i);
                }
                else if (pile.contains(i)){
                    bool = false;
                    debordement();
                }
            }
        }
        return 1;
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/

    public int notInPile(){
        for (int i=0; i < R; i++)
            if (!pile.contains(i)) return i;
        return R;
    }

    public void debordement() {
        while (pile.size() != R){
            int s = notInPile();
            pile.push(s);
            removed.add(s);
            spill.add(s);
            simplification();
        }
    }


    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/

    public void coloration()
    {
        this.simplification();
        this.debordement();
        this.selection();
    }

    void affiche()
    {
        System.out.println("vertex\tcolor");
        for(int i = 0; i < R; i++){
            System.out.println(i + "\t" + couleur[i]);
        }
    }
}