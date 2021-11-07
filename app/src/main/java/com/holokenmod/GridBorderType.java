package com.holokenmod;

public enum GridBorderType {
    BORDER_NONE(0),
    BORDER_SOLID(1),
    BORDER_WARN(3),
    BORDER_CAGE_SELECTED(4);

    private final int index;

    GridBorderType(final int index) {
        this.index = index;
    }

    int getIndex() {
        return index;
    }

    public boolean isHighlighted() {
        return this == BORDER_WARN || this == BORDER_CAGE_SELECTED;
    }
}
