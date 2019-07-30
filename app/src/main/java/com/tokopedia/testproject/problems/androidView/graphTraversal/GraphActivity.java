package com.tokopedia.testproject.problems.androidView.graphTraversal;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tokopedia.testproject.R;

import java.util.List;

import de.blox.graphview.BaseGraphAdapter;
import de.blox.graphview.Edge;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.energy.FruchtermanReingoldAlgorithm;

public class GraphActivity extends AppCompatActivity {
    private int nodeCount = 1;
    private Node currentNode;
    protected BaseGraphAdapter<ViewHolder> adapter;
    private static final String TAG = "GraphActivity";
    private static boolean[] isVisited;
    private static int[] dist;
    private int nodeDist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        final Graph graph = createGraph();
        isVisited = new boolean[graph.getNodeCount()];
        for (int i = 0; i < isVisited.length; i++){
            isVisited[i] = false;
        }

        dist = new int[graph.getNodeCount()];
        dist[0] = 0;
        dist[1] = 1;
        dist[2] = 1;
        dist[3] = 2;
        dist[4] = 2;
        dist[5] = 2;
        dist[6] = 2;
        setupAdapter(graph);
    }

    private void setupAdapter(Graph graph) {
        final GraphView graphView = findViewById(R.id.graph2);

        adapter = new BaseGraphAdapter<ViewHolder>(this, R.layout.node, graph) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.textView.setText(data.toString());
                Log.d(TAG, "onBindViewHolder: "+position);
                switch (dist[position]){
                    case 0:
                        viewHolder.cardView.setBackgroundColor(Color.RED);
                        break;
                    case 1:
                        viewHolder.cardView.setBackgroundColor(Color.GREEN);
                        break;
                    case 2:
                        viewHolder.cardView.setBackgroundColor(Color.BLUE);
                        break;

                }
            }
        };

        adapter.setAlgorithm(new FruchtermanReingoldAlgorithm());

        graphView.setAdapter(adapter);
        graphView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentNode = adapter.getNode(position);
                adapter.notifyDataChanged(currentNode);
                Snackbar.make(graphView, "Clicked on " + currentNode.getData().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * TODO
         * Given input:
         * 1. a graph that represents a tree (there is no cyclic node),
         * 2. a rootNode, and
         * 3. a target distance,
         * you have to traverse the graph and give the color to each node as below criteria:
         * 1. RootNode is purple
         * 2. Nodes with the distance are less than the target distance are colored green
         * 3. Nodes with the distance are equal to the target distance are colored orange
         * 4. Other Nodes are blue
         */
        traverseAndColorTheGraph(graph, graph.getNode(0), 2);
        Log.d(TAG, "setupAdapter: "+dist);
        for(int i = 0; i < dist.length; i++){
            Log.d(TAG, "setupAdapter: "+i+"-"+dist[i]);
        }
    }

    private void traverseAndColorTheGraph(Graph graph, Node rootNode, int target) {
        List<Node> x = graph.getNodes();
        List<Edge> y = graph.getEdges();
        for(int i = 0; i < x.size(); ++i){
            if(graph.getNode(i).getData() == rootNode.getData()){
                if(!isVisited[i]){
                    isVisited[i] = true;
                    //dist[i] = nodeDist;
                    Log.d(TAG, "traverse: " + rootNode.getData() + " visited "+ nodeDist);
                    boolean bool = false;
                    for(int j = 0; j < y.size(); j++) {
                        if (y.get(j).getSource().getData() == rootNode.getData() && !isVisited[i + 1]) {
                            bool = true;
                            nodeDist++;
                            traverseAndColorTheGraph(graph, y.get(j).getDestination(), target);
                        }
                    }
                    if(!bool){
                        nodeDist--;
                        Log.d(TAG, "traverse: fungsi else jalan "+nodeDist);
                        for(int j = 0; j < y.size(); j++) {
                            if (y.get(j).getDestination().getData() == rootNode.getData() && isVisited[j]) {
                               // nodeDist--;
                                Log.d(TAG, "traverse: fungsi else dapet node sebelumnya " + nodeDist);
                                traverseAndColorTheGraph(graph, y.get(j).getSource(), target);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "traverse: fungsi pindah node jalan " + nodeDist);
                    if(i+1 < x.size()) {
                        //nodeDist--;
                        if(isVisited[i+1]){
                            Log.d(TAG, "traverse: fungsi if pindah node "+nodeDist);
                            //nodeDist++;
                            //traverseAndColorTheGraph(graph, graph.getNode(i + 1), target);
                        } else {
                            Log.d(TAG, "traverse: fungsi else pindah node " + nodeDist);
                            //nodeDist++;
                            //traverseAndColorTheGraph(graph, graph.getNode(i + 1), target);
                        }
                        traverseAndColorTheGraph(graph, graph.getNode(i + 1), target);
                    }
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Graph createGraph() {
        final Graph graph = new Graph();
        final Node a = new Node(getNodeText());
        final Node b = new Node(getNodeText());
        final Node c = new Node(getNodeText());
        final Node d = new Node(getNodeText());
        final Node e = new Node(getNodeText());
        final Node f = new Node(getNodeText());
        final Node g = new Node(getNodeText());

        graph.addEdge(a, b);
        graph.addEdge(a, c);
        graph.addEdge(b, f);
        graph.addEdge(b, g);
        graph.addEdge(c, d);
        graph.addEdge(c, e);
        return graph;
    }

    private class ViewHolder {
        TextView textView;
        LinearLayout bgChanged;
        CardView cardView;

        ViewHolder(View view) {
            textView = view.findViewById(R.id.textView);
            bgChanged = view.findViewById(R.id.backgroud);
            cardView = view.findViewById(R.id.card_view);
        }
    }

    protected String getNodeText() {
        return "Node " + nodeCount++;
    }
}
