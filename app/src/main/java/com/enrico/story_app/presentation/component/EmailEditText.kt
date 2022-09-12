package com.enrico.story_app.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.enrico.story_app.R

class EmailEditText: AppCompatEditText {
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
        hint = resources.getString(R.string.email)
        background = ResourcesCompat.getDrawable(resources, R.drawable.custom_input, context.theme)
    }

    private fun init() {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(email: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches() && email.toString().isNotEmpty()){
                    error = resources.getString(R.string.email_not_valid)
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }
}