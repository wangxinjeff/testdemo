package com.hyphenate.easetest

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
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

    private val messageList = mutableListOf<TestMessageBean>()

    private lateinit var handler: Handler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
        val option = EMOptions()
        option.autoLogin = false
        option.appKey = appkey
        EMClient.getInstance().init(this, option)
        EMClient.getInstance().chatManager().addMessageListener(object : EMMessageListener {
            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                messages?.forEach { message ->
                    if (message.type == EMMessage.Type.TXT) {
                        val receiveTime = System.currentTimeMillis()
                        val elapsedTime =
                            receiveTime - message.getLongAttribute("timestamp")
                        val body = message.body as EMTextMessageBody

                        messageList.add(
                            TestMessageBean(
                                message.from,
                                message.getLongAttribute("timestamp").toString(),
                                receiveTime.toString(),
                                elapsedTime.toString(),
                                message.msgId,
                                body.message
                            )
                        )
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
                handler.post {
                    Toast.makeText(
                        applicationContext,
                        "Receiver login successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onError(code: Int, error: String?) {
                login()
            }

            override fun onProgress(progress: Int, status: String?) {

            }

        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (messageList.size > 0) {
            messageList.forEach { message ->
                EMLog.e(
                    TAG,
                    "Message from: " + message.from + ", send timestamp: " + message.sendTime + ", receive timestamp: "
                            + message.receiveTime + ", time consuming: " + message.elapsedTime + ", msgId: " + message.mid + ", content: " + message.msgData
                )
            }
            InputFileUtil.writeMsgCsvTitle(applicationContext)
            messageList.forEach { message ->
                InputFileUtil.writeMsg2CsvFile(
                    applicationContext, message
                )
            }
            messageList.clear()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}