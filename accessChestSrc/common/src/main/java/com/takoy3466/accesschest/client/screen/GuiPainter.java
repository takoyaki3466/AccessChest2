package com.takoy3466.accesschest.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Draws the vanilla styled panel and slot frames with plain rectangles, so the screen needs no texture of its own.
 */
final class GuiPainter {
    private static final int PANEL = 0xFFC6C6C6;
    private static final int HIGHLIGHT = 0xFFFFFFFF;
    private static final int SHADOW = 0xFF555555;
    private static final int OUTLINE = 0xFF000000;
    private static final int SLOT_BACKGROUND = 0xFF8B8B8B;
    private static final int SLOT_SHADOW = 0xFF373737;
    /** Same darkening as the 1.7.10 version used for unused slots (alpha 63). */
    private static final int INACTIVE_OVERLAY = 0x3F000000;

    /** Size of a slot frame around a 16x16 item. */
    static final int SLOT_FRAME_SIZE = 18;
    private static final int BORDER = 1;
    private static final int BEVEL = 2;

    private GuiPainter() {
    }

    static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, OUTLINE);
        graphics.fill(x + BORDER, y + BORDER, x + width - BORDER, y + height - BORDER, HIGHLIGHT);
        graphics.fill(x + BORDER + BEVEL, y + BORDER + BEVEL, x + width - BORDER, y + height - BORDER, SHADOW);
        graphics.fill(x + BORDER + BEVEL, y + BORDER + BEVEL, x + width - BORDER - BEVEL, y + height - BORDER - BEVEL, PANEL);
    }

    /** Draws an inset frame whose inner area starts at ({@code innerX}, {@code innerY}). */
    static void inset(GuiGraphics graphics, int innerX, int innerY, int innerWidth, int innerHeight) {
        int left = innerX - BORDER;
        int top = innerY - BORDER;
        int right = innerX + innerWidth + BORDER;
        int bottom = innerY + innerHeight + BORDER;
        graphics.fill(left, top, right, bottom, HIGHLIGHT);
        graphics.fill(left, top, right - BORDER, bottom - BORDER, SLOT_SHADOW);
        graphics.fill(innerX, innerY, innerX + innerWidth, innerY + innerHeight, SLOT_BACKGROUND);
    }

    /** Frame for the slot whose item is drawn at ({@code itemX}, {@code itemY}). */
    static void slot(GuiGraphics graphics, int itemX, int itemY) {
        inset(graphics, itemX, itemY, SLOT_FRAME_SIZE - 2 * BORDER, SLOT_FRAME_SIZE - 2 * BORDER);
    }

    static void inactiveSlot(GuiGraphics graphics, int itemX, int itemY) {
        slot(graphics, itemX, itemY);
        int size = SLOT_FRAME_SIZE - 2 * BORDER;
        graphics.fill(itemX, itemY, itemX + size, itemY + size, INACTIVE_OVERLAY);
    }
}
