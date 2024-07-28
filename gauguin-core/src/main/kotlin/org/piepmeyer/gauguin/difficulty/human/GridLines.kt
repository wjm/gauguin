package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class GridLines(
    private val grid: Grid,
) {
    fun allLines(): Set<GridLine> {
        val lines = mutableSetOf<GridLine>()

        for (column in 0..<grid.gridSize.width) {
            lines += GridLine(grid, GridLineType.COLUMN, column)
        }

        for (row in 0..<grid.gridSize.height) {
            lines += GridLine(grid, GridLineType.ROW, row)
        }

        return lines
    }

    fun linesWithEachPossibleValue(): Set<GridLine> {
        val lines = mutableSetOf<GridLine>()

        if (grid.gridSize.height == grid.gridSize.largestSide()) {
            for (column in 0..<grid.gridSize.width) {
                lines += GridLine(grid, GridLineType.COLUMN, column)
            }
        }

        if (grid.gridSize.width == grid.gridSize.largestSide()) {
            for (row in 0..<grid.gridSize.height) {
                lines += GridLine(grid, GridLineType.ROW, row)
            }
        }

        return lines
    }

    fun adjactPairsOflinesWithEachPossibleValue(): Set<Pair<GridLine, GridLine>> {
        val lines = mutableSetOf<Pair<GridLine, GridLine>>()

        if (grid.gridSize.height == grid.gridSize.largestSide()) {
            for (column in 0..<grid.gridSize.width - 1) {
                lines +=
                    Pair(
                        GridLine(grid, GridLineType.COLUMN, column),
                        GridLine(grid, GridLineType.COLUMN, column + 1),
                    )
            }
        }

        if (grid.gridSize.width == grid.gridSize.largestSide()) {
            for (row in 0..<grid.gridSize.height - 1) {
                lines +=
                    Pair(
                        GridLine(grid, GridLineType.ROW, row),
                        GridLine(grid, GridLineType.ROW, row + 1),
                    )
            }
        }

        return lines
    }
}
