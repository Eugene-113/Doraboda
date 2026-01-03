package com.univ.doraboda.repository

import com.univ.doraboda.util.DataStoreUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    fun setQuoteSetting(label: Boolean){
        dataStoreUtil.setQuoteSetting(label)
    }
}