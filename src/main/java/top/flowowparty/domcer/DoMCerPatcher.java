package top.flowowparty.domcer;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import top.flowowparty.domcer.listener.PatcherListener;

@Mod(modid = DoMCerPatcher.MODID, name = DoMCerPatcher.NAME, version = DoMCerPatcher.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class DoMCerPatcher {
    public static final String MODID = "domcerpatcher";
    public static final String VERSION = "1.0";
    public static final String NAME = "DoMCer Patcher";

    private PatcherListener patcherListener;

    public static FMLEventChannel fakeCustomSkinLoaderChannel;
    public static FMLEventChannel fakeUViewChannel;

    @EventHandler
    public void init(FMLInitializationEvent e) {
        patcherListener = new PatcherListener();
        MinecraftForge.EVENT_BUS.register(patcherListener);
        fakeCustomSkinLoaderChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("CustomSkinLoader");
        fakeCustomSkinLoaderChannel.register(patcherListener);
        fakeUViewChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("UView");
        fakeUViewChannel.register(patcherListener);
    }

    /**
     * 您可以修改此方法来添加对Gui隐藏的支持
     *
     * @author ImFl0wow
     */
    public static void beforeScreenshot() {
        // TODO
    }
    /**
     * @author ImFl0wow
     */
    public static void afterScreenshot() {
        // TODO
    }

}
