package com.stardew.craft;

import net.minecraft.client.gui.GuiGraphics;
import java.lang.reflect.Method;
import java.util.Arrays;

public class PrintMethods {
    public static void main(String[] args) {
        for (Method m : GuiGraphics.class.getMethods()) {
            if (m.getName().equals("renderTooltip")) {
                System.out.println(m.getName() + ": " + Arrays.toString(m.getParameterTypes()));
            }
        }
    }
}