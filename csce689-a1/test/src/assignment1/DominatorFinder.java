package assignment1;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;


public class DominatorFinder<N> {
    protected DirectedGraph<N> graph;
    protected Map<N,BitSet> nodeToFlowSet;
    protected Map<Integer,N> indexToNode;

    public DominatorFinder(DirectedGraph<N> graph){
        this.graph = graph;
        doAnalysis();
    }

    protected void doAnalysis(){
       //your code starts here    
    	Options.v().parse("My anlysis");
    	SootClass c = Scene.v().forceResolve("Analysis", SootClass.BODIES);
    	c.setApplicationClass();
    	Scene.v().loadNecessaryClasses();
    	SootMethod method = c.getMethodByName("myMethod");
    	List entryPoints = new ArrayList();
    	entryPoints.add(method);
    	Scene.v().setEntryPoints(entryPoints);
    	PackManager.v().runPacks();
    	
    	
    }  

    public DirectedGraph<N> getGraph(){
        return graph;
    }

    public List<N> getDominators(Object node){
        //reconstruct list of dominators from bitset
        List<N> result = new ArrayList<N>();
        BitSet bitSet = nodeToFlowSet.get(node);
        for(int i=0;i<bitSet.length();i++) {
            if(bitSet.get(i)) {
                result.add(indexToNode.get(i));
            }
        }
        return result;
    }
}