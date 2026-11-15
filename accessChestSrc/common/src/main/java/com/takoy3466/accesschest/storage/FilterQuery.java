package com.takoy3466.accesschest.storage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Search condition typed into the chest screen.
 * <ul>
 *     <li>empty text: every slot, including empty ones</li>
 *     <li>{@code exact:<text>}: display name, translation key or registry id equals the text</li>
 *     <li>{@code pri:<n>}: stacks whose sort priority is n</li>
 *     <li>anything else: display name, translation key or registry id contains the text (case insensitive)</li>
 * </ul>
 *
 * @param mode     how {@code text} is interpreted
 * @param text     search text (lower case for {@link Mode#CONTAINS})
 * @param priority priority for {@link Mode#PRIORITY}, unused otherwise
 */
public record FilterQuery(Mode mode, String text, int priority) {

    public static final int MAX_LENGTH = 100;
    public static final String EXACT_PREFIX = "exact:";
    public static final String PRIORITY_PREFIX = "pri:";
    public static final FilterQuery ALL = new FilterQuery(Mode.ALL, "", PriorityTable.DEFAULT_PRIORITY);

    public enum Mode {
        ALL,
        CONTAINS,
        EXACT,
        PRIORITY
    }

    public static FilterQuery parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return ALL;
        }
        if (raw.startsWith(PRIORITY_PREFIX)) {
            try {
                int priority = Integer.parseInt(raw.substring(PRIORITY_PREFIX.length()).trim());
                if (PriorityTable.isValid(priority)) {
                    return new FilterQuery(Mode.PRIORITY, "", priority);
                }
            } catch (NumberFormatException ignored) {
                // Not a priority command; the text is searched literally like any other text.
            }
        }
        if (raw.startsWith(EXACT_PREFIX)) {
            return new FilterQuery(Mode.EXACT, raw.substring(EXACT_PREFIX.length()), PriorityTable.DEFAULT_PRIORITY);
        }
        return new FilterQuery(Mode.CONTAINS, raw.toLowerCase(Locale.ROOT), PriorityTable.DEFAULT_PRIORITY);
    }

    public boolean matches(ItemStack stack, PriorityTable priorities) {
        return switch (mode) {
            case ALL -> true;
            case PRIORITY -> !stack.isEmpty() && priorities.priorityOf(stack) == priority;
            case EXACT -> !stack.isEmpty() && searchableNames(stack).anyMatch(text::equals);
            case CONTAINS -> !stack.isEmpty() && searchableNames(stack).anyMatch(name -> name.toLowerCase(Locale.ROOT).contains(text));
        };
    }

    private static Stream<String> searchableNames(ItemStack stack) {
        return Stream.of(
                stack.getHoverName().getString(),
                stack.getDescriptionId(),
                BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
        );
    }
}
