package rnd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Arrays;
import soot.Local;
import soot.MethodOrMethodContext;
// import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

public class MainClass {
    private static final String resDir = "./src/main/resources";

    private static void runSpark() {
        System.out.println("[spark] Starting analysis...");

        HashMap<String, String> opt = new HashMap<>();
        opt.put("enabled", "true");
        opt.put("verbose", "true");
        opt.put("propagator", "worklist");
        opt.put("simple-edges-bidirectional", "false");
        opt.put("on-fly-cg", "true");
        opt.put("set-impl", "double");
        opt.put("double-set-old", "hybrid");
        opt.put("double-set-new", "hybrid");
        SparkTransformer.v().transform("", opt);

        System.out.println("[spark] Done!");
    }

    public static void run(List<String> processDir, String mainClass) {
        Options.v().set_process_dir(processDir);
        Options.v().set_whole_program(true);
        Options.v().set_keep_line_number(true);

        Scene.v().loadNecessaryClasses();
        Scene.v().setMainClass(Scene.v().getSootClass(mainClass));

        runSpark();
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting...");

        run(Arrays.asList(resDir), "Test");

        System.out.println(("++++++++Classes and Methods++++++++++"));

        for (SootClass sc : Scene.v().getApplicationClasses()) {
            String s = sc.getName();
            System.out.println(s);
            for (SootMethod m : sc.getMethods()) {
                System.out.println("    " + m.getSignature());
            }
        }

        System.out.println(("+++++++++++++Call Graph++++++++++++++"));

        int numOfEdges = 0;
        CallGraph callGraph = Scene.v().getCallGraph();
        for(SootClass sc : Scene.v().getApplicationClasses()){
            for(SootMethod m : sc.getMethods()){
                Iterator<MethodOrMethodContext> targets = new Targets(callGraph.edgesOutOf(m));
                while (targets.hasNext()) {
                    numOfEdges++;
                    SootMethod tgt = (SootMethod) targets.next();
                    System.out.println(m + " may call " + tgt);
                }
            }
        }
        System.err.println("Total Edges:" + numOfEdges);

        System.out.println("+++++++++++++++SPARK++++++++++++++++++");

        Map<Integer, Local> locals = SparkResult.getLocals(Scene.v().getSootClass("Test").getMethodByName("go"), "Container");

        SparkResult.printLocalInstersects(locals);
    }
}