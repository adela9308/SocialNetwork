package socialnetwork.comunity;

import java.util.*;

public class Graph<T> {

    private Map<T, List<T> > map = new HashMap<>();
    private Map<T,Boolean> connectedCompMap=new HashMap<>();;
    private long maxLengthPath;

    /**
     * adauga un vf nou in graf
     * @param s nodul adaugat
     */
    public void addVertex(T s)
    {
        map.put(s, new LinkedList<T>());
    }

    /**
     * adauga o muchie in graf
     * @param source vf de pornire
     * @param destination vf destinatie
     * @param bidirectional indica daca e graf orientat sau nu
     */
    public void addEdge(T source,
                        T destination,
                        boolean bidirectional)
    {

        if (!map.containsKey(source))
            addVertex(source);

        if (!map.containsKey(destination))
            addVertex(destination);

        map.get(source).add(destination);
        if (bidirectional == true) {
            map.get(destination).add(source);
        }
    }

    /**
     * determina nr de muchii
     * @return nr de muchii din graf
     */
    public int getVertexCount()
    {
       /* System.out.println("The graph has "
                + map.keySet().size()
                + " vertex");

        */
        return map.keySet().size();
    }

    /**
     * aplica alg DFS pe un graf(parcurge in adancime)
     * @param start-el de la care se incepe parcurgerea
     * @param visited-vectorul de vizitati
     */
    void DFS(T start,Map<T,Boolean>visited){

        Stack<T> stack=new Stack<>();
        stack.push(start);

        while(!stack.empty()){
            start=stack.peek();
            stack.pop();
            connectedCompMap.put(start,true);
            if(visited.get(start)==false)
                visited.put(start,true);

            for(T t:map.get(start)){
                if(visited.get(t)==false) stack.push(t);
            }
        }
    }

    /**
     * @return Nr decomunitati=nr de componente conexe
     */
    public int numarComunitati(){
        int n=getVertexCount();
        Map<T,Boolean> visited=new HashMap<>(n);
        for(T t:map.keySet())
            visited.put(t,false);

        int count=0;
        for(T t:map.keySet()){
            if(visited.get(t)==false){
                DFS(t,visited);
                count++;
            }
        }
        return count;
    }


    /**
     * det cel mai lung drum de la un vf la restul
     * @param vertex varful de inceput
     * @param visited marcheaza el vizitate deja
     * @param nr lungimea drumului la un mom dat
     */
    public void longestPathVertex(T vertex,Map<T,Boolean> visited,long nr){
        if (nr > maxLengthPath) {
            maxLengthPath = nr;
        }
        for(T i: map.keySet())
            if (map.get(vertex).contains(i) && !visited.get(i)) {
                visited.put(i,true);
                visited.put(vertex,true);
                nr++;
                longestPathVertex(i, visited, nr);
                nr--;
                visited.put(i,false);
                visited.put(vertex,false);
            }
    }

    /**
     * determina cel mai lung drum dintr o componenta conexa
     * @return lungimea drumului
     */
    public long longestPathConnectedComp(){
        Map<T,Boolean> visited=new HashMap<>();
        long maxim=-1;
        for(T vertex: connectedCompMap.keySet()) {
            for (T visitedVertex : connectedCompMap.keySet()) {
                visited.put(visitedVertex, false);
            }
            maxLengthPath = -1;
            longestPathVertex(vertex,visited,0);
            if(maxim<maxLengthPath)
                maxim=maxLengthPath;
        }
        return maxim;

    }

    /**
     *
     * @return componenta conexa cu cel mai lung drum
     */
    public List<T> longestPathGraph(){
        List<T> ids=new ArrayList<>();
        Map<T,Boolean> visited=new HashMap<>();
        for(T vertex: map.keySet()){
            visited.put(vertex,false);
        }
        long maxGraphPath=-1;
        long longestPathConnectedCompo;
        for(T vertex: visited.keySet()){
            if(!visited.get(vertex)) {
               // connectedCompMap=new HashMap<>();
                connectedCompMap.clear();
                DFS(vertex, visited);
                if(connectedCompMap.keySet().size()>1) {
                    longestPathConnectedCompo = longestPathConnectedComp();
                    if(maxGraphPath<longestPathConnectedCompo) {
                        maxGraphPath = longestPathConnectedCompo;
                        ids.clear();
                        for(T t:connectedCompMap.keySet())
                            ids.add(t);
                    }
                }
            }
        }
        return ids;
    }



}
