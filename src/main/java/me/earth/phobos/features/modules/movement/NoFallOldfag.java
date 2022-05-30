package me.earth.phobos.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoFallOldfag
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Predict));
    private final Setting<Boolean> disconnect = this.register(new Setting<Boolean>("Disconnect", false));
    private final Setting<Integer> fallDist = this.register(new Setting<Integer>("FallDistance", Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(30), v -> this.mode.getValue() == Mode.Old));
    BlockPos n1;

    public NoFallOldfag() {
        super("NoFallBypass", "nf", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (NoFallOldfag.nullCheck()) {
            return;
        }
        if (this.mode.getValue().equals("Predict") && NoFallOldfag.mc.player.fallDistance > (float)this.fallDist.getValue().intValue() && this.predict(new BlockPos(NoFallOldfag.mc.player.posX, NoFallOldfag.mc.player.posY, NoFallOldfag.mc.player.posZ))) {
            NoFallOldfag.mc.player.motionY = 0.0;
            NoFallOldfag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(NoFallOldfag.mc.player.posX, (double)this.n1.getY(), NoFallOldfag.mc.player.posZ, false));
            NoFallOldfag.mc.player.fallDistance = 0.0f;
            if (this.disconnect.getValue().booleanValue()) {
                NoFallOldfag.mc.player.connection.getNetworkManager().closeChannel((ITextComponent)new TextComponentString(ChatFormatting.GOLD + "NoFall"));
            }
        }
        if (this.mode.getValue().equals("Old") && NoFallOldfag.mc.player.fallDistance > (float)this.fallDist.getValue().intValue()) {
            NoFallOldfag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(NoFallOldfag.mc.player.posX, 0.0, NoFallOldfag.mc.player.posZ, false));
            NoFallOldfag.mc.player.fallDistance = 0.0f;
        }
    }

    private boolean predict(BlockPos blockPos) {
        Minecraft mc = Minecraft.getMinecraft();
        this.n1 = blockPos.add(0, -this.fallDist.getValue().intValue(), 0);
        return mc.world.getBlockState(this.n1).getBlock() != Blocks.AIR;
    }

    public static enum Mode {
        Predict,
        Old;

    }
}
