package com.stardew.craft;
import net.minecraft.client.renderer.RenderType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
public class TestRT {
    public static void print() {
        for (Method m : RenderType.class.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers()) && m.getReturnType() == RenderType.class) {
                System.out.println(m.getName() + " " + java.util.Arrays.toString(m.getParameterTypes()));
            }
        }
    }
}
