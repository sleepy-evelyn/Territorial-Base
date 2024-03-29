package io.github.profjb58.territorial.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class CustomRenderLayers extends RenderLayer {

    //  Dummy
    public CustomRenderLayers(String nameIn, VertexFormat formatIn, VertexFormat.DrawMode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static final RenderLayer QUAD_LINES = new MultiPhase("territorial_quad_lines",
            VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS , 256, false, false,
            MultiPhaseParameters.builder()
                    .layering(VIEW_OFFSET_Z_LAYERING)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .texture(NO_TEXTURE)
                    .cull(DISABLE_CULLING)
                    .depthTest(LEQUAL_DEPTH_TEST)
                    .shader(COLOR_SHADER)
                    .texture(NO_TEXTURE)
                    .build(false));
}

