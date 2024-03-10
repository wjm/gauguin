package org.piepmeyer.gauguin.calculation

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.SingleCageUsage

@Serializable
data class GamePreviewVariant(
    val gridSize: GridSize,
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var singleCageUsage: SingleCageUsage,
    var difficultySetting: DifficultySetting,
) {
    companion object {
        fun fromGameVariant(variant: GameVariant): GamePreviewVariant {
            return GamePreviewVariant(
                gridSize = variant.gridSize,
                cageOperation = variant.options.cageOperation,
                showOperators = variant.options.showOperators,
                digitSetting = variant.options.digitSetting,
                singleCageUsage = variant.options.singleCageUsage,
                difficultySetting = variant.options.difficultySetting,
            )
        }
    }
}
