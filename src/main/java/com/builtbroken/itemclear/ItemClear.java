package com.builtbroken.itemclear;

import com.builtbroken.itemclear.server.CommandItemClear;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;

/**
 * Created by Dark on 7/4/2015.
 */
@Mod(modid = ItemClear.DOMAIN, name = ItemClear.NAME, version = ItemClear.VERSION, dependencies = ItemClear.DEPENDENCIES, acceptableRemoteVersions = "*")
public class ItemClear extends AbstractMod
{

    public static final String NAME = "Item Clearer";
    public static final String DOMAIN = "itemclear";
    public static final String PREFIX = DOMAIN + ":";

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static final String DEPENDENCIES = "required-after:voltzengine";

    @Mod.Instance(DOMAIN)
    public static ItemClear INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.itemclear.client.ClientProxy", serverSide = "com.builtbroken.itemclear.server.ServerProxy")
    public static CommonProxy proxy;

    public ItemClear()
    {
        super("itemclear");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }

    @Override
    public AbstractProxy getProxy()
    {
        return proxy;
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Setup command
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandItemClear());
    }

}
