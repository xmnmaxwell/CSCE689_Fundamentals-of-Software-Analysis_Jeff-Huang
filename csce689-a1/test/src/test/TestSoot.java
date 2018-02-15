package test;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.*;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import java.io.File;

public class TestSoot extends BodyTransformer {
	public static void main(String[] args)	{
	    String mainclass = "HelloThread";

	    //set classpath
	    String bootpath = System.getProperty("sun.boot.class.path");
	    String javapath = System.getProperty("java.class.path");
	    String path = bootpath + File.pathSeparator +javapath;
	    Scene.v().setSootClassPath(path);

            //add an intra-procedural analysis phase to Soot
	    TestSoot analysis = new TestSoot();
	    PackManager.v().getPack("jtp").add(new Transform("jtp.TestSoot", analysis));

            //load and set main class
	    Options.v().set_app(true);
	    SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
	    Scene.v().setMainClass(appclass);
	    Scene.v().loadNecessaryClasses();
            
            //start working
	    PackManager.v().runPacks();
	}