package kr.smart.carefarm.utils

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kr.smart.carefarm.R

class SemiCircleView(context: Context, attrs: AttributeSet?) : View(context, attrs), AnimatorUpdateListener {
    private val rimPath: Path
    private val rimPaint: Paint
    private val rimRect: RectF
    private val frontPath: Path
    private val frontPaint: Paint
    private val frontRect: RectF
    private var mWidth = 0
    private var thickness = 0
    private var mColorFront = 0
    private var mColorBack = 0
    override fun onDraw(canvas: Canvas) { //Draws our Paths(what to draw) using our Paints(how to draw)
        canvas.drawPath(rimPath, rimPaint)
        canvas.drawPath(frontPath, frontPaint)
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) { //Set the thickness of our circle to 1/8 of the width
        thickness = w / 8
        //makes the current width available to our other methods
        mWidth = w
        // Set the Rectangle coordinates to the full size of the View
        rimRect[0f, 0f, w.toFloat()] = w.toFloat()
        //This makes sure the Path is empty
        rimPath.reset()
        rimPath.moveTo(0f, w / 2.toFloat())
        //Draw exterior arc
        rimPath.arcTo(rimRect, 180f, 180f)
        // Draw right closing line
        rimPath.rLineTo(-thickness.toFloat(), 0f)
        // Move the side of rectangle inward (dx positive)
        rimRect.inset(thickness.toFloat(), thickness.toFloat())
        // Create & Add interior arc based on narrowed rectangle
        rimPath.addArc(rimRect, 0f, -180f)
        // Draw left closing line
        rimPath.rLineTo(-thickness.toFloat(), 0f)
    }

    private var mAnimator: ValueAnimator? = null
    fun startAnim() { // sets the range of our value
        mAnimator = ValueAnimator.ofInt(0, 180)
        mAnimator?.let { valueAnim ->
            // sets the duration of our animation
            valueAnim.setDuration(1000)
            // registers our AnimatorUpdateListener
            valueAnim.addUpdateListener(this)
            valueAnim.start()
        }

    }

    override fun onAnimationUpdate(animation: ValueAnimator) { //gets the current value of our animation
        val value = animation.animatedValue as Int
        //makes sure the path is empty
        frontPath.reset()
        //sets the rectangle for our arc
        frontRect[0f, 0f, width.toFloat()] = width.toFloat()
        // starts our drawing on the middle left
        frontPath.moveTo(0f, width / 2.toFloat())
        //draws an arc starting at 180 and moving clockwise for the corresponding value
        frontPath.arcTo(frontRect, 180f, value.toFloat())
        //moves our rectangle inward in order to draw the interior arc
        frontRect.inset(thickness.toFloat(), thickness.toFloat())
        //draws the interior arc starting at(180+value) and moving counter-clockwise for the corresponding value
        frontPath.arcTo(frontRect, 180 + value.toFloat(), -value.toFloat())
        //draws the closing line
        frontPath.rLineTo(-thickness.toFloat(), 0f)
        // Forces the view to reDraw itself
        invalidate()
    }

    init {
        //Getting our XML attributes declared in <declare-styleable>
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SemiCircleView,
            0, 0
        )
        try {
            mColorFront =
                a.getColor(R.styleable.SemiCircleView_colorFront, Color.CYAN)
            mColorBack =
                a.getColor(R.styleable.SemiCircleView_colorBack, Color.LTGRAY)
        } finally {
            a.recycle()
        }
        rimPath = Path()
        rimRect = RectF()
        rimPaint = Paint()
        rimPaint.style = Paint.Style.FILL
        rimPaint.color = mColorBack
        rimPaint.isAntiAlias = true
        frontPath = Path()
        frontRect = RectF()
        frontPaint = Paint()
        frontPaint.style = Paint.Style.FILL
        frontPaint.color = mColorFront
        frontPaint.isAntiAlias = true
    }
}