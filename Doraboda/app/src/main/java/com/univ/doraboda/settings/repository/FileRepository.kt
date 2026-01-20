package com.univ.doraboda.settings.repository

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class FileRepository @Inject constructor(@ApplicationContext private val context: Context){
    fun jsonFileSave(uri: Uri, json: String){
        val os = context.contentResolver.openOutputStream(uri)?.bufferedWriter()
        os?.write(json)
        os?.close()
    }

    fun jsonFileRead(uri: Uri): String{
        val ins = context.contentResolver.openInputStream(uri)
        val br = BufferedReader(InputStreamReader(ins))
        val builder = StringBuilder()
        var line: String?
        while((br.readLine().also { line = it }) != null){
            builder.append(line)
        }
        ins?.close()
        return builder.toString()
    }
}