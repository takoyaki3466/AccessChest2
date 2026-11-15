package com.takoy3466.accesschest.menu;

/**
 * Slot geometry of the chest menu. Shared by the menu (slot positions) and the screen (drawing).
 */
public final class ChestMenuLayout {

    public static final int COLUMNS = 12;
    public static final int VISIBLE_ROWS = 8;
    public static final int WINDOW_SLOT_COUNT = COLUMNS * VISIBLE_ROWS;

    public static final int SLOT_SIZE = 18;

    public static final int CHEST_X = 12;
    public static final int CHEST_Y = 18;

    public static final int PLAYER_INVENTORY_COLUMNS = 9;
    public static final int PLAYER_INVENTORY_ROWS = 3;
    public static final int PLAYER_INVENTORY_X = 12;
    public static final int PLAYER_INVENTORY_Y = 172;
    /** Gap between the main inventory rows and the hotbar, like vanilla containers. */
    private static final int HOTBAR_GAP = 4;
    public static final int HOTBAR_Y = PLAYER_INVENTORY_Y + PLAYER_INVENTORY_ROWS * SLOT_SIZE + HOTBAR_GAP;

    public static final int IMAGE_WIDTH = 256;
    public static final int IMAGE_HEIGHT = 254;

    private ChestMenuLayout() {
    }

    /**
     * @return the highest first visible row so that the last row of {@code itemCount} entries is still shown
     */
    public static int maxScrollRow(int itemCount) {
        int rows = (itemCount + COLUMNS - 1) / COLUMNS;
        return Math.max(0, rows - VISIBLE_ROWS);
    }
}
