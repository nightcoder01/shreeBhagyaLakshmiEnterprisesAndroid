package pathak.creations.sbl.common

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import pathak.creations.sbl.R
import java.util.*
import kotlin.math.roundToInt

class Otp : EditText
{
    private var mMask: String? = null
    private var mMaskChars: StringBuilder? = null
    private var mSingleCharHint: String? = null
    private var mAnimatedType = 0
    private var mSpace = 24f
    private var mCharSize = 0f
    private var mNumChars = 4f
    private var mTextBottomPadding = 8f
    private var mMaxLength = 4
    private var mLineCoords: Array<RectF?>? = null
    private lateinit var mCharBottom: FloatArray
    private var mCharPaint: Paint? = null
    private var mLastCharPaint: Paint? = null
    private var mSingleCharPaint: Paint? = null
    private var mPinBackground: Drawable? = null
    private var mTextHeight = Rect()
    private var mIsDigitSquare = false
    private var mClickListener: OnClickListener? = null
    private var mOnPinEnteredListener: OnPinEnteredListener? = null
    private var mLineStroke = 1f
    private var mLineStrokeSelected = 2f
    private var mLinesPaint: Paint? = null
    private var mAnimate = false
    private var mHasError = false
    private var mOriginalTextColors: ColorStateList? = null
    private var mStates = arrayOf(
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(android.R.attr.state_active),
        intArrayOf(android.R.attr.state_focused),
        intArrayOf(-android.R.attr.state_focused)
    )
    private var mColors = intArrayOf(
        Color.GREEN,
        Color.RED,
        Color.BLACK,
        Color.GRAY
    )
    private var mColorStates = ColorStateList(mStates, mColors)

    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun init(
        context: Context,
        attrs: AttributeSet
    ) {
        val multi = context.resources.displayMetrics.density
        mLineStroke *= multi
        mLineStrokeSelected *= multi
        mSpace *= multi
        mTextBottomPadding *= multi
        val ta = context.obtainStyledAttributes(attrs, R.styleable.otp, 0, 0)
        try {
            val outValue = TypedValue()
            ta.getValue(R.styleable.otp_pinAnimationType, outValue)
            mAnimatedType = outValue.data
            mMask = ta.getString(R.styleable.otp_pinCharacterMask)
            mSingleCharHint = ta.getString(R.styleable.otp_pinRepeatedHint)
            mLineStroke = ta.getDimension(R.styleable.otp_pinLineStroke, mLineStroke)
            mLineStrokeSelected =
                ta.getDimension(R.styleable.otp_pinLineStrokeSelected, mLineStrokeSelected)
            mSpace = ta.getDimension(R.styleable.otp_pinCharacterSpacing, mSpace)
            mTextBottomPadding =
                ta.getDimension(R.styleable.otp_pinTextBottomPadding, mTextBottomPadding)
            mIsDigitSquare = ta.getBoolean(R.styleable.otp_pinBackgroundIsSquare, mIsDigitSquare)
            mPinBackground = ta.getDrawable(R.styleable.otp_pinBackgroundDrawable)
            val colors = ta.getColorStateList(R.styleable.otp_pinLineColors)
            if (colors != null) {
                mColorStates = colors
            }
        } finally {
            ta.recycle()
        }
        mCharPaint = Paint(paint)
        mLastCharPaint = Paint(paint)
        mSingleCharPaint = Paint(paint)
        mLinesPaint = Paint(paint)
        mLinesPaint!!.strokeWidth = mLineStroke
        val outValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorControlActivated,
            outValue, true
        )
        val colorSelected =
            if (isInEditMode) Color.GRAY else ContextCompat.getColor(
                context,
                R.color.colorPrimary
            )
        mColors[0] = colorSelected
        val colorFocused =
            if (isInEditMode) Color.GRAY else ContextCompat.getColor(
                context,
                R.color.colorPrimary
            )
        mColors[0] = colorFocused
        val colorUnfocused =
            if (isInEditMode) Color.GRAY else ContextCompat.getColor(
                context,
                R.color.grey
            )
        mColors[2] = colorUnfocused
        setBackgroundResource(0)
        mMaxLength =
            attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 4)
        mNumChars = mMaxLength.toFloat()
        super.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onPrepareActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean {
                return false
            }

            override fun onActionItemClicked(
                mode: ActionMode,
                item: MenuItem
            ): Boolean {
                return false
            }
        })
        super.setOnClickListener { v ->
            setSelection(text.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
        super.setOnLongClickListener {
            this@Otp.setSelection(this@Otp.text.length)
            true
        }
        if (inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD == InputType.TYPE_TEXT_VARIATION_PASSWORD && TextUtils.isEmpty(
                mMask
            )
        ) {
            mMask = "\u25CF"
        } else if (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD == InputType.TYPE_NUMBER_VARIATION_PASSWORD && TextUtils.isEmpty(
                mMask
            )
        ) {
            mMask = "\u25CF"
        }
        if (!TextUtils.isEmpty(mMask)) {
            mMaskChars = maskChars
        }
        paint.getTextBounds("|", 0, 1, mTextHeight)
        mAnimate = mAnimatedType > -1
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mOriginalTextColors = textColors
        if (mOriginalTextColors != null) {
            mLastCharPaint!!.color = mOriginalTextColors!!.defaultColor
            mCharPaint!!.color = mOriginalTextColors!!.defaultColor
            mSingleCharPaint!!.color = currentHintTextColor
        }
        val availableWidth =
            width - ViewCompat.getPaddingEnd(this) - ViewCompat.getPaddingStart(this)
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }
        mLineCoords = arrayOfNulls(mNumChars.toInt())
        mCharBottom = FloatArray(mNumChars.toInt())
        var startX: Int
        val bottom = height - paddingBottom
        val rtlFlag: Int
        val isLayoutRtl =
            TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
        if (isLayoutRtl) {
            rtlFlag = -1
            startX = (width - ViewCompat.getPaddingStart(this) - mCharSize).toInt()
        } else {
            rtlFlag = 1
            startX = ViewCompat.getPaddingStart(this)
        }
        var i = 0
        while (i < mNumChars) {
            mLineCoords!![i] =
                RectF(startX.toFloat(), bottom.toFloat(), startX + mCharSize, bottom.toFloat())
            if (mPinBackground != null) {
                if (mIsDigitSquare) {
                    mLineCoords!![i]!!.top = paddingTop.toFloat()
                    mLineCoords!![i]!!.right = startX + mLineCoords!![i]!!.height()
                } else {
                    mLineCoords!![i]!!.top -= mTextHeight.height() + mTextBottomPadding * 2
                }
            }
            if (mSpace < 0) {
                val s: Int  = mCharSize.roundToInt()
                val sss  = startX + rtlFlag * s * 2
                startX = sss
                //sss.toInt()
            } else {
                startX += rtlFlag * (mCharSize + mSpace).toInt()
            }
            mCharBottom[i] = mLineCoords!![i]!!.bottom - mTextBottomPadding
            i++
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas) {
        val text = fullText
        val textLength = text.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(text, 0, textLength, textWidths)
        var hintWidth = 0f
        if (mSingleCharHint != null) {
            val hintWidths = FloatArray(mSingleCharHint!!.length)
            paint.getTextWidths(mSingleCharHint, hintWidths)
            for (i in hintWidths) {
                hintWidth += i
            }
        }
        var i = 0
        while (i < mNumChars) {
            if (mPinBackground != null) {
                updateDrawableState(i < textLength, i == textLength)
                mPinBackground!!.setBounds(
                    mLineCoords!![i]!!.left.toInt(),
                    mLineCoords!![i]!!.top.toInt(),
                    mLineCoords!![i]!!.right.toInt(),
                    mLineCoords!![i]!!.bottom.toInt()
                )
                mPinBackground!!.draw(canvas)
            }
            val middle = mLineCoords!![i]!!.left + mCharSize / 2
            if (textLength > i) {
                if (!mAnimate || i != textLength - 1) {
                    canvas.drawText(
                        text,
                        i,
                        i + 1,
                        middle - textWidths[i] / 2,
                        mCharBottom[i],
                        mCharPaint!!
                    )
                } else {
                    canvas.drawText(
                        text,
                        i,
                        i + 1,
                        middle - textWidths[i] / 2,
                        mCharBottom[i],
                        mLastCharPaint!!
                    )
                }
            } else if (mSingleCharHint != null) {
                canvas.drawText(
                    mSingleCharHint!!,
                    middle - hintWidth / 2,
                    mCharBottom[i],
                    mSingleCharPaint!!
                )
            }
            if (mPinBackground == null) {
                updateColorForLines(i <= textLength)
                canvas.drawLine(
                    mLineCoords!![i]!!.left,
                    mLineCoords!![i]!!.top,
                    mLineCoords!![i]!!.right,
                    mLineCoords!![i]!!.bottom,
                    mLinesPaint!!
                )
            }
            i++
        }
    }

    private val fullText: CharSequence
        get() = if (mMask == null) {
            text
        } else {
            maskChars
        }

    private val maskChars: java.lang.StringBuilder
        get() {
            if (mMaskChars == null) {
                mMaskChars = StringBuilder()
            }
            val textLength = text.length
            while (mMaskChars!!.length != textLength) {
                if (mMaskChars!!.length < textLength) {
                    mMaskChars!!.append(mMask)
                } else {
                    mMaskChars!!.deleteCharAt(mMaskChars!!.length - 1)
                }
            }
            return mMaskChars!!
        }

    private fun getColorForState(vararg states: Int): Int {
        return mColorStates.getColorForState(states, Color.GRAY)
    }

    private fun updateColorForLines(hasTextOrIsNext: Boolean) {
        if (mHasError) {
            mLinesPaint!!.color = getColorForState(android.R.attr.state_active)
        } else if (isFocused) {
            mLinesPaint!!.strokeWidth = mLineStrokeSelected
            mLinesPaint!!.color = getColorForState(android.R.attr.state_focused)
            if (hasTextOrIsNext) {
                mLinesPaint!!.color = getColorForState(android.R.attr.state_selected)
            }
        } else {
            mLinesPaint!!.strokeWidth = mLineStroke
            mLinesPaint!!.color = getColorForState(-android.R.attr.state_focused)
        }
    }

    private fun updateDrawableState(
        hasText: Boolean,
        isNext: Boolean
    ) {
        if (mHasError) {
            mPinBackground!!.state = intArrayOf(android.R.attr.state_active)
        } else if (isFocused) {
            mPinBackground!!.state = intArrayOf(android.R.attr.state_focused)
            if (isNext) {
                mPinBackground!!.state = intArrayOf(
                    android.R.attr.state_focused,
                    android.R.attr.state_selected
                )
            } else if (hasText) {
                mPinBackground!!.state = intArrayOf(
                    android.R.attr.state_focused,
                    android.R.attr.state_checked
                )
            }
        } else {
            mPinBackground!!.state = intArrayOf(-android.R.attr.state_focused)
        }
    }

    private fun setError(hasError: Boolean) {
        mHasError = hasError
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        setError(false)
        if (mLineCoords == null || !mAnimate) {
            if (mOnPinEnteredListener != null && text.length == mMaxLength) {
                mOnPinEnteredListener!!.onPinEntered(text)
            }
            return
        }
        if (mAnimatedType == -1) {
            invalidate()
            return
        }
        if (lengthAfter > lengthBefore) {
            if (mAnimatedType == 0) {
                animatePopIn()
            } else {
                animateBottomUp(text, start)
            }
        }
        if (mOnPinEnteredListener != null) {
            mOnPinEnteredListener!!.onTextChange(text)
        }
    }

    private fun animatePopIn() {
        val va = ValueAnimator.ofFloat(1f, paint.textSize)
        va.duration = 200
        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            mLastCharPaint!!.textSize = (animation.animatedValue as Float)
            this@Otp.invalidate()
        }
        if (text.length == mMaxLength && mOnPinEnteredListener != null) {
            va.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    mOnPinEnteredListener!!.onPinEntered(text)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        va.start()
    }

    private fun animateBottomUp(text: CharSequence, start: Int) {
        mCharBottom[start] = mLineCoords!![start]!!.bottom - mTextBottomPadding
        val animUp = ValueAnimator.ofFloat(
            mCharBottom[start] + paint.textSize,
            mCharBottom[start]
        )
        animUp.duration = 300
        animUp.interpolator = OvershootInterpolator()
        animUp.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            mCharBottom[start] = value
            this@Otp.invalidate()
        }
        mLastCharPaint!!.alpha = 255
        val animAlpha = ValueAnimator.ofInt(0, 255)
        animAlpha.duration = 300
        animAlpha.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            mLastCharPaint!!.alpha = value
        }
        val set = AnimatorSet()
        if (text.length == mMaxLength && mOnPinEnteredListener != null) {
            set.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    mOnPinEnteredListener!!.onPinEntered(getText())
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        set.playTogether(animUp, animAlpha)
        set.start()
    }

    interface OnPinEnteredListener {
        fun onPinEntered(str: CharSequence?)
        fun onTextChange(str: CharSequence?)
    }

    companion object {
        private const val XML_NAMESPACE_ANDROID =
            "http://schemas.android.com/apk/res/android"
    }
}