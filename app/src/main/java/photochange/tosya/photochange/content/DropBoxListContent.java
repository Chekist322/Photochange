package photochange.tosya.photochange.content;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DropBoxListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DropBoxItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DropBoxItem> ITEM_MAP = new HashMap<String, DropBoxItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DropBoxItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    private static DropBoxItem createDummyItem(int position) {
        return new DropBoxItem(String.valueOf(position), null, makeDetails(position));
    }

    private static DropBoxItem createItemFromUrl(int position) {
        return new DropBoxItem(String.valueOf(position), null, "");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DropBoxItem {
        public final String name;
        public final Bitmap avatar;
        public final String path;

        public DropBoxItem(String name, Bitmap avatar, String path) {
            this.name = name;
            this.avatar = avatar;
            this.path = path;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
