package com.rbk.stockmarketapp.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbk.stockmarketapp.domain.repository.StockRepository
import com.rbk.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository
) :ViewModel(){

    var state by mutableStateOf(CompanyListingState())
    private var searchJob:Job?=null

    fun onEvent(event :CompanyListingsEvents){
        when(event){

            is CompanyListingsEvents.Refresh->{

                getCompanyListings(fetchRemote = true)



            }
            is CompanyListingsEvents.OnSearchQueryChange ->{

                state=state.copy(searcQuery = event.query)
                searchJob?.cancel()
                searchJob=viewModelScope.launch {
                    delay(500L)
                    getCompanyListings()
                }

            }
        }
    }
   private fun getCompanyListings(
        query:String=state.searcQuery.toLowerCase(),
        fetchRemote:Boolean=false
    ){

        viewModelScope.launch {
            repository
                .getStockListings(fetchRemote,query)
                .collect{
                    result->
                    when(result){
                        is Resource.Success ->{

                            result.data?.let {
                                listings->{
                                    state=state.copy(
                                        companies = listings
                                    )
                            }
                            }

                        }
                        is Resource.Error ->Unit
                        is Resource.Loading->{
                            state=state.copy(isLoading = result.isLoading)
                        }
                    }
                }

        }
    }
}