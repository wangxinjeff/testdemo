package com.hyphenate.easetest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hyphenate.EMCallBack
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.util.EMLog

class OtherProcessService : Service() {
    private val TAG = "Easemob"
    /**
     * appkey，登录的账号、密码
     * Appkey, login account, password
     */
    private val appkey = "1193210624041558#chat-demo"
    private val username = "easemobtest2"
    private val password = "1"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        InputFileUtil.writeMsgCsvTitle(applicationContext)

        val option = EMOptions()
        option.autoLogin = false
        option.appKey = appkey
        EMClient.getInstance().init(this, option)
        EMClient.getInstance().chatManager().addMessageListener(object : EMMessageListener {
            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                messages?.forEach { message ->
                    if (message.type == EMMessage.Type.TXT) {
                        val receiveTime = System.currentTimeMillis()
                        val sendTime =
                            receiveTime - message.getLongAttribute("timestamp")
                        val body = message.body as EMTextMessageBody
                        InputFileUtil.writeMsg2CsvFile(applicationContext, TestMessageBean(
                            message.from,
                            message.getLongAttribute("timestamp").toString(),
                            receiveTime.toString(),
                            sendTime.toString(),
                            message.msgId,
                            body.message
                        ))
                        EMLog.e(TAG, "Send time: $sendTime ms, msgId: " + message.msgId)
                    }
                }
            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageRead(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageDelivered(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageRecalled(messages: MutableList<EMMessage>?) {

            }

            override fun onMessageChanged(message: EMMessage?, change: Any?) {

            }
        })
        login()
    }

    @Synchronized
    fun login() {
        EMClient.getInstance().login(username, password, object : EMCallBack {
            override fun onSuccess() {

            }

            override fun onError(code: Int, error: String?) {
                login()
            }

            override fun onProgress(progress: Int, status: String?) {

            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}