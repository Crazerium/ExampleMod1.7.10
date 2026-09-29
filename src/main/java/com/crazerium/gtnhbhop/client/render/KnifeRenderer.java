package com.crazerium.gtnhbhop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.crazerium.gtnhbhop.client.KnifeAnimationHandler;
import com.crazerium.gtnhbhop.client.render.model.ButterflyReferenceModel;

/**
 * Stage 4.1.2 - wide hand arc + stronger handle opening + explicit catch.
 *
 * The hand/grip point now travels through the wide reference arc instead of
 * staying almost fixed. Blade and free-handle keyframes remain articulated
 * around that moving grip, with a deliberate catch phase before idle.
 */
public final class KnifeRenderer implements IItemRenderer {

    private static final Keyframe[] KEYFRAMES = new Keyframe[] {
        // Stage 4.1.2 - wider hand arc. The root motion moves the actual hand and
        // grip point together, while blade/free-handle angles create the flip.
        // t, root xyz, root rot xyz, blade, held handle, free handle
        k(0.000F,  0.000F,  0.000F,  0.000F,   0F,   0F,   0F,    0F, -15F,    -6F),

        // Open: small lift first, then carry the whole grip toward the centre.
        k(0.060F, -0.005F,  0.020F, -0.003F,  -1F,   2F,  -4F,    8F, -13F,   -70F),
        k(0.130F, -0.035F,  0.055F, -0.008F,  -3F,   6F,  -9F,   35F,  -8F,  -150F),
        k(0.205F, -0.095F,  0.090F, -0.014F,  -6F,  10F, -15F,  100F,  -1F,  -235F),

        // Wide left pass from the Shorts. Unlike 4.1.1 the palm itself travels
        // with the knife, so the flourish is not confined to one point.
        k(0.285F, -0.165F,  0.105F, -0.018F,  -6F,  10F, -20F,  175F,   8F,  -315F),
        k(0.365F, -0.225F,  0.070F, -0.010F,  -2F,   6F, -22F,  245F,  15F,  -410F),
        k(0.445F, -0.235F,  0.015F,  0.004F,   3F,   0F, -14F,  320F,  20F,  -510F),

        // Bottom roll. The free channel is deliberately separated much more
        // strongly here so both butterfly handles remain readable on screen.
        k(0.525F, -0.200F, -0.060F,  0.020F,   8F,  -8F,   4F,  400F,  15F,  -605F),
        k(0.605F, -0.130F, -0.100F,  0.026F,   9F, -12F,  15F,  475F,   5F,  -680F),
        k(0.685F, -0.050F, -0.070F,  0.018F,   5F,  -8F,  10F,  545F,  -5F,  -735F),

        // Return to the right and make the catch explicit instead of snapping
        // directly back into idle. Positive X stays intentionally tiny so the
        // blade never leaves the right edge of the screen.
        k(0.755F,  0.005F, -0.010F,  0.006F,   0F,   0F,  -4F,  610F, -10F,  -770F),
        k(0.825F,  0.035F,  0.055F, -0.006F,  -5F,   8F, -12F,  665F, -12F,  -755F),
        k(0.885F,  0.018F,  0.090F, -0.010F,  -4F,   6F,  -8F,  700F, -14F,  -735F),
        k(0.935F,  0.006F,  0.055F, -0.006F,  -2F,   3F,  -4F,  714F, -15F,  -728F),
        k(0.975F,  0.000F,  0.018F, -0.002F,   0F,   1F,  -1F,  720F, -15F,  -726F),
        k(1.000F,  0.000F,  0.000F,  0.000F,   0F,   0F,   0F,  720F, -15F,  -726F)
    };

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        if (player == null) {
            return;
        }

        Pose pose = samplePose(KnifeAnimationHandler.getTrickProgress());

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glLoadIdentity();

        // Accepted compact-right first-person anchor from Stage 4.0.2.
        GL11.glTranslatef(0.58F, -0.20F, -0.24F);
        GL11.glScalef(0.70F, 0.70F, 0.70F);

        // Move hand + grip as one unit. This is deliberately small: the large
        // visible motion comes from the actual blade/handle keyframes, not from
        // throwing the entire first-person rig around the screen.
        GL11.glTranslatef(pose.rootX, pose.rootY, pose.rootZ);
        GL11.glRotatef(pose.rootRZ, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(pose.rootRY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(pose.rootRX, 1.0F, 0.0F, 0.0F);

        renderRightArm(mc, player);
        renderReferenceButterfly(pose);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void renderRightArm(Minecraft mc, EntityClientPlayerMP player) {
        if (player.isInvisible()) {
            return;
        }

        Render render = RenderManager.instance.getEntityRenderObject(player);
        if (!(render instanceof RenderPlayer)) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(player.getLocationSkin());

        GL11.glTranslatef(0.66F, -0.68F, -0.78F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
        GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(5.6F, 0.0F, 0.0F);

        GL11.glDisable(GL11.GL_CULL_FACE);
        ((RenderPlayer) render).renderFirstPersonArm(player);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glPopMatrix();
    }

    private static void renderReferenceButterfly(Pose pose) {
        GL11.glPushMatrix();

        // Exact Stage 4.0.2 idle anchor. The animation only changes articulated
        // part angles plus the small shared root motion above.
        GL11.glTranslatef(1.08F, -0.61F, -1.76F);
        GL11.glRotatef(-7.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(17.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-6.0F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(0.58F, 0.58F, 0.58F);

        ButterflyReferenceModel.render(pose.bladeAngle, pose.heldHandleAngle, pose.freeHandleAngle);

        GL11.glPopMatrix();
    }

    private static Pose samplePose(float progress) {
        float p = clamp01(progress);
        if (p <= KEYFRAMES[0].t) {
            return new Pose(KEYFRAMES[0]);
        }

        for (int i = 0; i < KEYFRAMES.length - 1; i++) {
            Keyframe a = KEYFRAMES[i];
            Keyframe b = KEYFRAMES[i + 1];
            if (p <= b.t) {
                float local = (p - a.t) / (b.t - a.t);
                return Pose.interpolate(a, b, smooth(local));
            }
        }

        return new Pose(KEYFRAMES[KEYFRAMES.length - 1]);
    }

    private static Keyframe k(
        float t,
        float rootX,
        float rootY,
        float rootZ,
        float rootRX,
        float rootRY,
        float rootRZ,
        float bladeAngle,
        float heldHandleAngle,
        float freeHandleAngle) {
        return new Keyframe(
            t,
            rootX,
            rootY,
            rootZ,
            rootRX,
            rootRY,
            rootRZ,
            bladeAngle,
            heldHandleAngle,
            freeHandleAngle);
    }

    private static final class Keyframe {
        final float t;
        final float rootX;
        final float rootY;
        final float rootZ;
        final float rootRX;
        final float rootRY;
        final float rootRZ;
        final float bladeAngle;
        final float heldHandleAngle;
        final float freeHandleAngle;

        Keyframe(
            float t,
            float rootX,
            float rootY,
            float rootZ,
            float rootRX,
            float rootRY,
            float rootRZ,
            float bladeAngle,
            float heldHandleAngle,
            float freeHandleAngle) {
            this.t = t;
            this.rootX = rootX;
            this.rootY = rootY;
            this.rootZ = rootZ;
            this.rootRX = rootRX;
            this.rootRY = rootRY;
            this.rootRZ = rootRZ;
            this.bladeAngle = bladeAngle;
            this.heldHandleAngle = heldHandleAngle;
            this.freeHandleAngle = freeHandleAngle;
        }
    }

    private static final class Pose {
        float rootX;
        float rootY;
        float rootZ;
        float rootRX;
        float rootRY;
        float rootRZ;
        float bladeAngle;
        float heldHandleAngle;
        float freeHandleAngle;

        Pose(Keyframe frame) {
            rootX = frame.rootX;
            rootY = frame.rootY;
            rootZ = frame.rootZ;
            rootRX = frame.rootRX;
            rootRY = frame.rootRY;
            rootRZ = frame.rootRZ;
            bladeAngle = frame.bladeAngle;
            heldHandleAngle = frame.heldHandleAngle;
            freeHandleAngle = frame.freeHandleAngle;
        }

        static Pose interpolate(Keyframe a, Keyframe b, float t) {
            Pose out = new Pose(a);
            out.rootX = lerp(a.rootX, b.rootX, t);
            out.rootY = lerp(a.rootY, b.rootY, t);
            out.rootZ = lerp(a.rootZ, b.rootZ, t);
            out.rootRX = lerp(a.rootRX, b.rootRX, t);
            out.rootRY = lerp(a.rootRY, b.rootRY, t);
            out.rootRZ = lerp(a.rootRZ, b.rootRZ, t);
            out.bladeAngle = lerp(a.bladeAngle, b.bladeAngle, t);
            out.heldHandleAngle = lerp(a.heldHandleAngle, b.heldHandleAngle, t);
            out.freeHandleAngle = lerp(a.freeHandleAngle, b.freeHandleAngle, t);
            return out;
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float smooth(float value) {
        float t = clamp01(value);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
