package top.flowowparty.domcer.core.transformers;

import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import top.flowowparty.domcer.core.TransformerManager;

/**
 * It will send a packet over 2097152 bytes to check if you use UView
 *
 * @author UView
 */
public class NettyCompressionDecoderTransformer {

    /**
     * Limit to 16777216 bytes
     *
     * @author UView
     */
    @TransformerManager.TransformTarget(className = "net.minecraft.network.NettyCompressionDecoder", methodNames = {"decode"}, desc = "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V")
    public static class decodeTransformer implements TransformerManager.IMethodTransformer {
        public void transform(ClassNode cn, MethodNode mn) {
            InsnList il = mn.instructions;
            ListIterator<AbstractInsnNode> iterator = il.iterator();

            while (iterator.hasNext()) {
                AbstractInsnNode node = (AbstractInsnNode) iterator.next();
                if (node instanceof LdcInsnNode) {
                    LdcInsnNode ln = (LdcInsnNode) node;
                    if (ln.cst instanceof Integer && (int) ln.cst == 2097152) {
                        ln.cst = 16777216;
                    }
                }
            }

        }
    }

    @TransformerManager.TransformTarget(className = "ei", methodNames = {"decode"}, desc = "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V")
    public static class eidecodeTransformer implements TransformerManager.IMethodTransformer {
        public void transform(ClassNode cn, MethodNode mn) {
            InsnList il = mn.instructions;
            ListIterator<AbstractInsnNode> iterator = il.iterator();

            while (iterator.hasNext()) {
                AbstractInsnNode node = (AbstractInsnNode) iterator.next();
                if (node instanceof LdcInsnNode) {
                    LdcInsnNode ln = (LdcInsnNode) node;
                    if (ln.cst instanceof Integer && (int) ln.cst == 2097152) {
                        ln.cst = 16777216;
                    }
                }
            }

        }
    }
}
 