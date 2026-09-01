package com.example.hpindicator.client;

import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

final class ConfigSlider extends AbstractSlider {

    private final double min;
    private final double max;
    private final TranslationTextComponent label;
    private final Consumer<Double> setter;

    ConfigSlider(int x, int y, int w, int h, TranslationTextComponent label,
                 double min, double max, double value, Consumer<Double> setter) {
        super(x, y, w, h, StringTextComponent.EMPTY, (value - min) / (max - min));
        this.label = label;
        this.min = min;
        this.max = max;
        this.setter = setter;
        this.updateMessage();
    }

    private double actual() {
        return this.min + this.value * (this.max - this.min);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.label.copy().append(": ").append(String.format("%.2f", actual())));
    }

    @Override
    protected void applyValue() {
        this.setter.accept(actual());
    }
}
