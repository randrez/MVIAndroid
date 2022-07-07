package com.scgts.sctrace.base.model

enum class SortCategory(val uiName: String, val serverName: String) {
    Oldest("Oldest (default)", "createdAt ASC"),
    Newest("Newest", "createdAt DESC"),
    DeliveryDate("Delivery date", "deliveryDate IS NULL, deliveryDate ASC"),
    Status("Status", "status"),
    Task("Task", "orderType, type");

    companion object {
        fun defaultCategory(): SortCategory = Oldest
        fun toList(): List<SortCategory> = listOf(Oldest, Newest, DeliveryDate, Status, Task)
    }
}