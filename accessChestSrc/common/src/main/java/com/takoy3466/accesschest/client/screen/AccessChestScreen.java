package com.takoy3466.accesschest.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.takoy3466.accesschest.client.ClientServices;
import com.takoy3466.accesschest.config.AccessChestConfig;
import com.takoy3466.accesschest.menu.ChestMenuLayout;
import com.takoy3466.accesschest.menu.StorageChestMenu;
import com.takoy3466.accesschest.menu.StoreTarget;
import com.takoy3466.accesschest.menu.ViewState;
import com.takoy3466.accesschest.network.AssignPriorityPayload;
import com.takoy3466.accesschest.network.EjectPayload;
import com.takoy3466.accesschest.network.SetFilterPayload;
import com.takoy3466.accesschest.network.SetScrollPayload;
import com.takoy3466.accesschest.network.SortPayload;
import com.takoy3466.accesschest.network.StorePayload;
import com.takoy3466.accesschest.storage.FilterQuery;
import com.takoy3466.accesschest.storage.PriorityTable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.OptionalInt;

/**
 * Screen of both chest kinds: a scrollable 12 x 8 window, a search field and the operation buttons.
 * All operations are requests to the server; the screen only shows what the server synchronised.
 */
public class AccessChestScreen extends AbstractContainerScreen<StorageChestMenu> {
    private static final String FILTER_HINT_KEY = "gui.accesschest.filter";
    private static final String CLEAR_KEY = "gui.accesschest.clear";
    private static final String SORT_KEY = "gui.accesschest.sort";
    private static final String SORT_TOOLTIP_KEY = "gui.accesschest.sort.tooltip";
    private static final String EJECT_KEY = "gui.accesschest.eject";
    private static final String STORE_INVENTORY_KEY = "gui.accesschest.store_inventory";
    private static final String STORE_HOTBAR_KEY = "gui.accesschest.store_hotbar";

    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLBAR_GAP = 4;
    private static final int SCROLLBAR_X = ChestMenuLayout.CHEST_X + ChestMenuLayout.COLUMNS * ChestMenuLayout.SLOT_SIZE + SCROLLBAR_GAP;
    private static final int SCROLLBAR_Y = ChestMenuLayout.CHEST_Y;
    private static final int SCROLLBAR_HEIGHT = ChestMenuLayout.VISIBLE_ROWS * ChestMenuLayout.SLOT_SIZE;

    private static final int CONTROLS_GAP = 6;
    private static final int CONTROLS_X = ChestMenuLayout.PLAYER_INVENTORY_X + ChestMenuLayout.PLAYER_INVENTORY_COLUMNS * ChestMenuLayout.SLOT_SIZE + CONTROLS_GAP;
    private static final int CONTROLS_WIDTH = 68;
    private static final int FILTER_Y = ChestMenuLayout.PLAYER_INVENTORY_Y;
    private static final int FILTER_HEIGHT = 16;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_SPACING = 20;
    private static final int FIRST_BUTTON_Y = FILTER_Y + FILTER_HEIGHT + 2;

    private static final int TITLE_Y = 6;
    /** The inventory label sits directly above the player inventory. */
    private static final int INVENTORY_LABEL_OFFSET = 9;
    private static final int NUMBER_KEY_COUNT = 10;

    private EditBox filterBox;
    private Button sortButton;
    private Button ejectButton;
    private Button storeInventoryButton;
    private Button storeHotbarButton;
    private boolean draggingScrollbar;
    /** Set when a key focused the search field, so that the same key press does not also type a character. */
    private boolean ignoreNextChar;
    /** Remembered row of a chest opened from an item, applied after the first view sync. */
    private OptionalInt pendingScrollRestore = OptionalInt.empty();

    public AccessChestScreen(StorageChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = ChestMenuLayout.IMAGE_WIDTH;
        this.imageHeight = ChestMenuLayout.IMAGE_HEIGHT;
        this.titleLabelX = ChestMenuLayout.CHEST_X;
        this.titleLabelY = TITLE_Y;
        this.inventoryLabelX = ChestMenuLayout.PLAYER_INVENTORY_X;
        this.inventoryLabelY = ChestMenuLayout.PLAYER_INVENTORY_Y - INVENTORY_LABEL_OFFSET;
        if (menu.data().remote()) {
            this.pendingScrollRestore = ScrollMemory.recall(menu.data().spec());
        }
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos + CONTROLS_X;

        filterBox = new EditBox(font, x, topPos + FILTER_Y, CONTROLS_WIDTH, FILTER_HEIGHT, Component.translatable(FILTER_HINT_KEY));
        filterBox.setMaxLength(FilterQuery.MAX_LENGTH);
        filterBox.setHint(Component.translatable(FILTER_HINT_KEY));
        filterBox.setResponder(text -> send(new SetFilterPayload(text)));
        addRenderableWidget(filterBox);

        addRenderableWidget(button(CLEAR_KEY, 0, () -> filterBox.setValue("")));
        sortButton = addRenderableWidget(button(SORT_KEY, 1, this::onSortPressed));
        sortButton.setTooltip(Tooltip.create(Component.translatable(SORT_TOOLTIP_KEY)));
        ejectButton = addRenderableWidget(button(EJECT_KEY, 2, () -> send(EjectPayload.INSTANCE)));
        storeInventoryButton = addRenderableWidget(button(STORE_INVENTORY_KEY, 1, () -> send(new StorePayload(StoreTarget.MAIN_INVENTORY))));
        storeHotbarButton = addRenderableWidget(button(STORE_HOTBAR_KEY, 2, () -> send(new StorePayload(StoreTarget.HOTBAR))));
        updateShiftButtons();
    }

    private Button button(String translationKey, int row, Runnable action) {
        return Button.builder(Component.translatable(translationKey), pressed -> action.run())
                .bounds(leftPos + CONTROLS_X, topPos + FIRST_BUTTON_Y + row * BUTTON_SPACING, CONTROLS_WIDTH, BUTTON_HEIGHT)
                .build();
    }

    private static void send(CustomPacketPayload payload) {
        ClientServices.NETWORK.sendToServer(payload);
    }

    // ---- Buttons -------------------------------------------------------------------------------------------------

    /** Sort, or assign a priority when a number key is held (as in the 1.7.10 version). */
    private void onSortPressed() {
        long window = minecraft.getWindow().getWindow();
        for (int digit = 0; digit < NUMBER_KEY_COUNT; digit++) {
            if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_0 + digit) && PriorityTable.isValid(digit)) {
                send(new AssignPriorityPayload(digit));
                return;
            }
        }
        send(SortPayload.INSTANCE);
    }

    /** Holding shift swaps "Sort"/"Eject" for the two store buttons. */
    private void updateShiftButtons() {
        boolean shift = hasShiftDown();
        sortButton.visible = !shift;
        ejectButton.visible = !shift;
        storeInventoryButton.visible = shift;
        storeHotbarButton.visible = shift;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateShiftButtons();
        restoreScrollOnceSynced();
    }

    /**
     * The view size arrives from the server after the screen opened; restoring earlier would clamp the row to 0.
     * An unfiltered view always has at least one slot, so a positive match count means the first sync happened.
     */
    private void restoreScrollOnceSynced() {
        if (pendingScrollRestore.isPresent() && menu.viewState().matchCount() > 0) {
            requestScroll(pendingScrollRestore.getAsInt());
            pendingScrollRestore = OptionalInt.empty();
        }
    }

    // ---- Scrolling -----------------------------------------------------------------------------------------------

    private void requestScroll(int row) {
        ViewState view = menu.viewState();
        int clamped = Mth.clamp(row, 0, view.maxScrollRow());
        if (clamped != view.scrollRow()) {
            send(new SetScrollPayload(clamped));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0) {
            int steps = (int) Math.signum(scrollY);
            requestScroll(menu.viewState().scrollRow() - steps * AccessChestConfig.WHEEL_SCROLL_ROWS.get());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        double x = mouseX - leftPos - SCROLLBAR_X;
        double y = mouseY - topPos - SCROLLBAR_Y;
        return x >= 0 && x < SCROLLER_WIDTH && y >= 0 && y < SCROLLBAR_HEIGHT;
    }

    private void scrollToMouse(double mouseY) {
        double travel = SCROLLBAR_HEIGHT - SCROLLER_HEIGHT;
        double fraction = (mouseY - topPos - SCROLLBAR_Y - SCROLLER_HEIGHT / 2.0) / travel;
        requestScroll((int) Math.round(Mth.clamp(fraction, 0.0, 1.0) * menu.viewState().maxScrollRow()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isOverScrollbar(mouseX, mouseY)) {
            draggingScrollbar = true;
            scrollToMouse(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar) {
            scrollToMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // ---- Keyboard ------------------------------------------------------------------------------------------------

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (filterBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                send(new SetFilterPayload(FilterQuery.EXACT_PREFIX + filterBox.getValue()));
                return true;
            }
            // Consume everything else so that typing "e" does not close the screen.
            return filterBox.keyPressed(keyCode, scanCode, modifiers) || filterBox.canConsumeInput();
        }
        if (minecraft.options.keyChat.matches(keyCode, scanCode) || minecraft.options.keyCommand.matches(keyCode, scanCode)) {
            setFocused(filterBox);
            ignoreNextChar = true;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F && hasControlDown() && hoveredSlot != null && hoveredSlot.hasItem()) {
            // The registry id works on dedicated servers, whose item names are not in the player's language.
            filterBox.setValue(BuiltInRegistries.ITEM.getKey(hoveredSlot.getItem().getItem()).toString());
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            requestScroll(menu.viewState().scrollRow() - ChestMenuLayout.VISIBLE_ROWS);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            requestScroll(menu.viewState().scrollRow() + ChestMenuLayout.VISIBLE_ROWS);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (ignoreNextChar) {
            ignoreNextChar = false;
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    // ---- Rendering -----------------------------------------------------------------------------------------------

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiPainter.panel(graphics, leftPos, topPos, imageWidth, imageHeight);
        renderChestSlots(graphics);
        renderPlayerSlots(graphics);
        renderScrollbar(graphics);
    }

    private void renderChestSlots(GuiGraphics graphics) {
        ViewState view = menu.viewState();
        for (int index = 0; index < ChestMenuLayout.WINDOW_SLOT_COUNT; index++) {
            int x = leftPos + ChestMenuLayout.CHEST_X + (index % ChestMenuLayout.COLUMNS) * ChestMenuLayout.SLOT_SIZE;
            int y = topPos + ChestMenuLayout.CHEST_Y + (index / ChestMenuLayout.COLUMNS) * ChestMenuLayout.SLOT_SIZE;
            if (view.isVisible(index)) {
                GuiPainter.slot(graphics, x, y);
            } else {
                GuiPainter.inactiveSlot(graphics, x, y);
            }
        }
    }

    private void renderPlayerSlots(GuiGraphics graphics) {
        for (int row = 0; row < ChestMenuLayout.PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < ChestMenuLayout.PLAYER_INVENTORY_COLUMNS; column++) {
                GuiPainter.slot(graphics,
                        leftPos + ChestMenuLayout.PLAYER_INVENTORY_X + column * ChestMenuLayout.SLOT_SIZE,
                        topPos + ChestMenuLayout.PLAYER_INVENTORY_Y + row * ChestMenuLayout.SLOT_SIZE);
            }
        }
        for (int column = 0; column < ChestMenuLayout.PLAYER_INVENTORY_COLUMNS; column++) {
            GuiPainter.slot(graphics,
                    leftPos + ChestMenuLayout.PLAYER_INVENTORY_X + column * ChestMenuLayout.SLOT_SIZE,
                    topPos + ChestMenuLayout.HOTBAR_Y);
        }
    }

    private void renderScrollbar(GuiGraphics graphics) {
        int x = leftPos + SCROLLBAR_X;
        int y = topPos + SCROLLBAR_Y;
        GuiPainter.inset(graphics, x, y, SCROLLER_WIDTH, SCROLLBAR_HEIGHT);

        ViewState view = menu.viewState();
        int maxRow = view.maxScrollRow();
        if (maxRow > 0) {
            int offset = (SCROLLBAR_HEIGHT - SCROLLER_HEIGHT) * view.scrollRow() / maxRow;
            graphics.blitSprite(SCROLLER_SPRITE, x, y + offset, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        } else {
            graphics.blitSprite(SCROLLER_DISABLED_SPRITE, x, y, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }
    }

    @Override
    public void removed() {
        if (menu.data().remote()) {
            ScrollMemory.remember(menu.data().spec(), menu.viewState().scrollRow());
        }
        super.removed();
    }
}
