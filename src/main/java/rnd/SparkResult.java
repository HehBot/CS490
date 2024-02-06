package rnd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import soot.Body;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;

public class SparkResult {
    private static int getLineNumber(Unit u) {
        for (Tag o : u.getTags())
            if (o instanceof LineNumberTag)
                return Integer.parseInt(o.toString());
        System.err.println("[WARNING] Line tag missing!");
        return -1;
    }

    public static Map<Integer, Local> getLocals(SootMethod sm, String typeName) {
        Map<Integer, Local> res = new HashMap<>();
        Body b = sm.retrieveActiveBody();
        for (Unit u : b.getUnits()) {
            int line = getLineNumber(u);
            for (ValueBox vb : u.getDefBoxes()) {
                Value v = vb.getValue();
                if (v.getType().toString().equals(typeName) && v instanceof Local)
                    res.put(line, (Local)v);
            }
        }
        return res;
    }

    public static void printLocalInstersects(Map<Integer, Local> locals) {
        PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
        for (Entry<Integer, Local> e1 : locals.entrySet()) {
            int line1 = e1.getKey();
            Local l1 = e1.getValue();

            PointsToSet r1 = pta.reachingObjects(l1);
            for (Entry<Integer, Local> e2 : locals.entrySet()) {
                int line2 = e2.getKey();
                Local l2 = e2.getValue();
                PointsToSet r2 = pta.reachingObjects(l2);
                if (line1 <= line2) {
                    System.out.println("[" + line1 + "," + line2 + "]\t Container intersect? " + r1.hasNonEmptyIntersection(r2));
                }
            }
        }
    }
}
