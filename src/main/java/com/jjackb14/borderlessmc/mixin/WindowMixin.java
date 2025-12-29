package com.jjackb14.borderlessmc.mixin;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Shadow public abstract long getHandle();
    @Shadow public abstract boolean isFullscreen();

    @Inject(method = "toggleFullscreen", at = @At("TAIL"))
    private void borderlessOnToggleFullscreen(CallbackInfo ci) {
        if (!isFullscreen()) {
            return;
        }

        long window = getHandle();

        applyBorderless(window);
    }

    private void applyBorderless(long window) {
        GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);

        long monitor = findMonitor(window);
        if (monitor == 0L) {
            monitor = GLFW.glfwGetPrimaryMonitor();
        }

        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
        if (mode == null) {
            return;
        }

        int mx = 0, my = 0;
        try (var stack = stackPush()) {
            IntBuffer px = stack.mallocInt(1);
            IntBuffer py = stack.mallocInt(1);

            GLFW.glfwGetMonitorPos(monitor, px, py);

            mx = px.get(0);
            my = py.get(0);
        }

        GLFW.glfwSetWindowMonitor(
                window,
                0,
                mx, my,
                mode.width(),
                mode.height(),
                mode.refreshRate()
        );
    }

    private void restoreWindowed(long window) {
        GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
    }

    private long findMonitor(long window) {
        int wx, wy, ww, wh;

        try (var stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            GLFW.glfwGetWindowPos(window, x, y);
            GLFW.glfwGetWindowSize(window, w, h);

            wx = x.get(0);
            wy = y.get(0);
            ww = w.get(0);
            wh = h.get(0);
        }

        int cx = wx + ww / 2;
        int cy = wy + wh / 2;

        var monitors = GLFW.glfwGetMonitors();
        if (monitors == null || monitors.remaining() == 0) {
            return 0L;
        }

        long bestMonitor = 0L;

        for (int i = 0; i < monitors.remaining(); i++) {
            long m = monitors.get(i);

            int mx, my;
            GLFWVidMode mode = GLFW.glfwGetVideoMode(m);
            if (mode == null) {
                continue;
            }

            try (var stack = stackPush()) {
                IntBuffer px = stack.mallocInt(1);
                IntBuffer py = stack.mallocInt(1);

                GLFW.glfwGetMonitorPos(m, px, py);

                mx = px.get(0);
                my = py.get(0);
            }

            int mw = mode.width();
            int mh = mode.height();

            if (cx >= mx && cx < mx + mw && cy >= my && cy < my + mh) {
                return m;
            }

            if (bestMonitor == 0L) {
                bestMonitor = m;
            }
        }

        return 0L;
    }
}