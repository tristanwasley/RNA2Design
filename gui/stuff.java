package fr.orsay.lri.varna.applications;

/**
 * Created by tristanwasley on 11/20/17.
 */
public class stuff {
    //view.addMouseListener(new CustomMouseListener());

    /*view.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Point pos = e.getPoint();
                int x = pos.x;
                int y = pos.y;
                Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);


                //if (graph.getAttribute("xy").equals("GU.x, GU.y")) {
                //    boo = true;
                //} else {


                    GraphicNode node = graph.addNode(nodel + "");
                    node.addAttribute("xy", GU.x, GU.y);
                    node.addAttribute("layout.frozen");
                    if (nodel > 0) {
                        graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                        graph.addAttribute("layout.frozen");
                    }
                    nodel += 1;
                }
            //}

            @Override
            public void mouseDragged(MouseEvent e) {
                //pathNodes();
                //ArrayList<Point> dragpts = new ArrayList<>();
                Point pos = e.getPoint();
                //dragpts.add(pos);
                int x = pos.x;
                int y = pos.y;

                while (e.getID() == MOUSE_DRAG) {
                    if (Math.sqrt(((int)getMousePosition().getX() - x)^2 + ((int)getMousePosition().getY() - y)^2) > 1) {
                        x = (int)getMousePosition().getX();
                        y = (int)getMousePosition().getY();
                        GraphicNode node = graph.addNode("" + nodel);
                        nodel += 1;
                        node.addAttribute(nodel + "", x, -y);
                        //node.addAttribute("layout.frozen");
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (boo) {
                    boo = false;
                    Point pos = e.getPoint();
                    int x = pos.x;
                    int y = pos.y;
                    Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);

                    GraphicNode node = graph.addNode(nodel + "");
                    node.addAttribute("xy", GU.x, GU.y);
                    node.addAttribute("layout.frozen");
                    if (nodel > 0) {
                        graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                        //graph.addAttribute("layout.frozen");
                    }
                    nodel += 1;

                }
            }
        });*/



        /*graph.display().getDefaultView().setMouseManager(new MouseManager(){
            @Override
            public void mouseClicked(MouseEvent e) {
                Point pos = e.getPoint();
                int x = pos.x;
                int y = pos.y;
                Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);

                Node node = graph.addNode(nodel + "");
                node.addAttribute("xy", GU.x, GU.y);
                node.addAttribute("layout.frozen");
                if (nodel > 0) {
                    graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                    //graph.addAttribute("layout.frozen");
                }
                nodel += 1;
            }
            @Override
            public void mousePressed(MouseEvent e) {

                Point pos = e.getPoint();
                int x = pos.x;
                int y = pos.y;
                Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);

                Node node = graph.addNode(nodel + "");
                node.addAttribute("xy", GU.x, GU.y);
                node.addAttribute("layout.frozen");
                if (nodel > 0) {
                    graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                    //graph.addAttribute("layout.frozen");
                }
                nodel += 1;

                if (e.getButton() == InputEvent.BUTTON3_MASK) {
                    pos = e.getPoint();
                    x = pos.x;
                    y = pos.y;

                    for (int i = 0; i < 100; i++) {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch(InterruptedException ex)
                        {
                            Thread.currentThread().interrupt();
                        }
                        if (Math.sqrt(((int)getMousePosition().getX() - x)^2 + ((int)getMousePosition().getY() - y)^2) > 1) {
                            x = (int)getMousePosition().getX();
                            y = (int)getMousePosition().getY();
                            node = graph.addNode("" + nodel);
                            nodel += 1;
                            node.addAttribute(nodel + "", x, -y);
                        }
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getButton() == InputEvent.BUTTON3_MASK) {
                    Point pos = e.getPoint();
                    int x = pos.x;
                    int y = pos.y;

                    for (int i = 0; i < 100; i++) {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch(InterruptedException ex)
                        {
                            Thread.currentThread().interrupt();
                        }
                        if (Math.sqrt(((int)getMousePosition().getX() - x)^2 + ((int)getMousePosition().getY() - y)^2) > 1) {
                            x = (int)getMousePosition().getX();
                            y = (int)getMousePosition().getY();
                            Node node = graph.addNode("" + nodel);
                            nodel += 1;
                            node.addAttribute(nodel + "", x, -y);
                        }
                    }
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {}
            @Override
            public void init(GraphicGraph graph, View view) {}
            @Override
            public void release() {}
        });*/

    //view.moveElementAtPx()


        /*view.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Point pos = e.getPoint();
                int x = pos.x;
                int y = pos.y;
                Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);


                //if (graph.getAttribute("xy").equals("GU.x, GU.y")) {
                //    boo = true;
                //} else {


                    Node node = graph.addNode(nodel + "");
                    node.addAttribute("xy", GU.x, GU.y);
                    node.addAttribute("layout.frozen");
                    if (nodel > 0) {
                        graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                        graph.addAttribute("layout.frozen");
                    }
                    nodel += 1;
                }
            //}

            @Override
            public void mouseDragged(MouseEvent e) {
                //pathNodes();
                //ArrayList<Point> dragpts = new ArrayList<>();
                Point pos = e.getPoint();
                //dragpts.add(pos);
                int x = pos.x;
                int y = pos.y;

                while (e.getID() == MOUSE_DRAG) {
                    if (Math.sqrt(((int)getMousePosition().getX() - x)^2 + ((int)getMousePosition().getY() - y)^2) > 1) {
                        x = (int)getMousePosition().getX();
                        y = (int)getMousePosition().getY();
                        Node node = graph.addNode("" + nodel);
                        nodel += 1;
                        node.addAttribute(nodel + "", x, -y);
                        //node.addAttribute("layout.frozen");
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (boo) {
                    boo = false;
                    Point pos = e.getPoint();
                    int x = pos.x;
                    int y = pos.y;
                    Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);

                    Node node = graph.addNode(nodel + "");
                    node.addAttribute("xy", GU.x, GU.y);
                    node.addAttribute("layout.frozen");
                    if (nodel > 0) {
                        graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                        //graph.addAttribute("layout.frozen");
                    }
                    nodel += 1;

                }
            }
        });*/



        /*zclass CustomMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) {
        }
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == InputEvent.BUTTON3_MASK) {
                Point pos = e.getPoint();
                int x = pos.x;
                int y = pos.y;

                for (int i = 0; i < 100; i++) {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException ex)
                    {
                        Thread.currentThread().interrupt();
                    }
                    if (Math.sqrt(((int)getMousePosition().getX() - x)^2 + ((int)getMousePosition().getY() - y)^2) > 1) {
                        x = (int)getMousePosition().getX();
                        y = (int)getMousePosition().getY();
                        Node node = graph.addNode("" + nodel);
                        nodel += 1;
                        node.addAttribute(nodel + "", x, -y);
                    }
                }
            }
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
    }*/





        /*Point pos = event.getPoint();
            int x = pos.x;
            int y = pos.y;
            Point3 GU = view.getCamera().transformPxToGu((double)x, (double)y);

            Node node = graph.addNode(nodel + "");
            node.addAttribute("xy", GU.x, GU.y);
            node.addAttribute("layout.frozen");
            curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());
            if (curElement != null) {
                mouseButtonPressOnElement(curElement, event);
            }
            if (nodel > 0) {
                graph.addEdge("e" + nodel, "" + (nodel - 1), "" + nodel);
                //graph.addAttribute("layout.frozen");
            }
            nodel += 1;
            try
            {
                Thread.sleep(200);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            if (curElement != null) {
                mouseButtonReleaseOffElement(curElement, event);
                curElement = null;
            }*/
}
