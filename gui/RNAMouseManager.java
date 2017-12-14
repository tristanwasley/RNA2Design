

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
    double xinit;
    double yinit;
    public static Element start = null;
    public static Element stop = null;

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

    // Leftover from DefaultMouseManager
    public EnumSet<InteractiveElement> getManagedTypes() {
        return types;
    }

    public void release() {
        view.removeMouseListener(this);
        view.removeMouseMotionListener(this);
    }


    protected void mouseButtonPress(MouseEvent event) {
        view.requestFocus();

        // Givens
        if (graph.getNodeSet().size() < 1) {
            nodel = 0;
            start = null;
            stop = null;
        }

        // Get the point of the click, convert it to a point on the graph, then add node plus attributes
        Point pos = event.getPoint();
        xinit = pos.x;
        yinit = pos.y;
        Point3 GU = view.getCamera().transformPxToGu(xinit, yinit);

        Node node = graph.addNode(nodel + "");
        node.addAttribute("xy", GU.x, GU.y);
        node.addAttribute("layout.frozen");
        if (nodel > 0) {

            // Add edge between previous node and just placed one.
            graph.addEdge("e_" + nodel, "" + (nodel - 1), "" + nodel).addAttribute("ui.style", "fill-color: blue;");

            // Maintenance of 3' red stop node.
            node.addAttribute("ui.style", "fill-color: red;");
            stop = node;
            if (nodel > 1) {

                // Turn previous node black
                if (graph.getNode("" + (nodel - 1)).hasAttribute("ui.style")) {
                    graph.getNode("" + (nodel - 1)).addAttribute("ui.style", "fill-color: black;");
                }
            }

        } else if (nodel == 0) {

            // 5' node maintenance
            node.addAttribute("ui.style", "fill-color: green;");
            start = node;
        }

        // Maintenance of what node the system is on.
        nodel += 1;
    }

    // Part of DefaultMouseManager I believe.
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
            // nothing yet
        } else {

            // deletes the edges of a node; used to free up nodes however it messed up the node numbering too much.
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


    // Describes the movement of a node.
    protected void elementMoving(GraphicElement element, MouseEvent event) {
        view.moveElementAtPx(element, event.getX(), event.getY());
    }

    // Place node wherever you let go of it.
    protected void mouseButtonReleaseOffElement(GraphicElement element,
                                                MouseEvent event) {
        view.freezeElement(element, false);
        if (event.getButton() != 3) {
            element.removeAttribute("ui.clicked");
        } else {
        }
    }

    // The current element.
    protected GraphicElement curElement;

    protected float x1, y1;

    public void mouseClicked(MouseEvent event) {
        // NOP
    }

    //  Specifies mouse press events.
    public void mousePressed(MouseEvent event) {
        if (graph.getNodeSet().size() < 1) {
            nodel = 0;
            start = null;
            stop = null;
        }

        // Get the current clicked item.
        curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());


        if (curElement != null) {

            mouseButtonPressOnElement(curElement, event);

            // If Alt is pressed down while clicking a node, delete it and revert system back a node from
            // previous node position.
            if (event.isAltDown()) {

                graph.removeNode(curElement.toString());
                int curEle = Integer.parseInt(curElement.toString());

                // Handles if the node deleted was in the middle of the sequence.
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

                    // "Edge" case ... ha ha
                    if (curEle + i != 0) {
                        graph.addEdge("e_" + (curEle + i), "" + (curEle - 1 + i), "" + (curEle + i)).addAttribute("ui.style", "fill-color: blue;");
                    } else {
                        // nothing
                    }
                }
                if (curEle == nodel - 1) {
                    graph.getNode((curEle - 1) + "").addAttribute("ui.style", "fill-color: red;");
                    graph.removeNode(curEle + "");
                    stop = graph.getNode((curEle - 1) + "");
                }

                // Because we just lost a node.
                nodel -= 1;
            }

            // Specifies native drag option which has soemthing to do with dragging to highlight an area.
        } else {
            x1 = event.getX();
            y1 = event.getY();
            mouseButtonPress(event);
            view.beginSelectionAt(x1, y1);

        }
    }

    // Functionality for making bonds
    public void mouseDragged(MouseEvent event) {
        if (curElement != null) {
            if (event.isShiftDown()) {
                if (view.allNodesOrSpritesIn(event.getX() - 5, event.getY() - 5, event.getX() + 5, event.getY() + 5).size() > 1) {
                    for (GraphicElement nerd : view.allNodesOrSpritesIn(event.getX() - 5, event.getY() - 5, event.getX() + 5, event.getY() + 5)) {
                        if (nerd != curElement) {
                            //work here    blue backbone, pink bonds

                            // Action node is the node being dragged; Passive node is the node being dragged over.
                            GraphicNode actionNode = graph.getNode(curElement.getId());
                            GraphicNode passiveNode = graph.getNode(nerd.getId());

                            // abbs = Action node Back BoneS (edges)
                            // abds = Action node BonDS (edges)
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

                            // pbbs = Passive node Back BoneS (edges)
                            // pbds = Passive node BonDS (edges)
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

                            // Related to the commented out code. Adding edge between singular node and the structure
                            // unimplemented right now.
                            int curEle = 0;
                            if (actionNode.toString().equals("0")) {
                                curEle = Integer.parseInt(passiveNode.toString());
                                boo = true;
                            } else if (passiveNode.toString().equals("0")){
                                curEle = Integer.parseInt(actionNode.toString());
                                boo = true;
                            }

                            // Rules for checking if its legal to make a bond between two nodes.
                            if (abbs + abds == 3 | pbbs + pbds == 3) {
                                // do nothing

                            } else if (abds == 0 && pbds == 0) {
                                if (abbs > 0 && pbbs > 0) {
                                    graph.addEdge("b_" + actionNode.toString() + "_" + passiveNode.toString(), actionNode.getId(), passiveNode.getId())
                                            .addAttribute("ui.style", "fill-color: pink;");

                                    // More code for attaching a single node to the rest of the rna Structure. Resolved
                                    // this by removing ability to make a single node. May add it back in later versions.
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

    // What happens when a mouse button is released. Included from DefaultMouseManager.
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

    // Simply looks at ambient mouse movement in the graph.
    public void mouseMoved(MouseEvent event) {
        if (graph.getNodeSet().size() < 1) {
            nodel = 0;
            start = null;
            stop = null;
        }

        // Ability to hold down Control and draw structures with fewer clicks
        // A bit buggy right now, not working well, best to avoid for now.
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
                nodel += 1;

            } else {

                // Tries pausing the algorithm to generate nodes at equal time points, but it has unexpected behavior
                // at the moment.

                /*try
                {
                    Thread.sleep(100);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }*/

                //event.getPoint().setLocation();
                double evx = event.getX();
                double evy = event.getY();
                Point3 eGU = view.getCamera().transformPxToGu(evx, evy);
                if (Math.sqrt((int) (eGU.x - xinit) ^ 2 + (int) (eGU.y - yinit) ^ 2) >= 7) {
                    Node node = graph.addNode(nodel + "");
                    node.addAttribute("xy", eGU.x, eGU.y);
                    node.addAttribute("layout.frozen");
                    if (nodel > 0) {
                        graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel).addAttribute("ui.style", "fill-color: blue;");
                        node.addAttribute("ui.style", "fill-color: red;");
                        stop = node;
                        if (nodel > 1) {
                            if (graph.getNode("" + (nodel - 1)).hasAttribute("ui.style")) {
                                graph.getNode("" + (nodel - 1)).addAttribute("ui.style", "fill-color: black;");
                            }
                        }
                    }

                    // logs previous position to determine when to drop another node.
                    nodel += 1;
                    xinit = eGU.x;
                    yinit = eGU.y;
                }
            }
        }
    }
}
