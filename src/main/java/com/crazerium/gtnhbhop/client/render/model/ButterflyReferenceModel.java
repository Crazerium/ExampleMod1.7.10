package com.crazerium.gtnhbhop.client.render.model;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

/**
 * Stage 4 reference butterfly knife built specifically from the Shorts silhouette.
 *
 * The model is deliberately split into blade / handle A / handle B around a single
 * pivot. Stage 4.0 only uses the static open pose; later stages can animate the
 * three parts without changing the geometry again.
 */
public final class ButterflyReferenceModel {

    private ButterflyReferenceModel() {}

    public static void renderIdle() {
        // Stage 4.0.2: tighter idle pair. Both handles sweep down into the palm
        // instead of one sticking straight out to the right. The small angle
        // difference still lets the viewer read two separate balisong halves.
        render(0.0F, -15.0F, -6.0F);
    }

    public static void render(float bladeAngle, float heldHandleAngle, float freeHandleAngle) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_LINE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Two channel handles share the same pivot. In the idle reference most
        // of them disappears into the hand, so they stay nearly parallel.
        renderHandle(heldHandleAngle, -0.060F, true);
        renderHandle(freeHandleAngle, 0.060F, false);

        GL11.glPushMatrix();
        GL11.glRotatef(bladeAngle, 0.0F, 0.0F, 1.0F);
        renderBlade();
        renderPivotAssembly();
        GL11.glPopMatrix();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
    }

    private static void renderBlade() {
        // The Shorts knife is not a straight dagger: it has a long swept blade,
        // a raised spine near the tip and a concave lower edge close to the pivot.
        final float[] blade = new float[] {
             0.02F,  0.11F,
            -0.22F,  0.15F,
            -0.49F,  0.21F,
            -0.78F,  0.31F,
            -1.05F,  0.42F,
            -1.28F,  0.51F,
            -1.48F,  0.55F,
            -1.38F,  0.39F,
            -1.17F,  0.24F,
            -0.91F,  0.085F,
            -0.62F, -0.030F,
            -0.33F, -0.090F,
            -0.10F, -0.070F
        };

        // Dark bluish steel body like the reference clip.
        GL11.glColor4f(0.33F, 0.35F, 0.39F, 1.0F);
        drawExtrudedXYPolygon(blade, -0.060F, 0.060F);

        // Lighter bevel / cutting edge following the lower curve.
        final float[] edge = new float[] {
            -0.12F, -0.050F,
            -0.34F, -0.070F,
            -0.62F, -0.012F,
            -0.90F,  0.100F,
            -1.14F,  0.245F,
            -1.34F,  0.385F,
            -1.46F,  0.525F,
            -1.37F,  0.390F,
            -1.16F,  0.270F,
            -0.91F,  0.140F,
            -0.62F,  0.030F,
            -0.34F, -0.018F
        };
        GL11.glColor4f(0.73F, 0.75F, 0.79F, 1.0F);
        drawFlatXYPolygon(edge, 0.062F);

        // Dark ridge near the spine makes the silhouette read less like a flat strip.
        final float[] ridge = new float[] {
            -0.19F, 0.130F,
            -0.50F, 0.195F,
            -0.82F, 0.305F,
            -1.09F, 0.420F,
            -1.31F, 0.505F,
            -1.14F, 0.390F,
            -0.81F, 0.255F,
            -0.48F, 0.160F
        };
        GL11.glColor4f(0.19F, 0.20F, 0.23F, 1.0F);
        drawFlatXYPolygon(ridge, -0.062F);

        // Short tang under the decorative pivot.
        GL11.glColor4f(0.16F, 0.17F, 0.19F, 1.0F);
        drawBox(-0.12F, -0.115F, -0.075F, 0.17F, 0.125F, 0.075F);
    }

    private static void renderHandle(float angle, float zOffset, boolean held) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.10F, -0.02F, zOffset);
        GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.10F, 0.02F, -zOffset);

        // Each half is a long tapered channel. The hand covers the far end in idle.
        final float[] shape = new float[] {
             0.03F,  0.11F,
             0.25F,  0.025F,
             0.55F, -0.105F,
             0.82F, -0.235F,
             1.03F, -0.335F,
             1.16F, -0.410F,
             1.09F, -0.485F,
             0.91F, -0.410F,
             0.70F, -0.320F,
             0.40F, -0.180F,
             0.09F, -0.030F
        };

        GL11.glColor4f(held ? 0.105F : 0.085F, held ? 0.11F : 0.09F, held ? 0.13F : 0.11F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, zOffset);
        drawExtrudedXYPolygon(shape, -0.045F, 0.045F);
        GL11.glPopMatrix();

        // Silver channel rim visible in the Shorts when the handle flips out.
        GL11.glColor4f(0.36F, 0.37F, 0.40F, 1.0F);
        GL11.glLineWidth(2.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, zOffset + 0.047F);
        drawXYOutline(shape);
        GL11.glPopMatrix();

        // Subtle inset strip makes each channel read as its own handle instead
        // of one thick black block when both halves overlap in first person.
        GL11.glColor4f(0.20F, 0.21F, 0.24F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, zOffset + 0.050F);
        drawBox(0.28F, -0.115F, -0.008F, 0.99F, -0.065F, 0.008F);
        GL11.glPopMatrix();

        // Small end cap.
        GL11.glColor4f(0.17F, 0.18F, 0.20F, 1.0F);
        drawBox(1.05F, -0.500F, zOffset - 0.055F, 1.20F, -0.395F, zOffset + 0.055F);

        GL11.glPopMatrix();
    }

    private static void renderPivotAssembly() {
        // Central dark butterfly joint.
        GL11.glColor4f(0.12F, 0.13F, 0.15F, 1.0F);
        drawOctagonalPrism(0.075F, -0.085F, 0.085F, 16);

        // Two small loops / ears around the joint mimic the distinctive guard
        // visible in the reference without requiring a texture.
        GL11.glColor4f(0.29F, 0.30F, 0.33F, 1.0F);
        drawRingOutline(-0.020F, 0.145F, 0.115F, 0.075F, 0.090F);
        drawRingOutline(0.095F, 0.055F, 0.095F, 0.060F, 0.090F);

        GL11.glColor4f(0.52F, 0.53F, 0.56F, 1.0F);
        drawOctagonalPrism(0.030F, -0.095F, 0.095F, 14);
    }

    private static void drawRingOutline(float cx, float cy, float rx, float ry, float z) {
        Tessellator tess = Tessellator.instance;
        GL11.glLineWidth(2.0F);
        tess.startDrawing(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 18; i++) {
            double a = (Math.PI * 2.0D * i) / 18.0D;
            tess.addVertex(cx + Math.cos(a) * rx, cy + Math.sin(a) * ry, z);
        }
        tess.draw();
    }

    private static void drawOctagonalPrism(float radius, float minZ, float maxZ, int sides) {
        Tessellator tess = Tessellator.instance;

        tess.startDrawing(GL11.GL_POLYGON);
        for (int i = 0; i < sides; i++) {
            double a = (Math.PI * 2.0D * i) / sides;
            tess.addVertex(Math.cos(a) * radius, Math.sin(a) * radius, minZ);
        }
        tess.draw();

        tess.startDrawing(GL11.GL_POLYGON);
        for (int i = sides - 1; i >= 0; i--) {
            double a = (Math.PI * 2.0D * i) / sides;
            tess.addVertex(Math.cos(a) * radius, Math.sin(a) * radius, maxZ);
        }
        tess.draw();

        tess.startDrawingQuads();
        for (int i = 0; i < sides; i++) {
            int n = (i + 1) % sides;
            double a1 = (Math.PI * 2.0D * i) / sides;
            double a2 = (Math.PI * 2.0D * n) / sides;
            float x1 = (float) (Math.cos(a1) * radius);
            float y1 = (float) (Math.sin(a1) * radius);
            float x2 = (float) (Math.cos(a2) * radius);
            float y2 = (float) (Math.sin(a2) * radius);
            tess.addVertex(x1, y1, minZ);
            tess.addVertex(x2, y2, minZ);
            tess.addVertex(x2, y2, maxZ);
            tess.addVertex(x1, y1, maxZ);
        }
        tess.draw();
    }

    private static void drawExtrudedXYPolygon(float[] points, float minZ, float maxZ) {
        int count = points.length / 2;
        Tessellator tess = Tessellator.instance;

        tess.startDrawing(GL11.GL_POLYGON);
        for (int i = 0; i < count; i++) {
            tess.addVertex(points[i * 2], points[i * 2 + 1], minZ);
        }
        tess.draw();

        tess.startDrawing(GL11.GL_POLYGON);
        for (int i = count - 1; i >= 0; i--) {
            tess.addVertex(points[i * 2], points[i * 2 + 1], maxZ);
        }
        tess.draw();

        tess.startDrawingQuads();
        for (int i = 0; i < count; i++) {
            int next = (i + 1) % count;
            float x1 = points[i * 2];
            float y1 = points[i * 2 + 1];
            float x2 = points[next * 2];
            float y2 = points[next * 2 + 1];
            tess.addVertex(x1, y1, minZ);
            tess.addVertex(x2, y2, minZ);
            tess.addVertex(x2, y2, maxZ);
            tess.addVertex(x1, y1, maxZ);
        }
        tess.draw();
    }

    private static void drawFlatXYPolygon(float[] points, float z) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_POLYGON);
        for (int i = 0; i < points.length; i += 2) {
            tess.addVertex(points[i], points[i + 1], z);
        }
        tess.draw();
    }

    private static void drawXYOutline(float[] points) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_LINE_LOOP);
        for (int i = 0; i < points.length; i += 2) {
            tess.addVertex(points[i], points[i + 1], 0.0F);
        }
        tess.draw();
    }

    private static void drawBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();

        tess.addVertex(minX, minY, minZ);
        tess.addVertex(maxX, minY, minZ);
        tess.addVertex(maxX, maxY, minZ);
        tess.addVertex(minX, maxY, minZ);

        tess.addVertex(minX, maxY, maxZ);
        tess.addVertex(maxX, maxY, maxZ);
        tess.addVertex(maxX, minY, maxZ);
        tess.addVertex(minX, minY, maxZ);

        tess.addVertex(minX, minY, maxZ);
        tess.addVertex(minX, minY, minZ);
        tess.addVertex(minX, maxY, minZ);
        tess.addVertex(minX, maxY, maxZ);

        tess.addVertex(maxX, minY, minZ);
        tess.addVertex(maxX, minY, maxZ);
        tess.addVertex(maxX, maxY, maxZ);
        tess.addVertex(maxX, maxY, minZ);

        tess.addVertex(minX, maxY, minZ);
        tess.addVertex(maxX, maxY, minZ);
        tess.addVertex(maxX, maxY, maxZ);
        tess.addVertex(minX, maxY, maxZ);

        tess.addVertex(minX, minY, maxZ);
        tess.addVertex(maxX, minY, maxZ);
        tess.addVertex(maxX, minY, minZ);
        tess.addVertex(minX, minY, minZ);

        tess.draw();
    }
}
