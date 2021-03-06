import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class GraphGen {
    private final int vertices;
    private int[][] twoD_array;
    private static final int NO_PARENT = -1;

    private static int[][] mapDist;
    private static int[][][] mapPath;

    public GraphGen(int v)
    {
        vertices = v + 1;
        twoD_array = new int[vertices][vertices];
        mapDist = new int[vertices][vertices];
        mapPath = new int[vertices][vertices][];
    }
    public void makeEdge(int to, int from, int edge)
    {
        try
        {
            twoD_array[to][from] = edge;
            twoD_array[from][to] = edge;
        }
        catch (ArrayIndexOutOfBoundsException index)
        {
            System.out.println("The vertices does not exists");
        }
    }
    public int getEdge(int to, int from)
    {
        try
        {
            return twoD_array[to][from];
        }
        catch (ArrayIndexOutOfBoundsException index)
        {
            System.out.println("The vertices does not exists");
        }
        return 0;
    }

    int getMinimumVertex(boolean [] mst, int [] key){
        int minKey = Integer.MAX_VALUE;
        int vertex = -1;
        for (int i = 1; i <vertices; i++) {
            if(mst[i]==false && minKey>key[i]){
                minKey = key[i];
                vertex = i;
            }
        }
        return vertex;
    }

    private static void dijkstra(int[][] adjacencyMatrix, int startVertex, int endVertex) {
        int nVertices = adjacencyMatrix[0].length - 1;
        System.out.println("nVertices: " + nVertices);
        // shortestDistances[i] will hold the
        // shortest distance from src to i
        int[] shortestDistances = new int[nVertices + 1];

        // added[i] will true if vertex i is
        // included / in shortest path tree
        // or shortest distance from src to
        // i is finalized
        boolean[] added = new boolean[nVertices + 1];

        // Initialize all distances as
        // INFINITE and added[] as false
        for (int vertexIndex = 1; vertexIndex <= nVertices; vertexIndex++) {
            shortestDistances[vertexIndex] = Integer.MAX_VALUE;
            added[vertexIndex] = false;
        }
        // Distance of source vertex from
        // itself is always 0
        shortestDistances[startVertex] = 0;

        // Parent array to store shortest
        // path tree
        int[] parents = new int[nVertices + 1];

        // The starting vertex does not
        // have a parent
        parents[startVertex] = NO_PARENT;

        // Find shortest path for all
        // vertices
        for (int i = 1; i <= nVertices; i++) {

            // Pick the minimum distance vertex
            // from the set of vertices not yet
            // processed. nearestVertex is
            // always equal to startNode in
            // first iteration.
            int nearestVertex = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for (int vertexIndex = 1; vertexIndex <= nVertices; vertexIndex++) {
                if (!added[vertexIndex] && shortestDistances[vertexIndex] < shortestDistance) {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }
            // Mark the picked vertex as
            // processed
            added[nearestVertex] = true;

            // Update dist value of the
            // adjacent vertices of the
            // picked vertex.
            for (int vertexIndex = 1; vertexIndex <= nVertices; vertexIndex++) {
                int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];
                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance + edgeDistance;
                }
            }
        }
        printSolution(startVertex, endVertex, shortestDistances, parents);
    }

    private static void printSolution(int startVertex, int endVertex, int[] distances, int[] parents) {
        int nVertices = distances.length - 1;

        System.out.println("nVertices: " + nVertices);
        System.out.print("Vertex\t Distance\tPath");

        mapDist[startVertex][endVertex] = distances[endVertex];

        System.out.print("\n" + startVertex + " -> " + endVertex + " \t\t " + distances[endVertex] + "\t\t");
        String pathOutput = "";
        pathOutput = printPath(endVertex, parents, pathOutput);

        String[] pathTemp = pathOutput.split(",");
        int[] path = new int[pathTemp.length];
        //Iterate from 1, because the delivery agent assumes it is already at the first location.
        for(int i = 2; i < pathTemp.length; i++) {
            path[i] = Integer.parseInt(pathTemp[i]);
        }
        mapPath[startVertex][endVertex] = path;

        for(int i = 2; i< pathTemp.length; i++){
            System.out.println("MAP PATH " + i + ": " + mapPath[startVertex][endVertex][i]);
        }
    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    private static String printPath(int currentVertex, int[] parents, String pathOutput) {
        // Base case : Source node has
        // been processed
        if (currentVertex == NO_PARENT) {
            return "";
        }
        pathOutput = printPath(parents[currentVertex], parents, pathOutput);
        pathOutput += ("," + String.valueOf(currentVertex));
        //System.out.print(currentVertex + " ");
        return pathOutput;
    }

    class ResultSet{
        int parent;
        int weight;
    }

    public boolean primMST(){
        boolean[] mst = new boolean[vertices];
        ResultSet[] resultSet = new ResultSet[vertices];
        int [] key = new int[vertices];

        //Initialize all the keys to infinity and
        //initialize resultSet for all the vertices
        for (int i = 1; i <vertices; i++) {
            key[i] = Integer.MAX_VALUE;
            resultSet[i] = new ResultSet();
        }

        //start from the vertex 0
        key[1] = 0;
        resultSet[1] = new ResultSet();
        resultSet[1].parent = -1;

        //create MST
        for (int i = 1; i <vertices; i++) {

            //get the vertex with the minimum key
            int vertex = getMinimumVertex(mst, key);

            //include this vertex in MST
            mst[vertex] = true;

            //iterate through all the adjacent vertices of above vertex and update the keys
            for (int j = 1; j <vertices; j++) {
                //check of the edge
                if(twoD_array[vertex][j]>0){
                    //check if this vertex 'j' already in mst and
                    //if no then check if key needs an update or not
                    if(mst[j]==false && twoD_array[vertex][j]<key[j]){
                        //update the key
                        key[j] = twoD_array[vertex][j];
                        //update the result set
                        resultSet[j].parent = vertex;
                        resultSet[j].weight = key[j];
                    }
                }
            }
        }
        //print mst
        printMST(resultSet);
        return false;
    }

    public void printMST(ResultSet[] resultSet){
        int total_min_weight = 0;

        System.out.println("Minimum Spanning Tree: ");
        for (int i = 1; i <vertices; i++) {
            total_min_weight += resultSet[i].weight;
        }
        System.out.println("Total minimum key: " + total_min_weight);
    }

    public static int getNumNodes(){
        int v;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of nodes: ");
        v = sc.nextInt();
        return  v;
    }
    public static int getMaxCon(){
        int eMax;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the maximum number of connections: ");
        eMax = sc.nextInt();
        return  eMax;
    }
    public static int getMinCon(){
        int eMin;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the minimum number of connections: ");
        eMin = sc.nextInt();
        return  eMin;
    }
    public static int getMaxDist(){
        int dMax;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the maximum distance: ");
        dMax = sc.nextInt();
        return  dMax;
    }
    public static int getMinDist(){
        int dMin;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the minimum distance: ");
        dMin = sc.nextInt();
        return  dMin;
    }

    public static GraphGen generateGraph(int v, int eMin, int eMax, Random r, int dMin, int dMax){
        GraphGen graphTemp = new GraphGen(v);
        int vTo, vFrom, dRand, eRand, count = 1;
        eRand = ThreadLocalRandom.current().nextInt(eMin, eMax + 1);
        while (count <= eRand) {
            vTo = r.nextInt(v) + 1;
            vFrom = r.nextInt(v) + 1;
            dRand = ThreadLocalRandom.current().nextInt(dMin, dMax + 1);
            if (vTo != vFrom) {
                graphTemp.makeEdge(vTo, vFrom, dRand);
                count++;
            }
        }
        return graphTemp;
    }


    public static void main(String args[]) {
        int v, dMin, dMax, eMin, eMax, sourceDijk, destDijk;
        boolean disGraph = true;
        Random r = new Random();
        Scanner sc = new Scanner(System.in);
        GraphGen graph = null;
        try {
            v = getNumNodes();
            eMin = getMinCon();
            eMax = getMaxCon();

            if (eMin > eMax) {
                System.out.println("Minimum cannot be greater than Maximum");
            }
            dMin = getMinDist();
            dMax = getMaxDist();

            if (dMin > dMax) {
                System.out.println("Minimum cannot be greater than Maximum");
            }
            int failed_attempts = 0;
            while (disGraph){
                graph = generateGraph(v, eMin, eMax, r, dMin, dMax);
                try {
                    disGraph = graph.primMST();
                } catch (Exception E) {
                    failed_attempts++;
                    System.out.println("Graph was disconnected, Trying again: " + failed_attempts);
                    disGraph = true;
                }
            }

            System.out.println("The two d array for the given graph is: ");
            System.out.print("  ");
            for (int i = 1; i <= v; i++)
                System.out.print(i + " ");
            System.out.println();
            for (int i = 1; i <= v; i++) {
                System.out.print(i + " ");
                for (int j = 1; j <= v; j++)
                    System.out.print(graph.getEdge(i, j) + " ");
                System.out.println();
            }

            System.out.println("EXPORTABLE 2D ARRAY:");
            for (int i = 1; i <= v; i++) {
                for (int j = 1; j <= v; j++)
                    if (j == v){
                        System.out.print(graph.getEdge(i, j) + "");
                        System.out.println();
                    }else{
                        System.out.print(graph.getEdge(i, j) + ", ");
                    }
            }
            for(int i = 1; i <= v; i++){
                for(int j = 1; j <= v; j++){
                    dijkstra(graph.twoD_array, i, j);
                }
            }
        } catch (Exception E) {
            System.out.println("Something went wrong");
        }
        sc.close();
    }
}
