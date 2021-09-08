package com.hyphenate.easetest

import android.content.Context
import com.hyphenate.util.EMLog
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object InputFileUtil {

    fun writeMsgCsvTitle(context: Context){
        try {
            val file = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + "msg_info.csv")
            if(file.exists()){
                file.delete()
            }
            EMLog.e("CsvFile", "msg_info.csv create")
            val bufferWriter = BufferedWriter(FileWriter(file, true))
            val inputString = "%s,%s,%s,%s,%s,%s"
            bufferWriter.write(
                String.format(
                    inputString,
                    "From",
                    "Message send timestamp",
                    "Message receive timestamp",
                    "Message time consuming",
                    "Message's id",
                    "Message's content"
                )
            )
            bufferWriter.newLine()
            bufferWriter.close()
        }catch (e: IOException){
            e.printStackTrace()
            EMLog.e("CsvFile", "msg_info.csv create:" + e.message)
        }
    }

    fun writeMsg2CsvFile(context: Context, bean:TestMessageBean){
        try {
            EMLog.e("CsvFile", "writeMsg2CsvFile:" + bean.mid)
            val file = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + "msg_info.csv")
            val bufferWriter = BufferedWriter(FileWriter(file, true))
            val inputString = "\t%s\t,\t%s\t,\t%s\t,\t%s\t,\t%s\t,\t%s\t"
            bufferWriter.write(
                String.format(
                    inputString,
                    bean.from,
                    bean.sendTime,
                    bean.receiveTime,
                    bean.elapsedTime,
                    bean.mid,
                    bean.msgData
                )
            )
            bufferWriter.newLine()
            bufferWriter.close()
        }catch (e: IOException){
            e.printStackTrace()
            EMLog.e("CsvFile", bean.mid + ":" + e.message)
        }
    }

    fun writeLoginCsvTitle(context: Context){
        try {
            val file = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + "login_info.csv")
            if(file.exists()){
                file.delete()
            }
            EMLog.e("CsvFile", "login_info.csv create")
            val bufferWriter = BufferedWriter(FileWriter(file, true))
            val inputString = "%s,%s,%s,%s"
            bufferWriter.write(
                String.format(
                    inputString,
                    "Login Start timestamp",
                    "Login succeeded or failed",
                    "Login Success timestamp",
                    "Login time consuming"
                )
            )
            bufferWriter.newLine()
            bufferWriter.close()
        }catch (e: IOException){
            e.printStackTrace()
            EMLog.e("CsvFile", "login_info.csv create:" + e.message)
        }
    }

    fun writeLogin2CsvFile(context: Context, bean:TestLoginBean){
        try {
            EMLog.e("CsvFile", "writeLogin2CsvFile:" + bean.loginTime)
            val file = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + "login_info.csv")
            val bufferWriter = BufferedWriter(FileWriter(file, true))
            val inputString = "\t%s\t,\t%s\t,\t%s\t,\t%s\t"
            bufferWriter.write(
                String.format(
                    inputString,
                    bean.loginTime,
                    bean.isSuccess,
                    bean.successTime,
                    bean.elapsedTime
                )
            )
            bufferWriter.newLine()
            bufferWriter.close()
        }catch (e: IOException){
            e.printStackTrace()
            EMLog.e("CsvFile", bean.loginTime + ":" + e.message)
        }
    }
}