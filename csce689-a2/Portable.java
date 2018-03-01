public class Portable {
    public static Logger LOG = new ConsoleLogger();
    private static final Type OBJECT_TYPE = Type.getType(Object.class);

    protected static final Class[] PORTABLE_ANNOTATIONS = new Class[]{
            Portable.class, PortableArray.class, PortableDate.class, PortableList.class, PortableMap.class, PortableSet.class};

    private static final Set<String> MAP_CLASSES = new HashSet<String>(Arrays.asList(
            Map.class.getName(), ConcurrentMap.class.getName(), ConcurrentNavigableMap.class.getName(),
            NavigableMap.class.getName(), SortedMap.class.getName(), ConcurrentHashMap.class.getName(),
            ConcurrentSkipListMap.class.getName(), HashMap.class.getName(), IdentityHashMap.class.getName(),
            LinkedHashMap.class.getName(), TreeMap.class.getName(), WeakHashMap.class.getName(), Hashtable.class.getName()
    ));
    private static final Set<String> SET_CLASSES = new HashSet<String>(Arrays.asList(
            Set.class.getName(), SortedSet.class.getName(), NavigableSet.class.getName(), ConcurrentSkipListSet.class.getName(),
            CopyOnWriteArraySet.class.getName(), HashSet.class.getName(), LinkedHashSet.class.getName(), TreeSet.class.getName()

    ));
    private static final Set<String> LIST_CLASSES = new HashSet<String>(Arrays.asList(
            List.class.getName(), ArrayList.class.getName(), CopyOnWriteArrayList.class.getName(), LinkedList.class.getName()
    ));

    private static final Set<String> COLLECTION_CLASSES = merge(new HashSet<String>(Arrays.asList(
            Collection.class.getName(), Queue.class.getName(), Deque.class.getName(), BlockingQueue.class.getName(), BlockingDeque.class.getName(),
            ArrayDeque.class.getName(), ArrayBlockingQueue.class.getName(), ConcurrentLinkedQueue.class.getName(),
            DelayQueue.class.getName(), LinkedBlockingQueue.class.getName(), LinkedBlockingDeque.class.getName(),
            PriorityBlockingQueue.class.getName(), PriorityQueue.class.getName(),
            Stack.class.getName(), SynchronousQueue.class.getName(), Vector.class.getName()
    )), SET_CLASSES, LIST_CLASSES);

    private static <T> Set<T> merge(Set<T>... sets) {
        Set<T> result = new HashSet<T>();
        for (Set<T> set : sets) {
            result.addAll(set);
        }
        return result;
    }

    public static boolean DEBUG = false;

    private ClassNode cn;
    private TreeMap<Integer, SortedSet<FieldNode>> properties = new TreeMap<Integer, SortedSet<FieldNode>>();

    public PortableTypeGenerator(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        cn = new ClassNode();
        reader.accept(cn, 0);
    }

    public UserType instrumentClass() {
        if (isPortableType() && !isEnum() && !isInstrumented()) {
            LOG.info("Instrumenting portable type " + cn.name);

            populatePropertyMap();
            implementDefaultConstructor();
            implementReadExternal();
            implementWriteExternal();

            // mark as instrumented
            cn.visibleAnnotations.add(new AnnotationNode(Type.getDescriptor(Instrumented.class)));

            Type serializerClass = getPofSerializer();
            SerializerType serializerType = serializerClass.getClassName().equals(PortableTypeSerializer.class.getName())
                                            ? null
                                            : new SerializerType(serializerClass.getClassName(), null);

            return new UserType(BigInteger.valueOf(getTypeId()),
                                cn.name.replace("/", "."),
                                serializerType);
        }

        return null;
    }

    private int getTypeId() {
        return (Integer) getAnnotationAttribute(getAnnotation(cn, PortableType.class), "id");
    }

    private Type getPofSerializer() {
        return (Type) getAnnotationAttribute(getAnnotation(cn, PortableType.class), "serializer");
    }

    public byte[] getClassBytes() {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(writer);
        return writer.toByteArray();
    }

    public void writeClass(OutputStream out) throws IOException {
        out.write(getClassBytes());
    }

    private void populatePropertyMap() {
        int count = 0;
        for (FieldNode fn : ((List<FieldNode>) cn.fields)) {
            AnnotationNode an = getAnnotation(fn, PORTABLE_ANNOTATIONS);
            if (an != null) {
                addProperty((Integer) getAnnotationAttribute(an, "since"), fn);
                count++;
            }
        }
        LOG.debug("Found " + count + " fields across " + properties.size() + " class version(s)");
    }

    private void addProperty(int version, FieldNode property) {
        SortedSet<FieldNode> nodes = properties.get(version);
        if (nodes == null) {
            nodes = new TreeSet<FieldNode>(new FieldNodeComparator());
            properties.put(version, nodes);
        }
        nodes.add(property);
    }

    private boolean isPortableType() {
        return hasAnnotation(cn, PortableType.class);
    }

    private boolean isEnum() {
        return (cn.access & ACC_ENUM) == ACC_ENUM;
    }

    private boolean isInstrumented() {
        return hasAnnotation(cn, Instrumented.class);
    }

    private boolean hasAnnotation(MemberNode node, Class annotationClass) {
        return getAnnotation(node, annotationClass) != null;
    }

    private AnnotationNode getAnnotation(MemberNode node, Class... annotationClasses) {
        if (node.visibleAnnotations != null) {
            for (Class annotationClass : annotationClasses) {
                String desc = Type.getDescriptor(annotationClass);
                for (AnnotationNode an : (List<AnnotationNode>) node.visibleAnnotations) {
                    if (desc.equals(an.desc)) return an;
                }
            }
        }
        return null;
    }