package io.github.profjb58.territorial.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class RenderUtils {

    /**
     * Utility for drawing lines made out of a series of quads in any direction
     *
     * @param matrices  Matrix stack, used to track the current view transformations e.g. translation, rotation
     * @param consumer  Buffer to render the model to
     * @param facing Direction in which the line should be drawn
     * @param w Line width
     * @param l Line length
     * @param colour RGB colour array
     * @param a Colour alpha
     */
    public static void drawQuadLine(MatrixStack matrices, VertexConsumer consumer, Direction facing, float w, float l, float[] colour, float a) {
        // Get the transformation matrix and translate to the center
        Matrix4f transMatrix = matrices.peek().getPositionMatrix();
        matrices.translate(0.5, 0.5, 0.5);

        float r = colour[0];
        float g = colour[1];
        float b = colour[2];

        // TODO - Switch to facing.getRotationQuaternion() approach at some point to optimise
        // Translations constructed from a north facing direction, hence rotate for other directions
        switch(facing) {
            case UP -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            case DOWN -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
            case SOUTH -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
            case EAST -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
            case WEST -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        }

        // left side
        matrices.translate(-w/2, -w/2, 0);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // right side
        matrices.translate(w, 0, 0);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // bottom side
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // top side
        matrices.translate(0, w, 0);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // end bit
        matrices.translate(0, -w, -l);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, 0).color(r,g,b,a).next();

        matrices.translate(0.5, 0.5, 0.5);
    }

    public static BlockPos getRaycastPos(PlayerEntity player, double maxDistance) {
        HitResult result = player.raycast(maxDistance, 1f, false);
        Vec3d hitVec = result.getPos();
        return new BlockPos(hitVec.x, hitVec.y, hitVec.z);
    }


}
