package com.unitip.mobile.features.offer.data.repositories

import arrow.core.Either
import com.unitip.mobile.features.offer.data.dtos.ApplyOfferPayload
import com.unitip.mobile.features.offer.data.dtos.CreateOfferPayload
import com.unitip.mobile.features.offer.data.dtos.GetOfferResponse
import com.unitip.mobile.features.offer.data.models.ApplyOfferResult
import com.unitip.mobile.features.offer.data.models.CreateOfferResult
import com.unitip.mobile.features.offer.data.sources.OfferApi
import com.unitip.mobile.features.offer.domain.models.GetAllOffersResult
import com.unitip.mobile.features.offer.domain.models.Offer
import com.unitip.mobile.features.offer.domain.models.OfferFreelancer
import com.unitip.mobile.shared.commons.extensions.mapToFailure
import com.unitip.mobile.shared.data.managers.SessionManager
import com.unitip.mobile.shared.domain.models.Failure
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfferRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val offerApi: OfferApi
) {
    suspend fun create(
        title: String,
        description: String,
        price: Number,
        type: String,
        pickupArea: String,
        deliveryArea: String,
        availableUntil: String
    ): Either<Failure, CreateOfferResult> {
        try {
            val token = sessionManager.read().token
            val response = offerApi.create(
                token = "Bearer $token",
                payload = CreateOfferPayload(
                    title = title,
                    description = description,
                    price = price,
                    type = type,
                    pickupArea = pickupArea,
                    deliveryArea = deliveryArea,
                    availableUntil = availableUntil,
                )
            )

            val result = response.body()
            // mereturn jika sukses dan jika tidak
            return when (response.isSuccessful && result != null) {
                true -> Either.Right(CreateOfferResult(id = result.id))
                false -> Either.Left(response.mapToFailure())
            }
        } catch (e: Exception) {
            return Either.Left(Failure(message = "Terjadi kesalahan tak terduga!"))
        }
    }

    suspend fun getOffers(
        page: Int = 1,
        type: String? = null
    ): Either<Failure, GetAllOffersResult> {
        return try {
            val session = sessionManager.read()
            val response = offerApi.getAllOffers(
                token = "Bearer ${session.token}",
                page = page,
                type = type
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Either.Right(
                            GetAllOffersResult(
                                offers = body.offers.map { apiOffer ->
                                    Offer(
                                        id = apiOffer.id,
                                        title = apiOffer.title,
                                        description = apiOffer.description,
                                        price = apiOffer.price,
                                        type = apiOffer.type,
                                        deliveryArea = apiOffer.destinationArea,
                                        pickupArea = apiOffer.pickupArea,
                                        availableUntil = apiOffer.availableUntil,
                                        offerStatus = apiOffer.offerStatus,
                                        freelancer = OfferFreelancer(name = apiOffer.freelancer.name),
                                        maxParticipants = apiOffer.maxParticipants,
                                    )
                                },
                                hasNext = body.pageInfo.page < body.pageInfo.totalPages
                            )
                        )
                    } else {
                        Either.Left(Failure("Response body is null"))
                    }
                }

                else -> Either.Left(response.mapToFailure())
            }
        } catch (e: Exception) {
            Either.Left(Failure("Terjadi kesalahan: ${e.message}"))
        }
    }

    suspend fun getOfferDetail(offerId: String): Either<Failure, Offer> {
        return try {
            val session = sessionManager.read()
            val response = offerApi.getOfferDetail(
                token = "Bearer ${session.token}",
                offerId = offerId
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Either.Right(body.offer.toDomainModel())
                    } else {
                        Either.Left(Failure("Response body is null"))
                    }
                }

                else -> Either.Left(response.mapToFailure())
            }
        } catch (e: Exception) {
            Either.Left(Failure("Terjadi kesalahan: ${e.message}"))
        }
    }

    private fun GetOfferResponse.ApiOffer.toDomainModel(): Offer {
        return Offer(
            id = this.id,
            title = this.title,
            description = this.description,
            price = this.price,
            type = this.type,
            pickupArea = this.pickupArea,
            deliveryArea = this.destinationArea,
            availableUntil = this.availableUntil,
            offerStatus = this.offerStatus,
            freelancer = OfferFreelancer(name = this.freelancer.name),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            applicantsCount = this.applicantsCount,
            hasApplied = this.hasApplied,
            maxParticipants = this.maxParticipants
        )
    }

    suspend fun applyOffer(
        offerId: String,
        note: String,
        destinationLocation: String,
        pickupLocation: String,
        pickupLatitude: Double,
        pickupLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double
    ): Either<Failure, ApplyOfferResult> {
        return try {
            val session = sessionManager.read()
            val response = offerApi.applyOffer(
                token = "Bearer ${session.token}",
                offerId = offerId,
                payload = ApplyOfferPayload(
                    note = note,
                    destinationLocation = destinationLocation,
                    pickupLocation = pickupLocation,
                    pickupLatitude = pickupLatitude,
                    pickupLongitude = pickupLongitude,
                    destinationLatitude = destinationLatitude,
                    destinationLongitude = destinationLongitude
                )
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Either.Right(ApplyOfferResult(id = body.id))
                    } else {
                        Either.Left(Failure("Response body is null"))
                    }
                }

                else -> Either.Left(response.mapToFailure())
            }
        } catch (e: Exception) {
            Either.Left(Failure("Terjadi kesalahan: ${e.message}"))
        }
    }
}


//
//    suspend fun getAll(): Either<Failure, GetAllOffersResult> {
//        try {
//            val token = sessionManager.read()?.token
//            val response = offerApi.getAll(
//                token = "Bearer $token",
//                type = GetAllOfferResponse.PageInfo.OfferType.SINGLE.value
//            )
//            val result = response.body()
//
//            return when (response.isSuccessful && result != null) {
//                true -> Either.Right(
//                    GetAllOffersResult(
//                        offers = result.offers.map {
//                            Offer(
//                                id = it.id,
//                                title = it.title,
//                                description = it.description,
//                                price = it.price,
//                                type = it.type,
//                                pickupArea = it.pickupArea,
//                                deliveryArea = it.deliveryArea,
//                                availableUntil = it.availableUntil,
//                                offerStatus = it.offerStatus,
//                                freelancer = OfferFreelancer(
//                                    name = it.freelancer.name
//                                )
//                            )
//                        },
//                        hasNext = result.pageInfo.page < result.pageInfo.totalPages
//                    )
//                )
//
//                false -> Either.Left(response.mapToFailure())
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return Either.Left(Failure(message = "Terjadi kesalahan tak terduga !"))
//        }
//    }