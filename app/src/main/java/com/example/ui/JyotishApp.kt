package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.AppLanguage
import com.example.model.t
import com.example.ui.components.GrahaDetailBottomSheet
import com.example.ui.components.JyotishTopBar
import com.example.ui.components.PrecisionInfoDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

object JyotishRoutes {
    const val HOME = "home"
    const val KUNDALI = "kundali"
    const val RASHIFAL = "rashifal"
    const val PANCHANG = "panchang"
    const val MATCHING = "matching"
    const val OWNER_PROFILE = "owner_profile"
    const val PREMIUM = "premium"
    const val WHATSAPP_SUPPORT = "whatsapp_support"
    const val CHAT_SUPPORT = "chat_support"
    const val ADMIN_SETTINGS = "admin_settings"
}

private data class NavTab(
    val route: String,
    val labelHi: String,
    val labelEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navTabs = listOf(
    NavTab(JyotishRoutes.HOME, "होम", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    NavTab(JyotishRoutes.KUNDALI, "कुंडली", "Kundali", Icons.Filled.Hexagon, Icons.Outlined.Hexagon),
    NavTab(JyotishRoutes.RASHIFAL, "राशिफल", "Rashifal", Icons.Filled.Stars, Icons.Outlined.Stars),
    NavTab(JyotishRoutes.PANCHANG, "पंचांग", "Panchang", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    NavTab(JyotishRoutes.MATCHING, "मिलान", "Match", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
)

@Composable
fun JyotishApp(viewModel: JyotishViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lang = uiState.language
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: JyotishRoutes.HOME

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToSubPage(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    JyotishKundaliTheme(isHindi = lang == AppLanguage.HI) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianMaroon)
        ) {
            val useRail = maxWidth >= 640.dp

            Scaffold(
                containerColor = ObsidianMaroon,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    JyotishTopBar(
                        language = lang,
                        subscriptionStatus = uiState.membership.status,
                        onToggleLanguage = viewModel::toggleLanguage,
                        onOpenPrecisionInfo = { viewModel.setPrecisionDialogVisible(true) },
                        onOpenOwnerProfile = { navigateToSubPage(JyotishRoutes.OWNER_PROFILE) },
                        onOpenPremium = { navigateToSubPage(JyotishRoutes.PREMIUM) },
                        onOpenWhatsAppSupport = { navigateToSubPage(JyotishRoutes.WHATSAPP_SUPPORT) },
                        onOpenChatSupport = { navigateToSubPage(JyotishRoutes.CHAT_SUPPORT) },
                        onOpenAdminSettings = { navigateToSubPage(JyotishRoutes.ADMIN_SETTINGS) }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { navigateToSubPage(JyotishRoutes.WHATSAPP_SUPPORT) },
                        containerColor = ShubhEmeraldBg,
                        contentColor = ShubhEmerald,
                        shape = RoundedCornerShape(28.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = lang.t("व्हाट्सएप सहायता", "WhatsApp Support")
                            )
                        },
                        text = {
                            Text(
                                text = lang.t("व्हाट्सएप", "WhatsApp"),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.testTag("floating_whatsapp_fab")
                    )
                },
                bottomBar = {
                    if (!useRail) {
                        NavigationBar(
                            containerColor = DeepMaroonSurface,
                            tonalElevation = 0.dp
                        ) {
                            navTabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = if (lang == AppLanguage.HI) tab.labelHi else tab.labelEn
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (lang == AppLanguage.HI) tab.labelHi else tab.labelEn,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldBright,
                                        selectedTextColor = GoldBright,
                                        indicatorColor = RoyalCrimson,
                                        unselectedIconColor = MutedGoldText,
                                        unselectedTextColor = MutedGoldText
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.route}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        NavigationRail(
                            containerColor = DeepMaroonSurface,
                            contentColor = IvoryCream
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            navTabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = if (lang == AppLanguage.HI) tab.labelHi else tab.labelEn
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (lang == AppLanguage.HI) tab.labelHi else tab.labelEn,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = GoldBright,
                                        selectedTextColor = GoldBright,
                                        indicatorColor = RoyalCrimson,
                                        unselectedIconColor = MutedGoldText,
                                        unselectedTextColor = MutedGoldText
                                    ),
                                    modifier = Modifier.testTag("rail_tab_${tab.route}")
                                )
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = JyotishRoutes.HOME,
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 1180.dp)
                        ) {
                            composable(JyotishRoutes.HOME) {
                                HomeScreen(
                                    uiState = uiState,
                                    onNavigateToKundali = { navigateToTab(JyotishRoutes.KUNDALI) },
                                    onNavigateToRashifal = { rashi ->
                                        viewModel.selectRashi(rashi)
                                        navigateToTab(JyotishRoutes.RASHIFAL)
                                    },
                                    onNavigateToPanchang = { navigateToTab(JyotishRoutes.PANCHANG) },
                                    onNavigateToMatching = { navigateToTab(JyotishRoutes.MATCHING) },
                                    onNavigateToOwnerProfile = { navigateToSubPage(JyotishRoutes.OWNER_PROFILE) },
                                    onNavigateToPremium = { navigateToSubPage(JyotishRoutes.PREMIUM) },
                                    onNavigateToWhatsAppSupport = { navigateToSubPage(JyotishRoutes.WHATSAPP_SUPPORT) },
                                    onNavigateToChatSupport = { navigateToSubPage(JyotishRoutes.CHAT_SUPPORT) },
                                    onNavigateToAdminSettings = { navigateToSubPage(JyotishRoutes.ADMIN_SETTINGS) },
                                    onInspectGraha = { g -> viewModel.inspectGraha(g) }
                                )
                            }
                            composable(JyotishRoutes.KUNDALI) {
                                KundaliScreen(
                                    uiState = uiState,
                                    onUpdateInput = viewModel::updateKundaliInput,
                                    onSearchPlaces = viewModel::searchPlaces,
                                    onGenerate = viewModel::generateKundali,
                                    onReset = viewModel::resetKundali,
                                    onSetChartStyle = viewModel::setChartStyle,
                                    onSetChartDivision = viewModel::setChartDivision,
                                    onInspectGraha = { g, pos -> viewModel.inspectGraha(g, pos) },
                                    onNavigateToPremium = { navigateToSubPage(JyotishRoutes.PREMIUM) }
                                )
                            }
                            composable(JyotishRoutes.RASHIFAL) {
                                RashifalScreen(
                                    uiState = uiState,
                                    onSelectRashi = viewModel::selectRashi
                                )
                            }
                            composable(JyotishRoutes.PANCHANG) {
                                PanchangScreen(
                                    uiState = uiState,
                                    onShiftDay = viewModel::shiftPanchangDay,
                                    onPickDate = viewModel::updatePanchangDate,
                                    onSelectPlace = viewModel::updatePanchangPlace,
                                    onSearchPlaces = viewModel::searchPlaces,
                                    onInspectGraha = { g -> viewModel.inspectGraha(g) }
                                )
                            }
                            composable(JyotishRoutes.MATCHING) {
                                MatchingScreen(
                                    uiState = uiState,
                                    onUpdateGroom = viewModel::updateGroomInput,
                                    onUpdateBride = viewModel::updateBrideInput,
                                    onSearchPlaces = viewModel::searchPlaces,
                                    onMatch = viewModel::calculateMatch,
                                    onReset = viewModel::resetMatching
                                )
                            }
                            composable(JyotishRoutes.OWNER_PROFILE) {
                                OwnerProfileScreen(
                                    uiState = uiState,
                                    onUpdatePhotoUri = viewModel::updateOwnerPhotoUri,
                                    onOpenAdminSettings = { navigateToSubPage(JyotishRoutes.ADMIN_SETTINGS) },
                                    onOpenWhatsAppSupport = { navigateToSubPage(JyotishRoutes.WHATSAPP_SUPPORT) },
                                    onOpenChatSupport = { navigateToSubPage(JyotishRoutes.CHAT_SUPPORT) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(JyotishRoutes.PREMIUM) {
                                PremiumScreen(
                                    uiState = uiState,
                                    onSelectBillingCycle = viewModel::selectBillingCycle,
                                    onAttemptCheckout = viewModel::attemptPremiumCheckout,
                                    onSetSubscriptionStatus = viewModel::setSubscriptionStatus,
                                    onOpenAdminSettings = { navigateToSubPage(JyotishRoutes.ADMIN_SETTINGS) },
                                    onOpenWhatsAppSupport = { navigateToSubPage(JyotishRoutes.WHATSAPP_SUPPORT) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(JyotishRoutes.WHATSAPP_SUPPORT) {
                                WhatsAppSupportScreen(
                                    uiState = uiState,
                                    onOpenAdminSettings = { navigateToSubPage(JyotishRoutes.ADMIN_SETTINGS) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(JyotishRoutes.CHAT_SUPPORT) {
                                ChatSupportScreen(
                                    uiState = uiState,
                                    onSubmitQuestion = viewModel::submitDemoChatQuestion,
                                    onClearChat = viewModel::clearDemoChat,
                                    onOpenWhatsAppSupport = { navigateToSubPage(JyotishRoutes.WHATSAPP_SUPPORT) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(JyotishRoutes.ADMIN_SETTINGS) {
                                AdminSettingsScreen(
                                    uiState = uiState,
                                    onSaveSettings = viewModel::saveAdminSettings,
                                    onResetDefaults = viewModel::resetAdminDefaults,
                                    onClearNotice = viewModel::clearAdminNotice,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }

        val inspected = uiState.inspectedGraha
        if (inspected != null) {
            GrahaDetailBottomSheet(
                graha = inspected,
                position = uiState.inspectedGrahaPosition,
                language = lang,
                onDismiss = viewModel::dismissGrahaInspector
            )
        }

        if (uiState.showPrecisionDialog) {
            PrecisionInfoDialog(
                language = lang,
                onDismiss = { viewModel.setPrecisionDialogVisible(false) }
            )
        }
    }
}
