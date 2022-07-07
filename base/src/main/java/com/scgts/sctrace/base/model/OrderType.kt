package com.scgts.sctrace.base.model

import java.util.*

enum class OrderType(val uuid: UUID, val serverName: String, val displayName: String) {
    INBOUND(
        uuid = UUID.fromString("0b06fe35-fcb9-4bb9-a27a-f4530d132786"),
        serverName = "Inbound",
        displayName = "Inbound"
    ),
    OUTBOUND(
        uuid = UUID.fromString("1955fc23-d00e-4ab0-b1ab-ebc7eb36797b"),
        serverName = "Outbound",
        displayName = "Outbound"
    ),
    CONSUMPTION(
        uuid = UUID.fromString("fd77e26a-1f56-48b1-b068-537469e63b9f"),
        serverName = "Consumption",
        displayName = "Consumption"
    ),
    RETURN_TRANSFER(
        uuid = UUID.fromString("2befa571-a4ae-4fec-bf35-86844ea49b04"),
        serverName = "Returns & transfers",
        displayName = "Returns & transfers"
    ),
    NO_TYPE(
        uuid = UUID.fromString("a078c125-4143-4d5c-9ded-57644325e0f6"),
        serverName = "¯\\_(ツ)_/¯",
        displayName = "¯\\_(ツ)_/¯"
    );

    companion object {
        private val map = values().associateBy(OrderType::serverName)
        operator fun get(serverName: String) = map[serverName] ?: NO_TYPE
    }
}