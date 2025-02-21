package com.unitip.mobile.features.offer.commons

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.unitip.mobile.features.offer.presentation.screens.ApplyOfferScreen
import com.unitip.mobile.features.offer.presentation.screens.CreateOfferScreen
import com.unitip.mobile.features.offer.presentation.screens.DetailApplicantOfferScreen
import com.unitip.mobile.features.offer.presentation.screens.DetailOfferScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.offerNavigation() {
    composable<OfferRoutes.Create> { CreateOfferScreen() }

    composable<OfferRoutes.DetailOfferCustomer> {
        val data = it.toRoute<OfferRoutes.DetailOfferCustomer>()
        DetailOfferScreen(offerId = data.offerId)
    }

    composable<OfferRoutes.ApplyOffer> {
        val data = it.toRoute<OfferRoutes.ApplyOffer>()
        ApplyOfferScreen(offerId = data.offerId, offerType = data.offerType, offerPickupLocation = data.offerPickupLocation)
    }

    composable<OfferRoutes.DetailApplicant> {
        val data = it.toRoute<OfferRoutes.DetailApplicant>()
        DetailApplicantOfferScreen(
            offerId = data.offerId,
            applicantId = data.applicantId
        )
    }
}