package com.rbk.stockmarketapp.data.mapper

import com.rbk.stockmarketapp.data.local.CompanyListingEntity
import com.rbk.stockmarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing() : CompanyListing{

    return CompanyListing(
        name=name,
        symbol=symbol,
        exchange=exchange,
        assetsType=assetsType
    )
}

fun CompanyListing.toCompanyListingEntity() : CompanyListingEntity{

    return CompanyListingEntity(
        name=name,
        symbol=symbol,
        exchange=exchange,
        assetsType=assetsType
    )
}