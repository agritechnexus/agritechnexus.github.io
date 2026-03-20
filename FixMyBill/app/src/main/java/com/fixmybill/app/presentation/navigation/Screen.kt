package com.fixmybill.app.presentation.navigation

sealed class Screen(val route: String) {

    data object Onboarding : Screen("onboarding")

    data object Auth : Screen("auth")

    data object Home : Screen("home")

    data object Scan : Screen("scan")

    data object Analysis : Screen("analysis/{billId}") {
        fun createRoute(billId: Long): String = "analysis/$billId"
    }

    data object History : Screen("history")

    data object Complaint : Screen("complaint/{billId}") {
        fun createRoute(billId: Long): String = "complaint/$billId"
    }

    data object Settings : Screen("settings")

    data object Premium : Screen("premium")
}
