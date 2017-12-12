package gui;

import com.sun.jdi.event.ExceptionEvent;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.*;
//import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.*;
import java.io.IOError;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

import static java.awt.Event.MOUSE_DRAG;

public class drawdiag extends JDialog {

    private JPanel contentPane = new JPanel();
    private JButton buttonOK = new JButton();
    private JButton buttonCancel = new JButton();
    private JTextArea textArea = new JTextArea();
    private JButton drawing = new JButton();
    private JButton predictStructureButton = new JButton();
    private JScrollPane DNA = new JScrollPane();
    private JPanel DrawArea = new JPanel();
    private JTextArea legend = new JTextArea();
    private JTextArea legend2 = new JTextArea();
    private JButton RemoveAllNodes = new JButton();
    private JButton buttonOptimize = new JButton();

    private Graph graph;
    private Viewer viewer;
    private View view;

    private int recurse;
    private boolean boo;
    private String rna;
    private String lastrna;
    private String rnaStr;
    private String _cache = "";
    private Edge b_edge;
    private boolean direction = false;
    ArrayList<String> _cacheStructs = new ArrayList<String>();
    ArrayList<String> possStrs = new ArrayList<>();
    ArrayList<String> lastList = new ArrayList<>();


    public drawdiag() {
        rnaStr = "";
        rna = "";
        lastrna = "";
        boo = false;
        recurse = 0;
        Dimension cP = new Dimension(800, 520);
        contentPane.setPreferredSize(cP);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(predictStructureButton);

        DNA.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //textArea.setPreferredSize(null);
        textArea.setEditable(true);
        textArea.setLineWrap(true);

        //textArea.setRows(2);
        DNA.add(textArea);
        DNA.setViewportView(textArea);
        //DNA.setVerticalScrollBar(DNAscroller);


        graph = new GraphicGraph("check");


        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();

        view = viewer.addDefaultView(false);
        view.getCamera().setAutoFitView(false);
        view.getCamera().setGraphViewport(0, 0, 650, 350);
        view.getCamera().setViewCenter(0, 0, 0);
        //view.getCamera().setViewPercent(1);
        view.setMouseManager(new RNAMouseManager());

        DrawArea.setLayout(new BorderLayout());
        DrawArea.setBorder(BorderFactory.createLoweredBevelBorder());
        DrawArea.add((Component) view, BorderLayout.CENTER);

        legend.setEditable(false);
        legend.setBorder(BorderFactory.createEtchedBorder(1));
        legend.append("Delete Node:                                         Alt->LeftClick\n" +
                "Add Edge Btwn Nodes:         Shift + LeftClick->Hover\n" +
                "Mode Node:                                       LeftClick->Drag\n" +
                "Add Node:                                                     LeftClick\n" +
                "Continuous Node Drawing:     Control->Move Cursor");

        legend2.setEditable(false);
        legend2.setBorder(BorderFactory.createLoweredBevelBorder());
        legend2.append("Start Node:                       GREEN\n" +
                "End Node:                         RED\n" +
                "RNA Backbone:                 BLUE\n" +
                "Nucleotide pair bond:       PINK\n\n" +
                "   MAKE SURE START AND END NODES ARE CORRECT");


        //buttonOK.setText("OK");
        buttonCancel.setText("Cancel");
        //drawing.setText("Align");
        predictStructureButton.setText("Predict");
        buttonOptimize.setText("Optimize");
        RemoveAllNodes.setText("Clear Nodes");


        contentPane.setLayout(null);

        contentPane.add(DrawArea);
        DrawArea.setBounds(5, 5, 650, 350); // if set to 0, first node will be placed correctly.

        contentPane.add(DNA);
        DNA.setBounds(5, 360, 650, 50);

        contentPane.add(legend);
        legend.setBounds(5, 415, 350, 100);

        contentPane.add(legend2);
        legend2.setBounds(360, 415, 350, 100);

        //contentPane.add(buttonOK);
        //buttonOK.setBounds(655, 5, 145, 30);

        contentPane.add(buttonCancel);

        //contentPane.add(drawing);
        //drawing.setBounds(655, 40, 145, 30);

        contentPane.add(predictStructureButton);
        predictStructureButton.setBounds(655, 75, 145, 30);

        contentPane.add(RemoveAllNodes);
        RemoveAllNodes.setBounds(655, 110, 145, 30);

        contentPane.add(buttonOptimize);
        buttonOptimize.setBounds(655, 145, 145, 30);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDraw();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        predictStructureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPredict();
            }
        });

        buttonOptimize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!rnaStr.equals("")) {
                    onOptimize();
                }
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        drawing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append("structure");
            }
        });

        RemoveAllNodes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.clear();
                textArea.setText("");
                recurse = 0;
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    private void onDraw() {
    }


    private void onOptimize2(String str) {
        ArrayList<String> First = new ArrayList<>();
        ArrayList<String> Second = new ArrayList<>();
        cleanUp();
        First = possStrs;
        mutate();
        cleanUp();
        Second = possStrs;
        if (First.size() < Second.size() || !Second.contains(rnaStr)) {
            rna = "";



        } else if (Second.size() == 1) {

        } else if (First.size() == Second.size()) {
            /*if (recurse > 5000) {

            } else {

            }*/
        }
    }

    private void onOptimize() {
        cleanUp(); // get structures from sequence, then clean up the ArrayList to readable format [rna, possStrs]

        if (!possStrs.contains(rnaStr)) {
            if (direction) {
                rna = "";
                rna += lastrna;
                possStrs = new ArrayList<>();
                for (String str : lastList) {
                    possStrs.add(str);
                }
            } else {
                if (recurse > 0) {
                    lastrna = "";
                    lastrna += rna;
                    lastList = new ArrayList<>();
                    for (String str : possStrs) {
                        lastList.add(str);
                    }
                } else {
                    lastrna += rna;  //correct
                    for (String str : possStrs) {
                        lastList.add(str);
                    }
                }
            }
            recurse += 1;
            //drawn structure was not one of the generated structures.
            mutate(); // mutate the sequence
            onOptimize(); // call Optimize again
            // recurse
        }
        if (possStrs.contains(rnaStr)){
            if (possStrs.size() < 4) {
                textArea.append("\n");
                for (String str : possStrs) {
                    textArea.append("___" + str);
                }
                // end

            } else if (possStrs.size() == lastList.size()) {
                if (recurse > 5000) {
                    recurse = 0;
                    direction = false;
                    onPredict();
                    onOptimize();
                    // recurse
                } else {
                    System.out.print(possStrs);
                    if (recurse > 0 && direction) {
                        lastrna = "";
                        lastrna += rna;
                        lastList = new ArrayList<>();
                        for (String str : possStrs) {
                            lastList.add(str);
                        }
                    } else {
                        lastrna += rna;
                        for (String str : possStrs) {
                            lastList.add(str);
                        }
                    }
                    recurse += 1;
                    mutate();
                    direction = true;
                    onOptimize();
                    // recurse
                }

            } else {

                if (lastList.size() > possStrs.size() || !direction) { // if it got better
                    if (recurse > 0) {
                        lastrna = "";
                        lastrna += rna;
                        lastList = new ArrayList<>();
                        for (String str : possStrs) {
                            lastList.add(str);
                        }
                    } else {
                        lastrna += rna;
                        for (String str : possStrs) {
                            lastList.add(str);
                        }
                    }
                    recurse += 1;
                    mutate();
                    direction = true;
                    onOptimize();
                    // recurse
                } else { // if it got worse
                    if (recurse > 0 && direction) {
                        rna = "";
                        rna += lastrna;
                    } else {
                        lastrna += rna;  // correct
                        for (String str : possStrs) {  // correct
                            lastList.add(str);
                        }
                    }
                    recurse += 1;
                    mutate();
                    direction = true;
                    onOptimize();
                    // recurse
                }
            }
        }
        if (possStrs.size() > 4) {
            onOptimize();
        }
        textArea.append("\n" + rna);
    }

    private void cleanUp() {
        ArrayList<String> initStrs = findPossibleStructures(rna);
        possStrs = new ArrayList<>();
        for (String str : initStrs) {
            while (str.contains("()")) {
                str = str.replace("()", "..");
            }
            possStrs.add(str);
        }
    }

    private void mutate() {
        ArrayList<Integer> nonbonds = new ArrayList<>();
        ArrayList<Integer> bonds = new ArrayList<>();
        Random r = new Random();

        String nucRepl = "";
        int nucToChange = 0;
        String curNuc = "";

        String secNucRepl = "";
        int secNuc = 0;

        Iterator<GraphicNode> g = RNAMouseManager.graph.getNodeIterator();
        while (g.hasNext()) {
            GraphicNode n = g.next();
            if (!bondCounter(n)) {
                nonbonds.add(Integer.parseInt(n.getId()));
            } else {
                bonds.add(Integer.parseInt(n.getId()));
            }
        }
        if (r.nextInt(2) == 0) {
            nucToChange = bonds.get(r.nextInt(bonds.size()));
            curNuc = rna.substring(nucToChange, nucToChange + 1);
            if (b_edge.toString().split("_")[1].equals(nucToChange + "")) {
                secNuc = Integer.parseInt(b_edge.toString().split("_")[2]);

            } else {
                secNuc = Integer.parseInt(b_edge.toString().split("_")[1]);
            }

            if (curNuc.equals("G")) {
                nucRepl = "C";
                secNucRepl = "G";

            } else if (curNuc.equals("C")) {
                nucRepl = "G";
                secNucRepl = "C";
            }
            if (nucToChange < secNuc) {
                rna = rna.substring(0, nucToChange) + nucRepl + rna.substring(nucToChange + 1, secNuc) + secNucRepl + rna.substring(secNuc + 1);
            } else {
                rna = rna.substring(0, secNuc) + secNucRepl + rna.substring(secNuc + 1, nucToChange) + nucRepl + rna.substring(nucToChange + 1);
            }
        } else {
            // U and C are the best for loops
            nucToChange = nonbonds.get(r.nextInt(nonbonds.size()));
            curNuc = rna.substring(nucToChange, nucToChange + 1);
            if (curNuc.equals("C")) {
                int newNuc = r.nextInt(27);
                if (newNuc < 21) {
                    nucRepl = "U";
                } else if (newNuc < 24) {
                    nucRepl = "G";
                } else if (newNuc < 27) {
                    nucRepl = "A";
                }

            } else if (curNuc.equals("U")) {
                int newNuc = r.nextInt(27);
                if (newNuc < 21) {
                    nucRepl = "C";
                } else if (newNuc < 24) {
                    nucRepl = "G";
                } else if (newNuc < 27) {
                    nucRepl = "A";
                }

            } else if (curNuc.equals("G")) {
                int newNuc = r.nextInt(27);
                if (newNuc < 11) {
                    nucRepl = "U";
                } else if (newNuc < 22) {
                    nucRepl = "C";
                } else if (newNuc < 27) {
                    nucRepl = "A";
                }

            } else if (curNuc.equals("A")) {
                int newNuc = r.nextInt(27);
                if (newNuc < 11) {
                    nucRepl = "U";
                } else if (newNuc < 22) {
                    nucRepl = "C";
                } else if (newNuc < 27) {
                    nucRepl = "G";
                }
            }
            rna = rna.substring(0, nucToChange) + nucRepl + rna.substring(nucToChange + 1);
        }

    }

    private void onPredict() {
        rna = "";
        rnaStr = "";
        Random rand = new Random();
        if (RNAMouseManager.start == null || RNAMouseManager.stop == null) {

        } else {
            //Set<GraphicNode> allnodes = new HashSet<>();
            GraphicGraph rnaGraph = RNAMouseManager.graph;
            //allnodes.addAll(rnaGraph.getNodeSet());
            //rnaGraph.
            Element stt = RNAMouseManager.start;
            Element stp = RNAMouseManager.stop;
            Node stNode = null;
            int rnaLen = rnaGraph.getNodeCount();
            Map<GraphicNode, String> nodeToNuc = new HashMap<>();
            Map<GraphicNode, String> dontVisit = new HashMap<>();
            ArrayList<String> visitedEdges = new ArrayList<>();
            int AUGC = 0;

            //Gets iterator over all nodes.
            //Set<GraphicNode> nodes = rnaGraph.getNodeSet();

            //GraphicNode curNode = rnaGraph.getNode(stt.getId());
            //While there are nodes in the graph.
            for (int bigI = 0; bigI < rnaLen; bigI++) {
                GraphicNode curNode = rnaGraph.getNode(bigI + "");

                    //If it is, make an iterator over all of its edges.
                Iterator<Edge> Nodal = curNode.getEdgeIterator();

                while (Nodal.hasNext()) {

                    /*if (Nodal.size() > 2) {
                        textArea.append("Can not compute this fold.");
                        break;
                    }*/
                    Edge curEdge = Nodal.next();

                    if (curEdge.toString().matches("e_\\d+") /*&& !nodeToNuc.containsKey(curNode)*/) {
                        //String prevNode = (Integer.parseInt(curEdge.toString().split("_")[1]) - 1) + "";
                        if (!dontVisit.containsKey(curNode) && !visitedEdges.contains(curEdge.toString())) {
                            visitedEdges.add(curEdge.toString());
                            dontVisit.put(curNode, curNode.getId());
                            rnaStr += ".";
                            AUGC = rand.nextInt(32);
                            if (bondCounter(curNode)) {
                                if (AUGC < 2) {
                                    rna += "A";
                                    nodeToNuc.put(curNode, "A");
                                } else if (AUGC < 3) {
                                    rna += "U";
                                    nodeToNuc.put(curNode, "U");
                                } else if (AUGC < 17) {
                                    rna += "G";
                                    nodeToNuc.put(curNode, "G");
                                } else if (AUGC < 32) {
                                    rna += "C";
                                    nodeToNuc.put(curNode, "C");
                                }

                            } else {
                                if (AUGC < 13) {
                                    rna += "A";
                                    nodeToNuc.put(curNode, "A");
                                } else if (AUGC < 26) {
                                    rna += "U";
                                    nodeToNuc.put(curNode, "U");
                                } else if (AUGC < 29) {
                                    rna += "G";
                                    nodeToNuc.put(curNode, "G");
                                } else if (AUGC < 32) {
                                    rna += "C";
                                    nodeToNuc.put(curNode, "C");
                                }
                            }
                        }
                    }
                    if (curEdge.toString().matches("b_\\d+_\\d+")) {
                        String bond = curEdge.toString();
                        String[] bonds = bond.split("_");
                        String to;
                        String from;
                        if (curNode.getId().equals(bonds[1])) {
                            to = bonds[2];
                            from = bonds[1];
                        } else {
                            to = bonds[1];
                            from = bonds[2];
                        }
                        GraphicNode pairedNode = rnaGraph.getNode(to);
                        if (dontVisit.containsKey(curNode) && Integer.parseInt(from) - Integer.parseInt(to) < 0) {
                            rnaStr = rnaStr.substring(0, Integer.parseInt(curNode.getId()));
                            rnaStr += "(";
                        }
                        if (dontVisit.containsKey(pairedNode)) {
                            rnaStr += ")";

                            if (nodeToNuc.get(pairedNode).equals("U")) {
                                rna += "A";
                                nodeToNuc.put(curNode, "A");
                            } else if (nodeToNuc.get(pairedNode).equals("A")) {
                                rna += "U";
                                nodeToNuc.put(curNode, "U");
                            } else if (nodeToNuc.get(pairedNode).equals("C")) {
                                rna += "G";
                                nodeToNuc.put(curNode, "G");
                            } else if (nodeToNuc.get(pairedNode).equals("G")) {
                                rna += "C";
                            }
                        } else {
                            dontVisit.put(pairedNode, to);
                            //dontVisit.put(rnaGraph.getNode(from), from);
                        }
                    }
                }
            }
        }
        textArea.append(rna + "\n" + rnaStr);
    }

    private Boolean bondCounter(GraphicNode n) {
        Iterator<Edge> edges = n.getEdgeIterator();
        boolean yes = false;
        while (edges.hasNext()) {
            Edge e = edges.next();
            if (e.toString().contains("b_")) {
                b_edge = e;
                yes = true;
            }
        }
        return yes;
    }

    private void onOK() {
        // add your code here
        textArea.append("Hello World");
        //System.out.println("Hello World");
        //dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private ArrayList<String> findPossibleStructures(String str) {
        ArrayList<String> sols = getStructs();
        return sols;
    }

    public static void main(String[] args) {
        drawdiag dialog = new drawdiag();
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public String getSeq()
    {
        return rna;
    }


    private boolean canBasePairAll(char a, char b)
    {
        return true;
    }

    private boolean canBasePairBasic(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='G'))
            return true;
        if ((a=='U')&&(b=='A'))
            return true;
        if ((a=='A')&&(b=='U'))
            return true;
        if ((a=='G')&&(b=='U'))
            return true;
        if ((a=='U')&&(b=='G'))
            return true;
        return false;
    }

    private double basePairScoreBasic(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return 1.0;
        if ((a=='C')&&(b=='G'))
            return 1.0;
        if ((a=='U')&&(b=='A'))
            return 1.0;
        if ((a=='A')&&(b=='U'))
            return 1.0;
        if ((a=='G')&&(b=='U'))
            return 1.0;
        if ((a=='U')&&(b=='G'))
            return 1.0;
        return Double.NEGATIVE_INFINITY;
    }


    private boolean canBasePairNussinov(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='G'))
            return true;
        if ((a=='U')&&(b=='A'))
            return true;
        if ((a=='A')&&(b=='U'))
            return true;
        if ((a=='U')&&(b=='G'))
            return true;
        if ((a=='G')&&(b=='U'))
            return true;
        return false;
    }

    private double basePairScoreNussinov(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return 3.0;
        if ((a=='C')&&(b=='G'))
            return 3.0;
        if ((a=='U')&&(b=='A'))
            return 2.0;
        if ((a=='A')&&(b=='U'))
            return 2.0;
        if ((a=='U')&&(b=='G'))
            return 1.0;
        if ((a=='G')&&(b=='U'))
            return 1.0;
        return Double.NEGATIVE_INFINITY;
    }

    private boolean canBasePairINRIA(char a, char b)
    {
        if ((a=='U')&&(b=='A'))
            return true;
        if ((a=='A')&&(b=='U'))
            return true;
        if ((a=='G')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='G'))
            return true;

        if ((a=='A')&&(b=='G'))
            return true;
        if ((a=='G')&&(b=='A'))
            return true;
        if ((a=='U')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='U'))
            return true;
        if ((a=='A')&&(b=='A'))
            return true;
        if ((a=='U')&&(b=='U'))
            return true;

        if ((a=='U')&&(b=='G'))
            return true;
        if ((a=='G')&&(b=='U'))
            return true;
        if ((a=='A')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='A'))
            return true;
        return false;
    }

    private double basePairScoreINRIA(char a, char b)
    {
        if ((a=='U')&&(b=='A'))
            return 3;
        if ((a=='A')&&(b=='U'))
            return 3;
        if ((a=='G')&&(b=='C'))
            return 3;
        if ((a=='C')&&(b=='G'))
            return 3;

        if ((a=='A')&&(b=='G'))
            return 2;
        if ((a=='G')&&(b=='A'))
            return 2;
        if ((a=='U')&&(b=='C'))
            return 2;
        if ((a=='C')&&(b=='U'))
            return 2;
        if ((a=='A')&&(b=='A'))
            return 2;
        if ((a=='U')&&(b=='U'))
            return 2;

        if ((a=='U')&&(b=='G'))
            return 1;
        if ((a=='G')&&(b=='U'))
            return 1;
        if ((a=='A')&&(b=='C'))
            return 1;
        if ((a=='C')&&(b=='A'))
            return 1;
        return Double.NEGATIVE_INFINITY;
    }

    private boolean canBasePair(char a, char b)
    {
        return canBasePairBasic(a,b);
        //return canBasePairNussinov(a,b);
        //return canBasePairINRIA(a,b);
    }

    private double basePairScore(char a, char b)
    {
        return basePairScoreBasic(a,b);
        //return basePairScoreNussinov(a,b);
        //return basePairScoreINRIA(a,b);
    }

    public double[][] fillMatrix(String seq)
    {
        int n = seq.length();
        double[][] tab = new double[n][n];
        for(int m=1;m<=n;m++)
        {
            for(int i=0;i<n-m+1;i++)
            {
                int j = i+m-1;
                tab[i][j] = 0;
                if (i<j)
                {
                    tab[i][j] = Math.max(tab[i][j], tab[i+1][j]);
                    for (int k=i+1;k<=j;k++)
                    {
                        if (canBasePair(seq.charAt(i),seq.charAt(k)))
                        {
                            double fact1 = 0;
                            if (k>i+1)
                            {
                                fact1 = tab[i+1][k-1];
                            }
                            double fact2 = 0;
                            if (k<j)
                            {
                                fact2 = tab[k+1][j];
                            }
                            tab[i][j] = Math.max(tab[i][j],basePairScore(seq.charAt(i),seq.charAt(k))+fact1+fact2);
                        }
                    }
                }
            }
        }
        return tab;
    }

    public static ArrayList<Double> combine(double bonus, ArrayList<Double> part1, ArrayList<Double> part2)
    {
        ArrayList<Double> base = new ArrayList<Double>();
        for(double d1: part1)
        {
            for(double d2: part2)
            {
                base.add(bonus+d1+d2);
            }
        }
        return base;
    }

    public static ArrayList<Double> selectBests(ArrayList<Double> base)
    {
        ArrayList<Double> result = new ArrayList<Double>();
        double best = Double.NEGATIVE_INFINITY;
        for(double val: base)
        {
            best = Math.max(val, best);
        }
        for(double val: base)
        {
            if (val == best)
                result.add(val);
        }
        return result;
    }


    private ArrayList<String> backtrack(double[][] tab, String seq)
    {
        return backtrack(tab,seq, 0, seq.length()-1);
    }

    private ArrayList<String> backtrack(double[][] tab, String seq, int i, int j)
    {
        ArrayList<String> resultB = new ArrayList<String>();
        if (i<j)
        {
            ArrayList<Integer> indices = new ArrayList<Integer>();
            indices.add(-1);
            for (int k=i+1;k<=j;k++)
            {
                indices.add(k);
            }
            for (int k : indices)
            {
                if (k==-1)
                {
                    if (tab[i][j] == tab[i+1][j])
                    {
                        for (String s : backtrack(tab, seq, i+1,j))
                        {
                            resultB.add("."+s);
                        }
                    }
                }
                else
                {
                    if (canBasePair(seq.charAt(i),seq.charAt(k)))
                    {
                        double fact1 = 0;
                        if (k>i+1)
                        {
                            fact1 = tab[i+1][k-1];
                        }
                        double fact2 = 0;
                        if (k<j)
                        {
                            fact2 = tab[k+1][j];
                        }
                        if (tab[i][j]==basePairScore(seq.charAt(i),seq.charAt(k))+fact1+fact2)
                        {
                            for (String s1:backtrack(tab, seq, i+1,k-1))
                            {
                                for (String s2:backtrack(tab, seq, k+1,j))
                                {
                                    resultB.add("("+s1+")"+s2);
                                }
                            }
                        }
                    }
                }
            }
        }
        else if  (i==j)
        {
            resultB.add(".");
        }
        else
        {
            resultB.add("");
        }
        return resultB;
    }

    public BigInteger count(String seq)
    {
        int n = seq.length();

        BigInteger[][] tab = new BigInteger[n][n];
        for(int m=1;m<=n;m++)
        {
            for(int i=0;i<n-m+1;i++)
            {
                int j = i+m-1;
                tab[i][j] = BigInteger.ZERO;
                if (i<j)
                {
                    tab[i][j] = tab[i][j].add(tab[i+1][j]);
                    for (int k=i+1;k<=j;k++)
                    {
                        if (canBasePair(seq.charAt(i),seq.charAt(k)))
                        {
                            BigInteger fact1 = BigInteger.ONE;
                            if (k>i+1)
                            {
                                fact1 = tab[i+1][k-1];
                            }
                            BigInteger fact2 = BigInteger.ONE;
                            if (k<j)
                            {
                                fact2 = tab[k+1][j];
                            }
                            tab[i][j] = tab[i][j].add(fact1.multiply(fact2));
                        }
                    }
                }
                else
                {
                    tab[i][j] = BigInteger.ONE;
                }
            }
        }
        return tab[0][n-1];
    }

    public ArrayList<String> getStructs() {
        String seq = getSeq();
        seq = seq.toUpperCase();
        if (!_cache.equals(seq))
        {
            double[][] mfe = fillMatrix(seq);
            _cacheStructs = backtrack(mfe,seq);
            _cache = seq;
        }
        return _cacheStructs;
    }
}
