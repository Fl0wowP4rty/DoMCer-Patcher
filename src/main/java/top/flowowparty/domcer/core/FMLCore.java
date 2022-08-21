package top.flowowparty.domcer.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * @author UView
 */
@IFMLLoadingPlugin.SortingIndex(value = 0x7FFFFFFF)
public class FMLCore implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return new String[]{LaunchWrapper.class.getName()};
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
