package com.unitip.mobile.shared.commons

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unitip.mobile.features.auth.commons.AuthRoutes
import com.unitip.mobile.features.auth.commons.authNavigation
import com.unitip.mobile.features.chat.commons.chatNavigation
import com.unitip.mobile.features.example.utils.exampleNavigation
import com.unitip.mobile.features.home.commons.HomeRoutes
import com.unitip.mobile.features.home.commons.homeNavigation
import com.unitip.mobile.features.job.commons.jobNavigation
import com.unitip.mobile.features.location.commons.locationNavigation
import com.unitip.mobile.features.offer.commons.offerNavigation
import com.unitip.mobile.features.setting.commons.settingNavigation
import com.unitip.mobile.features.test.TestScreen

@Composable
fun ApplicationNavigationGraph(
    navController: NavHostController,
    isAuthenticated: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = when (isAuthenticated) {
            true -> HomeRoutes.Index
//            true -> LocationRoutes.PickLocation
//            true -> JobRoutes.Create
//            true -> ChatRoutes.Conversation(
//                roomId = "b48fc434-6b59-4dfb-9b22-8ba4d27068ac",
//                otherUserId = "rec_cscfpbjss7tdpjmkt6m0",
//                otherUserName = "Rizal Dwi Anggoro"
//            )

            false -> AuthRoutes.Index
        },
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    ) {
        authNavigation()
        homeNavigation()
        jobNavigation()
        offerNavigation()
        chatNavigation()
        exampleNavigation()
        locationNavigation()
        settingNavigation()

        // test
        composable<Routes.Test> {
            TestScreen()
        }
    }
}