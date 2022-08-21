package top.flowowparty.domcer.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import top.flowowparty.domcer.core.transformers.NettyCompressionDecoderTransformer;

/**
 * @author UView
 */
public class LaunchWrapper implements IClassTransformer {
    private static final TransformerManager.IClassTransformer[] CLASS_TRANSFORMERS = new TransformerManager.IClassTransformer[0];
    private static final TransformerManager.IMethodTransformer[] METHOD_TRANSFORMERS = new TransformerManager.IMethodTransformer[]{new NettyCompressionDecoderTransformer.decodeTransformer(), new NettyCompressionDecoderTransformer.eidecodeTransformer()};
    private final TransformerManager transformerManager = new TransformerManager(CLASS_TRANSFORMERS, METHOD_TRANSFORMERS);

    public byte[] transform(String obfClassName, String className, byte[] bytes) {
        if (!this.transformerManager.classMap.containsKey(className) && !this.transformerManager.map.containsKey(className)) {
            return bytes;
        }
        ClassNode cn = new ClassNode();
        if (bytes != null && bytes.length > 0) {
            ClassReader cr = new ClassReader(bytes);
            cr.accept(cn, 0);
        } else {
            cn.name = className.replace(".", "/");
            cn.version = 52;
            cn.superName = "java/lang/Object";
        }
        this.transformerManager.transform(cn);
        for (MethodNode mn : cn.methods) {
            String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(obfClassName, mn.name, mn.desc);
            String methodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(mn.desc);
            this.transformerManager.transform(cn, mn, className, methodName, methodDesc);
        }
        ClassWriter cw = new ClassWriter(1);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
