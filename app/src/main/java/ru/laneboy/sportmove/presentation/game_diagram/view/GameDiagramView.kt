package ru.laneboy.sportmove.presentation.game_diagram.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import ru.laneboy.sportmove.R
import ru.laneboy.sportmove.domain.GameDiagram
import ru.laneboy.sportmove.util.dpToPx
import kotlin.math.pow

class GameDiagramView : ViewGroup {

    private var gameDiagram: GameDiagram? = null

    var onGameClick: ((GameDiagram) -> Unit)? = null

    private var nodeDeep = 0
        set(value) {
            bottomNodeCount = 2.0.pow(value).toInt()
            field = value
        }
    private var bottomNodeCount = 0


    private val frameWidth = dpToPx(150)
    private val frameHeight = dpToPx(80)
    private val frameDistanceX = dpToPx(100)
    private val frameDistanceY = dpToPx(50)

    private var diagramViews: DiagramViews? = null

    private val linePaint = Paint().apply {
        strokeWidth = dpToPx(5).toFloat()
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isDither = true
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        gameDiagram?.let {
            addDiagramsView(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (frameDistanceX * nodeDeep) + (frameWidth * (nodeDeep + 1))
        val height = (frameDistanceY * (bottomNodeCount - 1)) + (frameHeight * bottomNodeCount)
//        Log.d(
//            "MainLog", "Measure width: ${width}\n" +
//                    "Measure height: ${height}\n" +
//                    "Diagram deep: ${nodeDeep}\n" +
//                    "Diagram bottomNodeCount: ${bottomNodeCount}\n" +
//                    "Frame width: ${frameWidth}\n" +
//                    "Distance X: ${frameDistanceX}\n" +
//                    "Frame height: ${frameHeight}\n" +
//                    "Distance Y: ${frameDistanceY}"
//        )
        diagramViews?.let {
            diagramMeasure(it, width, height)
        }

        setMeasuredDimension(
            width,
            height
        )
    }

    private fun diagramMeasure(
        diagramViews: DiagramViews,
        width: Int,
        height: Int
    ) {
        if (diagramViews.linearLayout != null) {
            measureChild(diagramViews.linearLayout, width, height)
        }
        if (diagramViews.previousTopLinearLayout != null) {
            diagramMeasure(
                diagramViews.previousTopLinearLayout!!,
                width,
                height
            )
        }
        if (diagramViews.previousBottomLinearLayout != null) {
            diagramMeasure(
                diagramViews.previousBottomLinearLayout!!,
                width,
                height
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        diagramViews?.let {
            diagramLayout(
                it,
                measuredWidth,
                measuredHeight,
                listOf()
            )
        }
    }


    private fun diagramLayout(
        diagramViews: DiagramViews,
        width: Int,
        height: Int,
        nodePosition: List<NodePosition>
    ) {
        if (diagramViews.linearLayout != null) {
            val left =
                width - (frameWidth * (nodePosition.size + 1)) - (frameDistanceX * nodePosition.size)
            val right =
                width - (frameWidth * nodePosition.size) - (frameDistanceX * nodePosition.size)
            var cellY = height / 2.0
            nodePosition.forEachIndexed { index, position ->
                if (position == NodePosition.TOP) {
                    cellY -= height / 2.0.pow(index + 2)
                } else {
                    cellY += height / 2.0.pow(index + 2)
                }
            }
            val top = cellY - frameHeight / 2
            val bottom = cellY + frameHeight / 2
            diagramViews.linearLayout!!.layout(left, top.toInt(), right, bottom.toInt())
        }
        if (diagramViews.previousTopLinearLayout != null) {
            val newNodePosition = nodePosition.toMutableList().apply { add(NodePosition.TOP) }
            diagramLayout(
                diagramViews.previousTopLinearLayout!!, width, height, newNodePosition.toList()
            )
        }
        if (diagramViews.previousBottomLinearLayout != null) {
            val newNodePosition = nodePosition.toMutableList().apply { add(NodePosition.BOTTOM) }
            diagramLayout(
                diagramViews.previousBottomLinearLayout!!, width, height, newNodePosition.toList()
            )
        }
    }


    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas != null) {
            diagramViews?.let {
                diagramDraw(
                    canvas,
                    it,
                    measuredWidth,
                    measuredHeight,
                    listOf()
                )
            }
        }
        super.dispatchDraw(canvas)

    }


    private fun diagramDraw(
        canvas: Canvas,
        diagramViews: DiagramViews,
        width: Int,
        height: Int,
        nodePosition: List<NodePosition>
    ) {
        if (diagramViews.linearLayout != null && diagramViews.previousBottomLinearLayout != null && diagramViews.previousTopLinearLayout != null) {
            val startX =
                width - (frameWidth * (nodePosition.size + 1)) - (frameDistanceX * nodePosition.size)
            val endX =
                width - (frameWidth * (nodePosition.size + 1)) - (frameDistanceX * (nodePosition.size + 1))
            var startY = height / 2.0
            nodePosition.forEachIndexed { index, position ->
                if (position == NodePosition.TOP) {
                    startY -= height / 2.0.pow(index + 2)
                } else {
                    startY += height / 2.0.pow(index + 2)
                }
            }
            var topEndY = startY - height / 2.0.pow(nodePosition.size + 2)
            var bottomEndY = startY + height / 2.0.pow(nodePosition.size + 2)

            val pathTop = Path().apply {
                moveTo(startX.toFloat(), startY.toFloat())
                lineTo(startX - frameDistanceX / 2f, startY.toFloat())
                lineTo(startX - frameDistanceX / 2f, topEndY.toFloat())
                lineTo(endX.toFloat(), topEndY.toFloat())
            }
            val pathBottom = Path().apply {
                moveTo(startX.toFloat(), startY.toFloat())
                lineTo(startX - frameDistanceX / 2f, startY.toFloat())
                lineTo(startX - frameDistanceX / 2f, bottomEndY.toFloat())
                lineTo(endX.toFloat(), bottomEndY.toFloat())
            }
            canvas.drawPath(pathTop, linePaint)
            canvas.drawPath(pathBottom, linePaint)
        }
        if (diagramViews.previousTopLinearLayout != null) {
            val newNodePosition = nodePosition.toMutableList().apply { add(NodePosition.TOP) }
            diagramDraw(
                canvas,
                diagramViews.previousTopLinearLayout!!,
                width,
                height,
                newNodePosition.toList()
            )
        }
        if (diagramViews.previousBottomLinearLayout != null) {
            val newNodePosition = nodePosition.toMutableList().apply { add(NodePosition.BOTTOM) }
            diagramDraw(
                canvas,
                diagramViews.previousBottomLinearLayout!!,
                width,
                height,
                newNodePosition.toList()
            )
        }
    }

    fun setGameDiagram(gameDiagram: GameDiagram) {
        this.gameDiagram = gameDiagram
        nodeDeep = 0
        isRightNode = true
        addDiagramsView(gameDiagram)
    }

    fun updateGameDiagram(gameDiagram: GameDiagram) {
        this.gameDiagram = gameDiagram
        nodeDeep = 0
        isRightNode = true
        addDiagramsView(gameDiagram)
        requestLayout()
    }

    private fun addDiagramsView(gameDiagram: GameDiagram) {
        diagramViews = DiagramViews()
        createDiagramViews(gameDiagram, diagramViews!!)
    }

    private var isRightNode = false

    private fun createDiagramViews(gameDiagram: GameDiagram, diagramView: DiagramViews) {
        if (gameDiagram.gameData?.firstTeam != null && gameDiagram.gameData?.secondTeam != null) {

            diagramView.linearLayout = createGameView(gameDiagram)
        }
        if (gameDiagram.previousTopGame != null) {
            if (isRightNode)
                nodeDeep += 1
            val previousTopDiagramViews = DiagramViews()
            diagramView.previousTopLinearLayout = previousTopDiagramViews
            createDiagramViews(gameDiagram.previousTopGame!!, previousTopDiagramViews)
            isRightNode = false
        }
        if (gameDiagram.previousBottomGame != null) {
            val previousBottomDiagramViews = DiagramViews()
            diagramView.previousBottomLinearLayout = previousBottomDiagramViews
            createDiagramViews(gameDiagram.previousBottomGame!!, previousBottomDiagramViews)

        }
    }

    private fun createGameView(gameDiagram: GameDiagram): FrameLayout {
        val frameLayout = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundResource(R.drawable.bg_game)
        }
        val linearLayout = LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
            orientation = LinearLayout.VERTICAL
            setOnClickListener {
                onGameClick?.invoke(gameDiagram)
            }
            val firstTeamTextView = TextView(context).apply {
                text = gameDiagram.gameData!!.firstTeam
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(
                    frameWidth,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            }
            this.addView(firstTeamTextView)
            val x = TextView(context).apply {
                text = "X"
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END

                layoutParams = LinearLayout.LayoutParams(
                    frameWidth,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            }
            this.addView(x)
            val secondTeamTextView = TextView(context).apply {
                text = gameDiagram.gameData!!.secondTeam
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(
                    frameWidth,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                )
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            }
            this.addView(secondTeamTextView)
        }
        frameLayout.addView(linearLayout)
        addView(frameLayout)
        return frameLayout
    }


    data class DiagramViews(
        var linearLayout: FrameLayout? = null,
        var previousTopLinearLayout: DiagramViews? = null,
        var previousBottomLinearLayout: DiagramViews? = null
    )

    enum class NodePosition {
        TOP,
        BOTTOM
    }
}