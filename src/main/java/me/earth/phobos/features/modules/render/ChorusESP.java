package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.ChorusEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public
class ChorusESP
        extends Module {
    private final Setting < Integer > time = this.register ( new Setting <> ( "Duration" , 500 , 50 , 3000 ) );
    private final Setting < Boolean > box = this.register ( new Setting <> ( "Box" , true ) );
    private final Setting < Boolean > outline = this.register ( new Setting <> ( "Outline" , true ) );
    private final Setting < Integer > boxR = this.register ( new Setting <> ( "BoxR" , 180 , 0 , 255 , v -> this.box.getValue ( ) ) );
    private final Setting < Integer > boxG = this.register ( new Setting <> ( "BoxG" , 0 , 0 , 255 , v -> this.box.getValue ( ) ) );
    private final Setting < Integer > boxB = this.register ( new Setting <> ( "BoxB" , 180 , 0 , 255 , v -> this.box.getValue ( ) ) );
    private final Setting < Integer > boxA = this.register ( new Setting <> ( "BoxA" , 180 , 0 , 255 , v -> this.box.getValue ( ) ) );
    private final Setting < Float > lineWidth = this.register ( new Setting <> ( "LineWidth" , 1.0f , 0.1f , 5.0f , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > outlineR = this.register ( new Setting <> ( "OutlineR" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > outlineG = this.register ( new Setting <> ( "OutlineG" , 0 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > outlineB = this.register ( new Setting <> ( "OutlineB" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > outlineA = this.register ( new Setting <> ( "OutlineA" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Timer timer = new Timer ( );
    private double x;
    private double y;
    private double z;
    // TODO: 7/28/2021 chorus switch and chorus rotate
    public
    ChorusESP ( ) {
        super ( "ChorusESP" , "Renders a chorus sound packet" , Module.Category.RENDER , true , false , false );
    }

    @SubscribeEvent
    public
    void onChorus ( ChorusEvent event ) {
        this.x = event.getChorusX ( );
        this.y = event.getChorusY ( );
        this.z = event.getChorusZ ( );
        this.timer.reset ( );
    }

    @Override
    public
    void onRender3D ( Render3DEvent render3DEvent ) {
        if ( timer.passedMs ( time.getValue ( ) ) ) return;
        AxisAlignedBB pos = RenderUtil.interpolateAxis ( new AxisAlignedBB ( x - 0.3 , y , z - 0.3 , x + 0.3 , y + 1.8 , z + 0.3 ) );
        if ( this.outline.getValue ( ) )
            RenderUtil.drawBlockOutline ( pos , new Color ( this.outlineR.getValue ( ) , this.outlineG.getValue ( ) , this.outlineB.getValue ( ) , this.outlineA.getValue ( ) ) , this.lineWidth.getValue ( ) );
        if ( this.box.getValue ( ) )
            RenderUtil.drawFilledBox ( pos , ColorUtil.toRGBA ( this.boxR.getValue ( ) , this.boxG.getValue ( ) , this.boxB.getValue ( ) , this.boxA.getValue ( ) ) );
    }
}