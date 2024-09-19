package com.rbk.stockmarketapp.presentation.company_listings

import com.rbk.stockmarketapp.domain.model.CompanyListing

data class CompanyListingState(
    val companies:List<CompanyListing> = emptyList(),
    val isLoading: Boolean=false,
    val isRefreshing:Boolean=false,
    val searcQuery:String=""
)
