package com.example.storyapp.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class NameEditText : AppCompatEditText {

    private lateinit var nameImage: Drawable

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

        nameImage = ContextCompat.getDrawable(context, R.drawable.ic_name) as Drawable
        setCompoundDrawablesWithIntrinsicBounds(nameImage, null, null, null)


        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}