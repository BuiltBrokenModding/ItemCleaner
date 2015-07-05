package com.builtbroken.itemclear.server;

import com.builtbroken.itemclear.ItemClear;
import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.lib.helper.NBTUtility;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import java.io.File;
import java.util.*;

/**
 * Created by Dark on 7/4/2015.
 */
public class TickHandler implements IVirtualObject
{
    public static boolean enabled = true;
    public static int WORLD_CHECK_MIN = 100;
    public static int CHUNK_CHECK_MIN = 10;
    public static int ITEM_KILL_COUNT = 10;
    public static List<Item> destoryList = new ArrayList();


    public static final TickHandler instance = new TickHandler();

    static
    {
        SaveManager.registerClass("ItemClearerTickHandler", TickHandler.class);
    }

    private TickHandler()
    {
        File file = getSaveFile();
        if (file != null && file.exists())
        {
            load(NBTUtility.loadData(file));
        }
        else
        {
            //Default values
            addBlockToDestoryList(Blocks.cobblestone);
            addBlockToDestoryList(Blocks.dirt);
            addBlockToDestoryList(Blocks.sapling);
            addBlockToDestoryList(Blocks.cactus);
            addBlockToDestoryList(Blocks.reeds);
            addItemToDestoryList(Items.wheat_seeds);
            addItemToDestoryList(Items.bone);
            addItemToDestoryList(Items.rotten_flesh);
        }
        SaveManager.register(this);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!enabled)
        {
            FMLCommonHandler.instance().bus().unregister(this);
            return;
        }
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            try
            {
                //TODO add profiler, this needs to run very fast
                //TODO add debug
                //TODO mutltithread it it becomes an issue? Also add config for threading
                if (event.world.loadedEntityList.size() > WORLD_CHECK_MIN && event.world.getWorldInfo().getWorldTime() % 3 == 0)
                {
                    if (event.world.getChunkProvider() instanceof ChunkProviderServer)
                    {
                        ChunkProviderServer provider = (ChunkProviderServer) event.world.getChunkProvider();
                        for (Object object : provider.loadedChunks)
                        {
                            if (object instanceof Chunk)
                            {
                                Chunk chunk = (Chunk) object;

                                //Check # of entities in chunk, this should only iterate a max of 16 times
                                int entities = 0;
                                for (Object l : chunk.entityLists)
                                {
                                    if (l instanceof Collection)
                                    {
                                        entities += ((Collection) l).size();
                                    }
                                }

                                //If entity count is high enough do calculations
                                if (entities > CHUNK_CHECK_MIN)
                                {
                                    HashMap<Item, List<EntityItem>> item_map = new HashMap();
                                    for (Object l : chunk.entityLists)
                                    {
                                        if (l instanceof List)
                                        {
                                            List<Entity> list = (List) l;
                                            for (Entity entity : list)
                                            {
                                                if (entity instanceof EntityItem && ((EntityItem) entity).getEntityItem() != null && destoryList.contains(((EntityItem) entity).getEntityItem().getItem()))
                                                {
                                                    if (!item_map.containsKey(((EntityItem) entity).getEntityItem().getItem()))
                                                    {
                                                        item_map.put(((EntityItem) entity).getEntityItem().getItem(), new ArrayList<EntityItem>());
                                                    }
                                                    item_map.get(((EntityItem) entity).getEntityItem().getItem()).add((EntityItem) entity);
                                                }
                                            }
                                        }
                                    }

                                    //Kill items if number of entities is higher than min limit
                                    for (Map.Entry<Item, List<EntityItem>> entry : item_map.entrySet())
                                    {
                                        if (entry.getValue() != null && entry.getValue().size() > ITEM_KILL_COUNT)
                                        {
                                            debug("Removing " + entry.getValue().size() + " of " + entry.getKey().getUnlocalizedName() + "  in chunk[" + chunk.xPosition + ", " + chunk.zPosition + "]");
                                            for (EntityItem item : entry.getValue())
                                            {
                                                item.setDead();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e)
            {
                ItemClear.INSTANCE.logger().catching(e);
            }
        }
    }

    public void debug(String msg)
    {
        if (Engine.runningAsDev)
            ItemClear.INSTANCE.logger().info("[TickHandler]" + msg);
    }

    public void addBlockToDestoryList(Block block)
    {
        if (block != null)
        {
            addItemToDestoryList(Item.getItemFromBlock(block));
        }
    }

    public void addItemToDestoryList(Item item)
    {
        if (item != null && !destoryList.contains(item))
            destoryList.add(item);
    }


    @Override
    public boolean shouldSaveForWorld(World world)
    {
        return world != null && world.provider.dimensionId == 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("list"))
        {
            NBTTagCompound list = nbt.getCompoundTag("list");
            int count = list.getInteger("count");
            for (int i = 0; i < count; i++)
            {
                String name = list.getString("c" + i);
                Object item = Item.itemRegistry.getObject(name);
                if (item instanceof Item)
                {
                    destoryList.add((Item) item);
                }
                else
                {
                    Object block = Block.blockRegistry.getObject(name);
                    if (block instanceof Block)
                    {
                        destoryList.add(Item.getItemFromBlock((Block) block));
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagCompound list = new NBTTagCompound();
        int c = 0;
        for (Item item : destoryList)
        {
            String name;
            if (item instanceof ItemBlock)
            {
                name = Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item));
            }
            else
            {
                name = Item.itemRegistry.getNameForObject(item);
            }
            list.setString("c" + c, name);
            c++;
        }
        list.setInteger("count", c);
        nbt.setTag("list", list);
        return nbt;
    }

    @Override
    public File getSaveFile()
    {
        return new File(NBTUtility.getSaveDirectory(), "/bbm/itemclearer/data.dat");
    }

    @Override
    public void setSaveFile(File file)
    {

    }
}
