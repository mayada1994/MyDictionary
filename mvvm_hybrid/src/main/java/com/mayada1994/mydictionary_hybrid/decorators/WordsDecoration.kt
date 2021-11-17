package com.mayada1994.mydictionary_hybrid.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.mydictionary_hybrid.R

class WordsDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private var mPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.raw_sienna)
    }
    private var mHeightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, context.resources.displayMetrics).toInt()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.children.forEachIndexed { index, _ ->
            val view: View = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(view)
            parent.adapter?.let { adapter ->
                if (position != adapter.itemCount - 1) {
                    c.drawRect(view.left.toFloat(), view.bottom.toFloat(), view.right.toFloat(), view.bottom.toFloat() + mHeightDp, mPaint)
                }
            }
        }
    }

}