package com.univ.doraboda.settings.repository

import com.univ.doraboda.settings.util.DataStoreUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepository @Inject constructor(private val dataStoreUtil: DataStoreUtil) {
    fun getLabelSetting(): Flow<Int> {
        return dataStoreUtil.getLabelSetting()
    }

    fun setLabelSetting(label: Int){
        dataStoreUtil.setLabelSetting(label)
    }

    fun getQuoteSetting(): Flow<Boolean>{
        return dataStoreUtil.getQuoteSetting()
    }

    suspend fun setQuoteSetting(label: Boolean){
        dataStoreUtil.setQuoteSetting(label)
    }
}