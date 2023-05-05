package com.example.storyapp.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class EmailEditText : AppCompatEditText {

    private lateinit var emailImage: Drawable
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

        emailImage = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        setCompoundDrawablesWithIntrinsicBounds(emailImage, null, null, null)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s.trim()).matches() && isRegister){
                    error = context.getString(R.string.email_invalid)
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}