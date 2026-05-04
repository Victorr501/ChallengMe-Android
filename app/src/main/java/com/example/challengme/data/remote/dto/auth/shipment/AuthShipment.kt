package com.example.challengme.data.remote.dto.auth.shipment

import com.google.gson.annotations.SerializedName

data class AuthShipment(
    @SerializedName("token") val token: String
)