

/**
 * Created by tristanwasley on 11/20/17.
 */
package gui;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.view.View;
//import org.graphstream.ui.view.util.InteractiveElement;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.graph.Element;

import javax.swing.text.AbstractDocument;

import static java.awt.event.MouseEvent.BUTTON3;


public class RNAMouseManager implements MouseManager
{
    private int nodel = 0;
    Double[] pos;
    double xinit;
    double yinit;
    Point3 GU;
    //private Map<Integer, Node> allNodes = new TreeMap<>();
    public static Element start = null;
    public static Element stop = null;
    // Attribute

    /**
     * The view this manager operates upon.
     */
    protected View view;

    /**
     * The graph to modify according to the view actions.
     */
    public static  GraphicGraph graph;


    final private EnumSet<InteractiveElement> types;

    // Construction
    public RNAMouseManager() {
        this(EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE));
    }

    public RNAMouseManager(EnumSet<InteractiveElement> types) {
        this.types = types;
    }

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        this.graph = graph;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }

    public EnumSet<InteractiveElement> getManagedTypes() {
        return types;
    }

    public void release() {
        view.removeMouseListener(this);
        view.removeMouseMotionListener(this);
    }

    /*public Element getStart() {
        return start;
    }

    public Element getStop() {
        return stop;
    }*/

    // Command

    protected void mouseButtonPress(MouseEvent event) {
        view.requestFocus();

        // Unselect all.

        //if (event.isShiftDown() && nodel > 0) {
            /*if (types.contains(InteractiveElement.NODE)) {
                for (Node node : graph) {

                    if (node.hasAttribute("ui.selected")) {
                        //node.removeAttribute("ui.selected");
                        graph.removeEdge(nodel - 1 + "", nodel + "");
                        graph.removeNode(nodel + "");
                        nodel -= 1;
                    }
                }
            }

            if (types.contains(InteractiveElement.SPRITE)) {
                for (GraphicSprite sprite : graph.spriteSet()) {
                    if (sprite.hasAttribute("ui.selected"))
                        sprite.removeAttribute("ui.selected");
                }
            }

            if (types.contains(InteractiveElement.EDGE)) {
                for (Edge edge : graph.getEdgeSet()) {
                    if (edge.hasAttribute("ui.selected"))
                        edge.removeAttribute("ui.selected");
                }
            }*/
        //} else {
        if (graph.getNodeSet().size() < 1) {
            //allNodes = new TreeMap<>();
            nodel = 0;
            start = null;
            stop = null;
        }
        Point pos = event.getPoint();
        xinit = pos.x;
        yinit = pos.y;
        Point3 GU = view.getCamera().transformPxToGu(xinit, yinit);

        Node node = graph.addNode(nodel + "");
        node.addAttribute("xy", GU.x, GU.y);
        node.addAttribute("layout.frozen");
        if (nodel > 0) {
            graph.addEdge("e_" + nodel, "" + (nodel - 1), "" + nodel).addAttribute("ui.style", "fill-color: blue;");
            node.addAttribute("ui.style", "fill-color: red;");
            stop = node;
            if (nodel > 1) {
                if (graph.getNode("" + (nodel - 1)).hasAttribute("ui.style")) {
                    graph.getNode("" + (nodel - 1)).addAttribute("ui.style", "fill-color: black;");
                }
            }

            //graph.addAttribute("layout.frozen");
        } else if (nodel == 0) {
            node.addAttribute("ui.style", "fill-color: green;");
            start = node;
        }
        //allNodes.put(nodel, node);
        nodel += 1;
        //}
    }

    protected void mouseButtonRelease(MouseEvent event,
                                      Iterable<GraphicElement> elementsInArea) {
        for (GraphicElement element : elementsInArea) {
            if (!element.hasAttribute("ui.selected"))
                element.addAttribute("ui.selected");
        }
    }

    protected void mouseButtonPressOnElement(GraphicElement element,
                                             MouseEvent event) {
        view.freezeElement(element, true);
        if (event.isControlDown()) {
            /*if (event.getButton() == 1) {
                if (start == null) {
                    element.addAttribute("ui.style", "fill-color: green;");
                    start = element;
                } else if (start == element) {
                    element.addAttribute("ui.style", "fill-color: black;");
                    start = null;
                }
            } else if (event.getButton() == 3) {
                if (stop == null) {
                    element.addAttribute("ui.style", "fill-color: red;");
                    stop = element;
                } else if (stop == element) {
                    element.addAttribute("ui.style", "fill-color: black;");
                    stop = null;
                }
            }*/
        } else {
            /*if (event.getButton() == 3) {
                element.addAttribute("ui.selected");
                String ele = element.toString();
                double elex = element.getX();
                double eley = element.getY();
                graph.removeNode(ele);
                Node node = graph.addNode(ele);
                node.addAttribute("xy", elex, eley);
                node.addAttribute("layout.frozen");
            }*/
        }
        /*} else {
            element.addAttribute("ui.clicked");
        }*/
    }

    protected void elementMoving(GraphicElement element, MouseEvent event) {
        view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonReleaseOffElement(GraphicElement element,
                                                MouseEvent event) {
        view.freezeElement(element, false);
        if (event.getButton() != 3) {
            element.removeAttribute("ui.clicked");
        } else {
        }
    }

    // Mouse Listener

    protected GraphicElement curElement;

    protected float x1, y1;

    public void mouseClicked(MouseEvent event) {
        // NOP
    }

    public void mousePressed(MouseEvent event) {
        if (graph.getNodeSet().size() < 1) {
            //allNodes = new TreeMap<>();
            nodel = 0;
            start = null;
            stop = null;
        }
        curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());


        if (curElement != null) {
            mouseButtonPressOnElement(curElement, event);
            if (event.isAltDown()) {
                graph.removeNode(curElement.toString());
                int curEle = Integer.parseInt(curElement.toString());
                for (int i = 0; i < nodel - curEle - 1; i++) {
                    Node movN = graph.getNode((i + curEle + 1) + "");
                    graph.removeNode((i + curEle + 1) + "");
                    Node newN = graph.addNode((i + curEle) + "");
                    newN.addAttribute("xy", movN.getAttribute("xy"));
                    newN.addAttribute("layout.frozen");
                    if (curEle + i + 2 == nodel) {
                        newN.addAttribute("ui.style", "fill-color: red;");
                        stop = newN;
                    }
                    if (curEle + i != 0) {
                        graph.addEdge("e_" + (curEle + i), "" + (curEle - 1 + i), "" + (curEle + i)).addAttribute("ui.style", "fill-color: blue;");
                    } else {
                    }
                }
                if (curEle == nodel - 1) {
                    graph.getNode((curEle - 1) + "").addAttribute("ui.style", "fill-color: red;");
                    graph.removeNode(curEle + "");
                    stop = graph.getNode((curEle - 1) + "");
                }
                //allNodes.remove(nodel);
                nodel -= 1;
            }
        } else {
            x1 = event.getX();
            y1 = event.getY();
            mouseButtonPress(event);
            view.beginSelectionAt(x1, y1);

        }
    }

    public void mouseDragged(MouseEvent event) {
        if (curElement != null) {
            if (event.isShiftDown()) {
                if (view.allNodesOrSpritesIn(event.getX() - 5, event.getY() - 5, event.getX() + 5, event.getY() + 5).size() > 1) {
                    for (GraphicElement nerd : view.allNodesOrSpritesIn(event.getX() - 5, event.getY() - 5, event.getX() + 5, event.getY() + 5)) {
                        if (nerd != curElement) {
                            //work here    blue backbone, pink bonds
                            GraphicNode actionNode = graph.getNode(curElement.getId());
                            GraphicNode passiveNode = graph.getNode(nerd.getId());

                            int abbs = 0;
                            int abds = 0;
                            if (actionNode.getDegree() > 0) {
                                for (Edge e : actionNode.getEdgeSet()) {
                                    if (e.toString().split("_")[0].equals("e")) {
                                        abbs += 1;
                                    } else {
                                        abds += 1;
                                    }
                                }
                            }

                            int pbbs = 0;
                            int pbds = 0;
                            boolean boo = false;
                            if (passiveNode.getDegree() > 0) {
                                for (Edge e : passiveNode.getEdgeSet()) {
                                    if (e.toString().split("_")[0].equals("e")) {
                                        pbbs += 1;
                                    } else {
                                        pbds += 1;
                                    }
                                }
                            }

                            int curEle = 0;
                            if (actionNode.toString().equals("0")) {
                                curEle = Integer.parseInt(passiveNode.toString());
                                boo = true;
                            } else if (passiveNode.toString().equals("0")){
                                curEle = Integer.parseInt(actionNode.toString());
                                boo = true;
                            }

                            if (abbs + abds == 3 | pbbs + pbds == 3) {
                                // do nothing

                            } else if (abds == 0 && pbds == 0) {
                                if (abbs > 0 && pbbs > 0) {
                                    graph.addEdge("b_" + actionNode.toString() + "_" + passiveNode.toString(), actionNode.getId(), passiveNode.getId())
                                            .addAttribute("ui.style", "fill-color: pink;");
                                } /*else if (boo) {
                                    for (int i = 0; i < nodel - 1; i++) {
                                        Node movN = graph.getNode(i + "");
                                        graph.removeNode(i + "");
                                        if (i == 0) {
                                            graph.addNode("0").addAttribute(graph.getNode(curEle + "").getAttribute("xy"));
                                            graph.removeNode(curEle + "");
                                        }
                                        Node newN = graph.addNode((i + 1) + "");
                                        newN.addAttribute("xy", movN.getAttribute("xy"));
                                        newN.addAttribute("layout.frozen");
                                        graph.addEdge("e_" + (i + 1), "" + i, "" + (i + 1));
                                    }
                                } else {
                                    graph.addEdge("e_" + curEle, actionNode.getId(), passiveNode.getId());
                                }*/



                            } /*else if (actionNode.toString().equals("0") | passiveNode.toString().equals("0")) {
                                if (actionNode.toString().equals("0")) {
                                    curEle = Integer.parseInt(actionNode.toString());
                                } else {
                                    curEle = Integer.parseInt(passiveNode.toString());
                                }


                            } else if (passiveNode.getEdgeSet().size() == 0 && actionNode.getEdgeSet().size() == 2) {
                                Iterator<Edge> edges = actionNode.getEdgeIterator();
                                int bbs = 0;
                                int bds = 0;
                                while (edges.hasNext()) {
                                    Edge e = edges.next();
                                    if (e.toString().split("_")[0].equals("e")) {
                                        bbs += 1;
                                    } else {
                                        bds += 1;
                                    }
                                }
                                if (bbs == 2 && passiveNode.getEdgeSet().size() != 0) {
                                    graph.addEdge("b_" + curElement.toString() + "_" + nerd.toString(), actionNode, passiveNode);
                                } else if (bbs == 2 && passiveNode.getEdgeSet().size() == 0) {
                                    //do nothing
                                } else {
                                    graph.addEdge("e_" + nerd.toString(), actionNode, passiveNode);
                                }

                            } else if (passiveNode.getEdgeSet().size() == 0) {
                                Iterator<Edge> edges = passiveNode.getEdgeIterator();
                                int bbs = 0;
                                int bds = 0;
                                while (edges.hasNext()) {
                                    Edge e = edges.next();
                                    if (e.toString().split("_")[0].equals("e")) {
                                        bbs += 1;
                                    } else {
                                        bds += 1;
                                    }
                                }
                            } else if (passiveNode.getEdgeSet().size() == 0 && actionNode.getEdgeSet().size() == 0) {

                            }*/
                        }
                    }
                }
            }
            elementMoving(curElement, event);
        } else {
            view.selectionGrowsAt(event.getX(), event.getY());
        }
    }

    public void mouseReleased(MouseEvent event) {
        if (curElement != null) {
            mouseButtonReleaseOffElement(curElement, event);
            curElement = null;
        } else {
            float x2 = event.getX();
            float y2 = event.getY();
            float t;

            if (x1 > x2) {
                t = x1;
                x1 = x2;
                x2 = t;
            }
            if (y1 > y2) {
                t = y1;
                y1 = y2;
                y2 = t;
            }

            mouseButtonRelease(event, view.allNodesOrSpritesIn(x1, y1, x2, y2));
            view.endSelectionAt(x2, y2);
        }
    }

    public void mouseEntered(MouseEvent event) {
        // NOP
    }

    public void mouseExited(MouseEvent event) {
        // NOP
    }

    public void mouseMoved(MouseEvent event) {
        if (graph.getNodeSet().size() < 1) {
            //allNodes = new TreeMap<>();
            nodel = 0;
            start = null;
            stop = null;
        }
        if (event.isControlDown()) {
            if (nodel == 0) {
                Point pos = event.getPoint();
                xinit = pos.x;
                yinit = pos.y;
                Point3 GU = view.getCamera().transformPxToGu(xinit, yinit);
                Node node = graph.addNode(nodel + "");
                node.addAttribute("xy", GU.x, GU.y);
                node.addAttribute("layout.frozen");
                node.addAttribute("ui.style", "fill-color: green;");
                /*curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
                if (curElement != null) {
                    mouseButtonPressOnElement(curElement, event);
                }*/
                //allNodes.put(nodel, node);
                nodel += 1;

            } else {
                try
                {
                    Thread.sleep(100);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
                double evx = event.getX();
                double evy = event.getY();
                Point3 eGU = view.getCamera().transformPxToGu(evx, evy);
                if (Math.sqrt((int) (eGU.x - xinit) ^ 2 + (int) (eGU.y - yinit) ^ 2) >= 7) {
                    Node node = graph.addNode(nodel + "");
                    node.addAttribute("xy", eGU.x, eGU.y);
                    node.addAttribute("layout.frozen");
                    if (nodel > 0) {
                        graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel).addAttribute("ui.style", "fill-color: blue;");
                        //graph.addAttribute("layout.frozen");
                        node.addAttribute("ui.style", "fill-color: red;");
                        stop = node;
                        if (nodel > 1) {
                            if (graph.getNode("" + (nodel - 1)).hasAttribute("ui.style")) {
                                graph.getNode("" + (nodel - 1)).addAttribute("ui.style", "fill-color: black;");
                            }
                        }
                    }
                    //allNodes.put(nodel, node);
                    nodel += 1;
                    xinit = eGU.x;
                    yinit = eGU.y;
                }
            }
        }
    }
}
