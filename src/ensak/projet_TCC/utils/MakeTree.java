package ensak.projet_TCC.utils;

import ensak.projet_TCC.modeles.LeafNode;
import ensak.projet_TCC.modeles.Node;

import java.util.*;

public class MakeTree {
    private int leafNodeID = 0;

    /**
     * pile de tous les symboles et des operateurs
     */
    private final Stack<Node> nodes = new Stack<>();
    private final Stack<Character> operators = new Stack<>();

    /**
     * l'alphabet des expressions regulieres et des operateurs
     */
    private final Set<Character> alphabet = new HashSet<>();
    private final ArrayList<Character> allOperators = new ArrayList<>();

    /**
     * methode permettant de transformer une expression reguliere
     * en arbre binaire de recherche
     * @param regularExpression represente l'expression reguliere a tranformer
     * @return
     */
    public Node treeGenerator(String regularExpression)
    {
        Character[] operators = {'*', '|', '&'};
        allOperators.addAll(Arrays.asList(operators));
        // caracteres acceptables
        Character[] character = new Character[26 + 26];
        for (int i = 65; i<= 90; i++)
        {
            character[i - 65] = (char) i;
            character[i - 65 + 26] = (char) (i + 32);
        }

        Character[] integers = {'0','1','2','3','4','5','6','7','8','9'};
        Character[] others = {'#','\\','=','_','.','*','/','+','-',' ', '(',')'};
        alphabet.addAll(Arrays.asList(character));
        alphabet.addAll(Arrays.asList(integers));
        alphabet.addAll(Arrays.asList(others));
        regularExpression = adaptRegularExpression(regularExpression);

        nodes.clear();
        this.operators.clear();

        boolean isSymbol = false;
        int size = this.operators.size();

        for (int i = 0; i < regularExpression.length(); i++)
        {
            if(regularExpression.charAt(i) == '\\')
            {
                isSymbol = true;
                continue;
            }
            if (isSymbol || isAcceptableCharactar(regularExpression.charAt(i)))
            {
                if (isSymbol)
                {
                    pushNodeStack("\\" + regularExpression.charAt(i));
                }
                else {
                    pushNodeStack(Character.toString(regularExpression.charAt(i)));
                }
                isSymbol = false;
            }
            else if (this.operators.isEmpty()){
                this.operators.push(regularExpression.charAt(i));
            }else if (regularExpression.charAt(i) == '(')
            {
                this.operators.push(regularExpression.charAt(i));
            }else if (regularExpression.charAt(i) == ')')
            {

                while (this.operators.get(size -1) != '(')
                {
                    realizeOperation();
                }
                this.operators.pop();
            }
            else {
                while (!this.operators.isEmpty() && priorityOperator(regularExpression.charAt(i),this.operators.get(size -1)))
                {
                    realizeOperation();
                }
                this.operators.push(regularExpression.charAt(i));
            }
        }
        while (this.operators.isEmpty())
            realizeOperation();

        return nodes.pop();
    }


    /**
     * en fonction de la priorite des operateur cette methode fait une
     * comparaision entre deux operateurs et dit si le premier operateur
     * est prioritaire par rapport au second ou nom
     * @param firstCaracter premier operateur
     * @param secondCaracter second operateur
     * @return
     */
    private boolean priorityOperator(char firstCaracter, Character secondCaracter)
    {
        if (firstCaracter == secondCaracter)
            return true;
        if (firstCaracter == '*')
            return false;
        if (secondCaracter == '*')
            return true;
        if (firstCaracter == '&')
            return false;
        if (secondCaracter == '&')
            return true;
        return firstCaracter != '|';
    }

    /**
     * methode permettant de realiser les differentes operations
     * que doivent faire les opeateurs
     */
    private void realizeOperation()
    {
        if(this.operators.size() > 0)
        {
            char operator = operators.pop();
            switch (operator) {
                case ('|') -> union();
                case ('&') -> concatenation();
                case ('*') -> star();
                default -> System.out.println("Le l'operateur" + operator + " est inconnu");
            }
        }
    }

    /**
     * operation etoile (*)
     */
    private void star()
    {
        Node node = nodes.pop();
        Node root = new Node("*");
        root.setLeft(node);
        root.setRight(null);
        node.setParent(root);
        nodes.push(root);
    }

    /**
     * realisation de l'operation de concatenation
     */

    private void concatenation()
    {
        Node node1 = nodes.pop();
        Node node2 = nodes.pop();
        Node root = new Node("&");
        root.setLeft(node1);
        root.setRight(node2);
        node1.setParent(root);
        node2.setParent(root);
        nodes.push(root);
    }

    /**
     * realisation de l'operation d'union
     */

    private void union()
    {
        Node node1 = nodes.pop();
        Node node2 = nodes.pop();
        Node root = new Node("|");
        root.setLeft(node1);
        root.setRight(node2);
        node1.setParent(root);
        node2.setParent(root);
        nodes.push(root);
    }


    /**
     * methode permettant de mettre tous les symboles
     * dans la piles des noeuds
     * @param symbol
     */
    private void pushNodeStack(String symbol)
    {
        Node node = new LeafNode(symbol, ++leafNodeID);
        node.setRight(null);
        node.setLeft(null);
        nodes.push(node);
    }

    /**
     * methode permettant d'adapter l'expression reguliere qui sera entré par
     * l'utilisateur ceci pour en faciliter la manipulation
     * @param regularExpression
     * @return
     */
    private String adaptRegularExpression(String regularExpression)
    {
        // creation d'une chaine de caractere vide
        String newRegularExpression = new  String("");

        for (int i = 0; i < regularExpression.length() -1; i++)
        {
            if (regularExpression.charAt(i) == '\\' && isAcceptableCharactar(regularExpression.charAt(i+1)))
            {
                newRegularExpression += regularExpression.charAt(i);
            }
            else if (regularExpression.charAt(i) == '\\' && regularExpression.charAt(i+1) == '(')
            {
                newRegularExpression += regularExpression.charAt(i);
            }
            else if ((isAcceptableCharactar(regularExpression.charAt(i)) || (regularExpression.charAt(i) == '(' && i>0
            && regularExpression.charAt(i-1) == '\\')) && isAcceptableCharactar(regularExpression.charAt(i+1)))
            {
                newRegularExpression += regularExpression.charAt(i) + "&";
            }else if ((isAcceptableCharactar(regularExpression.charAt(i)) || (regularExpression.charAt(i) == '('
            && i > 0 && regularExpression.charAt(i -1) == '\\')) && regularExpression.charAt(i +1) == '(')
            {
                newRegularExpression += regularExpression.charAt(i) + "&";
            }
            else if (regularExpression.charAt(i) == ')' && isAcceptableCharactar(regularExpression.charAt(i+1)))
            {
                newRegularExpression += regularExpression.charAt(i) + "&";
            }
            else if (regularExpression.charAt(i) == '*' && isAcceptableCharactar(regularExpression.charAt(i+1)))
            {
                newRegularExpression += regularExpression.charAt(i) + "&";
            }
            else if (regularExpression.charAt(i) == '*' && regularExpression.charAt(i+1)== '(')
            {
                newRegularExpression += regularExpression.charAt(i) + "&";
            }
            else if (regularExpression.charAt(i) == ')' && regularExpression.charAt(i+1) == '(')
            {
                newRegularExpression += regularExpression.charAt(i) + "&";
            }
            else
            {
                newRegularExpression += regularExpression.charAt(i);
            }

        }
        newRegularExpression += regularExpression.charAt(regularExpression.length() -1);

        return newRegularExpression;
    }

    /**
     * teste si un caractere est contnue dans l'ensemble des caracteres
     * acceptables
     * @param charactar le caractere a tester
     * @return
     */
    private boolean isAcceptableCharactar(char charactar)
    {
        if (allOperators.contains(charactar))
            return false;
        for (Character c: alphabet)
        {
            if (c == charactar && charactar != '(' && charactar != ')')
                return true;
        }
        return false;
    }

    /**
     * methode permmettant de faire une lecture en inorder
     * on lit toujours les fils les plus a gauche avant de lire la droite
     * de facon recursive
     * @param node l'arbre que l'on veut lire
     */
    public void printInorderTree(Node node)
    {
        if (node ==null)
        {
            return;
        }
        printInorderTree(node.getLeft());
        printInorderTree(node.getRight());
    }

    public int getLeafNodeID() {
        return leafNodeID;
    }

    public void setLeafNodeID(int leafNodeID) {
        this.leafNodeID = leafNodeID;
    }
}
