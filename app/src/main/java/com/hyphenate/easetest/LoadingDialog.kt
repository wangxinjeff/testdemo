package com.hyphenate.easetest

import android.R
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.hyphenate.easetest.databinding.LoadingAlertBinding


class LoadingDialog(context: Context): Dialog(context) {

    class Builder(private val context: Context) {
        private lateinit var binding: LoadingAlertBinding
        //提示信息
        private var message = ""

        //是否展示提示信息
        private var isShowMessage = true

        //是否按返回键取消
        private var isCancelable = true

        //是否取消
        private var isCancelOutside = false

        /**
         * 设置提示信息
         * @param message
         * @return
         */
        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        /**
         * 设置是否显示提示信息
         * @param isShowMessage
         * @return
         */
        fun setShowMessage(isShowMessage: Boolean): Builder {
            this.isShowMessage = isShowMessage
            return this
        }

        /**
         * 设置是否可以按返回键取消
         * @param isCancelable
         * @return
         */
        fun setCancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }

        fun updateMessage(message: String){
            binding.tipTextView.text = message
        }

        /**
         * 设置是否可以取消
         * @param isCancelOutside
         * @return
         */
        fun setCancelOutside(isCancelOutside: Boolean): Builder {
            this.isCancelOutside = isCancelOutside
            return this
        }

        //创建Dialog
        fun create(): LoadingDialog {
            val inflater = LayoutInflater.from(context)
            binding = LoadingAlertBinding.inflate(inflater)
            //设置带自定义主题的dialog
            val loadingDailog = LoadingDialog(context)
            if (isShowMessage) {
                binding.tipTextView.text = message
            } else {
                binding.tipTextView.visibility = View.GONE
            }
            loadingDailog.setContentView(binding.root)
            loadingDailog.setCancelable(isCancelable)
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside)
            return loadingDailog
        }
    }
}