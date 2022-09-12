package com.enrico.story_app.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.enrico.story_app.R

class PasswordEditText: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textSize = 14f
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        hint = resources.getString(R.string.password)
        background = ResourcesCompat.getDrawable(resources, R.drawable.custom_input, context.theme)
    }

    private fun init() {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(password.toString().length < 6 && password.toString().isNotEmpty()){
                    error = "Password harus 6 karakter"
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }
}