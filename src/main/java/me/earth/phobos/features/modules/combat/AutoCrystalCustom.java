package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCrystalCustom extends Module {
  private final Timer placeTimer = new Timer();
  
  private final Timer breakTimer = new Timer();
  
  private final Timer preditTimer = new Timer();
  
  private final Timer manualTimer = new Timer();
  
  private final Setting<Integer> attackFactor = register(new Setting("PredictDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  public Setting<Boolean> place = register(new Setting("Place", Boolean.valueOf(true)));
  
  public Setting<Float> placeDelay = register(new Setting("PlaceDelay", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(300.0F)));
  
  public Setting<Float> placeRange = register(new Setting("PlaceRange", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
  
  public Setting<Boolean> explode = register(new Setting("Break", Boolean.valueOf(true)));
  
  public Setting<Boolean> packetBreak = register(new Setting("PacketBreak", Boolean.valueOf(true)));
  
  public Setting<Boolean> predicts = register(new Setting("Predict", Boolean.valueOf(true)));
  
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  public Setting<Float> breakDelay = register(new Setting("BreakDelay", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(300.0F)));
  
  public Setting<Float> breakRange = register(new Setting("BreakRange", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
  
  public Setting<Float> breakWallRange = register(new Setting("BreakWallRange", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
  
  public Setting<Boolean> opPlace = register(new Setting("1.13 Place", Boolean.valueOf(true)));
  
  public Setting<Boolean> suicide = register(new Setting("AntiSuicide", Boolean.valueOf(true)));
  
  public Setting<Boolean> autoswitch = register(new Setting("AutoSwitch", Boolean.valueOf(true)));
  
  public Setting<Boolean> ignoreUseAmount = register(new Setting("IgnoreUseAmount", Boolean.valueOf(true)));
  
  public Setting<Integer> wasteAmount = register(new Setting("UseAmount", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(5)));
  
  public Setting<Boolean> facePlaceSword = register(new Setting("FacePlaceSword", Boolean.valueOf(true)));
  
  public Setting<Float> targetRange = register(new Setting("TargetRange", Float.valueOf(4.0F), Float.valueOf(1.0F), Float.valueOf(12.0F)));
  
  public Setting<Float> minDamage = register(new Setting("MinDamage", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
  
  public Setting<Float> facePlace = register(new Setting("FacePlaceHP", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(36.0F)));
  
  public Setting<Float> breakMaxSelfDamage = register(new Setting("BreakMaxSelf", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(12.0F)));
  
  public Setting<Float> breakMinDmg = register(new Setting("BreakMinDmg", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
  
  public Setting<Float> minArmor = register(new Setting("MinArmor", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(80.0F)));
  
  public Setting<SwingMode> swingMode = register(new Setting("Swing", SwingMode.MainHand));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true)));
  
  public Setting<Boolean> renderDmg = register(new Setting("RenderDmg", Boolean.valueOf(true)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true)));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  EntityEnderCrystal crystal;
  
  private EntityLivingBase target;
  
  private BlockPos pos;
  
  private int hotBarSlot;
  
  private boolean armor;
  
  private boolean armorTarget;
  
  private int crystalCount;
  
  private int predictWait;
  
  private int predictPackets;
  
  private boolean packetCalc;
  
  private float yaw = 0.0F;
  
  private EntityLivingBase realTarget;
  
  private int predict;
  
  private float pitch = 0.0F;
  
  private boolean rotating = false;
  
  public AutoCrystalCustom() {
    super("OyveyAutoCrystal", "Automatically places crystals (custom version)", Module.Category.COMBAT, true, false, false);
  }
  
  public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
    ArrayList<BlockPos> circleblocks = new ArrayList<>();
    int cx = loc.getX();
    int cy = loc.getY();
    int cz = loc.getZ();
    int x = cx - (int)r;
    while (x <= cx + r) {
      int z = cz - (int)r;
      while (z <= cz + r) {
        int y = sphere ? (cy - (int)r) : cy;
        while (true) {
          float f = sphere ? (cy + r) : (cy + h), f2 = f;
          if (y >= f)
            break; 
          double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
          if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
            BlockPos l = new BlockPos(x, y + plus_y, z);
            circleblocks.add(l);
          } 
          y++;
        } 
        z++;
      } 
      x++;
    } 
    return circleblocks;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && ((Boolean)this.rotate.getValue()).booleanValue() && this.rotating && event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      packet.yaw = this.yaw;
      packet.pitch = this.pitch;
      this.rotating = false;
    } 
  }
  
  private void rotateTo(Entity entity) {
    if (((Boolean)this.rotate.getValue()).booleanValue()) {
      float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
      this.yaw = angle[0];
      this.pitch = angle[1];
      this.rotating = true;
    } 
  }
  
  private void rotateToPos(BlockPos pos) {
    if (((Boolean)this.rotate.getValue()).booleanValue()) {
      float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
      this.yaw = angle[0];
      this.pitch = angle[1];
      this.rotating = true;
    } 
  }
  
  public void onEnable() {
    this.placeTimer.reset();
    this.breakTimer.reset();
    this.predictWait = 0;
    this.hotBarSlot = -1;
    this.pos = null;
    this.crystal = null;
    this.predict = 0;
    this.predictPackets = 1;
    this.target = null;
    this.packetCalc = false;
    this.realTarget = null;
    this.armor = false;
    this.armorTarget = false;
  }
  
  public void onDisable() {
    this.rotating = false;
  }
  
  public void onTick() {
    onCrystal();
  }
  
  public String getDisplayInfo() {
    if (this.realTarget != null)
      return this.realTarget.getName(); 
    return null;
  }
  
  public void onCrystal() {
    if (AutoCrystal.mc.world == null || AutoCrystal.mc.player == null)
      return; 
    this.realTarget = null;
    manualBreaker();
    this.crystalCount = 0;
    if (!((Boolean)this.ignoreUseAmount.getValue()).booleanValue())
      for (Entity crystal : AutoCrystal.mc.world.loadedEntityList) {
        if (!(crystal instanceof EntityEnderCrystal) || !IsValidCrystal(crystal))
          continue; 
        boolean count = false;
        double damage = calculateDamage(this.target.getPosition().getX() + 0.5D, this.target.getPosition().getY() + 1.0D, this.target.getPosition().getZ() + 0.5D, (Entity)this.target);
        if (damage >= ((Float)this.minDamage.getValue()).floatValue())
          count = true; 
        if (!count)
          continue; 
        this.crystalCount++;
      }  
    this.hotBarSlot = -1;
    if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
      int crystalSlot = (AutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? AutoCrystal.mc.player.inventory.currentItem : -1, n = crystalSlot;
      if (crystalSlot == -1)
        for (int l = 0; l < 9; ) {
          if (AutoCrystal.mc.player.inventory.getStackInSlot(l).getItem() != Items.END_CRYSTAL) {
            l++;
            continue;
          } 
          crystalSlot = l;
          this.hotBarSlot = l;
        }  
      if (crystalSlot == -1) {
        this.pos = null;
        this.target = null;
        return;
      } 
    } 
    if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && AutoCrystal.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
      this.pos = null;
      this.target = null;
      return;
    } 
    if (this.target == null)
      this.target = (EntityLivingBase)getTarget(); 
    if (this.target == null) {
      this.crystal = null;
      return;
    } 
    if (this.target.getDistance((Entity)AutoCrystal.mc.player) > 12.0F) {
      this.crystal = null;
      this.target = null;
    } 
    this.crystal = AutoCrystal.mc.world.loadedEntityList.stream().filter(this::IsValidCrystal).map(p_Entity -> (EntityEnderCrystal)p_Entity).min(Comparator.comparing(p_Entity -> Float.valueOf(this.target.getDistance((Entity)p_Entity)))).orElse(null);
    if (this.crystal != null && ((Boolean)this.explode.getValue()).booleanValue() && this.breakTimer.passedMs(((Float)this.breakDelay.getValue()).longValue())) {
      this.breakTimer.reset();
      if (((Boolean)this.packetBreak.getValue()).booleanValue()) {
        rotateTo((Entity)this.crystal);
        AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)this.crystal));
      } else {
        rotateTo((Entity)this.crystal);
        AutoCrystal.mc.playerController.attackEntity((EntityPlayer)AutoCrystal.mc.player, (Entity)this.crystal);
      } 
      if (this.swingMode.getValue() == SwingMode.MainHand) {
        AutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
      } else if (this.swingMode.getValue() == SwingMode.OffHand) {
        AutoCrystal.mc.player.swingArm(EnumHand.OFF_HAND);
      } 
    } 
    if (this.placeTimer.passedMs(((Float)this.placeDelay.getValue()).longValue()) && ((Boolean)this.place.getValue()).booleanValue()) {
      this.placeTimer.reset();
      double damage = 0.5D;
      for (BlockPos blockPos : placePostions(((Float)this.placeRange.getValue()).floatValue())) {
        double targetRange;
        if (blockPos == null || this.target == null || !AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty() || (targetRange = this.target.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ())) > ((Float)this.targetRange.getValue()).floatValue() || this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)
          continue; 
        double targetDmg = calculateDamage(blockPos.getX() + 0.5D, blockPos.getY() + 1.0D, blockPos.getZ() + 0.5D, (Entity)this.target);
        this.armor = false;
        for (ItemStack is : this.target.getArmorInventoryList()) {
          float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
          float red = 1.0F - green;
          int dmg = 100 - (int)(red * 100.0F);
          if (dmg > ((Float)this.minArmor.getValue()).floatValue())
            continue; 
          this.armor = true;
        } 
        if (targetDmg < ((Float)this.minDamage.getValue()).floatValue() && (((Boolean)this.facePlaceSword.getValue()).booleanValue() ? (this.target.getAbsorptionAmount() + this.target.getHealth() > ((Float)this.facePlace.getValue()).floatValue()) : (AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword || this.target.getAbsorptionAmount() + this.target.getHealth() > ((Float)this.facePlace.getValue()).floatValue())) && (((Boolean)this.facePlaceSword.getValue()).booleanValue() ? !this.armor : (AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword || !this.armor)))
          continue; 
        double selfDmg;
        if (((selfDmg = calculateDamage(blockPos.getX() + 0.5D, blockPos.getY() + 1.0D, blockPos.getZ() + 0.5D, (Entity)AutoCrystal.mc.player)) + (((Boolean)this.suicide.getValue()).booleanValue() ? 2.0D : 0.5D) >= (AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) && selfDmg >= targetDmg && targetDmg < (this.target.getHealth() + this.target.getAbsorptionAmount())) || damage >= targetDmg)
          continue; 
        this.pos = blockPos;
        damage = targetDmg;
      } 
      if (damage == 0.5D) {
        this.pos = null;
        this.target = null;
        this.realTarget = null;
        return;
      } 
      if (this.hotBarSlot != -1 && ((Boolean)this.autoswitch.getValue()).booleanValue() && !AutoCrystal.mc.player.isPotionActive(MobEffects.WEAKNESS))
        AutoCrystal.mc.player.inventory.currentItem = this.hotBarSlot; 
      if (!((Boolean)this.ignoreUseAmount.getValue()).booleanValue()) {
        int crystalLimit = ((Integer)this.wasteAmount.getValue()).intValue();
        if (this.crystalCount >= crystalLimit)
          return; 
        if (damage < ((Float)this.minDamage.getValue()).floatValue())
          crystalLimit = 1; 
        if (this.crystalCount < crystalLimit && this.pos != null) {
          rotateToPos(this.pos);
          AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
        } 
      } else if (this.pos != null) {
        rotateToPos(this.pos);
        AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
      } 
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
  public void onPacketReceive(PacketEvent.Receive event) {
    SPacketSpawnObject packet;
    if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject)event.getPacket()).getType() == 51 && ((Boolean)this.predicts.getValue()).booleanValue() && this.preditTimer.passedMs(((Integer)this.attackFactor.getValue()).longValue()) && ((Boolean)this.predicts.getValue()).booleanValue() && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.packetBreak.getValue()).booleanValue() && this.target != null) {
      if (!isPredicting(packet))
        return; 
      CPacketUseEntity predict = new CPacketUseEntity();
      predict.entityId = packet.getEntityID();
      predict.action = CPacketUseEntity.Action.ATTACK;
      AutoCrystal.mc.player.connection.sendPacket((Packet)predict);
    } 
  }
  
  private boolean isPredicting(SPacketSpawnObject packet) {
    BlockPos packPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
    if (AutoCrystal.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > ((Float)this.breakRange.getValue()).floatValue())
      return false; 
    if (!canSeePos(packPos) && AutoCrystal.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > ((Float)this.breakWallRange.getValue()).floatValue())
      return false; 
    double targetDmg = calculateDamage(packet.getX() + 0.5D, packet.getY() + 1.0D, packet.getZ() + 0.5D, (Entity)this.target);
    if (EntityUtil.isInHole((Entity)AutoCrystal.mc.player) && targetDmg >= 1.0D)
      return true; 
    double selfDmg = calculateDamage(packet.getX() + 0.5D, packet.getY() + 1.0D, packet.getZ() + 0.5D, (Entity)AutoCrystal.mc.player);
    double d = ((Boolean)this.suicide.getValue()).booleanValue() ? 2.0D : 0.5D;
    if (selfDmg + d < (AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) && targetDmg >= (this.target.getAbsorptionAmount() + this.target.getHealth()))
      return true; 
    this.armorTarget = false;
    for (ItemStack is : this.target.getArmorInventoryList()) {
      float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
      float red = 1.0F - green;
      int dmg = 100 - (int)(red * 100.0F);
      if (dmg > ((Float)this.minArmor.getValue()).floatValue())
        continue; 
      this.armorTarget = true;
    } 
    if (targetDmg >= ((Float)this.breakMinDmg.getValue()).floatValue() && selfDmg <= ((Float)this.breakMaxSelfDamage.getValue()).floatValue())
      return true; 
    return (EntityUtil.isInHole((Entity)this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= ((Float)this.facePlace.getValue()).floatValue());
  }
  
  private boolean IsValidCrystal(Entity p_Entity) {
    if (p_Entity == null)
      return false; 
    if (!(p_Entity instanceof EntityEnderCrystal))
      return false; 
    if (this.target == null)
      return false; 
    if (p_Entity.getDistance((Entity)AutoCrystal.mc.player) > ((Float)this.breakRange.getValue()).floatValue())
      return false; 
    if (!AutoCrystal.mc.player.canEntityBeSeen(p_Entity) && p_Entity.getDistance((Entity)AutoCrystal.mc.player) > ((Float)this.breakWallRange.getValue()).floatValue())
      return false; 
    if (this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0F)
      return false; 
    double targetDmg = calculateDamage(p_Entity.getPosition().getX() + 0.5D, p_Entity.getPosition().getY() + 1.0D, p_Entity.getPosition().getZ() + 0.5D, (Entity)this.target);
    if (EntityUtil.isInHole((Entity)AutoCrystal.mc.player) && targetDmg >= 1.0D)
      return true; 
    double selfDmg = calculateDamage(p_Entity.getPosition().getX() + 0.5D, p_Entity.getPosition().getY() + 1.0D, p_Entity.getPosition().getZ() + 0.5D, (Entity)AutoCrystal.mc.player);
    double d = ((Boolean)this.suicide.getValue()).booleanValue() ? 2.0D : 0.5D;
    if (selfDmg + d < (AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) && targetDmg >= (this.target.getAbsorptionAmount() + this.target.getHealth()))
      return true; 
    this.armorTarget = false;
    for (ItemStack is : this.target.getArmorInventoryList()) {
      float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
      float red = 1.0F - green;
      int dmg = 100 - (int)(red * 100.0F);
      if (dmg > ((Float)this.minArmor.getValue()).floatValue())
        continue; 
      this.armorTarget = true;
    } 
    if (targetDmg >= ((Float)this.breakMinDmg.getValue()).floatValue() && selfDmg <= ((Float)this.breakMaxSelfDamage.getValue()).floatValue())
      return true; 
    return (EntityUtil.isInHole((Entity)this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= ((Float)this.facePlace.getValue()).floatValue());
  }
  
  EntityPlayer getTarget() {
    EntityPlayer closestPlayer = null;
    for (EntityPlayer entity : AutoCrystal.mc.world.playerEntities) {
      if (AutoCrystal.mc.player == null || AutoCrystal.mc.player.isDead || entity.isDead || entity == AutoCrystal.mc.player || Phobos.friendManager.isFriend(entity.getName()) || entity.getDistance((Entity)AutoCrystal.mc.player) > 12.0F)
        continue; 
      this.armorTarget = false;
      for (ItemStack is : entity.getArmorInventoryList()) {
        float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
        float red = 1.0F - green;
        int dmg = 100 - (int)(red * 100.0F);
        if (dmg > ((Float)this.minArmor.getValue()).floatValue())
          continue; 
        this.armorTarget = true;
      } 
      if (EntityUtil.isInHole((Entity)entity) && entity.getAbsorptionAmount() + entity.getHealth() > ((Float)this.facePlace.getValue()).floatValue() && !this.armorTarget && ((Float)this.minDamage.getValue()).floatValue() > 2.2F)
        continue; 
      if (closestPlayer == null) {
        closestPlayer = entity;
        continue;
      } 
      if (closestPlayer.getDistance((Entity)AutoCrystal.mc.player) <= entity.getDistance((Entity)AutoCrystal.mc.player))
        continue; 
      closestPlayer = entity;
    } 
    return closestPlayer;
  }
  
  private void manualBreaker() {
    RayTraceResult result;
    if (this.manualTimer.passedMs(200L) && AutoCrystal.mc.gameSettings.keyBindUseItem.isKeyDown() && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = AutoCrystal.mc.objectMouseOver) != null)
      if (result.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
        Entity entity = result.entityHit;
        if (entity instanceof EntityEnderCrystal) {
          if (((Boolean)this.packetBreak.getValue()).booleanValue()) {
            AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
          } else {
            AutoCrystal.mc.playerController.attackEntity((EntityPlayer)AutoCrystal.mc.player, entity);
          } 
          this.manualTimer.reset();
        } 
      } else if (result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
        BlockPos mousePos = new BlockPos(AutoCrystal.mc.objectMouseOver.getBlockPos().getX(), AutoCrystal.mc.objectMouseOver.getBlockPos().getY() + 1.0D, AutoCrystal.mc.objectMouseOver.getBlockPos().getZ());
        for (Entity target : AutoCrystal.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(mousePos))) {
          if (!(target instanceof EntityEnderCrystal))
            continue; 
          if (((Boolean)this.packetBreak.getValue()).booleanValue()) {
            AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(target));
          } else {
            AutoCrystal.mc.playerController.attackEntity((EntityPlayer)AutoCrystal.mc.player, target);
          } 
          this.manualTimer.reset();
        } 
      }  
  }
  
  private boolean canSeePos(BlockPos pos) {
    return (AutoCrystal.mc.world.rayTraceBlocks(new Vec3d(AutoCrystal.mc.player.posX, AutoCrystal.mc.player.posY + AutoCrystal.mc.player.getEyeHeight(), AutoCrystal.mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) == null);
  }
  
  private NonNullList<BlockPos> placePostions(float placeRange) {
    NonNullList<BlockPos> positions = NonNullList.create();
    positions.addAll((Collection)getSphere(new BlockPos(Math.floor(AutoCrystal.mc.player.posX), Math.floor(AutoCrystal.mc.player.posY), Math.floor(AutoCrystal.mc.player.posZ)), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, true)).collect(Collectors.toList()));
    return positions;
  }
  
  private boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
    BlockPos boost = blockPos.add(0, 1, 0);
    BlockPos boost2 = blockPos.add(0, 2, 0);
    try {
      if (!((Boolean)this.opPlace.getValue()).booleanValue()) {
        if (AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
          return false; 
        if (AutoCrystal.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || AutoCrystal.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR)
          return false; 
        if (!specialEntityCheck)
          return (AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty()); 
        for (Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
          if (entity instanceof EntityEnderCrystal)
            continue; 
          return false;
        } 
        for (Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
          if (entity instanceof EntityEnderCrystal)
            continue; 
          return false;
        } 
      } else {
        if (AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
          return false; 
        if (AutoCrystal.mc.world.getBlockState(boost).getBlock() != Blocks.AIR)
          return false; 
        if (!specialEntityCheck)
          return AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty(); 
        for (Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
          if (entity instanceof EntityEnderCrystal)
            continue; 
          return false;
        } 
      } 
    } catch (Exception ignored) {
      return false;
    } 
    return true;
  }
  
  private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
    float doubleExplosionSize = 12.0F;
    double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
    Vec3d vec3d = new Vec3d(posX, posY, posZ);
    double blockDensity = 0.0D;
    try {
      blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
    } catch (Exception exception) {}
    double v = (1.0D - distancedsize) * blockDensity;
    float damage = (int)((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D);
    double finald = 1.0D;
    if (entity instanceof EntityLivingBase)
      finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion((World)AutoCrystal.mc.world, null, posX, posY, posZ, 6.0F, false, true)); 
    return (float)finald;
  }
  
  private float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
    float damage = damageI;
    if (entity instanceof EntityPlayer) {
      EntityPlayer ep = (EntityPlayer)entity;
      DamageSource ds = DamageSource.causeExplosionDamage(explosion);
      damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
      int k = 0;
      try {
        k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
      } catch (Exception exception) {}
      float f = MathHelper.clamp(k, 0.0F, 20.0F);
      damage *= 1.0F - f / 25.0F;
      if (entity.isPotionActive(MobEffects.RESISTANCE))
        damage -= damage / 4.0F; 
      damage = Math.max(damage, 0.0F);
      return damage;
    } 
    damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
    return damage;
  }
  
  private float getDamageMultiplied(float damage) {
    int diff = AutoCrystal.mc.world.getDifficulty().getId();
    return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
  }
  
  public enum SwingMode {
    MainHand, OffHand, None;
  }
}
