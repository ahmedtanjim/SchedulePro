package com.shiftboard.schedulepro.content.group.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import kotlinx.coroutines.flow.collect
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors()
) {
    val TrackWidth = 70.dp
    val ThumbDiameter = 30.dp
    val SwitchWidth = TrackWidth
    val SwitchHeight = ThumbDiameter
    val animationSpec = TweenSpec<Float>(durationMillis = 100)
    val thumbPathLength = TrackWidth - ThumbDiameter
    val minBound = 0f

    val DefaultSwitchPadding = 2.dp
    val maxBound = with(LocalDensity.current) { thumbPathLength.toPx() }
    val swipeableState = rememberSwipeableStateFor(checked, onCheckedChange ?: {}, animationSpec)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
        } else {
            Modifier
        }

    Box(
        modifier
            .then(toggleableModifier)
            .swipeable(
                state = swipeableState,
                anchors = mapOf(minBound to false, maxBound to true),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                enabled = enabled && onCheckedChange != null,
                reverseDirection = isRtl,
                interactionSource = interactionSource,
                resistance = null
            )
            .wrapContentSize(Alignment.Center)
            .padding(DefaultSwitchPadding)
            .requiredSize(
                SwitchWidth,
                SwitchHeight
            )
    ) {
        SwitchImpl(
            checked = checked,
            enabled = enabled,
            colors = colors,
            thumbValue = swipeableState.offset,
            interactionSource = interactionSource
        )
    }
}


@Composable
fun BoxScope.SwitchImpl(
    checked: Boolean,
    enabled: Boolean,
    colors: SwitchColors,
    thumbValue: State<Float>,
    interactionSource: InteractionSource
) {
    val ThumbDefaultElevation = 1.dp
    val ThumbPressedElevation = 6.dp
    val interactions = remember { mutableStateListOf<Interaction>() }
    71.dp

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    val hasInteraction = interactions.isNotEmpty()
    val elevation = if (hasInteraction) {
        ThumbPressedElevation
    } else {
        ThumbDefaultElevation
    }
    val trackColor = Color.LightGray
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(15.dp))
            .background(trackColor)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = R.drawable.people),
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(Color(0XFF35383D))
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.employees),
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(Color(0XFF35383D))
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
    val thumbColor = Color(0XFF265AA5)
    val elevationOverlay = LocalElevationOverlay.current
    val absoluteElevation = LocalAbsoluteElevation.current + elevation
    val resolvedThumbColor =
        if (thumbColor == MaterialTheme.colors.surface && elevationOverlay != null) {
            elevationOverlay.apply(thumbColor, absoluteElevation)
        } else {
            thumbColor
        }
    Box(modifier = Modifier
        .align(Alignment.CenterStart)
        .offset { IntOffset(thumbValue.value.roundToInt(), 0) }
        .indication(
            interactionSource = interactionSource,
            indication = rememberRipple(bounded = false, radius = 24.dp)
        )
        .requiredWidth(40.dp)
        .requiredHeight(30.dp)
        .shadow(elevation, RoundedCornerShape(15.dp), clip = false)
        .background(resolvedThumbColor, RoundedCornerShape(13.dp))
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = painterResource(id = if (checked) R.drawable.employees else R.drawable.people),
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun <T : Any> rememberSwipeableStateFor(
    value: T,
    onValueChange: (T) -> Unit,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
): SwipeableState<T> {
    val swipeableState = remember {
        SwipeableState(
            initialValue = value,
            animationSpec = animationSpec,
            confirmStateChange = { true }
        )
    }
    val forceAnimationCheck = remember { mutableStateOf(false) }
    LaunchedEffect(value, forceAnimationCheck.value) {
        if (value != swipeableState.currentValue) {
            swipeableState.animateTo(value)
        }
    }
    DisposableEffect(swipeableState.currentValue) {
        if (value != swipeableState.currentValue) {
            onValueChange(swipeableState.currentValue)
            forceAnimationCheck.value = !forceAnimationCheck.value
        }
        onDispose { }
    }
    return swipeableState
}