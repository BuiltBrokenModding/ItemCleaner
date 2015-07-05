package com.builtbroken.itemclear.server;

import com.builtbroken.mc.prefab.commands.AbstractCommand;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Dark on 7/4/2015.
 */
public class CommandItemClear extends AbstractCommand
{
    public CommandItemClear()
    {
        super("itemclearer");
    }


    @Override
    public boolean handleEntityPlayerCommand(EntityPlayer player, String[] args)
    {
        return handleConsoleCommand(player, args);
    }

    @Override
    public boolean handleConsoleCommand(ICommandSender sender, String[] args)
    {
        boolean two = args.length > 1;
        if (args[0].equalsIgnoreCase("addBlock"))
        {
            if (two)
            {
                Object object = Block.getBlockFromName(args[1]);
                if (object instanceof Block)
                {
                    if (!TickHandler.destoryList.contains(Item.getItemFromBlock((Block) object)))
                    {
                        TickHandler.destoryList.add(Item.getItemFromBlock((Block) object));
                        sender.addChatMessage(new ChatComponentText("Block added to remove list"));
                        return true;
                    }
                    else
                    {
                        sender.addChatMessage(new ChatComponentText("List already contains that block"));
                        return true;
                    }
                }
                sender.addChatMessage(new ChatComponentText("Block was not found"));
                return true;
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Block name is required"));
                return true;
            }
        }
        else if (args[0].equalsIgnoreCase("addItem"))
        {
            if (two)
            {
                Object object = Item.itemRegistry.getObject(args[1]);
                if (object instanceof Item)
                {
                    if (!TickHandler.destoryList.contains(object))
                    {
                        TickHandler.destoryList.add((Item) object);
                        sender.addChatMessage(new ChatComponentText("Item added to remove list"));
                        return true;
                    }
                    else
                    {
                        sender.addChatMessage(new ChatComponentText("List already contains that item"));
                        return true;
                    }
                }
                sender.addChatMessage(new ChatComponentText("Item was not found"));
                return true;
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Item name is required"));
                return true;
            }
        }
        else if (args[0].equalsIgnoreCase("list"))
        {
            if (TickHandler.destoryList.size() == 0)
            {
                sender.addChatMessage(new ChatComponentText("nothing to list"));
                return true;
            }
            else
            {
                int pages = 1 + TickHandler.destoryList.size() / 5;
                int page = 0;
                if (two)
                {
                    try
                    {
                        page = Integer.parseInt("" + args[1]);
                    } catch (NumberFormatException e)
                    {
                        System.out.println("Invalid number " + args[1]);
                        sender.addChatMessage(new ChatComponentText("need a valid number"));
                        return true;
                    }
                }

                if (page >= pages)
                {
                    sender.addChatMessage(new ChatComponentText("page not found"));
                    return true;
                }

                sender.addChatMessage(new ChatComponentText("-====[Page " + (page + 1) + " of " + pages + "]====-"));
                int start = page * 5;
                for (int i = 0; i < 5; i++)
                {
                    if (start + i < TickHandler.destoryList.size())
                    {
                        Item item = TickHandler.destoryList.get(start + i);
                        String name;
                        if (item instanceof ItemBlock)
                        {
                            name = Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item));
                        }
                        else
                        {
                            name = Item.itemRegistry.getNameForObject(item);
                        }
                        sender.addChatMessage(new ChatComponentText("  " + name));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
