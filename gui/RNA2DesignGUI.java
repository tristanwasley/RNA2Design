package gui;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.*;

public class RNA2DesignGUI extends JDialog {

    private JPanel contentPane;
    private JPanel DrawArea;

    private JScrollPane DNA;
    private JScrollPane currStrs;

    // Where the RNA sequence will show up
    private JTextArea textArea;
    // Where the dot-bracket notation for RNA structure[s] will show up
    private JTextArea dotBracketText;
    private JTextArea legend;
    private JTextArea legend2;

    private JButton buttonCancel;
    private JButton predictStructureButton;
    private JButton RemoveAllNodes;
    private JButton buttonOptimize;

    // The graph that the user draws on, and its viewers.
    private Graph graph;
    private Viewer viewer;
    private View view;

    private int recurse;
    private String rna;
    private String rnaStr;
    // Related to Nussinov Algorithm.
    private String _cache = "";
    private Edge b_edge;
    private ArrayList<String> _cacheStructs = new ArrayList<String>();
    // The current set of dot-bracket structures being assessed.
    private ArrayList<String> possStrs = new ArrayList<>();

    private void initiate() {
        contentPane = new JPanel();
        buttonCancel = new JButton();
        textArea = new JTextArea();
        predictStructureButton = new JButton();
        DNA = new JScrollPane();
        currStrs = new JScrollPane();
        dotBracketText = new JTextArea();
        DrawArea = new JPanel();
        legend = new JTextArea();
        legend2 = new JTextArea();

        RemoveAllNodes = new JButton();
        buttonOptimize = new JButton();


        graph = new GraphicGraph("UsersGraph");
        recurse = 0;
        _cache = "";
        b_edge = null;
        _cacheStructs = new ArrayList<>();
        possStrs = new ArrayList<>();
    }


    public RNA2DesignGUI(String sequence, String structure) {

        // Initialize the rna strand and rna structure
        rna = sequence;
        rnaStr = structure;


        // Initiate all components.
        initiate();

        // Setting up the main panel.
        Dimension cP = new Dimension(800, 520);
        contentPane.setPreferredSize(cP);
        setContentPane(contentPane);
        setModal(true);
        // Highlights the first button to be used.
        getRootPane().setDefaultButton(predictStructureButton);

        // Set up the scroll panes that will contain the RNA text and structures
        DNA.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        currStrs.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //currStrs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Set up the text fields
        dotBracketText.setEditable(false);
        dotBracketText.setLineWrap(true);
        //dotBracketText.setBorder(BorderFactorygetBorder().;
        textArea.setEditable(true);
        textArea.setLineWrap(true);

        // Inlay the text Areas into Scroll panels.
        DNA.add(textArea);
        DNA.setViewportView(textArea);
        currStrs.add(dotBracketText);
        currStrs.setViewportView(dotBracketText);


        // Set up the view and viewer for the graph.
        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();

        view = viewer.addDefaultView(false);
        view.getCamera().setAutoFitView(false);
        view.getCamera().setGraphViewport(0, 0, 650, 350);
        view.getCamera().setViewCenter(0, 0, 0);

        // Add custom mouse manager to allow for RNA drawing.
        view.setMouseManager(new RNAMouseManager());

        // Set up the rest of the Drawing Area, add the view.
        DrawArea.setLayout(new BorderLayout());
        DrawArea.setBorder(BorderFactory.createLoweredBevelBorder());
        DrawArea.add((Component) view, BorderLayout.CENTER);

        // Add instructions and other useful info for the user.
        legend.setEditable(false);
        legend.setBorder(BorderFactory.createEtchedBorder(1));
        legend.append("Delete Node:                                         Alt->LeftClick\n" +
                "Add Edge Btwn Nodes:         Shift + LeftClick->Hover\n" +
                "Mode Node:                                       LeftClick->Drag\n" +
                "Add Node:                                                     LeftClick\n" +
                "Continuous Drawing(glitchy):  Control->Move Cursor\n" +
                "Exit Program:                                                    Escape");

        legend2.setEditable(false);
        legend2.setBorder(BorderFactory.createLoweredBevelBorder());
        legend2.append("5' Node:                              GREEN\n" +
                "3' Node:                               RED\n" +
                "RNA Backbone:                    BLUE\n" +
                "Base Pair Bond:                    PINK\n\n" +
                "  MAKE SURE 5' AND 3' NODES ARE CORRECT");


        // Set the text that each button displays.
        predictStructureButton.setText("Predict");
        buttonOptimize.setText("Optimize");
        RemoveAllNodes.setText("Clear Nodes");

        // Set the text that the boxes may or may not display if given sequences to begin with.
        if (rna.contains(">")) {
            textArea.setText(rna.split(">")[1]);
            dotBracketText.setText(rnaStr.split(">")[1]);
        } else {
            // do nothing
        }


        // Get rid of any prior layout of the GUI.
        contentPane.setLayout(null);


        // Add all of the premade panes/panels to the main contentPane, in specified locations.
        contentPane.add(DrawArea);
        DrawArea.setBounds(5, 5, 650, 350); // if set to 0, first node will be placed correctly...Not changed for aesthetic reasons.

        contentPane.add(DNA);
        DNA.setBounds(5, 360, 650, 50);

        contentPane.add(legend);
        legend.setBounds(5, 415, 350, 100);

        contentPane.add(legend2);
        legend2.setBounds(360, 415, 295, 100);


        // Add all the buttons too.
        contentPane.add(predictStructureButton);
        predictStructureButton.setBounds(655, 5, 145, 30);

        contentPane.add(RemoveAllNodes);
        RemoveAllNodes.setBounds(655, 40, 145, 30);

        contentPane.add(buttonOptimize);
        buttonOptimize.setBounds(655, 75, 145, 30);

        contentPane.add(currStrs);
        currStrs.setBounds(660, 110, 135, 405);


        // On clicking "Predict", calls function that determines dot-bracket notation
        predictStructureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPredict();
            }
        });

        // On clicking "Optimize", recursively finds the most likely RNA sequence to fold into the structure desired.
        buttonOptimize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!rnaStr.equals("")) {
                    onOptimize2();
                }
            }
        });

        // On clicking "Clear Nodes", clears all changing text boxes and graph.
        RemoveAllNodes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.clear();
                textArea.setText("");
                dotBracketText.setText("");
                recurse = 0;
                rna = "";
                possStrs = new ArrayList<>();
            }
        });

        // Call onCancel() when cross is clicked.
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


    // Refined Optimization method based on one of my prior, messier recursion algorithm, located in onOptimize2.java
    private void onOptimize2() {

        // Get the current RNA structures for the sequence.
        cleanUp();
        printAllStrs(possStrs);
        ArrayList<String> First = new ArrayList<>();
        for (String s : possStrs) {
            First.add(s);
        }
        String previousRNA = "" + rna;

        // Point "mutate" the sequence, get the resultant potential RNA structures.
        mutate();
        cleanUp();
        ArrayList<String> Second = new ArrayList<>();
        for (String s : possStrs) {
            Second.add(s);
        }


        // If the number of total possible structures has gone down, and possStrs still contains the drawn structure:
        if (First.size() >= Second.size() && Second.contains(rnaStr)) {

            // 1st condition for ending Optimization.
            // If the drawn structure is the only possible structure left:
            if (Second.size() == 1) {

                // Append relevant info to relevant text boxes.
                textArea.setText("Bing! Your optimization is done:\n" + rna);
                dotBracketText.setText("Possible Folds:\n" + rnaStr);

                // 2nd condition for ending Optimization.
                // Check for if, at certain point one must come to the conclusion that the drawn RNA structure
                // is most likely not plausible, or there is not enough variation to refine the structures any further.
            } else if (Second.size() <= 5 && recurse >= 500) {

                // Append all possible structures to the dotBracketText box.
                dotBracketText.setText("");
                dotBracketText.append("Possible Folds:\n");
                for (String finStruct : possStrs) {
                    dotBracketText.append(finStruct + "\n");
                }

                // Append the current RNA sequence to the textArea(RNA seq.) box.
                textArea.setText("Bing! Your optimization is done:\n" + rna);

                // If it hasn't recursed enough to make the previous decision:
            } else if (recurse < 500) {

                // Call onOptimize2 on the new RNA sequence because it is just as good or better than the previous.
                recurse += 1;
                onOptimize2();

            } else if (recurse >= 500) {

                onPredict();
                recurse = 0;
                onOptimize2();
            }

            // If the new RNA sequence has more possible secondary structures,
            // or the drawn structure is no longer a possible structure:
        } else {

            // For sticky situations.
            if (recurse >= 500) {
                onPredict();
                recurse = 0;
                onOptimize2();
            } else {

                // Return global rna back to previous state.
                rna = "";
                rna += previousRNA;

                // Return global possStrs to previous state.
                possStrs = new ArrayList<>();

                for (String s : First) {
                    possStrs.add(s);
                }

                // Retry.
                recurse += 1;
                onOptimize2();
            }
        }
    }

    public ArrayList<String> findPossibleStructures(String potStrs) {
        NussinovV nuss = new NussinovV();
        ArrayList<String> sols = nuss.getStructs(potStrs);
        return sols;
    }

    public void printAllStrs(ArrayList<String> visStrs) {
        dotBracketText.setText("");
        dotBracketText.append("Possible Folds:\n");
        for (String s : visStrs) {
            dotBracketText.append(s + "\n");
        }
    }



    // get possStrs from sequence, then clean up the ArrayList to readable format Edited:[rna, possStrs]
    // allows for comparison between my derived structure, and the generated ones.
    private void cleanUp() {
        ArrayList<String> initStrs = findPossibleStructures(rna);
        possStrs = new ArrayList<>();
        dotBracketText.setText("");
        for (String str : initStrs) {
            while (str.contains("()")) {
                str = str.replace("()", "..");
            }
            possStrs.add(str);
        }
    }

    // My way of sampling a lot of possible RNA sequences in a short amount of time.
    // Depending on whether the node chosen from the structure is paired with another, or is part of a loop,
    // mutate it to another base that is likely to be in that location.
    // (i.e. mainly A's and U's in loops and "G's and C's in stems --- could get more complicated with addition of
    // binding sites, larger sequences, interactions between RNAs.)
    private void mutate() {
        ArrayList<Integer> nonbonds = new ArrayList<>();
        ArrayList<Integer> bonds = new ArrayList<>();

        // Not exactly deterministic, but necessary to save time when deriving a sequence from a structure.
        Random r = new Random();

        // The nucleotide letter to be swapped with where the current one is.
        String nucRepl = "";

        // These are the same thing, int is the current nucleotide slot in the sequence, and curNuc is the letter.
        int nucToChange = 0;
        String curNuc = "";

        // These relate to changing the paired nucleotide in the sequence too.
        String secNucRepl = "";
        int secNuc = 0;


        // Sort all of the nodes into whether or not they have a bond with another nucleotide, or just a backbone.
        Iterator<GraphicNode> g = RNAMouseManager.graph.getNodeIterator();
        while (g.hasNext()) {
            GraphicNode n = g.next();
            if (!bondCounter(n)) {
                nonbonds.add(Integer.parseInt(n.getId()));
            } else {
                bonds.add(Integer.parseInt(n.getId()));
            }
        }

        // Make a choice between changing a loop and changing a stem.

        // Choose to change a stem.
        if (r.nextInt(100) < 66) {

            // locate the current nucleotide and its location in the sequence.
            nucToChange = bonds.get(r.nextInt(bonds.size()));
            curNuc = rna.substring(nucToChange, nucToChange + 1);

            // Figured out the bonded nucleotide by the labelling of the edge that goes from the current node to it.
            if (b_edge.toString().split("_")[1].equals(nucToChange + "")) {
                secNuc = Integer.parseInt(b_edge.toString().split("_")[2]);

            } else {
                secNuc = Integer.parseInt(b_edge.toString().split("_")[1]);
            }

            // Gets the replacement for the other nucleotide.
            if (curNuc.equals("G")) {
                nucRepl = "C";
                secNucRepl = "G";

            } else if (curNuc.equals("C")) {
                nucRepl = "G";
                secNucRepl = "C";
            }

            // Determines where to place the two nucleotides in the sequence based off of locations in the sequence.
            if (nucToChange < secNuc) {
                rna = rna.substring(0, nucToChange) + nucRepl + rna.substring(nucToChange + 1, secNuc) + secNucRepl
                        + rna.substring(secNuc + 1);
            } else {
                rna = rna.substring(0, secNuc) + secNucRepl + rna.substring(secNuc + 1, nucToChange) + nucRepl
                        + rna.substring(nucToChange + 1);
            }

            // Choose to change a loop.
        } else {

            // Same method as above.
            nucToChange = nonbonds.get(r.nextInt(nonbonds.size()));
            curNuc = rna.substring(nucToChange, nucToChange + 1);

            // Difference here is that the loop nucleotides are chosen with a randomness of 1/4.
            if (curNuc.equals("C")) {
                int newNuc = r.nextInt(32);
                if (newNuc < 8) { //21
                    nucRepl = "U";
                } else if (newNuc < 16) { //24
                    nucRepl = "G";
                } else if (newNuc < 24) { //27
                    nucRepl = "A";
                } else if (newNuc < 32) {
                    nucRepl = "C";
                }

            } else if (curNuc.equals("U")) {
                int newNuc = r.nextInt(32);
                if (newNuc < 8) { //21
                    nucRepl = "C";
                } else if (newNuc < 16) { //24
                    nucRepl = "G";
                } else if (newNuc < 24) { //27
                    nucRepl = "A";
                } else if (newNuc < 32) {
                    nucRepl = "U";
                }

            } else if (curNuc.equals("G")) {
                int newNuc = r.nextInt(27);
                if (newNuc < 8) { //11
                    nucRepl = "U";
                } else if (newNuc < 16) { //22
                    nucRepl = "C";
                } else if (newNuc < 24) { //27
                    nucRepl = "A";
                } else if (newNuc < 32) {
                    nucRepl = "G";
                }

            } else if (curNuc.equals("A")) {
                int newNuc = r.nextInt(27); //
                if (newNuc < 8) { //11
                    nucRepl = "U";
                } else if (newNuc < 16) { //22
                    nucRepl = "C";
                } else if (newNuc < 24) { //27
                    nucRepl = "G";
                } else if (newNuc < 32) {
                    nucRepl = "A";
                }
            }
            rna = rna.substring(0, nucToChange) + nucRepl + rna.substring(nucToChange + 1);
        }

    }

    // Takes in the drawn structure and discerns a possible preliminary sequence.
    private void onPredict() {

        // This is for RNA2DesignDemo. It's a differnt integration for sequences that are input, rather than made.
        if (rna.contains(">")) {
            rna = rna.split(">")[1];
            rnaStr = rna.split(">")[1];

            textArea.setText("Preliminary Sequence:\n" + rna);
            dotBracketText.setText("Drawn Fold:\n" + rnaStr);
        } else {

            // Clear everything out for another Prediction of the sequence.
            textArea.setText("");
            dotBracketText.setText("");
            recurse = 0;
            rna  = "";
            possStrs = new ArrayList<>();
        }

        // If the user hasn't drawn a structure yet, the call will end.
        if (RNAMouseManager.start == null || RNAMouseManager.stop == null) {
            // Do nothing

        } else {

            int AUGC;
            Random rand = new Random();
            GraphicGraph rnaGraph = RNAMouseManager.graph;
            int rnaLen = rnaGraph.getNodeCount();
            Map<GraphicNode, String> nodeToNuc = new HashMap<>();
            Map<GraphicNode, String> dontVisit = new HashMap<>();
            ArrayList<String> visitedEdges = new ArrayList<>();


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

                    //Visits one node, looks at its edges.
                    if (curEdge.toString().matches("e_\\d+") /*&& !nodeToNuc.containsKey(curNode)*/) {
                        if (!dontVisit.containsKey(curNode) && !visitedEdges.contains(curEdge.toString())) {
                            visitedEdges.add(curEdge.toString());
                            dontVisit.put(curNode, curNode.getId());
                            rnaStr += ".";
                            AUGC = rand.nextInt(32);

                            // If the node is bonded to another, replace it with either a G or a C:
                            if (bondCounter(curNode)) {
                                if (AUGC < 16) {
                                    rna += "G";
                                    nodeToNuc.put(curNode, "G");
                                } else {
                                    rna += "C";
                                    nodeToNuc.put(curNode, "C");
                                }
                                /*if (AUGC < 2) {
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
                                }*/

                                // Else it's a loop and start the position off as an A.
                            } else {
                                rna += "A";
                                nodeToNuc.put(curNode, "A");
                                /*if (AUGC < 13) {
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
                                }*/
                            }
                        }
                    }

                    // If the current edge being looked at is a bond:
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

                        // If the code comes across a nucleotide that has already been mentioned, change it to match
                        // the nucleotide close to the 5'.
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

                            // Else the node being visted is the earl nucleotide, then add it to the dontVisit list.
                        } else {
                            dontVisit.put(pairedNode, to);
                        }
                    }
                }
            }
        }

        // Output rna and structure to GUI.
        textArea.setText("Preliminary Sequence:\n" + rna);
        dotBracketText.setText("Drawn Fold:\n" + rnaStr);
    }


    // True if it finds a bond that represents a nucleotide pair, return true.
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

    // Ends program.
    private void onCancel() {
        dispose();
    }


    public static void main(String[] args) {
        String rnaTest = "";
        String strTest = "";
        RNA2DesignGUI dialog = new RNA2DesignGUI(rnaTest, strTest);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
