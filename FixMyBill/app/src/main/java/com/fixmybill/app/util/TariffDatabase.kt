package com.fixmybill.app.util

object TariffDatabase {

    data class TariffSlabInfo(
        val slabStart: Int,
        val slabEnd: Int,
        val ratePerUnit: Double,
        val description: String
    )

    private data class BoardConfig(
        val slabs: List<TariffSlabInfo>,
        val fixedCharges: List<Pair<IntRange, Double>>,
        val fuelSurchargeRate: Double,
        val electricityDutyRate: Double,
        val minimumCharges: Double,
        val isTelescopicBilling: Boolean = true
    )

    private val tariffData: Map<String, BoardConfig> = mapOf(
        "TSSPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units"),
                TariffSlabInfo(301, 400, 7.00, "301-400 units"),
                TariffSlabInfo(401, 500, 8.50, "401-500 units"),
                TariffSlabInfo(501, Int.MAX_VALUE, 9.50, "Above 500 units")
            ),
            fixedCharges = listOf(
                0..50 to 30.0,
                51..100 to 50.0,
                101..200 to 70.0,
                201..300 to 100.0,
                301..400 to 130.0,
                401..500 to 150.0,
                501..Int.MAX_VALUE to 200.0
            ),
            fuelSurchargeRate = 0.10,
            electricityDutyRate = 0.06,
            minimumCharges = 30.0
        ),
        "TSNPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units"),
                TariffSlabInfo(301, 400, 7.00, "301-400 units"),
                TariffSlabInfo(401, 500, 8.50, "401-500 units"),
                TariffSlabInfo(501, Int.MAX_VALUE, 9.50, "Above 500 units")
            ),
            fixedCharges = listOf(
                0..50 to 30.0,
                51..100 to 50.0,
                101..200 to 70.0,
                201..300 to 100.0,
                301..400 to 130.0,
                401..500 to 150.0,
                501..Int.MAX_VALUE to 200.0
            ),
            fuelSurchargeRate = 0.10,
            electricityDutyRate = 0.06,
            minimumCharges = 30.0
        ),
        "APSPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units"),
                TariffSlabInfo(301, Int.MAX_VALUE, 8.50, "Above 300 units")
            ),
            fixedCharges = listOf(
                0..50 to 25.0,
                51..100 to 45.0,
                101..200 to 60.0,
                201..300 to 90.0,
                301..Int.MAX_VALUE to 120.0
            ),
            fuelSurchargeRate = 0.12,
            electricityDutyRate = 0.06,
            minimumCharges = 25.0
        ),
        "APEPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units"),
                TariffSlabInfo(301, Int.MAX_VALUE, 8.50, "Above 300 units")
            ),
            fixedCharges = listOf(
                0..50 to 25.0,
                51..100 to 45.0,
                101..200 to 60.0,
                201..300 to 90.0,
                301..Int.MAX_VALUE to 120.0
            ),
            fuelSurchargeRate = 0.12,
            electricityDutyRate = 0.06,
            minimumCharges = 25.0
        ),
        "BESCOM" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 30, 4.10, "0-30 units"),
                TariffSlabInfo(31, 100, 5.55, "31-100 units"),
                TariffSlabInfo(101, 200, 7.10, "101-200 units"),
                TariffSlabInfo(201, Int.MAX_VALUE, 8.15, "Above 200 units")
            ),
            fixedCharges = listOf(
                0..30 to 35.0,
                31..100 to 55.0,
                101..200 to 75.0,
                201..Int.MAX_VALUE to 100.0
            ),
            fuelSurchargeRate = 0.11,
            electricityDutyRate = 0.09,
            minimumCharges = 35.0
        ),
        "MESCOM" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 30, 4.10, "0-30 units"),
                TariffSlabInfo(31, 100, 5.55, "31-100 units"),
                TariffSlabInfo(101, 200, 7.10, "101-200 units"),
                TariffSlabInfo(201, Int.MAX_VALUE, 8.15, "Above 200 units")
            ),
            fixedCharges = listOf(
                0..30 to 35.0,
                31..100 to 55.0,
                101..200 to 75.0,
                201..Int.MAX_VALUE to 100.0
            ),
            fuelSurchargeRate = 0.11,
            electricityDutyRate = 0.09,
            minimumCharges = 35.0
        ),
        "MSEDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 100, 4.71, "0-100 units"),
                TariffSlabInfo(101, 300, 7.88, "101-300 units"),
                TariffSlabInfo(301, 500, 10.29, "301-500 units"),
                TariffSlabInfo(501, Int.MAX_VALUE, 11.72, "Above 500 units")
            ),
            fixedCharges = listOf(
                0..100 to 70.0,
                101..300 to 115.0,
                301..500 to 160.0,
                501..Int.MAX_VALUE to 210.0
            ),
            fuelSurchargeRate = 0.15,
            electricityDutyRate = 0.16,
            minimumCharges = 70.0
        ),
        "TANGEDCO" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 100, 0.0, "0-100 units (free for bimonthly up to 200)"),
                TariffSlabInfo(101, 200, 2.00, "101-200 units"),
                TariffSlabInfo(201, 500, 3.00, "201-500 units"),
                TariffSlabInfo(501, Int.MAX_VALUE, 6.00, "Above 500 units")
            ),
            fixedCharges = listOf(
                0..100 to 0.0,
                101..200 to 30.0,
                201..500 to 50.0,
                501..Int.MAX_VALUE to 80.0
            ),
            fuelSurchargeRate = 0.05,
            electricityDutyRate = 0.05,
            minimumCharges = 0.0
        ),
        "TNEB" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 100, 0.0, "0-100 units (free)"),
                TariffSlabInfo(101, 200, 2.00, "101-200 units"),
                TariffSlabInfo(201, 500, 3.00, "201-500 units"),
                TariffSlabInfo(501, Int.MAX_VALUE, 6.00, "Above 500 units")
            ),
            fixedCharges = listOf(
                0..100 to 0.0,
                101..200 to 30.0,
                201..500 to 50.0,
                501..Int.MAX_VALUE to 80.0
            ),
            fuelSurchargeRate = 0.05,
            electricityDutyRate = 0.05,
            minimumCharges = 0.0
        ),
        "BSES" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 200, 3.00, "0-200 units"),
                TariffSlabInfo(201, 400, 4.50, "201-400 units"),
                TariffSlabInfo(401, 800, 6.50, "401-800 units"),
                TariffSlabInfo(801, Int.MAX_VALUE, 7.75, "Above 800 units")
            ),
            fixedCharges = listOf(
                0..200 to 20.0,
                201..400 to 40.0,
                401..800 to 80.0,
                801..Int.MAX_VALUE to 125.0
            ),
            fuelSurchargeRate = 0.08,
            electricityDutyRate = 0.05,
            minimumCharges = 20.0
        ),
        "TATA_POWER_DELHI" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 200, 3.00, "0-200 units"),
                TariffSlabInfo(201, 400, 4.50, "201-400 units"),
                TariffSlabInfo(401, 800, 6.50, "401-800 units"),
                TariffSlabInfo(801, Int.MAX_VALUE, 7.75, "Above 800 units")
            ),
            fixedCharges = listOf(
                0..200 to 25.0,
                201..400 to 50.0,
                401..800 to 100.0,
                801..Int.MAX_VALUE to 150.0
            ),
            fuelSurchargeRate = 0.08,
            electricityDutyRate = 0.05,
            minimumCharges = 25.0
        ),
        "WBSEDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 25, 5.28, "0-25 units"),
                TariffSlabInfo(26, 60, 5.83, "26-60 units"),
                TariffSlabInfo(61, 100, 6.41, "61-100 units"),
                TariffSlabInfo(101, 150, 7.16, "101-150 units"),
                TariffSlabInfo(151, Int.MAX_VALUE, 8.84, "Above 150 units")
            ),
            fixedCharges = listOf(
                0..25 to 15.0,
                26..60 to 30.0,
                61..100 to 50.0,
                101..150 to 65.0,
                151..Int.MAX_VALUE to 85.0
            ),
            fuelSurchargeRate = 0.10,
            electricityDutyRate = 0.06,
            minimumCharges = 15.0
        )
    )

    fun getTariffSlabs(utilityBoard: String, category: String = "DOMESTIC"): List<TariffSlabInfo> {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.slabs ?: emptyList()
    }

    fun getFixedCharges(utilityBoard: String, units: Int): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        val config = tariffData[normalizedBoard] ?: return 0.0
        return config.fixedCharges.firstOrNull { units in it.first }?.second ?: 0.0
    }

    fun getFuelSurchargeRate(utilityBoard: String): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.fuelSurchargeRate ?: 0.10
    }

    fun getElectricityDutyRate(utilityBoard: String): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.electricityDutyRate ?: 0.06
    }

    fun getMinimumCharges(utilityBoard: String): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.minimumCharges ?: 0.0
    }

    fun isTelescopicBilling(utilityBoard: String): Boolean {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.isTelescopicBilling ?: true
    }

    fun getSupportedBoards(): List<String> = tariffData.keys.toList()

    private fun normalizeBoard(board: String): String {
        val upper = board.uppercase().trim()
        return when {
            upper.contains("TSSPDCL") -> "TSSPDCL"
            upper.contains("TSNPDCL") -> "TSNPDCL"
            upper.contains("APSPDCL") -> "APSPDCL"
            upper.contains("APEPDCL") || upper.contains("APEPCDL") -> "APEPDCL"
            upper.contains("BESCOM") -> "BESCOM"
            upper.contains("MESCOM") -> "MESCOM"
            upper.contains("MSEDCL") -> "MSEDCL"
            upper.contains("TANGEDCO") || upper.contains("TNEB") -> "TANGEDCO"
            upper.contains("BSES") -> "BSES"
            upper.contains("TATA") && upper.contains("DELHI") -> "TATA_POWER_DELHI"
            upper.contains("WBSEDCL") -> "WBSEDCL"
            tariffData.containsKey(upper) -> upper
            else -> tariffData.keys.firstOrNull { it.contains(upper) || upper.contains(it) } ?: upper
        }
    }
}
