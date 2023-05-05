package com.example.storyapp.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class PasswordEditText : AppCompatEditText {

    private lateinit var passwordImage: Drawable
    var isRegister = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(){

        passwordImage = ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable
        setCompoundDrawablesWithIntrinsicBounds(passwordImage, null, null, null)
        transformationMethod = PasswordTransformationMethod.getInstance()

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty() && s.length < 8 && isRegister){
                    error = context.getString(R.string.password_invalid)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}