package com.holokenmod;

import java.util.HashMap;
import java.util.Map;

class GridCellBorders {
    private final Map<Direction, GridBorderType> borders = new HashMap<>();

    GridCellBorders(GridBorderType north, GridBorderType east, GridBorderType south, GridBorderType west) {
        borders.put(Direction.NORTH, north);
        borders.put(Direction.WEST, west);
        borders.put(Direction.SOUTH, south);
        borders.put(Direction.EAST, east);
    }

    GridBorderType getBorderType(Direction direction) {
        return borders.get(direction);
    }

    void setBorderType(Direction direction, GridBorderType borderType) {
        borders.put(direction, borderType);
    }
}
