package com.hyphenate.easetest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.EMCallBack
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions
import com.hyphenate.easetest.databinding.ActivityMainBinding
import com.hyphenate.util.EMLog
import java.util.*

class MainActivity : AppCompatActivity() {

    /**
     * appkey，登录的账号、密码，接收方id
     * Appkey, login account, password, and receiver ID
     */
    private val appkey = "1193210624041558#chat-demo"
    private val username = "easemobtest1"
    private val password = "1"
    private val toUsername = "easemobtest2"

    private val TAG = "Easemob"

    private lateinit var binding: ActivityMainBinding

    // 计算登录次数 Counting login times
    private var loginLimit = 0

    // 计算登录成功次数 Count the number of successful login times
    private var loginSucLimit = 0

    // 计算登录失败次数 Count the number of login failures
    private var loginFaiLimit = 0

    // 计算消息发送次数 Calculates the number of times the message is sent
    private var sendLimit = 0

    // 计算消息发送成功次数 Calculates the number of successful message sending
    private var sendSucLimit = 0

    // 计算消息发送失败次数 Calculates the number of message sending failures
    private var sendFaiLimit = 0
    private lateinit var dialog: LoadingDialog
    private lateinit var builder: LoadingDialog.Builder

    // 最短登录耗时 Minimum Login time
    private var loginFirstTime = 0L

    // 登录最长耗时 Maximum Login Duration
    private var loginLastTime = 0L

    // 发送最短耗时 Shortest sending time
    private var sendFirstTime = 0L

    // 发送最长耗时 Maximum Sending Time
    private var sendLastTime = 0L

    // 测试次数 Test times
    private val loginTotal = 1000
    private val sendTotal = 1000

    // 登录token Login token
    private var loginToken = ""

    private lateinit var handler: Handler

    private lateinit var serviceIntent: Intent

    private val infoList = mutableListOf<TestLoginBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = object : Handler(this.mainLooper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        loginTest()
                    }
                    1 -> {
                        messageTest()
                    }
                }
            }
        }

        val option = EMOptions()
        option.appKey = appkey
        option.autoLogin = false
        EMClient.getInstance().init(this, option)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(this, OtherProcessService::class.java)
        startService(serviceIntent)

        initView()
    }

    private fun initView() {
        builder = LoadingDialog.Builder(this@MainActivity)
            .setMessage("In the test")
            .setCancelable(false)
        dialog = builder.create()

        binding.btnLog.setOnClickListener { v ->
            writeLog()
        }

        binding.btnLogin.setOnClickListener { v ->
            dialog.show()
            EMClient.getInstance().logout(false)
            loginLimit = 0
            loginSucLimit = 0
            loginFaiLimit = 0
            loginFirstTime = 0L
            loginLastTime = 0L
            handler.sendEmptyMessageDelayed(0, 300)
        }

        binding.btnMessage.setOnClickListener { v ->
            dialog.show()
            builder.updateMessage("Test message")
            EMClient.getInstance().login(username, password, object : EMCallBack {
                override fun onSuccess() {
                    runOnUiThread {
                        sendLimit = 0
                        sendSucLimit = 0
                        sendFaiLimit = 0
                        sendFirstTime = 0L
                        sendLastTime = 0L
                        messageTest()
                    }
                }

                override fun onError(code: Int, error: String?) {
                    runOnUiThread {
                        if (code == EMError.USER_ALREADY_LOGIN) {
                            sendLimit = 0
                            sendSucLimit = 0
                            sendFaiLimit = 0
                            sendFirstTime = 0L
                            sendLastTime = 0L
                            messageTest()
                        } else {
                            dialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Login failed: $error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onProgress(progress: Int, status: String?) {

                }
            })
        }
    }

    @Synchronized
    private fun loginTest() {
        loginLimit++
        builder.updateMessage("Test login:$loginLimit times")
        EMLog.e(TAG, "loginNumber=$loginLimit")
        val startLogin = System.currentTimeMillis()

        if (loginToken.isNotEmpty()) {
            EMLog.e(TAG, "loginWithToken")
            EMClient.getInstance().loginWithToken(username, loginToken, object : EMCallBack {
                @SuppressLint("SetTextI18n")
                override fun onSuccess() {
                    runOnUiThread {
                        val successTime = System.currentTimeMillis()
                        val loginTime = successTime - startLogin
                        if (loginTime > loginLastTime || loginLastTime == 0L) {
                            loginLastTime = loginTime
                        }
                        if (loginTime < loginFirstTime || loginFirstTime == 0L) {
                            loginFirstTime = loginTime
                        }

                        infoList.add(
                            TestLoginBean(
                                startLogin.toString(),
                                true,
                                successTime.toString(),
                                loginTime.toString()
                            )
                        )

                        loginSucLimit++
                        showLoginParam()
                        binding.tvLog.text =
                            binding.tvLog.text.toString() + "Login for the $loginLimit times: succeeded, elapsed time : $loginTime ms \n"
                        binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        if (loginLimit < loginTotal) {
                            EMClient.getInstance().logout(false)
                            handler.sendEmptyMessageDelayed(0, 300)
                        } else {
                            EMLog.e(
                                TAG,
                                "Login test 1000 times，successNumber=$loginSucLimit, failNumber=$loginFaiLimit"
                            )
                            dialog.dismiss()
                        }
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onError(code: Int, error: String?) {
                    runOnUiThread {
                        EMLog.e(
                            TAG,
                            "Login failed, $code : $error"
                        )

                        infoList.add(TestLoginBean(startLogin.toString(), false, "", ""))

                        loginFaiLimit++
                        showLoginParam()
                        binding.tvLog.text =
                            binding.tvLog.text.toString() + "Login for the $loginLimit times: failed \n"
                        binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        if (loginLimit < loginTotal) {
                            loginTest()
                        } else {
                            EMLog.e(
                                TAG,
                                "Login test 1000 times，successNumber=$loginSucLimit, failNumber=$loginFaiLimit"
                            )
                            dialog.dismiss()
                        }
                    }
                }

                override fun onProgress(progress: Int, status: String?) {

                }
            })
        } else {
            EMLog.e(TAG, "login")
            EMClient.getInstance().login(username, password, object : EMCallBack {
                @SuppressLint("SetTextI18n")
                override fun onSuccess() {
                    runOnUiThread {
                        val successTime = System.currentTimeMillis()
                        val loginTime = successTime - startLogin
                        if (loginTime > loginLastTime || loginLastTime == 0L) {
                            loginLastTime = loginTime
                        }
                        if (loginTime < loginFirstTime || loginFirstTime == 0L) {
                            loginFirstTime = loginTime
                        }

                        infoList.add(
                            TestLoginBean(
                                startLogin.toString(),
                                true,
                                successTime.toString(),
                                loginTime.toString()
                            )
                        )

                        loginSucLimit++
                        loginToken = EMClient.getInstance().accessToken
                        showLoginParam()
                        binding.tvLog.text =
                            binding.tvLog.text.toString() + "Login for the $loginLimit times: succeeded, elapsed time: $loginTime ms \n"

                        if (loginLimit < loginTotal) {
                            EMClient.getInstance().logout(false)
                            handler.sendEmptyMessageDelayed(0, 300)
                        } else {
                            EMLog.e(
                                TAG,
                                "Login test 1000 times，successNumber=$loginSucLimit, failNumber=$loginFaiLimit"
                            )
                            dialog.dismiss()
                        }
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onError(code: Int, error: String?) {
                    runOnUiThread {
                        EMLog.e(
                            TAG,
                            "Login failed, $code : $error"
                        )

                        infoList.add(TestLoginBean(startLogin.toString(), false, "", ""))

                        loginFaiLimit++
                        showLoginParam()
                        binding.tvLog.text =
                            binding.tvLog.text.toString() + "Login for the $loginLimit times: failed \n"
                        if (loginLimit < loginTotal) {
                            loginTest()
                        } else {
                            EMLog.e(
                                TAG,
                                "Login test 1000 times，successNumber=$loginSucLimit, failNumber=$loginFaiLimit"
                            )
                            dialog.dismiss()
                        }
                    }
                }

                override fun onProgress(progress: Int, status: String?) {

                }

            })
        }
    }

    @Synchronized
    private fun messageTest() {
        sendLimit++
        builder.updateMessage("Test message: $sendLimit times")
        val startSend = System.currentTimeMillis()
        val message = EMMessage.createTxtSendMessage(
            "Test message: $sendLimit time",
            toUsername
        )
        message.setAttribute("timestamp", startSend);
        message.setMessageStatusCallback(object : EMCallBack {
            @SuppressLint("SetTextI18n")
            override fun onSuccess() {
                runOnUiThread {
                    val sendTime = System.currentTimeMillis() - startSend
                    if (sendTime > sendLastTime || sendLastTime == 0L) {
                        sendLastTime = sendTime
                    }
                    if (sendTime < sendFirstTime || sendFirstTime == 0L) {
                        sendFirstTime = sendTime
                    }
                    sendSucLimit++
                    showSendParam()
                    binding.tvLog.text =
                        binding.tvLog.text.toString() + "Send message for the $sendLimit times: succeeded, elapsed time:$sendTime  ms \n"
                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    if (sendLimit < sendTotal) {
                        handler.sendEmptyMessageDelayed(1, 100)
                    } else {
                        EMLog.e(
                            TAG,
                            "SendMessage test 1000 times，successNumber=$sendSucLimit, failNumber=$sendFaiLimit"
                        )
                        dialog.dismiss()
                    }
                }

            }

            @SuppressLint("SetTextI18n")
            override fun onError(code: Int, error: String?) {
                runOnUiThread {
                    EMLog.e(
                        TAG,
                        "Send failed, $code : $error"
                    )

                    sendFaiLimit++
                    showSendParam()
                    binding.tvLog.text =
                        binding.tvLog.text.toString() + "Send message for the $sendLimit time: failed \n"
                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    if (sendLimit < sendTotal) {
                        messageTest()
                    } else {
                        EMLog.e(
                            TAG,
                            "SendMessage test 1000 times，successNumber=$sendSucLimit, failNumber=$sendFaiLimit"
                        )
                        dialog.dismiss()
                    }
                }
            }

            override fun onProgress(progress: Int, status: String?) {

            }
        })
        EMClient.getInstance().chatManager().sendMessage(message)
    }

    @SuppressLint("SetTextI18n")
    fun showLoginParam() {
        binding.loginSuc.text = "$loginSucLimit times"

        binding.firstLogin.text = "$loginFirstTime ms"
        binding.lastLogin.text = "$loginLastTime ms"
    }

    @SuppressLint("SetTextI18n")
    fun showSendParam() {
        binding.sendSuc.text = "$sendSucLimit times"

        binding.firstSend.text = "$sendFirstTime ms"
        binding.lastSend.text = "$sendLastTime ms"
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, OtherProcessService::class.java))
        handler.removeCallbacksAndMessages(null)
    }

    private fun writeLog() {
        if (infoList.size > 0) {
            infoList.forEach { info ->
                EMLog.e(
                    TAG,
                    "Login Start timestamp: " + info.loginTime
                            + ", Login succeeded: " + info.isSuccess
                            + ", Login Success timestamp: " + info.successTime
                            + ", Login time consuming: " + info.elapsedTime
                )
            }
            InputFileUtil.writeLoginCsvTitle(this)
            infoList.forEach { info ->
                InputFileUtil.writeLogin2CsvFile(this@MainActivity, info)
            }
            infoList.clear()
        }
        startService(serviceIntent)
    }
}

