package com.rbk.stockmarketapp.data.repository

import androidx.room.Dao
import coil.network.HttpException
import com.opencsv.CSVReader
import com.rbk.stockmarketapp.data.csv.CSVParser
import com.rbk.stockmarketapp.data.csv.CompanyListingParser
import com.rbk.stockmarketapp.data.local.StockDatabase
import com.rbk.stockmarketapp.data.local.StockDoa
import com.rbk.stockmarketapp.data.mapper.toCompanyListing
import com.rbk.stockmarketapp.data.mapper.toCompanyListingEntity
import com.rbk.stockmarketapp.data.remote.StockAPI
import com.rbk.stockmarketapp.domain.model.CompanyListing
import com.rbk.stockmarketapp.domain.repository.StockRepository
import com.rbk.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockAPI,
    val db: StockDatabase,
    val companyListingParser: CSVParser<CompanyListing>
):StockRepository{
    private val dao: StockDoa =db.dao
    override suspend fun getStockListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {

        return flow {
            emit(Resource.Loading(true))
            val localListing=dao.searchCompanyListing(query)
            emit(Resource.Success(
                data=localListing.map { it.toCompanyListing() }
            ))

            val isDbEmpty=localListing.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache=!isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListing=try {

                val response=api.getListings()

                //val csvReader=CSVReader(InputStreamReader(response.byteStream()))
                /*instead of doing this stuff we are going to fallow SOLID*/
                companyListingParser.parse(response.byteStream())

            }catch (e:IOException){

                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null

            }catch (e:HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))

                null
            }

            remoteListing?.let {
                listings->
                dao.clearCompanyListing()

                dao.insertCompanyListing(
                    listings.map { it.toCompanyListingEntity() }
                )

                emit(Resource.Success(

                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }

                ))

                emit(Resource.Loading(false))
                emit(Resource.Success(listings))
            }
        }
    }


}