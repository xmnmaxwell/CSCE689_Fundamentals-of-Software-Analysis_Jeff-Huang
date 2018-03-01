public class Transformer implements ClassFileTransformer {
    public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new Transformer());
    }
    public byte[] transform(ClassLoader loader,String cname, Class<?> c, ProtectionDomain d, byte[] cbuf) throws IllegalClassFormatException {
	    System.out.println("transforming class "+cname);
	    return cbuf;
    }
}
