package com.builtbroken.itemclear.server;

import com.builtbroken.itemclear.CommonProxy;
import com.builtbroken.itemclear.ItemClear;
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
        TickHandler.WORLD_CHECK_MIN = ItemClear.INSTANCE.getConfig().getInt("World_Check_Min", "TickHandler", TickHandler.WORLD_CHECK_MIN, 10, 10000, "Controls when the tick handler scans the world. Value is number of entities in each world");
        TickHandler.CHUNK_CHECK_MIN = ItemClear.INSTANCE.getConfig().getInt("Chunk_Check_Min", "TickHandler", TickHandler.WORLD_CHECK_MIN, 10, 10000, "Controls when the tick handler scans a chunk. Value is number of entities in each chunk");
        TickHandler.ITEM_KILL_COUNT = ItemClear.INSTANCE.getConfig().getInt("Item_Kill_Trigger", "TickHandler", TickHandler.WORLD_CHECK_MIN, 10, 10000, "Controls when the tick handler removes items. Value is number of entities not items in a stack");

        FMLCommonHandler.instance().bus().register(TickHandler.instance);
    }
}
