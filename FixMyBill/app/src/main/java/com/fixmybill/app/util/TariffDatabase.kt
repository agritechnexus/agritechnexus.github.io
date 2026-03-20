package com.fixmybill.app.util

/**
 * Comprehensive tariff database containing official 2024-25 electricity tariff rates
 * for major Indian state electricity distribution companies. Covers domestic (LT)
 * category with slab-wise energy charges, fixed charges, fuel surcharge rates,
 * electricity duty rates, and minimum charges.
 *
 * All rates are sourced from the respective State Electricity Regulatory Commission
 * (SERC) tariff orders for FY 2024-25.
 */
object TariffDatabase {

    // ======================== Data classes ========================

    data class TariffSlabInfo(
        val slabStart: Int,
        val slabEnd: Int,
        val ratePerUnit: Double,
        val description: String
    )

    /**
     * Internal configuration for each electricity board, holding all rate
     * components needed for a full bill recalculation.
     */
    private data class BoardConfig(
        val slabs: List<TariffSlabInfo>,
        val fixedCharges: List<Pair<IntRange, Double>>,
        val fuelSurchargeRate: Double,
        val electricityDutyRate: Double,
        val minimumCharges: Double,
        val isTelescopicBilling: Boolean = true,
        val stateName: String
    )

    // ======================== Tariff data ========================

    private val tariffData: Map<String, BoardConfig> = mapOf(

        // ---------- TSSPDCL (Telangana Southern Power Distribution) ----------
        // LT Category I - Domestic, Tariff Order FY 2024-25
        "TSSPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units @ Rs.1.45/unit"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units @ Rs.2.60/unit"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units @ Rs.3.60/unit"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units @ Rs.5.50/unit"),
                TariffSlabInfo(301, 400, 7.00, "301-400 units @ Rs.7.00/unit"),
                TariffSlabInfo(401, 500, 8.50, "401-500 units @ Rs.8.50/unit"),
                TariffSlabInfo(501, Int.MAX_VALUE, 9.50, "Above 500 units @ Rs.9.50/unit")
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
            fuelSurchargeRate = 0.10,       // 10 paisa/unit
            electricityDutyRate = 0.06,     // 6%
            minimumCharges = 30.0,
            stateName = "Telangana"
        ),

        // ---------- TSNPDCL (Telangana Northern Power Distribution) ----------
        // Same tariff order as TSSPDCL for domestic category
        "TSNPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units @ Rs.1.45/unit"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units @ Rs.2.60/unit"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units @ Rs.3.60/unit"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units @ Rs.5.50/unit"),
                TariffSlabInfo(301, 400, 7.00, "301-400 units @ Rs.7.00/unit"),
                TariffSlabInfo(401, 500, 8.50, "401-500 units @ Rs.8.50/unit"),
                TariffSlabInfo(501, Int.MAX_VALUE, 9.50, "Above 500 units @ Rs.9.50/unit")
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
            minimumCharges = 30.0,
            stateName = "Telangana"
        ),

        // ---------- BESCOM (Bangalore Electricity Supply Company) ----------
        // Domestic - KERC Tariff Order FY 2024-25
        "BESCOM" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 30, 4.10, "0-30 units @ Rs.4.10/unit"),
                TariffSlabInfo(31, 100, 5.55, "31-100 units @ Rs.5.55/unit"),
                TariffSlabInfo(101, 200, 7.10, "101-200 units @ Rs.7.10/unit"),
                TariffSlabInfo(201, Int.MAX_VALUE, 8.15, "Above 200 units @ Rs.8.15/unit")
            ),
            fixedCharges = listOf(
                0..30 to 35.0,
                31..100 to 55.0,
                101..200 to 75.0,
                201..Int.MAX_VALUE to 100.0
            ),
            fuelSurchargeRate = 0.11,       // 11 paisa/unit
            electricityDutyRate = 0.09,     // 9%
            minimumCharges = 35.0,
            stateName = "Karnataka"
        ),

        // ---------- MESCOM (Mangalore Electricity Supply Company) ----------
        // Same KERC domestic tariff as BESCOM
        "MESCOM" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 30, 4.10, "0-30 units @ Rs.4.10/unit"),
                TariffSlabInfo(31, 100, 5.55, "31-100 units @ Rs.5.55/unit"),
                TariffSlabInfo(101, 200, 7.10, "101-200 units @ Rs.7.10/unit"),
                TariffSlabInfo(201, Int.MAX_VALUE, 8.15, "Above 200 units @ Rs.8.15/unit")
            ),
            fixedCharges = listOf(
                0..30 to 35.0,
                31..100 to 55.0,
                101..200 to 75.0,
                201..Int.MAX_VALUE to 100.0
            ),
            fuelSurchargeRate = 0.11,
            electricityDutyRate = 0.09,
            minimumCharges = 35.0,
            stateName = "Karnataka"
        ),

        // ---------- MSEDCL (Maharashtra State Electricity Distribution) ----------
        // Domestic - MERC Tariff Order FY 2024-25
        "MSEDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 100, 4.71, "0-100 units @ Rs.4.71/unit"),
                TariffSlabInfo(101, 300, 7.88, "101-300 units @ Rs.7.88/unit"),
                TariffSlabInfo(301, 500, 10.29, "301-500 units @ Rs.10.29/unit"),
                TariffSlabInfo(501, Int.MAX_VALUE, 11.72, "Above 500 units @ Rs.11.72/unit")
            ),
            fixedCharges = listOf(
                0..100 to 70.0,
                101..300 to 115.0,
                301..500 to 160.0,
                501..Int.MAX_VALUE to 210.0
            ),
            fuelSurchargeRate = 0.15,       // 15 paisa/unit
            electricityDutyRate = 0.16,     // 16%
            minimumCharges = 70.0,
            stateName = "Maharashtra"
        ),

        // ---------- TANGEDCO (Tamil Nadu Generation and Distribution) ----------
        // Domestic - TNERC Tariff Order FY 2024-25 (bimonthly billing)
        "TANGEDCO" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 100, 0.00, "0-100 units FREE (bimonthly up to 200)"),
                TariffSlabInfo(101, 200, 2.00, "101-200 units @ Rs.2.00/unit"),
                TariffSlabInfo(201, 500, 3.00, "201-500 units @ Rs.3.00/unit"),
                TariffSlabInfo(501, Int.MAX_VALUE, 6.00, "Above 500 units @ Rs.6.00/unit")
            ),
            fixedCharges = listOf(
                0..100 to 0.0,
                101..200 to 30.0,
                201..500 to 50.0,
                501..Int.MAX_VALUE to 80.0
            ),
            fuelSurchargeRate = 0.05,       // 5 paisa/unit
            electricityDutyRate = 0.05,     // 5%
            minimumCharges = 0.0,
            stateName = "Tamil Nadu"
        ),

        // Alias: TNEB -> TANGEDCO rates
        "TNEB" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 100, 0.00, "0-100 units FREE"),
                TariffSlabInfo(101, 200, 2.00, "101-200 units @ Rs.2.00/unit"),
                TariffSlabInfo(201, 500, 3.00, "201-500 units @ Rs.3.00/unit"),
                TariffSlabInfo(501, Int.MAX_VALUE, 6.00, "Above 500 units @ Rs.6.00/unit")
            ),
            fixedCharges = listOf(
                0..100 to 0.0,
                101..200 to 30.0,
                201..500 to 50.0,
                501..Int.MAX_VALUE to 80.0
            ),
            fuelSurchargeRate = 0.05,
            electricityDutyRate = 0.05,
            minimumCharges = 0.0,
            stateName = "Tamil Nadu"
        ),

        // ---------- BSES (BSES Rajdhani / BSES Yamuna, Delhi) ----------
        // Domestic - DERC Tariff Order FY 2024-25
        "BSES" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 200, 3.00, "0-200 units @ Rs.3.00/unit"),
                TariffSlabInfo(201, 400, 4.50, "201-400 units @ Rs.4.50/unit"),
                TariffSlabInfo(401, 800, 6.50, "401-800 units @ Rs.6.50/unit"),
                TariffSlabInfo(801, Int.MAX_VALUE, 7.75, "Above 800 units @ Rs.7.75/unit")
            ),
            fixedCharges = listOf(
                0..200 to 20.0,
                201..400 to 40.0,
                401..800 to 80.0,
                801..Int.MAX_VALUE to 125.0
            ),
            fuelSurchargeRate = 0.08,       // 8 paisa/unit
            electricityDutyRate = 0.05,     // 5%
            minimumCharges = 20.0,
            stateName = "Delhi"
        ),

        // ---------- TATA POWER DELHI (TPDDL) ----------
        // Domestic - DERC Tariff Order FY 2024-25
        "TATA_POWER_DELHI" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 200, 3.00, "0-200 units @ Rs.3.00/unit"),
                TariffSlabInfo(201, 400, 4.50, "201-400 units @ Rs.4.50/unit"),
                TariffSlabInfo(401, 800, 6.50, "401-800 units @ Rs.6.50/unit"),
                TariffSlabInfo(801, Int.MAX_VALUE, 7.75, "Above 800 units @ Rs.7.75/unit")
            ),
            fixedCharges = listOf(
                0..200 to 25.0,
                201..400 to 50.0,
                401..800 to 100.0,
                801..Int.MAX_VALUE to 150.0
            ),
            fuelSurchargeRate = 0.08,
            electricityDutyRate = 0.05,
            minimumCharges = 25.0,
            stateName = "Delhi"
        ),

        // ---------- APSPDCL (Andhra Pradesh Southern Power Distribution) ----------
        // Domestic LT Category I - APERC Tariff Order FY 2024-25
        "APSPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units @ Rs.1.45/unit"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units @ Rs.2.60/unit"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units @ Rs.3.60/unit"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units @ Rs.5.50/unit"),
                TariffSlabInfo(301, Int.MAX_VALUE, 8.50, "Above 300 units @ Rs.8.50/unit")
            ),
            fixedCharges = listOf(
                0..50 to 25.0,
                51..100 to 45.0,
                101..200 to 60.0,
                201..300 to 90.0,
                301..Int.MAX_VALUE to 120.0
            ),
            fuelSurchargeRate = 0.12,       // 12 paisa/unit
            electricityDutyRate = 0.06,     // 6%
            minimumCharges = 25.0,
            stateName = "Andhra Pradesh"
        ),

        // ---------- APEPDCL (Andhra Pradesh Eastern Power Distribution) ----------
        // Same APERC tariff as APSPDCL
        "APEPDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 50, 1.45, "0-50 units @ Rs.1.45/unit"),
                TariffSlabInfo(51, 100, 2.60, "51-100 units @ Rs.2.60/unit"),
                TariffSlabInfo(101, 200, 3.60, "101-200 units @ Rs.3.60/unit"),
                TariffSlabInfo(201, 300, 5.50, "201-300 units @ Rs.5.50/unit"),
                TariffSlabInfo(301, Int.MAX_VALUE, 8.50, "Above 300 units @ Rs.8.50/unit")
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
            minimumCharges = 25.0,
            stateName = "Andhra Pradesh"
        ),

        // ---------- WBSEDCL (West Bengal State Electricity Distribution) ----------
        // Domestic - WBERC Tariff Order FY 2024-25
        "WBSEDCL" to BoardConfig(
            slabs = listOf(
                TariffSlabInfo(0, 25, 5.28, "0-25 units @ Rs.5.28/unit"),
                TariffSlabInfo(26, 60, 5.83, "26-60 units @ Rs.5.83/unit"),
                TariffSlabInfo(61, 100, 6.41, "61-100 units @ Rs.6.41/unit"),
                TariffSlabInfo(101, 150, 7.16, "101-150 units @ Rs.7.16/unit"),
                TariffSlabInfo(151, Int.MAX_VALUE, 8.84, "Above 150 units @ Rs.8.84/unit")
            ),
            fixedCharges = listOf(
                0..25 to 15.0,
                26..60 to 30.0,
                61..100 to 50.0,
                101..150 to 65.0,
                151..Int.MAX_VALUE to 85.0
            ),
            fuelSurchargeRate = 0.10,       // 10 paisa/unit
            electricityDutyRate = 0.06,     // 6%
            minimumCharges = 15.0,
            stateName = "West Bengal"
        )
    )

    // ======================== Public API ========================

    /**
     * Returns the tariff slabs for the given utility board and category.
     * Currently only DOMESTIC category is fully populated; returns empty
     * for unrecognised boards or categories.
     */
    fun getTariffSlabs(utilityBoard: String, category: String = "DOMESTIC"): List<TariffSlabInfo> {
        if (category.uppercase() != "DOMESTIC") return emptyList()
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.slabs ?: emptyList()
    }

    /**
     * Returns the fixed charge applicable for the given total units consumed.
     * Fixed charges are slab-dependent for most boards (e.g. TSSPDCL charges
     * Rs.30 for 0-50 units and Rs.200 for 500+ units).
     */
    fun getFixedCharges(utilityBoard: String, units: Int): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        val config = tariffData[normalizedBoard] ?: return 0.0
        return config.fixedCharges.firstOrNull { units in it.first }?.second ?: 0.0
    }

    /**
     * Returns the fuel surcharge / fuel adjustment charge (FAC) rate in
     * rupees per unit (e.g. 0.10 means 10 paisa per unit).
     */
    fun getFuelSurchargeRate(utilityBoard: String): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.fuelSurchargeRate ?: 0.10
    }

    /**
     * Returns the electricity duty rate as a decimal fraction
     * (e.g. 0.06 means 6% of energy charges).
     */
    fun getElectricityDutyRate(utilityBoard: String): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.electricityDutyRate ?: 0.06
    }

    /**
     * Returns the minimum monthly charges for the given utility board.
     * If the calculated energy + fixed charge is below this amount, the
     * minimum charge applies instead.
     */
    fun getMinimumCharges(utilityBoard: String): Double {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.minimumCharges ?: 0.0
    }

    /**
     * Returns whether the given board uses telescopic (incremental slab)
     * billing. All major Indian domestic boards currently use telescopic billing.
     */
    fun isTelescopicBilling(utilityBoard: String): Boolean {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.isTelescopicBilling ?: true
    }

    /**
     * Returns the state name associated with the given utility board.
     */
    fun getStateName(utilityBoard: String): String? {
        val normalizedBoard = normalizeBoard(utilityBoard)
        return tariffData[normalizedBoard]?.stateName
    }

    /**
     * Returns the list of all supported electricity board codes.
     */
    fun getSupportedBoards(): List<String> = tariffData.keys.toList()

    // ======================== Board name normalisation ========================

    /**
     * Normalises various representations of board names (abbreviations,
     * full names, OCR variations) to the canonical key used in [tariffData].
     */
    fun normalizeBoard(board: String): String {
        val upper = board.uppercase().trim()
        return when {
            upper.contains("TSSPDCL") -> "TSSPDCL"
            upper.contains("TSNPDCL") -> "TSNPDCL"
            upper.contains("APSPDCL") -> "APSPDCL"
            upper.contains("APEPDCL") || upper.contains("APEPCDL") -> "APEPDCL"
            upper.contains("BESCOM") -> "BESCOM"
            upper.contains("MESCOM") -> "MESCOM"
            upper.contains("MSEDCL") || upper.contains("MAHAVITARAN") -> "MSEDCL"
            upper.contains("TANGEDCO") -> "TANGEDCO"
            upper.contains("TNEB") -> "TNEB"
            upper.contains("BSES") -> "BSES"
            upper.contains("TATA") && upper.contains("DELHI") -> "TATA_POWER_DELHI"
            upper.contains("TPDDL") -> "TATA_POWER_DELHI"
            upper.contains("WBSEDCL") -> "WBSEDCL"
            tariffData.containsKey(upper) -> upper
            else -> tariffData.keys.firstOrNull {
                it.contains(upper) || upper.contains(it)
            } ?: upper
        }
    }
}
