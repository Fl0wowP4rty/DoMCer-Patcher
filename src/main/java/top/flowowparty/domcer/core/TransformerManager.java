package top.flowowparty.domcer.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author UView
 */
public class TransformerManager {
    public Map<String, IClassTransformer> classMap = new HashMap<String, IClassTransformer>();
    public Map<String, Map<String, IMethodTransformer>> map = new HashMap<String, Map<String, IMethodTransformer>>();

    public TransformerManager(IMethodTransformer... methodTransformers) {
        this(new IClassTransformer[0], methodTransformers);
    }

    public TransformerManager(IClassTransformer[] classTransformers, IMethodTransformer[] methodTransformers) {
        TransformTarget tt;
        for (IClassTransformer iClassTransformer : classTransformers) {
            tt = this.getTransformTarget(iClassTransformer.getClass());
            if (tt == null) continue;
            this.addClassTransformer(tt.className(), iClassTransformer);
        }
        for (IMethodTransformer iMethodTransformer : methodTransformers) {
            tt = this.getTransformTarget(iMethodTransformer.getClass());
            if (tt == null) continue;
            this.addMethodTransformer(tt, tt.className(), iMethodTransformer);
        }
    }

    public TransformTarget getTransformTarget(Class<?> cl) {
        if (!cl.isAnnotationPresent(TransformTarget.class)) {
            return null;
        }
        return cl.getAnnotation(TransformTarget.class);
    }

    private void addClassTransformer(String className, IClassTransformer transformer) {
        if (!this.classMap.containsKey(className)) {
            this.classMap.put(className, transformer);
        }
    }

    public void addMethodTransformer(TransformTarget target, String className, IMethodTransformer transformer) {
        if (!this.map.containsKey(className)) {
            this.map.put(className, new HashMap());
        }
        for (String methodName : target.methodNames()) {
            this.map.get(className).put(methodName + target.desc(), transformer);
        }
    }

    public ClassNode transform(ClassNode classNode) {
        IClassTransformer transformer = this.classMap.get(FMLDeobfuscatingRemapper.INSTANCE.map(classNode.name).replace("/", "."));
        if (transformer != null) {
            try {
                transformer.transform(classNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classNode;
    }

    public MethodNode transform(ClassNode classNode, MethodNode methodNode, String className, String methodName, String methodDesc) {
        Map<String, IMethodTransformer> transMap = this.map.get(className);
        String methodTarget = methodName + methodDesc;
        if (transMap != null && transMap.containsKey(methodTarget)) {
            try {
                transMap.get(methodTarget).transform(classNode, methodNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return methodNode;
    }

    public static interface IMethodTransformer {
        public void transform(ClassNode var1, MethodNode var2);
    }

    public static interface IClassTransformer {
        public void transform(ClassNode var1);
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE})
    public static @interface TransformTarget {
        public String className();

        public String[] methodNames() default {};

        public String desc() default "";
    }
}
