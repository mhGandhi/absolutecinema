package net.absolutecinema.rendering.shader;

import java.util.List;

public record LayoutEntry (String name, int size, FieldType type, boolean normalize){
    public static String layoutToString(List<LayoutEntry> pLayout){
        StringBuilder sb = new StringBuilder();
        for (LayoutEntry entry : pLayout) {
            sb.append("[name=").append(entry.name())
                    .append(", size=").append(entry.size())
                    .append(", type=").append(entry.type())
                    .append(", normalize=").append(entry.normalize())
                    .append("]\n");
        }
        return sb.toString();
    }
}
