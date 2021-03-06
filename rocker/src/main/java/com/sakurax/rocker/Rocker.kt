package com.sakurax.rocker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*


class Rocker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):
    View(context, attrs, defStyleAttr){

    //画笔
    private lateinit var mPaint: Paint
    //位置
    private lateinit var mAreaPosition: Point
    private lateinit var mRockerPosition: Point

    private var mAreaBitmap: Bitmap? = null
    private var mRockerBitmap: Bitmap? = null

    private var mRockerColor: Int=0
    private var mAreaColor: Int=0

    private var mAreaAlpha = 150
    private var mRockerAlpha =30

    private var mAreaRadius = -1
    private var mRockerRadius = -1

    private var onHandleListener: ((x: Float, y: Float) -> Unit)? = null

    init{

        initPaint()
        initAttrs(context, attrs)
    }

    //初始化画笔
    private fun initPaint(){
        mPaint = Paint()
        mPaint.isAntiAlias=true//抗锯齿
        mPaint.style= Paint.Style.FILL//涂满
    }

    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?){
        //默认大小
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.rockerview)
        mAreaRadius = mTypedArray.getDimensionPixelOffset(
            R.styleable.rockerview_area_radius, dip2px(
                context,
                75.toFloat()
            )
        )
        mRockerRadius = mTypedArray.getDimensionPixelOffset(
            R.styleable.rockerview_rocker_radius, dip2px(
                context,
                25.toFloat()
            )
        )
        mAreaAlpha = mTypedArray.getInt(R.styleable.rockerview_area_alpha, 30)
        mRockerAlpha = mTypedArray.getInt(R.styleable.rockerview_rocker_alpha, 150)
        val mAreaBackGround: Drawable? = mTypedArray.getDrawable(R.styleable.rockerview_area_background)
        val mRockerBackGround: Drawable? = mTypedArray.getDrawable(R.styleable.rockerview_rocker_background)

        mTypedArray.recycle()
        when (mAreaBackGround) {
            is BitmapDrawable -> {
                mAreaBitmap = mAreaBackGround.bitmap
            }
            is ColorDrawable -> {
                mAreaBitmap = null
                mAreaColor = mAreaBackGround.color
            }
            else -> {
                mAreaBitmap = null
                mAreaColor = Color.CYAN
            }
        }
        when (mRockerBackGround) {
            is BitmapDrawable -> {
                mRockerBitmap = mRockerBackGround.bitmap
            }
            is ColorDrawable -> {
                mRockerBitmap = null
                mRockerColor = mRockerBackGround.color
            }
            else -> {
                mRockerBitmap = null
                mRockerColor = Color.CYAN
            }
        }
    }

    private fun getDistance(x1: Int, y1: Int, x2: Int, y2: Int):Int {
        val x = x1-x2
        val y = y1-y2
        return sqrt(x * x.toFloat() + y * y.toFloat()).toInt()
    }

    private fun dip2px(context: Context, dpValue: Float):Int {

        val scale:Float = context.applicationContext.resources.displayMetrics.density

        return (dpValue*scale+0.5f).toInt()

    }

    private fun drawRocker(canvas: Canvas){
        if(mRockerBitmap != null){
            mPaint.color = Color.BLACK
            val mSrcRect = Rect(0, 0, mRockerBitmap!!.width, mRockerBitmap!!.height)
            val mDestRect = Rect(
                mRockerPosition.x - mRockerRadius,
                mRockerPosition.y - mRockerRadius,
                mRockerPosition.x + mRockerRadius,
                mRockerPosition.y + mRockerRadius
            )
            canvas.drawBitmap(mRockerBitmap!!, mSrcRect, mDestRect, mPaint)
        }else{
            mPaint.color = mRockerColor
            mPaint.alpha = mRockerAlpha
            canvas.drawCircle(
                mRockerPosition.x.toFloat(),
                mRockerPosition.y.toFloat(),
                mRockerRadius.toFloat(),
                mPaint
            )
        }
    }

    private fun drawArea(canvas: Canvas){
        if(mAreaBitmap != null){
            mPaint.color = Color.BLACK
            val mSrcRect = Rect(0, 0, mAreaBitmap!!.width, mAreaBitmap!!.height)
            val mDestRect = Rect(
                mAreaPosition.x - mAreaRadius,
                mAreaPosition.y - mAreaRadius,
                mAreaPosition.x + mAreaRadius,
                mAreaPosition.y + mAreaRadius
            )
            canvas.drawBitmap(mAreaBitmap!!, mSrcRect, mDestRect, mPaint)
        }else{
            mPaint.color = mAreaColor
            mPaint.alpha = mAreaAlpha
            canvas.drawCircle(
                mAreaPosition.x.toFloat(),
                mAreaPosition.y.toFloat(),
                mAreaRadius.toFloat(),
                mPaint
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val len = getDistance(mAreaPosition.x, mAreaPosition.y, event.x.toInt(), event.y.toInt())

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                mRockerPosition.x = mAreaPosition.x
                mRockerPosition.y = mAreaPosition.y
            }
            else -> {
                if (len <= mAreaRadius) {
                    mRockerPosition.x = event.x.toInt()
                    mRockerPosition.y = event.y.toInt()
                } else {
                    val rad = atan2(
                        event.y - mAreaRadius - mRockerRadius,
                        event.x - mAreaRadius - mRockerRadius
                    )
                    mRockerPosition.x = mAreaPosition.x+(mAreaRadius* cos(rad)).toInt()
                    mRockerPosition.y = mAreaPosition.y+(mAreaRadius* sin(rad)).toInt()
                }
            }
        }
        val mX:Float=(mRockerPosition.x.toFloat()-mAreaRadius-mRockerRadius)/mAreaRadius
        val mY:Float=-(mRockerPosition.y.toFloat()-mAreaRadius-mRockerRadius)/mAreaRadius
        onHandleListener?.let { it(mX, mY) }
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val wSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        val measureWidth: Int = if (wSpecMode == MeasureSpec.AT_MOST || wSpecMode == MeasureSpec.UNSPECIFIED || wSpecSize < 0) {
            (mAreaRadius + mRockerRadius)*2
        } else {
            wSpecSize
        }

        val measureHeight: Int = if (hSpecMode == MeasureSpec.AT_MOST || hSpecMode == MeasureSpec.UNSPECIFIED || hSpecSize <0) {
            (mAreaRadius + mRockerRadius)*2
        } else{
            hSpecSize
        }

        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mAreaPosition = Point(w / 2, h / 2)
        mRockerPosition = Point(mAreaPosition)
        val tempRadius:Int = min(w - paddingLeft - paddingRight, h - paddingTop - paddingBottom) /2
        if (mAreaRadius == -1)
            mAreaRadius = (tempRadius.toFloat()*0.75).toInt()
        if (mRockerRadius == -1)
            mRockerRadius = (tempRadius.toFloat()*0.25).toInt()
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        if (isInEditMode) {
            canvas.drawColor(Color.WHITE)
        }
        try {
            drawArea(canvas)
            drawRocker(canvas)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getAreaRadius():Int{
        return mAreaRadius
    }
    fun setAreaRadius(areaRadius: Int){
        mAreaRadius=areaRadius
    }
    fun getRockerRadius(): Int {
        return mRockerRadius
    }

    fun setRockerRadius(rockerRadius: Int) {
        mRockerRadius = rockerRadius
    }

    fun getAreaBitmap(): Bitmap? {
        return mAreaBitmap
    }

    fun setAreaBitmap(areaBitmap: Bitmap) {
        mAreaBitmap = areaBitmap
    }

    fun getRockerBitmap(): Bitmap? {
        return mRockerBitmap
    }

    fun setRockerBitmap(rockerBitmap: Bitmap) {
        mRockerBitmap = rockerBitmap
    }

    fun getAreaColor(): Int {
        return mAreaColor
    }

    fun setAreaColor(areaColor: Int) {
        mAreaColor = areaColor
        mAreaBitmap = null
    }

    fun getRockerColor(): Int {
        return mRockerColor
    }

    fun setRockerColor(rockerColor: Int) {
        mRockerColor = rockerColor
        mRockerBitmap = null
    }

    fun setRockerListener(listener:(Float, Float) -> Unit){
        this.onHandleListener=listener
    }

}

