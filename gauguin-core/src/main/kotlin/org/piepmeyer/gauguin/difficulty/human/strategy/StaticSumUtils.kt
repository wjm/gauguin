package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell

object StaticSumUtils {
    fun staticSum(
        grid: Grid,
        cage: GridCage,
    ): Int {
        if (cage.cageType == GridCageType.SINGLE) {
            return cage.cells.first().value
        }

        if (cage.cells.all { it.isUserValueSet }) {
            return cage.cells.map { it.userValue }.sum()
        }

        return ValidPossiblesCalculator(grid, cage)
            .calculatePossibles()
            .first()
            .sum()
    }

    fun staticSumInCells(
        grid: Grid,
        cage: GridCage,
        cells: List<GridCell>,
    ): Int {
        if (cage.cageType == GridCageType.SINGLE) {
            return if (cage.cells.first() in cells) {
                cage.cells.first().value
            } else {
                0
            }
        }

        val filteredCells = cage.cells.filter { it in cells }

        if (filteredCells.all { it.isUserValueSet }) {
            return filteredCells.sumOf { it.userValue }
        }

        return ValidPossiblesCalculator(grid, cage)
            .calculatePossibles()
            .map { it.filterIndexed { index, _ -> cage.cells[index] in cells } }
            .first()
            .sum()
    }

    fun hasStaticSum(
        grid: Grid,
        cage: GridCage,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE || cage.action == GridCageAction.ACTION_ADD) {
            return true
        }

        val validPossiblesSums =
            ValidPossiblesCalculator(grid, cage)
                .calculatePossibles()
                .map { it.sum() }
                .distinct()

        return validPossiblesSums.size == 1
    }

    fun hasStaticSumInCells(
        grid: Grid,
        cage: GridCage,
        cells: List<GridCell>,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE && cage.cells.first() in cells) {
            return true
        }

        val validPossiblesSums =
            ValidPossiblesCalculator(grid, cage)
                .calculatePossibles()
                .map { it.filterIndexed { index, _ -> cage.cells[index] in cells } }
                .map { it.sum() }
                .distinct()

        return validPossiblesSums.size == 1
    }
}
