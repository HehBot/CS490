package rnd;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import soot.Body;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
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

    public static Map<Integer, Local> getLocals(SootMethod sm) {
        Map<Integer, Local> res = new HashMap<>();
        Body b = sm.retrieveActiveBody();
        for (Unit u : b.getUnits()) {
            int line = getLineNumber(u);
            System.out.print(line);
            System.out.println(" " + u.toString());
            for (ValueBox vb : u.getDefBoxes()) {
                Value v = vb.getValue();
                if (v instanceof Local)
                    res.put(line, (Local)v);
            }
        }
        return res;
    }

    public static Map<SootMethod, List<SootField>> getFieldModifications(SootClass sc) {
        Map<SootMethod, List<SootField>> modified = new HashMap<SootMethod, List<SootField>>();
        for (SootMethod sm : sc.getMethods()) {
            if (sm.isConstructor())
                continue;
            List<SootField> l = new ArrayList<SootField>();
            Body b = sm.retrieveActiveBody();
            for (Unit u : b.getUnits()) {
                if (u instanceof AssignStmt) {
                    AssignStmt a = (AssignStmt)u;
                    Value v = a.getLeftOp();
                    if (v instanceof FieldRef) {
                        FieldRef f = (FieldRef)v;
                        SootField sf = f.getField();
                        if (sf.getDeclaringClass() == sc)
                            l.add(sf);
                    }
                }
            }
            if (l.size() > 0)
                modified.put(sm, l);
        }
        return modified;
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
                if (l1.getType() != l2.getType())
                    continue;
                PointsToSet r2 = pta.reachingObjects(l2);
                if (line1 <= line2) {
                    System.out.println("[" + line1 + "," + line2 + "]\t " + l1.getType().toString() +" intersect? " + r1.hasNonEmptyIntersection(r2));
                }
            }
        }
    }
}