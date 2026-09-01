package com.example.hpindicator.client;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class ModKeys {

    public static final KeyBinding OPEN_MENU =
            new KeyBinding("key.hpindicator.open", GLFW.GLFW_KEY_HOME, "key.categories.hpindicator");

    private ModKeys() {
    }
}
