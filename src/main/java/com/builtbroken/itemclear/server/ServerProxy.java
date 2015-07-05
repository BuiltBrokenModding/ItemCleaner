package com.builtbroken.itemclear.server;

import com.builtbroken.itemclear.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Created by Dark on 7/4/2015.
 */
public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
    }

    @Override
    public void init()
    {
        super.init();
        FMLCommonHandler.instance().bus().register(new TickHandler());
    }
}
