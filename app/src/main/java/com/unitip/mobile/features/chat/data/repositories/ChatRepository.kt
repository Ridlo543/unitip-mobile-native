package com.unitip.mobile.features.chat.data.repositories

import arrow.core.Either
import com.unitip.mobile.features.chat.data.dtos.CreateRoomPayload
import com.unitip.mobile.features.chat.data.dtos.SendMessagePayload
import com.unitip.mobile.features.chat.data.dtos.SendMessageResponse
import com.unitip.mobile.features.chat.data.dtos.UpdateReadCheckpointPayload
import com.unitip.mobile.features.chat.data.sources.ChatApi
import com.unitip.mobile.features.chat.domain.models.GetAllMessagesResult
import com.unitip.mobile.features.chat.domain.models.Message
import com.unitip.mobile.features.chat.domain.models.OtherUser
import com.unitip.mobile.features.chat.domain.models.Room
import com.unitip.mobile.features.chat.domain.models.UpdateReadCheckpointResult
import com.unitip.mobile.shared.commons.extensions.mapToFailure
import com.unitip.mobile.shared.data.managers.SessionManager
import com.unitip.mobile.shared.domain.models.Failure
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatApi: ChatApi,
    private val sessionManager: SessionManager
) {
    suspend fun getAllRooms(): Either<Failure, List<Room>> {
        try {
            val token = sessionManager.read()?.token
            val response = chatApi.getAllRooms(token = "Bearer $token")
            val result = response.body()

            return when (response.isSuccessful && result != null) {
                true -> Either.Right(result.rooms.map {
                    Room(
                        id = it.id,
                        lastMessage = it.lastMessage,
                        lastSentUserId = it.lastSentUserId,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                        unreadMessageCount = it.unreadMessageCount,
                        otherUser = OtherUser(
                            id = it.otherUser.id,
                            name = it.otherUser.name
                        )
                    )
                })

                false -> Either.Left(response.mapToFailure())
            }
        } catch (e: Exception) {
            return Either.Left(Failure(message = "Terjadi kesalahan tak terduga!"))
        }
    }

    suspend fun sendMessage(
        roomId: String,
        id: String,
        message: String,
        otherId: String,
        otherUnreadMessageCount: Int
    ): Either<Failure, SendMessageResponse> = try {
        val token = sessionManager.read()?.token
        val response = chatApi.sendMessage(
            token = "Bearer $token",
            roomId = roomId,
            payload = SendMessagePayload(
                id = id,
                message = message,
                otherId = otherId,
                otherUnreadMessageCount = otherUnreadMessageCount
            )
        )
        val result = response.body()

        when (response.isSuccessful && result != null) {
            true -> Either.Right(
                SendMessageResponse(
                    id = result.id,
                    message = result.message,
                    createdAt = result.createdAt,
                    updatedAt = result.updatedAt
                )
            )

            false -> Either.Left(response.mapToFailure())
        }
    } catch (e: Exception) {
        Either.Left(Failure(message = "Terjadi kesalahan tak terduga!"))
    }

    suspend fun getAllMessages(
        roomId: String
    ): Either<Failure, GetAllMessagesResult> = try {
        val token = sessionManager.read()?.token
        val response = chatApi.getAllMessages(
            token = "Bearer $token",
            roomId = roomId
        )
        val result = response.body()

        when (response.isSuccessful && result != null) {
            true -> Either.Right(GetAllMessagesResult(
                otherUser = OtherUser(
                    id = result.otherUser.id,
                    lastReadMessageId = result.otherUser.lastReadMessageId
                ),
                messages = result.messages.map {
                    Message(
                        id = it.id,
                        message = it.message,
                        isDeleted = it.isDeleted,
                        roomId = it.roomId,
                        userId = it.userId,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
            ))

            false -> Either.Left(response.mapToFailure())
        }
    } catch (e: Exception) {
        Either.Left(Failure(message = "Terjadi kesalahan tak terduga!"))
    }

    suspend fun updateReadCheckpoint(
        roomId: String,
        lastReadMessageId: String
    ): Either<Failure, UpdateReadCheckpointResult> = try {
        val token = sessionManager.read()?.token
        val response = chatApi.updateReadCheckpoint(
            token = "Bearer $token",
            roomId = roomId,
            payload = UpdateReadCheckpointPayload(
                lastReadMessageId = lastReadMessageId
            )
        )
        val result = response.body()

        when (response.isSuccessful && result != null) {
            true -> Either.Right(
                UpdateReadCheckpointResult(
                    id = result.id,
                    roomId = result.roomId,
                    userId = result.userId,
                    lastReadMessageId = result.lastReadMessageId
                )
            )

            else -> Either.Left(response.mapToFailure())
        }
    } catch (e: Exception) {
        Either.Left(Failure(message = "Terjadi kesalahan tak terduga!"))
    }

    suspend fun createRoom(members: List<String>): Either<Failure, String> {
        return try {
            val session = sessionManager.read()
            val response = chatApi.createRoom(
                token = "Bearer ${session.token}",
                payload = CreateRoomPayload(members)
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Either.Right(body.id)
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

    suspend fun checkRoom(members: List<String>): Either<Failure, String?> {
        return try {
            val session = sessionManager.read()
            val response = chatApi.checkRoom(
                token = "Bearer ${session.token}",
                members = members.joinToString(",")
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        Either.Right(body.roomId)
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