package com.example.hpindicator.client;

import com.example.hpindicator.ModConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(new TranslationTextComponent("gui.hpindicator.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        this.addButton(new ConfigSlider(cx - 100, 50, 200, 20,
                new TranslationTextComponent("gui.hpindicator.scale"),
                0.5D, 3.0D, ModConfig.scale, v -> ModConfig.scale = v));

        this.addButton(new ConfigSlider(cx - 100, 76, 200, 20,
                new TranslationTextComponent("gui.hpindicator.offset"),
                0.0D, 1.0D, ModConfig.verticalOffset, v -> ModConfig.verticalOffset = v));

        Button toggle = new Button(cx - 100, 102, 200, 20, numberLabel(),
                b -> {
                    ModConfig.showNumber = !ModConfig.showNumber;
                    b.setMessage(numberLabel());
                });
        this.addButton(toggle);

        this.addButton(new Button(cx - 100, this.height - 30, 200, 20,
                new TranslationTextComponent("gui.done"),
                b -> this.minecraft.setScreen(this.parent)));
    }

    private static ITextComponent numberLabel() {
        return new TranslationTextComponent(ModConfig.showNumber
                ? "gui.hpindicator.number_on"
                : "gui.hpindicator.number_off");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 25, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void removed() {
        ModConfig.save();
        super.removed();
    }
}
