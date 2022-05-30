package me.earth.phobos.mixin.mixins;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.RenderItemEvent;
import me.earth.phobos.features.modules.render.GlintModify;
import me.earth.phobos.features.modules.render.ViewModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderItem.class})
public
class MixinRenderItem {

    @Shadow
    private
    void renderModel ( IBakedModel model , int color , ItemStack stack ) {
    }

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
    private
    int renderEffect ( int oldValue ) {
        return Phobos.moduleManager.getModuleByName ( "GlintModify" ).isEnabled ( ) ? GlintModify.getColor ( 1 , 1 ).getRGB ( )
                : oldValue;
    }

    @Inject(method = {"renderItemModel"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift = At.Shift.BEFORE)})
    private
    void renderItemModel ( ItemStack stack , IBakedModel bakedModel , ItemCameraTransforms.TransformType transform , boolean leftHanded , CallbackInfo ci ) {
        RenderItemEvent event = new RenderItemEvent (
                0 , 0 , 0 ,
                0 , 0 , 0 ,
                0.0 , 0.0 , 1.0 ,
                0.0 , 0.0 , 0.0 ,
                1.0 , 1.0 , 1.0 , 1.0 ,
                1.0 , 1.0 // , 1.0 , 1.0
        );
        MinecraftForge.EVENT_BUS.post ( event );
        if ( ViewModel.getInstance ( ).isEnabled ( ) ) {
            if ( ! leftHanded ) {
                GlStateManager.scale ( event.getMainHandScaleX ( ) , event.getMainHandScaleY ( ) , event.getMainHandScaleZ ( ) );
            } else {
                GlStateManager.scale ( event.getOffHandScaleX ( ) , event.getOffHandScaleY ( ) , event.getOffHandScaleZ ( ) );
            }
        }

    }
}
