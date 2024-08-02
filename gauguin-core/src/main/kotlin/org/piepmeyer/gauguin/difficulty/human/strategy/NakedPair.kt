package org.piepmeyer.gauguin.difficulty.human.strategy

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

private val logger = KotlinLogging.logger {}

/**
 * Finds a naked pair, that is two cell in the same row or column which have to same list of
 * exactly two possible values. As these values could not occur in any other cells beside these
 * two, these values get deletes from the other cages possibles.
 */
class NakedPair : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val cellsWithoutUserValue = grid.cells.filter { !it.isUserValueSet }

        cellsWithoutUserValue.forEach { cell ->
            cellsWithoutUserValue.forEach { otherCell ->
                if (isNakedPair(cell, otherCell)) {
                    val possibles = cell.possibles

                    val cellsOfSameRowOrColumn =
                        if (cell.row == otherCell.row) {
                            grid.getCellsAtSameRow(cell) - otherCell
                        } else {
                            grid.getCellsAtSameColumn(cell) - otherCell
                        }

                    val cellsWithPossibles =
                        cellsOfSameRowOrColumn
                            .filter { !it.isUserValueSet }
                            .filter { it.possibles.intersect(possibles).isNotEmpty() }

                    if (cellsWithPossibles.isNotEmpty()) {
                        cellsWithPossibles.forEach {
                            it.possibles -= possibles
                        }

                        logger.trace { "Naked pair found: ${cell.cellNumber} and ${otherCell.cellNumber}" }

                        return true
                    }
                }
            }
        }

        return false
    }

    private fun isNakedPair(
        cell: GridCell,
        otherCell: GridCell,
    ) = cell != otherCell &&
        (cell.row == otherCell.row || cell.column == otherCell.column) &&
        cell.possibles.size == 2 &&
        otherCell.possibles.size == 2 &&
        cell.possibles.containsAll(otherCell.possibles)
}
