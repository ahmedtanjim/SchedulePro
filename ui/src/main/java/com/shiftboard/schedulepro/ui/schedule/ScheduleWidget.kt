package com.shiftboard.schedulepro.ui.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.*
import android.widget.OverScroller
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.shiftboard.schedulepro.core.common.utils.*
import com.shiftboard.schedulepro.core.network.model.schedule.SummaryIconModel
import com.shiftboard.schedulepro.ui.R
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.height
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min


class ScheduleWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {
    private val palette: SchedulePalette by lazy { SchedulePalette(context) }
    private val headerHeight: Float by lazy { resources.getDimension(R.dimen.schedule_header_height) }

    private val cellSize: Float get() = (width / COLUMNS).toFloat()

    private val iconSize: Float get() = (cellSize / 4f)

    private val rowHeight: Float get() = cellSize
    private val today: LocalDate by lazy { LocalDate.now() }

    private val checkDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_thick_check)
    }
    private val bidDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_bidindicator)
    }
    private val openShiftDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_bidavailindicator)
    }

    private var activeDate: LocalDate = LocalDate.now()
    private var ghostDate: LocalDate? = null

    private var weekStart: DayOfWeek = DayOfWeek.SUNDAY

    private var displayMode: DisplayMode = DisplayMode.WEEK
        // If we leave the month display mode we are clearing the ghost date so we don't
        // accidentally display it anymore
        set(value) {
            if (field == DisplayMode.MONTH && value != DisplayMode.MONTH) {
                ghostDate = null
            }
            field = value
        }

    // we are using a ghost date to show a different month than the active month
    private val displayDate: LocalDate
        get() =
            if (displayMode == DisplayMode.MONTH)
                ghostDate ?: activeDate else activeDate

    private val accumulatedScrollOffset = PointF()
    private var velocityTracker: VelocityTracker? = null
    private var isSmoothScrolling: Boolean = false
    private var isScrolling: Boolean = false
    private var currentDirection = Direction.NONE
    private var maximumVelocity = 0f
    private var distanceX = 0f

    private var lastAutoScrollFromFling: Long = 0

    private var densityAdjustedSnapVelocity = 0
    private var distanceThresholdForAutoScroll = 0
    private var iconHash = HashMap<LocalDate, List<SummaryIconModel>>()

    var visibleDateRangeListener: OnVisibleRangeChangeListener =
        OnVisibleRangeChangeListener { _, _ -> }
    var dateSelectedListener: OnDateSelectedListener = OnDateSelectedListener { }

    init {
        val screenDensity = context.resources.displayMetrics.density
        val configuration = ViewConfiguration
            .get(context)
        densityAdjustedSnapVelocity = (screenDensity * SNAP_VELOCITY_DIP_PER_SECOND).toInt()
        maximumVelocity = configuration.scaledMaximumFlingVelocity.toFloat()

    }

    fun setIconHash(hash: HashMap<LocalDate, List<SummaryIconModel>>) {
        iconHash = hash
        invalidate()
    }

    fun setSelectedDate(date: LocalDate) {
        activeDate = date
        postRange(date)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        calculateXPositionOffset()

        drawHeader(canvas)
        drawGrid(canvas)

        when {
            accumulatedScrollOffset.x < 0 -> {
                drawRight(canvas)
                drawCalendar(canvas)
            }
            accumulatedScrollOffset.x > 0 -> {
                drawLeft(canvas)
                drawCalendar(canvas)
            }
            else -> {
                drawCalendar(canvas)
            }
        }
    }

    private fun drawLeft(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(0f, headerHeight - 2.dp, accumulatedScrollOffset.x, height.toFloat())

        val renderedDate = if (displayMode == DisplayMode.WEEK) {
            displayDate.minusWeeks(1L)
        } else {
            displayDate.minusWeeks(1L)
        }

        val indexHeight = rowHeight * activeWeekIndex(renderedDate)

        val offsetHeight = min(indexHeight, (WEEKS * rowHeight) - (height - headerHeight))
        val yOffset = headerHeight - offsetHeight

        val startDate = renderedDate.yearMonth.atDay(1)
        val offset = startDate.dayOfWeek.daysAfter(weekStart).toLong()
        val start = startDate.minusDays(offset)

        (0 until DAYS).forEach { index ->
            val date = start.plusDays(index.toLong())
            drawDay(canvas, false,
                date.isEqual(today), date.dayOfMonth.toString(),
                accumulatedScrollOffset.x + cellSize * (index % COLUMNS) - width,
                yOffset + (cellSize * (index / COLUMNS)), iconHash[date] ?: listOf())
        }

        canvas.restore()
    }

    private fun drawRight(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(width + accumulatedScrollOffset.x,
            headerHeight - 2.dp,
            width.toFloat(),
            height.toFloat())

        val renderedDate = if (displayMode == DisplayMode.WEEK) {
            displayDate.plusWeeks(1L)
        } else {
            displayDate.plusMonths(1L)
        }

        val indexHeight = rowHeight * activeWeekIndex(renderedDate)

        val offsetHeight = min(indexHeight, (WEEKS * rowHeight) - (height - headerHeight))
        val yOffset = headerHeight - offsetHeight

        val startDate = renderedDate.yearMonth.atDay(1)
        val offset = startDate.dayOfWeek.daysAfter(weekStart).toLong()
        val start = startDate.minusDays(offset)

        (0 until DAYS).forEach { index ->
            val date = start.plusDays(index.toLong())
            drawDay(canvas, false, date.isEqual(today), date.dayOfMonth.toString(),
                width + accumulatedScrollOffset.x + cellSize * (index % COLUMNS),
                yOffset + (cellSize * (index / COLUMNS)), iconHash[date] ?: listOf())
        }

        canvas.restore()
    }

    private fun drawCalendar(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(0f, headerHeight - 2.dp, width.toFloat(), height.toFloat())

        val indexHeight = rowHeight * activeWeekIndex(displayDate)

        val offsetHeight = min(indexHeight, (WEEKS * rowHeight) - (height - headerHeight))
        val yOffset = headerHeight - offsetHeight

        val startDate = displayDate.yearMonth.atDay(1)
        val offset = startDate.dayOfWeek.daysAfter(weekStart).toLong()
        val start = startDate.minusDays(offset)

        (0 until DAYS).forEach { index ->
            val date = start.plusDays(index.toLong())
            drawDay(canvas, date.isEqual(activeDate),
                date.isEqual(today), date.dayOfMonth.toString(),
                accumulatedScrollOffset.x + cellSize * (index % COLUMNS),
                yOffset + (cellSize * (index / COLUMNS)), iconHash[date] ?: listOf())
        }

        canvas.restore()
    }

    private fun calculateXPositionOffset() {
        if (currentDirection === Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX
        }
    }

    private fun resetScroll() {
        distanceX = 0f
        accumulatedScrollOffset.x = 0f
//        scroller.startScroll(0, 0, 0, 0)
    }

    private fun drawDay(
        canvas: Canvas, selected: Boolean, today: Boolean,
        dayOfMonth: String, posX: Float, posY: Float, icons: List<SummaryIconModel>,
    ) {
        when {
            selected -> {
                canvas.drawCircle(posX + (cellSize / 2f),
                    posY + (cellSize / 3f),
                    (cellSize / 3.75f),
                    palette.selectedHighlight)
                canvas.drawText(dayOfMonth,
                    posX + cellSize / 2f,
                    posY + cellSize / 3f - (palette.selectedLabel.height / 2f),
                    palette.selectedLabel)
            }
            today -> {
                canvas.drawCircle(posX + (cellSize / 2f),
                    posY + (cellSize / 3f),
                    (cellSize / 3.75f),
                    palette.todayHighlight)
                canvas.drawText(dayOfMonth,
                    posX + cellSize / 2f,
                    posY + cellSize / 3f - (palette.todayLabel.height / 2f),
                    palette.todayLabel)
            }
            else -> {
                canvas.drawText(dayOfMonth,
                    posX + cellSize / 2f,
                    posY + cellSize / 3f - (palette.dateLabel.height / 2f),
                    palette.dateLabel)
            }
        }

        when (icons.size) {
            0 -> { /* pass */
            }
            1 -> {
                canvas.drawIcon(posX, posY, 1f, icons[0])
            }
            2 -> {
                canvas.drawIcon(posX, posY, 0.5f, icons[0])
                canvas.drawIcon(posX, posY, 1.5f, icons[1])
            }
            3 -> {
                canvas.drawIcon(posX, posY, 0f, icons[0])
                canvas.drawIcon(posX, posY, 1f, icons[1])
                canvas.drawIcon(posX, posY, 2f, icons[2])
            }
            else -> {
                canvas.drawIcon(posX, posY, 0f, icons[0])
                canvas.drawIcon(posX, posY, 1f, icons[1])
                canvas.drawAdditional(posX, posY, 2f)
            }
        }
    }

    private fun Canvas.drawIcon(posX: Float, posY: Float, index: Float, icon: SummaryIconModel) {
        when (icon.type) {
            "Shift" -> {
                if (icon.hasOvertime) {
                    drawOTShift(posX, posY, index, icon.color)
                } else {
                    drawShift(posX, posY, index, icon.color)
                }
            }
            "Leave" -> {
                drawLeave(posX, posY, index, icon.color)
            }
            "PendingLeaveRequest" -> {
                drawLeave(posX, posY, index, icon.color)
            }
            "SignUp" -> {
                drawSignup(posX, posY, index, icon.color)
            }
            "ProjectedShift" -> {
                drawProjectedShift(posX, posY, index, icon.color)
            }
            "OpenShift" -> {
                drawBid(posX, posY, index, icon.color)
            }
            "OpenShiftRequest" -> {
                drawOpenShift(posX, posY, index, icon.color)
            }
            else -> {
            }
        }
    }

    private fun activeWeekIndex(date: LocalDate): Int {
        val startOfMonth = date.yearMonth.atDay(1)

        val offset = startOfMonth.dayOfWeek.daysAfter(weekStart)
        val index = date.dayOfMonth + offset - 1

        return index / COLUMNS
    }

    private fun drawHeader(canvas: Canvas) {
        val text = when (displayMode) {
            DisplayMode.MONTH -> {
                val element = displayDate.yearMonth

                if (element.year != LocalDate.now().year) "${
                    element.month.getDisplayName(TextStyle.FULL_STANDALONE,
                        Locale.getDefault())
                } ${element.year}"
                else element.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            }
            DisplayMode.WEEK -> {
                val week = YearWeek.fromOffset(weekStart, displayDate)

                val startOfWeek = week.atDay(weekStart)
                val endOfWeek = startOfWeek.plusDays(6L)

                val month = if (startOfWeek.month == endOfWeek.month) {
                    startOfWeek.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                } else {
                    "${
                        startOfWeek.month.getDisplayName(TextStyle.FULL_STANDALONE,
                            Locale.getDefault())
                    } - ${
                        endOfWeek.month.getDisplayName(TextStyle.FULL_STANDALONE,
                            Locale.getDefault())
                    }"
                }

                if (endOfWeek.year != LocalDate.now().year) "$month ${endOfWeek.year}" else month
            }
        }
        canvas.drawText(text,
            16.dp.toFloat(),
            16.dp - palette.headerText.height,
            palette.headerText)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        displayMode = if (height - headerHeight > (cellSize * 3)) {
            DisplayMode.MONTH
        } else {
            DisplayMode.WEEK
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val y = 56.dp - palette.labelText.height
        val dayWidthHalf = cellSize / 2f

        (0 until COLUMNS).forEach {
            val dow = weekStart.plus(it.toLong())
            val x = (2 * it + 1) * dayWidthHalf
            canvas.drawText(
                dow.getDisplayName(TextStyle.NARROW, Locale.getDefault()).toUpperCase(
                    Locale.getDefault()
                ), x, y, palette.labelText
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val originalHeight = MeasureSpec.getSize(heightMeasureSpec)

        val calculatedHeight: Int = originalWidth * WEEKS / COLUMNS
        distanceThresholdForAutoScroll = (width * 0.5f).toInt()

        val finalWidth: Int
        var finalHeight: Int

        if (calculatedHeight > originalHeight) {
            finalWidth = originalHeight * WEEKS / COLUMNS
            finalHeight = originalHeight
        } else {
            finalWidth = originalWidth
            finalHeight = calculatedHeight
        }

        finalHeight += headerHeight.toInt()

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.AT_MOST)
        )
    }

    override fun computeScroll() {
        super.computeScroll()

        if (scroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = scroller.currX.toFloat()
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }

        velocityTracker?.addMovement(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                    resetScroll()
                    invalidate()
                }
                isSmoothScrolling = false
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                velocityTracker?.computeCurrentVelocity(500)
            }
            MotionEvent.ACTION_UP -> {
                handleHorizontalScrolling()
                velocityTracker?.recycle()
                velocityTracker?.clear()
                velocityTracker = null
                isScrolling = false
            }
        }

        invalidate()

        // on touch action finished (CANCEL or UP), we re-allow the parent container to intercept touch events (scroll inside ViewPager + RecyclerView issue #82)
        if ((event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP)) {
            parent.requestDisallowInterceptTouchEvent(false)
        }

        // always allow gestureDetector to detect onSingleTap and scroll events
        return gestureDetector.onTouchEvent(event)
    }

    private fun handleHorizontalScrolling() {
        val velocityX: Int = computeVelocity()
        handleSmoothScrolling(velocityX)
        invalidate()
    }

    private fun computeVelocity(): Int {
        velocityTracker!!.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, maximumVelocity)
        return velocityTracker!!.xVelocity.toInt()
    }

    private fun handleSmoothScrolling(velocityX: Int) {
        val distanceScrolled = (accumulatedScrollOffset.x).toInt()
        val isEnoughTimeElapsedSinceLastSmoothScroll: Boolean =
            System.currentTimeMillis() - lastAutoScrollFromFling > LAST_FLING_THRESHOLD_MILLIS

        if (velocityX > densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollPrevious()
        } else if (velocityX < -densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollNext()
        } else if (isScrolling && distanceScrolled > distanceThresholdForAutoScroll) {
            scrollPrevious()
        } else if (isScrolling && distanceScrolled < -distanceThresholdForAutoScroll) {
            scrollNext()
        } else {
            isSmoothScrolling = false
            // snap back
            scroller.startScroll(accumulatedScrollOffset.x.toInt(),
                0,
                -accumulatedScrollOffset.x.toInt(),
                0)
        }
        resetScroll()
    }

    private fun scrollNext() {
        lastAutoScrollFromFling = System.currentTimeMillis()
        when (displayMode) {
            DisplayMode.WEEK -> {
                val newDate = displayDate.plusWeeks(1L)
                activeDate = newDate
                dateSelectedListener.onDateSelected(newDate)
                postRange(newDate)

            }
            DisplayMode.MONTH -> {
                val newDate = displayDate.plusMonths(1L).atStartOfMonth()
                ghostDate = newDate
                postRange(newDate)
            }
        }
        val remainingScrollAfterFingerLifted = -width - accumulatedScrollOffset.x

        scroller.startScroll(accumulatedScrollOffset.x.toInt(), 0,
            remainingScrollAfterFingerLifted.toInt(), 0,
            (abs(remainingScrollAfterFingerLifted.toInt()) / width.toFloat() * ANIMATION_SCREEN_SET_DURATION_MILLIS).toInt())

        isSmoothScrolling = true
        performScrollCallback()
    }

    private fun postRange(date: LocalDate) {
        visibleDateRangeListener.onRangeChange(date.minusMonths(1).atStartOfMonth(),
            date.plusMonths(1).atEndOfMonth())
    }

    private fun scrollPrevious() {
        lastAutoScrollFromFling = System.currentTimeMillis()
        when (displayMode) {
            DisplayMode.WEEK -> {
                val newDate = displayDate.minusWeeks(1L)
                activeDate = newDate
                dateSelectedListener.onDateSelected(newDate)
                postRange(newDate)
            }
            DisplayMode.MONTH -> {
                val newDate = displayDate.minusMonths(1L).atStartOfMonth()
                ghostDate = newDate
                postRange(newDate)
            }
        }

        val remainingScrollAfterFingerLifted = width - accumulatedScrollOffset.x

        scroller.startScroll(accumulatedScrollOffset.x.toInt(), 0,
            remainingScrollAfterFingerLifted.toInt(), 0,
            (abs(remainingScrollAfterFingerLifted.toInt()) / width.toFloat() * ANIMATION_SCREEN_SET_DURATION_MILLIS).toInt())

        isSmoothScrolling = true
        performScrollCallback()
    }

    private fun performScrollCallback() {

    }

    private fun onScrollView(
        dX: Float,
        dY: Float,
    ) {
        //ignore scrolling callback if already smooth scrolling
        if (isSmoothScrolling) {
            return
        }

        if (currentDirection == Direction.NONE) {
            currentDirection = if (abs(dX) > abs(dY)) {
                Direction.HORIZONTAL
            } else {
                Direction.VERTICAL
            }
        }

        isScrolling = true
        distanceX = dX
    }


    private fun handleOnClick(x: Float, y: Float) {
        if (RectF(0f, headerHeight - 2.dp, width.toFloat(), height.toFloat()).contains(x, y)) {
            val index = when (displayMode) {
                DisplayMode.WEEK -> {
                    val rowIndex = activeWeekIndex(displayDate)

                    val columnIndex = floor(x / cellSize).toInt()
                    (rowIndex * COLUMNS + columnIndex)
                }
                DisplayMode.MONTH -> {
                    val columnIndex = floor(x / cellSize).toInt()
                    val rowIndex = floor((y - headerHeight) / cellSize).toInt()
                    (rowIndex * COLUMNS + columnIndex)
                }
            }

            val startDate = displayDate.yearMonth.atDay(1)
            val offset = startDate.dayOfWeek.daysAfter(weekStart).toLong()
            val start = startDate.minusDays(offset)

            val date = start.plusDays(index.toLong())
            dateSelectedListener.onDateSelected(date)
        }

        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    private var scroller: OverScroller = OverScroller(context)

    private val gestureDetector = GestureDetectorCompat(context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean {
                scroller.forceFinished(true)
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                handleOnClick(e.x, e.y)
                return super.onSingleTapUp(e)
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
            ): Boolean {
                if (abs(distanceX) > 0) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    onScrollView(distanceX, distanceY)
                    invalidate()
                    return true
                }

                return false
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float,
            ): Boolean {
                scroller.forceFinished(true)
                return true
            }
        })


    /*
    * Icon draw functions
    * note about index, the index position allows a float value between 0 and 2, but is expecting
    * the values 0, .5, 1, 1.5, 2 this allows the draw function to handle the half steps for 2 icons
    * without having to understand the logic of how many icons there are.
    */
    private fun iconXPos(offset: Float, index: Float): Float =
        offset + (cellSize - (iconSize * 3)) / 2f + (iconSize * index)

    private fun Canvas.drawOTShift(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        palette.iconPaint.color = color
        drawRoundRect(xStart + iconPadding, yStart + iconPadding,
            xStart + iconSize - iconPadding, yStart + iconSize - iconPadding,
            2.dp.toFloat(), 2.dp.toFloat(), palette.iconPaint)
        if (isLowContrast(color)) {
            drawRoundRect(xStart + iconPadding, yStart + iconPadding,
                xStart + iconSize - iconPadding, yStart + iconSize - iconPadding,
                2.dp.toFloat(), 2.dp.toFloat(), palette.iconPaintLC)
        }

        drawCircle(xStart + iconSize - iconSize / 4f,
            yStart + iconSize / 4f,
            iconSize / 4f,
            palette.outerCirclePaint)
        drawCircle(xStart + iconSize - iconSize / 4f,
            yStart + iconSize / 4f,
            iconSize / 6f,
            palette.innerCirclePaint)
    }


    private fun Canvas.drawShift(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        palette.iconPaint.color = color
        drawRoundRect(xStart + iconPadding, yStart + iconPadding,
            xStart + iconSize - iconPadding, yStart + iconSize - iconPadding,
            2.dp.toFloat(), 2.dp.toFloat(), palette.iconPaint)
        if (isLowContrast(color)) {
            drawRoundRect(xStart + iconPadding, yStart + iconPadding,
                xStart + iconSize - iconPadding, yStart + iconSize - iconPadding,
                2.dp.toFloat(), 2.dp.toFloat(), palette.iconPaintLC)
        }
    }

    private fun Canvas.drawLeave(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        palette.iconPaint.color = color
        drawCircle(xStart + iconSize / 2f,
            yStart + iconSize / 2f,
            iconSize / 2f - iconPadding,
            palette.iconPaint)

        if (isLowContrast(color)) {
            drawCircle(xStart + iconSize / 2f,
                yStart + iconSize / 2f,
                iconSize / 2f - iconPadding,
                palette.iconPaintLC)
        }
    }

    private fun Canvas.drawProjectedShift(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        palette.projectedIconPaint.color = color
        drawRoundRect(xStart + iconPadding, yStart + iconPadding,
            xStart + iconSize - iconPadding, yStart + iconSize - iconPadding,
            2.dp.toFloat(), 2.dp.toFloat(), palette.projectedIconPaint)
        if (isLowContrast(color)) {
            drawRoundRect(xStart + iconPadding, yStart + iconPadding,
                xStart + iconSize - iconPadding, yStart + iconSize - iconPadding,
                2.dp.toFloat(), 2.dp.toFloat(), palette.iconPaintLC)
        }
    }

    private fun Canvas.drawSignup(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        checkDrawable?.setBounds(
            (xStart + (iconPadding / 2f)).toInt(),
            (yStart + (iconPadding / 2f)).toInt(),
            (xStart + iconSize - (iconPadding / 2f)).toInt(),
            (yStart + iconSize - (iconPadding / 2f)).toInt()
        )
        checkDrawable?.draw(this)
    }

    private fun Canvas.drawOpenShift(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        bidDrawable?.setBounds(
            (xStart + (iconPadding / 2f)).toInt(),
            (yStart + (iconPadding / 2f)).toInt(),
            (xStart + iconSize - (iconPadding / 2f)).toInt(),
            (yStart + iconSize - (iconPadding / 2f)).toInt()
        )
        bidDrawable?.draw(this)
    }

    private fun Canvas.drawBid(
        posX: Float, posY: Float, // the day position
        index: Float, // the icon slot index
        @ColorInt color: Int,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        openShiftDrawable?.setBounds(
            (xStart + (iconPadding / 2f)).toInt(),
            (yStart + (iconPadding / 2f)).toInt(),
            (xStart + iconSize - (iconPadding / 2f)).toInt(),
            (yStart + iconSize - (iconPadding / 2f)).toInt()
        )
        openShiftDrawable?.draw(this)
    }

    // Adds an outer stroke to invisible colors like white
    private fun isLowContrast(@ColorInt color: Int): Boolean {
        return colorContrast(color, Color.WHITE) < 1.25
    }

    private fun Canvas.drawAdditional(
        posX: Float, posY: Float, // the day position
        index: Float,
    ) {
        val xStart = iconXPos(posX, index)
        val yStart = posY + cellSize - iconSize - iconPadding

        drawLine(xStart + iconPadding, yStart + iconSize / 2f, xStart + iconSize - iconPadding,
            yStart + iconSize / 2f, palette.plusPaint)
        drawLine(xStart + iconSize / 2f, yStart + iconPadding, xStart + iconSize / 2f,
            yStart + iconSize - iconPadding, palette.plusPaint)
    }

    fun interface OnDateSelectedListener {
        fun onDateSelected(date: LocalDate)
    }

    fun interface OnVisibleRangeChangeListener {
        fun onRangeChange(startDate: LocalDate, endDate: LocalDate)
    }

    enum class DisplayMode {
        WEEK, MONTH
    }

    private enum class Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    companion object {
        const val DAYS = 42
        const val COLUMNS = 7
        const val WEEKS = 6

        private val iconPadding = 3.dp.toFloat()

        private const val ANIMATION_SCREEN_SET_DURATION_MILLIS = 700f
        private const val SNAP_VELOCITY_DIP_PER_SECOND = 400f

        private const val VELOCITY_UNIT_PIXELS_PER_SECOND = 1000
        private const val LAST_FLING_THRESHOLD_MILLIS = 300
    }
}
