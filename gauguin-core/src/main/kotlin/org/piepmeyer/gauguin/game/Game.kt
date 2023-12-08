package org.piepmeyer.gauguin.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.grid.GridSolveService
import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager
import org.piepmeyer.gauguin.undo.UndoManager

data class Game(
    var grid: Grid,
    var undoManager: UndoManager,
    var gridUI: GridView
) : KoinComponent {
    private val statisticsManager: StatisticsManager by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    private var lastCellWithModifiedPossibles: GridCell? = null
    private var solvedListener: GameSolvedListener? = null

    private val gridCreationListeners = mutableListOf<GridCreationListener>()

    private var gameMode: GameMode = RegularGameMode(this, applicationPreferences)
    private val gameModeListeners = mutableListOf<GameModeListener>()

    fun enterFastFinishingMode() {
        gameMode = FastFinishingGameMode(this)
        gameModeListeners.forEach { it.changedGameMode() }

        gridUI.invalidate()
    }

    fun exitFastFinishingMode() {
        gameMode = RegularGameMode(this, applicationPreferences)
        gameModeListeners.forEach { it.changedGameMode() }

        gridUI.invalidate()
    }

    fun addGridCreationListener(gridCreationListener: GridCreationListener) {
        gridCreationListeners += gridCreationListener
    }

    fun updateGrid(newGrid: Grid) {
        grid = newGrid
        gridUI.grid = grid

        ensureNotInFastFinishingMode()

        gridCreationListeners.forEach { it.freshGridWasCreated() }
    }

    fun enterNumber(number: Int) {
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive) {
            return
        }

        gridHasBeenPlayed()

        clearLastModified()
        undoManager.saveUndo(selectedCell, false)
        selectedCell.setUserValueExtern(number)

        if (applicationPreferences.removePencils()) {
            removePossibles(selectedCell)
        }

        lastCellWithModifiedPossibles = null

        if (grid.isSolved) {
            selectedCell.isSelected = false
            selectedCell.cage().setSelected(false)
            grid.isActive = false

            ensureNotInFastFinishingMode()

            solvedListener?.puzzleSolved()
            statisticsManager.puzzleSolved(grid)
        }

        grid.userValueChanged()

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    private fun ensureNotInFastFinishingMode() {
        if (isInFastFinishingMode()) {
            exitFastFinishingMode()
        }
    }

    fun setSolvedHandler(listener: GameSolvedListener?) {
        solvedListener = listener
    }

    fun enterPossibleNumber(number: Int) {
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive) {
            return
        }

        gridHasBeenPlayed()

        gameMode.enterPossibleNumber(selectedCell, number)

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun enterPossibleNumberCore(selectedCell: GridCell, number: Int) {
        clearLastModified()
        undoManager.saveUndo(selectedCell, false)
        if (selectedCell.isUserValueSet) {
            val oldValue = selectedCell.userValue
            selectedCell.clearUserValue()
            selectedCell.togglePossible(oldValue)
            grid.userValueChanged()
        }
        selectedCell.togglePossible(number)

        lastCellWithModifiedPossibles = selectedCell
    }

    private fun gridHasBeenPlayed() {
        if (!grid.startedToBePlayed) {
            grid.startedToBePlayed = true

            statisticsManager.puzzleStartedToBePlayed()
        }
    }

    private fun removePossibles(selectedCell: GridCell) {
        val possibleCells = grid.getPossiblesInRowCol(selectedCell)
        for (cell in possibleCells) {
            undoManager.saveUndo(cell, true)
            cell.isLastModified = true
            cell.removePossible(selectedCell.userValue)
        }
    }

    fun cellClicked(cell: GridCell) {
        selectCell(cell)

        gameMode.cellClicked(cell)

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun selectCell(cell: GridCell) {
        grid.selectedCell = cell

        for (c in grid.cells) {
            c.isSelected = false
            c.cage().setSelected(false)
        }

        cell.isSelected = true
        cell.cage().setSelected(true)
    }

    fun eraseSelectedCell() {
        val selectedCell = grid.selectedCell ?: return

        if (!grid.isActive) {
            return
        }

        if (selectedCell.isUserValueSet || selectedCell.possibles.isNotEmpty()) {
            clearLastModified()
            undoManager.saveUndo(selectedCell, false)
            selectedCell.clearUserValue()
            selectedCell.clearPossibles()
            grid.userValueChanged()
        }
    }

    fun longClickOnSelectedCell(): Boolean {
        val selectedCell = grid.selectedCell ?: return false

        if (!grid.isActive) {
            return false
        }

        gameMode.cellLongClicked(selectedCell)

        return true
    }

    fun copyPossiblesFromLastEnteredCell(selectedCell: GridCell) {
        lastCellWithModifiedPossibles?.let {
            if (it.cage() == selectedCell.cage()) {
                undoManager.saveUndo(selectedCell, false)
                selectedCell.addPossibles(it.possibles)
                gridUI.invalidate()
            }
        }
    }

    private fun clearUserValues() {
        ensureNotInFastFinishingMode()

        grid.clearUserValues()
        gridUI.invalidate()
    }

    private fun clearLastModified() {
        grid.clearLastModified()
        gridUI.invalidate()
    }

    fun revealSelectedCage(): Boolean {
        grid.selectedCell ?: return false
        GridSolveService(grid).revealSelectedCage()
        gridUI.invalidate()
        return true
    }

    fun solveGrid() {
        GridSolveService(grid).solveGrid()
        gridUI.invalidate()
    }

    fun revealSelectedCell(): Boolean {
        grid.selectedCell ?: return false
        GridSolveService(grid).revealSelectedCell()
        gridUI.invalidate()
        return true
    }

    fun markInvalidChoices() {
        grid.markInvalidChoices(applicationPreferences.showDupedDigits())
        gridUI.invalidate()
    }

    fun undoOneStep() {
        ensureNotInFastFinishingMode()

        clearLastModified()
        undoManager.restoreUndo()
        grid.userValueChanged()

        gridUI.invalidate()
    }

    fun restartGame() {
        clearUserValues()
        grid.cells.forEach { it.clearPossibles() }
        grid.isActive = true
    }

    fun clearUndoList() {
        undoManager.clear()
    }

    fun solveAllMissingCells() {
        grid.cells.forEach {
            cellClicked(it)
            enterNumber(it.value)
        }
    }

    fun addGameModeListener(listener: GameModeListener) {
        gameModeListeners += listener
    }

    fun isInFastFinishingMode(): Boolean = gameMode.isFastFinishingMode()
}
