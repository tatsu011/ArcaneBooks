package com.haniitsu.arcanebooks.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.NotImplementedException;

public class PlayerJoinPacket implements IMessage
{
    public static class Handler implements IMessageHandler<PlayerJoinPacket, IMessage>
    {
        @Override
        public IMessage onMessage(PlayerJoinPacket message, MessageContext ctx)
        {
            throw new NotImplementedException("Not implemented yet.");
        }
        
        /*
        
        Version of the above method for Minecraft v1.8.
        
        @Override
        public IMessage onMessage(PlayerJoinPacket message, MessageContext ctx)
        {
            IThreadListener mainThread = Minecraft.getMinecraft();
            
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    throw new NotImplementedException("Not implemented yet.");
                }
            });
            return null; // no response in this case
        }
        
        */
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
}