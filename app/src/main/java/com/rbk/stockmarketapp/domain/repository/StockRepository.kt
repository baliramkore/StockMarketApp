package com.rbk.stockmarketapp.domain.repository

import com.rbk.stockmarketapp.domain.model.CompanyListing
import com.rbk.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getStockListings(
        fetchRemote:Boolean,
        query:String
    ): Flow<Resource<List<CompanyListing>>>

}