package com.example.hpindicator.client;

import com.example.hpindicator.ModConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

public final class HeartOverlay {

    // Текстура берётся из активного ресурспака, поэтому сердечки
    // всегда выглядят так же, как в HUD (в т.ч. с текстурпаками).
    private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

    // Готовый ванильный RenderType для плоских текстурок (как у шрифта)
    private static final RenderType HEART_TYPE = RenderType.text(ICONS);

    private static final int LIGHT = LightTexture.pack(15, 15);
    private static final int PER_ROW = 10;

    // UV-координаты в icons.png (256x256)
    private static final float CONTAINER_U = 16.0F; // пустая рамка
    private static final float FULL_U = 52.0F;      // полное сердце
    private static final float HALF_U = 61.0F;      // половинка
    private static final float V = 0.0F;

    private static final float SIZE = 9.0F;       // размер спрайта
    private static final float STEP = 8.0F;       // шаг между сердцами
    private static final float ROW_STEP = 10.0F;  // шаг между рядами

    private static final float BASE_PIXEL = 0.025F; // базовый размер пикселя в мире

    private HeartOverlay() {
    }

    public static void render(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = event.getPlayer();

        if (mc.player == null || player == null) {
            return;
        }
        if (!player.isAlive() || player.isSpectator()) {
            return;
        }
        if (player == mc.player && mc.options.getCameraType() == PointOfView.FIRST_PERSON) {
            return;
        }
        if (player.distanceToSqr(mc.player) > 64.0D * 64.0D) {
            return;
        }

        float hp = MathHelper.clamp(player.getHealth(), 0.0F, player.getMaxHealth());
        float maxHp = Math.max(1.0F, player.getMaxHealth());

        MatrixStack ms = event.getMatrixStack();
        IRenderTypeBuffer buffers = event.getBuffers();

        int containers = MathHelper.ceil(maxHp / 2.0F);
        int rows = (containers + PER_ROW - 1) / PER_ROW;
        int cols = Math.min(containers, PER_ROW);

        float pixel = BASE_PIXEL * (float) ModConfig.scale;
        float totalW = cols * STEP + (SIZE - STEP);
        float startY = -(rows - 1) * ROW_STEP;

        ms.pushPose();
        ms.translate(0.0D, player.getBbHeight() + ModConfig.verticalOffset, 0.0D);
        // Поворачиваем к камере (как ванильные ники)
        ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        ms.scale(-pixel, -pixel, pixel);

        Matrix4f mat = ms.last().pose();
        IVertexBuilder buf = buffers.getBuffer(HEART_TYPE);

        for (int i = 0; i < containers; i++) {
            int row = i / PER_ROW;
            int col = i % PER_ROW;
            float x = -totalW / 2.0F + col * STEP;
            float y = startY + row * ROW_STEP;

            blit(buf, mat, x, y, CONTAINER_U, V);

            float heartHp = hp - i * 2.0F;
            if (heartHp >= 2.0F) {
                blit(buf, mat, x, y, FULL_U, V);
            } else if (heartHp >= 1.0F) {
                blit(buf, mat, x, y, HALF_U, V);
            }
        }

        if (ModConfig.showNumber) {
            String text = format(hp);
            int textWidth = mc.font.width(text);
            float ty = startY - mc.font.lineHeight - 1.0F;
            mc.font.drawInBatch(text, -textWidth / 2.0F, ty, 0xFFFFFFFF,
                    true, mat, buffers, false, 0, LIGHT);
        }

        ms.popPose();

        if (buffers instanceof IRenderTypeBuffer.Impl) {
            ((IRenderTypeBuffer.Impl) buffers).endBatch();
        }
    }

    private static void blit(IVertexBuilder b, Matrix4f m, float x, float y, float u, float v) {
        float u0 = u / 256.0F;
        float v0 = v / 256.0F;
        float u1 = (u + SIZE) / 256.0F;
        float v1 = (v + SIZE) / 256.0F;

        b.vertex(m, x, y, 0.0F).color(255, 255, 255, 255).uv(u0, v0).uv2(LIGHT).endVertex();
        b.vertex(m, x, y + SIZE, 0.0F).color(255, 255, 255, 255).uv(u0, v1).uv2(LIGHT).endVertex();
        b.vertex(m, x + SIZE, y + SIZE, 0.0F).color(255, 255, 255, 255).uv(u1, v1).uv2(LIGHT).endVertex();
        b.vertex(m, x + SIZE, y, 0.0F).color(255, 255, 255, 255).uv(u1, v0).uv2(LIGHT).endVertex();
    }

    private static String format(float hp) {
        if (Math.abs(hp - Math.round(hp)) < 0.05F) {
            return String.valueOf(Math.round(hp));
        }
        return String.format("%.1f", hp);
    }
}
