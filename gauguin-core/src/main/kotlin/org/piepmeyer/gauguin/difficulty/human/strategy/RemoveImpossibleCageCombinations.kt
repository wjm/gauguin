package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid

/**
 * Looks out if a cage's cells contain possibles which are not included in any
 * valid combination. If so, deletes these possibles out of all the cage's
 * cells.
 */
class RemoveImpossibleCageCombinations : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val creator = GridSingleCageCreator(grid.variant, cage)

                val reducedPossibles = PossiblesReducer(grid, cage).reduceToPossileCombinations(creator.possibleCombinations)

                if (reducedPossibles) {
                    return true
                }
            }

        return false
    }
}
