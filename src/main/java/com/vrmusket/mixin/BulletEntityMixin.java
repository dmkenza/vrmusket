
package com.vrmusket.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.musketmod.BulletEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.vrmusket.VRMusket.isDebugEnabled;

@Mixin(BulletEntity.class)
public abstract class BulletEntityMixin extends AbstractHurtingProjectile {

    private int groundTimer = 0;
    private static final int GROUND_LIFETIME = 300;

    private static final EntityDataAccessor<Boolean> IS_GROUNDED = SynchedEntityData.defineId(BulletEntity.class, EntityDataSerializers.BOOLEAN);

    protected BulletEntityMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void defineSynchedData();


    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void defineSynchedDataInject(CallbackInfo ci) {
        ((BulletEntity) (Object) this).getEntityData().define(IS_GROUNDED, false);
    }

    public boolean isGrounded() {
        return ((BulletEntity) (Object) this).getEntityData().get(IS_GROUNDED);
    }

    public void setGrounded(boolean grounded) {
        ((BulletEntity) (Object) this).getEntityData().set(IS_GROUNDED, grounded);
    }

//    @ModifyConstant(
//            method = "setVelocity",
//            constant = @Constant(floatValue = 20.0f)
//    )
//    private float modifyVelocityDivisor(float originalDivisor) {
//        return 20.0f;
//    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(intValue = 100)
    )
    private int modifyLivetimeConstant(int originalValue) {
        return 400;
    }


    @ModifyConstant(
            method = "tick()V",
            constant = @Constant(doubleValue = 0.99)
    )
    private double disableAirResistance(double originalValue) {
        if(isGrounded()){
            return 1.0;
        }else {
            return originalValue;
        }
    }



    /**
     * Do not deal damage if the bullet is on the ground
     */

    @Inject(
            method = "onHitEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventOnHitEntityIfOnGround(EntityHitResult hitResult, CallbackInfo ci) {
        if (this.isGrounded()) {
            ci.cancel();
        }
    }

    //Gravity
    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = 0.05)
    )
    private double modifyGravityConstant(double originalValue) {
        if(isGrounded()){
            return 0.0;
        }else {
            return originalValue;
        }
    }


    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tickOnGroundCheck(CallbackInfo ci) {
        BulletEntity self = (BulletEntity) (Object) this;
        if (isGrounded()) {
            groundTimer++;
            if (groundTimer >= GROUND_LIFETIME) {
                self.discard();
            }
            ci.cancel();
        }
    }


    /**
     * Remove the bullet if it has dealt damage
     */

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lewewukek/musketmod/BulletEntity;onHit(Lnet/minecraft/world/phys/HitResult;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    private void afterOnHitInjection(CallbackInfo ci, Level level, Vec3 velocity, Vec3 from, Vec3 _to, Vec3 waterPos, HitResult hitResult, EntityHitResult entityHitResult) {

        if(isGrounded()){
            return;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BulletEntity self = (BulletEntity) (Object) this;
            setGrounded(true);
            self.setPos(hitResult.getLocation());
            self.setDeltaMovement(Vec3.ZERO);
            ci.cancel();
        }

        if (hitResult instanceof EntityHitResult entityHit) {
            BulletEntity self = (BulletEntity) (Object) this;
            self.discard();
        }
    }

    /**
     * In the original mod, there are no projectiles, and they do not remain on the ground.
     */

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lewewukek/musketmod/BulletEntity;discard()V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onBlockHitStop(CallbackInfo ci) {
        if(isGrounded()){
            return;
        }

        BulletEntity self = (BulletEntity) (Object) this;
        HitResult hitResult = self.level().clip(
                new ClipContext(
                        self.position(),
                        self.position().add(self.getDeltaMovement()),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        self
                )
        );

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            setGrounded( true);
            groundTimer = 0;
            self.noPhysics = true;
            self.setPos(hitResult.getLocation());
            self.setDeltaMovement(Vec3.ZERO);
            ci.cancel();
        }
    }


    /**
     * In the original mod, there were frequent issues with hitting targets.
     * The vanila ProjectileUtil should be used.
     */

    @Inject(
            method = "findHitEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectFindHitEntity(Vec3 start, Vec3 end, CallbackInfoReturnable<EntityHitResult> cir) {
        if(isGrounded()){
            return;
        }

        BulletEntity self = (BulletEntity) (Object) this;

        AABB searchBox = self.getBoundingBox().expandTowards(self.getDeltaMovement()).inflate(0.4);

        EntityHitResult result = ProjectileUtil.getEntityHitResult(
                self.level(),
                self,
                start,
                end,
                searchBox,
                self::canHitEntity,
                0.4f
        );

        cir.setReturnValue(result);
    }


    // In the original mod, the smoke particles of the bullet are missing
    @Inject(method = "tick", at = @At("HEAD"))
    private void injectLifetimeAndParticles(CallbackInfo ci) {
        if(isGrounded()){
            return;
        }

        BulletEntity self = (BulletEntity) (Object) this;
        Level world = self.level();

        if (world.isClientSide && !self.isFirstTick() && !isDebugEnabled) {
            Vec3 pos = self.position();
            if (self.pelletCount() == 1) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
            } else {
                world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
            }
        }

    }
}