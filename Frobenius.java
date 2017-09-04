package frobenius;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.IndexMinPQ;


// directed weighted edges
class DirectedEdge implements Comparable<DirectedEdge> {
    private final int v;            // tail
    private final int w;            // head
    private final int weight;       // edge weights are only integers in the 
                                    // Frobenius graph
    public DirectedEdge(int v, int w, int weight) {
        this.v = v;
        this.w = w;
        this.weight = weight;
    }
    
    public int weight()
    {   return weight;  }
    
    public int from()
    {   return v;  }
    
    public int to()
    {   return w;  }
    
    public int compareTo(DirectedEdge that) {
        if      (this.weight() < that.weight()) return -1;
        else if (this.weight() > that.weight()) return 1;
        else                                    return 0;
    }
}

// Frobenius edge-weighted digraph
class FrobeniusGraph {
    private final int V;                        // number of vertices
    private int E;                              // number of edges
    private Bag<DirectedEdge>[] adj;     // adjacency lists
    
    public FrobeniusGraph(int a[]) {
        V = a[0];
        E = 0;
        
        adj = (Bag<DirectedEdge>[]) new Bag[V];
        for(int i=0; i < V; i++)
            adj[i] = new Bag<DirectedEdge>();
        
        System.out.print("Generating graph...");
        int r[];                            // remainders
        r = new int[a.length - 1];      
        for(int i=0; i<r.length; i++) {
            r[i] = a[i+1] % a[0];           // get each remainder: a1 % a0, a2 % a0, etc.
            AddEdges(a[0], r[i], a[i+1]);   // add a0 edges
        }
        System.out.println("done.");
    }
    
    public int V() { return V; }
    public int E() { return E; }
    
    public void addEdge(DirectedEdge e) {
        adj[e.from()].add(e);
        E++;
    }
    
    private void AddEdges(int a, int r, int weight) {
        // a is a[0] (first term)
        // r is result of a[i] % a0
        // weight is the a[i] (ith term)
        for(int j=0; j < a; j++)
            addEdge(new DirectedEdge(j, (j+r)%a, weight) );
    }
    
    public Iterable<DirectedEdge> adj(int v) {
        return adj[v];
    }
    
    public Iterable<DirectedEdge> edges() {
        LinkedList<DirectedEdge> list = new LinkedList<>();
        for(int i=0; i < V; i++) 
            for(DirectedEdge e : adj(i))
                list.add(e);
        return list;
    }
}

class DijkstraSP {
    private DirectedEdge[] edgeTo;
    private int[] distTo;
    private IndexMinPQ<Integer> pq;   
    
    public DijkstraSP(FrobeniusGraph G, int s) {
        edgeTo = new DirectedEdge[G.V()];
        distTo = new int[G.V()];
        pq = new IndexMinPQ<Integer>(G.V());
        
        for(int i=0; i < G.V(); i++)
            distTo[i] = 2147483647;     // if weights are greater than this 
        distTo[s] = 0;                  // then we cannot use type integer in Java
        
        System.out.print("Finding shortest paths...");
        
        pq.insert(s, 0);
        while(!pq.isEmpty()) 
            relax(G, pq.delMin());
        
        System.out.println("done.");
    }
    
    private void relax(FrobeniusGraph G, int v) {
        for(DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (distTo[v] + e.weight() < distTo[w]) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
                if(pq.contains(w))  pq.changeKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
        }
    }
    
    public int distTo(int v) {
        return distTo[v];
    }
}





public class Frobenius {
    
    private static int gcd(int a, int b) {
        int r;
        
        while(b!=0) {
            r = a % b;
            a = b;
            b = r;
        }
        return a;
    }
    
    public static boolean gcdEquals1(int a[]) {         
        int g = gcd(a[0], a[1]);                    // take GCD of first 2 terms
        
        for(int i=2; i<a.length; i++) {                     
                g = gcd(a[i], g);                   // take GCD of 
                if( g == 1 ) return true;           // GCD so far and next term
            }
        return false;                               
    }
    
    

    public static void main(String[] args) {
        
        int terms[] = {4093, 8191, 16381, 32749, 65521};
        int diameter = 0;
        int frobenius;
        
        Arrays.sort(terms);             // sort in increasing order
        
        if(!gcdEquals1(terms)) {        // ensure total GCD = 1
            System.out.println("GCD not equal to one - please try again.");
            return;
        }
        
        FrobeniusGraph graph = new FrobeniusGraph(terms);
        DijkstraSP paths = new DijkstraSP(graph, 0);
        
        for(int i=1; i<terms[0]; i++) {
            if(paths.distTo(i) > diameter)
                diameter = paths.distTo(i);     // take largest of shortest paths
        }
        
        // subtract first term provided diameter is larger
        frobenius = diameter < terms[0] ? diameter : diameter - terms[0];
        
        System.out.print("\nGiven " + terms.length + " terms: ");
        for(int i=0; i<terms.length; i++)
            System.out.print(terms[i] + " ");
        System.out.println("\nthe Frobenius # is: " + frobenius);
        
    }
    
}

/*
Generating graph...done.
Finding shortest paths...done.

Given 6 terms: 47 74 97 126 157 188 
the Frobenius # is: 481
*/

/*
Generating graph...done.
Finding shortest paths...done.

Given 5 terms: 4093 8191 16381 32749 65521 
the Frobenius # is: 6753481
BUILD SUCCESSFUL (total time: 1 second)
*/