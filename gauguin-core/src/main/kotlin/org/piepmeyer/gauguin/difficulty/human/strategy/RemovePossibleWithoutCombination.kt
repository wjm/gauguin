package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

/**
 * Calculates all possible combinations per cage and deletes one possible that is not contained
 * in one of the combinations.
 */
class RemovePossibleWithoutCombination : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val possibles = GridSingleCageCreator(grid.variant, cage).possibleCombinations

                cage.cells.forEachIndexed { index, cageCell ->
                    if (!cageCell.isUserValueSet) {
                        cageCell.possibles.forEach { possibleValue ->
                            if (possibles.none { it[index] == possibleValue }) {
                                cageCell.removePossible(possibleValue)

                                return true
                            }
                        }
                    }
                }
            }

        return false
    }
}
