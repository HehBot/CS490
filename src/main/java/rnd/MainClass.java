package rnd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import soot.Local;
import soot.MethodOrMethodContext;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.dava.internal.AST.ASTTryNode.container;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

public class MainClass {
    private static final String resDir = "./src/main/resources";

    public static void run(List<String> processDir, String mainClass) {
        Options.v().set_whole_program(true);
        Options.v().set_keep_line_number(true);
        Options.v().no_bodies_for_excluded();
        String[] inc = {"org.apache", "org.w3c"};
        Options.v().set_include(Arrays.asList(inc));
        Options.v().set_process_dir(processDir);
        // String reflLogPath = "reflection-log:" + benchmarkPath + "/refl.log";
        // Options.v().setPhaseOption("cg", reflLogPath);
        // Options.v().set_main_class("Harness");
        Options.v().set_main_class(mainClass);

        Scene.v().loadNecessaryClasses();

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

        // String[] args = {
        //     // "-cp", processDir, "-pp",
        //     "-f", "c",
        //     "-w",
        //     "-keep-line-number",
        //     "-no-writeout-body-releasing",
        //     "-p", "cg.spark", "enabled:true",
        //     "-p", "cg.spark", "verbose:true",
        //     "-p", "cg.spark", "propagator:worklist",
        //     "-p", "cg.spark", "simple-edges-bidirectional:false",
        //     "-p", "cg.spark", "on-fly-cg:true",
        //     "-p", "cg.spark", "set-impl:double",
        //     "-p", "cg.spark", "double-set-old:hybrid",
        //     "-p", "cg.spark", "double-set-new:hybrid",
        //     "-process-dir", processDir,
        //     "-main-class", mainClass
        // };

        // soot.Main.main(args);
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting...");

        run(Arrays.asList(resDir), "Test");

        System.out.println(("++++++++Classes, Fields and Methods++++++++++"));

        for (SootClass sc : Scene.v().getApplicationClasses()) {
            String s = sc.getName();
            System.out.println(s);
            System.out.println("    Methods:");
            for (SootMethod m : sc.getMethods()) {
                System.out.println("        " + m.getSignature());
            }
            System.out.println("    Fields:");
            for (SootField f : sc.getFields()) {
                System.out.println("        " + f.getSignature());
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

        System.out.println("++++++++++Field Modifications+++++++++");

        for (SootClass sc : Scene.v().getApplicationClasses()) {
            Map<SootMethod, List<SootField>> modified = SparkResult.getFieldModifications(sc);
            System.out.print(sc.toString() + " ");
            System.out.println(modified);
        }

        System.out.println("+++++++++++++++++SPARK++++++++++++++++");

        for(SootClass sc : Scene.v().getApplicationClasses()){
            for(SootMethod m : sc.getMethods()){
                if (m.isConstructor())
                    continue;
                Map<Integer, Local> locals = SparkResult.getLocals(m);
                System.out.println(m.getSignature());
                SparkResult.printLocalInstersects(locals);
                System.out.println();
            }
        }
    }
}
