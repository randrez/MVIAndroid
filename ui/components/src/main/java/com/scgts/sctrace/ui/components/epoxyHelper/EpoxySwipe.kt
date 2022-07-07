package com.scgts.sctrace.ui.components.epoxyHelper

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.airbnb.epoxy.EpoxyViewHolder
import com.scgts.sctrace.root.components.R

inline fun <reified T : EpoxyModel<*>> attachEpoxySwipe(
    resources: Resources,
    epoxyController: EpoxyController,
    recyclerView: RecyclerView,
    noinline rightSwipeClick: ((T) -> Unit)? = null,
    noinline leftSwipeClick: ((T) -> Unit)? = null,
    @ColorRes rightSwipeColor: Int = R.color.red,
    @ColorRes leftSwipeColor: Int? = R.color.green,
    @DrawableRes rightSwipeIcon: Int = R.drawable.ic_scgts_delete,
    @DrawableRes leftSwipeIcon: Int? = R.drawable.ic_scgts_edit,
) = EpoxySwipe(
    resources,
    epoxyController,
    T::class.java,
    recyclerView,
    rightSwipeColor = rightSwipeColor,
    leftSwipeColor = leftSwipeColor,
    rightSwipeIcon = rightSwipeIcon,
    leftSwipeIcon = leftSwipeIcon,
    rightSwipeClick = rightSwipeClick,
    leftSwipeClick = leftSwipeClick
)

@Suppress("UNCHECKED_CAST")
@SuppressLint("ClickableViewAccessibility")
class EpoxySwipe<T : EpoxyModel<*>>(
    resources: Resources,
    epoxyController: EpoxyController,
    targetModelClass: Class<T>,
    recyclerView: RecyclerView,
    @ColorRes rightSwipeColor: Int,
    @ColorRes leftSwipeColor: Int?,
    @DrawableRes rightSwipeIcon: Int,
    @DrawableRes leftSwipeIcon: Int?,
    rightSwipeClick: ((T) -> Unit)?,
    leftSwipeClick: ((T) -> Unit)?
) : EpoxyModelTouchCallback<T>(epoxyController, targetModelClass) {
    private val rightSwipeBackground = ColorDrawable(resources.getColor(rightSwipeColor, null))
    private var leftSwipeBackground: ColorDrawable? =
        leftSwipeColor?.let { ColorDrawable(resources.getColor(it, null)) }
    private val rightSwipeIcon = ResourcesCompat.getDrawable(resources, rightSwipeIcon, null)
    private var leftSwipeIcon: Drawable? =
        leftSwipeIcon?.let { ResourcesCompat.getDrawable(resources, leftSwipeIcon, null) }
    private var currentSwipedViewHolder: EpoxyViewHolder? = null
    private var hasLiftedFinger = false
    private var dialogTag: Boolean = false

    init {
        // Reverts recycler item views if the user selects a different item
        View.OnTouchListener { _, event ->
            val viewHolder = currentSwipedViewHolder
            val touchPoint = Point(event.rawX.toInt(), event.rawY.toInt())
            val rect = Rect().also { currentSwipedViewHolder?.itemView?.getGlobalVisibleRect(it) }
            if (event.action == DOWN && hasLiftedFinger) {
                hasLiftedFinger = false

                if(dialogTag){
                    rect.bottom = rect.bottom + 100
                }

                if ((rect.top > touchPoint.y || rect.bottom < touchPoint.y)) {
                    viewHolder?.let {
                        epoxyController.notifyModelChanged(it.adapterPosition)
                    }
                } else if (rect.top < touchPoint.y && rect.bottom > touchPoint.y && viewHolder != null) {
                    if (touchPoint.x < rect.left) {
                        rightSwipeClick?.invoke(viewHolder.model as T)
                    } else if (touchPoint.x > rect.right) {
                        leftSwipeClick?.invoke(viewHolder.model as T)
                    }
                }
            }
            hasLiftedFinger = event.action == UP || hasLiftedFinger
            false
        }.also {
            recyclerView.setOnTouchListener(it)
        }
        ItemTouchHelper(this).attachToRecyclerView(recyclerView)
    }

    override fun getMovementFlagsForModel(model: T, adapterPosition: Int): Int =
        makeMovementFlags(0, RIGHT or LEFT)

    /**
     * OnChildDraw tasks itself with drawing the different colored backgrounds on swipe as well as
     * drawing any icons that need to be shown. The maxSwipeDistance prevents the view being swiped
     * any further than a fifth of it's width
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val maxSwipeDistance = viewHolder.itemView.right / 4
        val newDx = when {
            dX > 0 -> {
                if (dX > maxSwipeDistance) {
                    maxSwipeDistance
                } else dX
            }
            dX < 0 -> {
                if (leftSwipeBackground != null) {
                    if (dX < -maxSwipeDistance) {
                        -maxSwipeDistance
                    } else dX
                } else 0
            }
            else -> dX
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            newDx.toFloat(),
            dY,
            actionState,
            isCurrentlyActive
        )
        currentSwipedViewHolder = viewHolder
        viewHolder.itemView.let {
            handleIconDrawing(it, dX, rightSwipeIcon)
            handleIconDrawing(it, dX, leftSwipeIcon)
            when {
                dX > 0 -> {
                    rightSwipeBackground.setBounds(
                        it.left,
                        it.top,
                        it.left + newDx.toInt(),
                        it.bottom
                    )
                    leftSwipeBackground?.setBounds(0, 0, 0, 0)
                }
                dX < 0 -> {
                    leftSwipeBackground?.setBounds(
                        it.right + newDx.toInt(),
                        it.top,
                        it.right,
                        it.bottom
                    )
                    rightSwipeBackground.setBounds(0, 0, 0, 0)
                }
                else -> {
                    leftSwipeBackground?.setBounds(0, 0, 0, 0)
                    rightSwipeBackground.setBounds(0, 0, 0, 0)
                }
            }
            leftSwipeBackground?.draw(c)
            rightSwipeBackground.draw(c)
            rightSwipeIcon?.draw(c)
            leftSwipeIcon?.draw(c)
        }
    }

    private fun handleIconDrawing(
        itemView: View,
        dX: Float,
        icon: Drawable?,
    ) {
        val isRightSwipeIcon = icon == rightSwipeIcon
        if (icon != null) {
            val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + icon.intrinsicHeight
            when {
                dX > 0 -> {
                    val iconLeftSide = if (isRightSwipeIcon) {
                        itemView.left + iconMargin
                    } else {
                        0
                    }
                    val iconRightSide = if (isRightSwipeIcon) {
                        itemView.left + iconMargin + icon.intrinsicWidth
                    } else {
                        0
                    }
                    if (iconRightSide < dX) {
                        icon.setBounds(iconLeftSide, iconTop, iconRightSide, iconBottom)
                    } else {
                        icon.setBounds(0, 0, 0, 0)
                    }
                }
                dX < 0 -> {
                    val iconLeftSide = if (isRightSwipeIcon) {
                        0
                    } else {
                        itemView.right - iconMargin - icon.intrinsicWidth
                    }
                    val iconRightSide = if (isRightSwipeIcon) {
                        0
                    } else {
                        itemView.right - iconMargin
                    }
                    if (iconLeftSide - itemView.right > dX) {
                        icon.setBounds(iconLeftSide, iconTop, iconRightSide, iconBottom)
                    } else icon.setBounds(0, 0, 0, 0)
                }
                else -> icon.setBounds(0, 0, 0, 0)
            }
        }
    }

    fun disableLeftSwipe() {
        leftSwipeBackground = null
        leftSwipeIcon = null
    }

    fun setDialogTag(value: Boolean) {
        dialogTag = value
    }

    /**
     * We never want to fully swipe items off the recycler view so 1f prevents the view from
     * registering as siwped
     */
    override fun getSwipeThreshold(viewHolder: EpoxyViewHolder): Float = 1f
}