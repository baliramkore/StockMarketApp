package com.rbk.stockmarketapp.domain.model

data class CompanyListing(

    val name:String,
    val symbol:String,
    val exchange:String,
    val assetsType:String
)
