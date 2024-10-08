package dev.supergooey.pical.features.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.supergooey.pical.MealLog
import dev.supergooey.pical.R
import dev.supergooey.pical.ui.theme.DURATION_LONG
import dev.supergooey.pical.ui.theme.EmphasizedEasing
import dev.supergooey.pical.ui.theme.PicalTheme
import java.time.LocalDate

@PreviewLightDark
@Composable
private fun LogHistoryScreenPreview() {
  SharedTransitionPreviewHelper { sharedTransitionScope, animatedVisibilityScope ->
    PicalTheme {
      LogHistoryScreen(
        state = LogHistoryFeature.State(
          rows = listOf(
            HistoryRowState(
              dayDisplay = "Today",
              dayCalories = 2000,
              isToday = true,
              date = LocalDate.of(2024, 9, 13),
              logs = listOf(
                MealLog(id = 0, valid = true),
                MealLog(id = 1, valid = true),
                MealLog(id = 2, valid = true),
              )
            ),
            HistoryRowState(
              dayDisplay = "October 5, 2023",
              dayCalories = 2000,
              isToday = false,
              date = LocalDate.of(1993, 3, 13),
              logs = listOf(
                MealLog(id = 0, valid = true),
                MealLog(id = 1, valid = true),
                MealLog(id = 2, valid = true),
              )
            ),
            HistoryRowState(
              dayDisplay = "March 13, 1993",
              dayCalories = 2000,
              isToday = false,
              date = LocalDate.of(1993, 3, 13),
              logs = listOf(
                MealLog(id = 0, valid = true),
                MealLog(id = 1, valid = true),
                MealLog(id = 2, valid = true),
              )
            ),
            HistoryRowState(
              dayDisplay = "January 8, 1993",
              dayCalories = 2000,
              isToday = false,
              date = LocalDate.of(1993, 3, 13),
              logs = listOf(
                MealLog(id = 0, valid = true),
                MealLog(id = 1, valid = true),
                MealLog(id = 2, valid = true),
              )
            ),
          )
        ),
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        navigate = {}
      )
    }
  }
}

@Composable
fun SharedTransitionPreviewHelper(
  previewContent: @Composable (
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
  ) -> Unit
) {
  SharedTransitionLayout(modifier = Modifier.wrapContentSize()) {
    AnimatedContent(modifier = Modifier.wrapContentSize(), targetState = true) { state ->
      previewContent(
        this@SharedTransitionLayout,
        this@AnimatedContent
      )
    }
  }
}

@Composable
fun LogHistoryScreen(
  state: LogHistoryFeature.State,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  navigate: (LogHistoryFeature.Location) -> Unit
) {
  Scaffold(
    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
  ) { paddingValues ->
    Surface(
      modifier = Modifier.padding(paddingValues),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(items = state.rows) { row ->
          HistoryScreenRow(
            state = row,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
          ) {
            if (row.isToday) {
              navigate(LogHistoryFeature.Location.Back)
            } else {
              navigate(LogHistoryFeature.Location.Logs(row.date.toString()))
            }
          }
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun HistoryScreenRowPreview() {
  SharedTransitionPreviewHelper { sharedTransitionScope, animatedVisibilityScope ->
    PicalTheme {
      HistoryScreenRow(
        state = HistoryRowState(
          dayDisplay = "March 13, 1993",
          dayCalories = 2000,
          isToday = true,
          date = LocalDate.of(1993, 3, 13),
          logs = listOf(
            MealLog(id = 0, valid = true),
            MealLog(id = 1, valid = true),
            MealLog(id = 2, valid = true),
          )
        ),
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onClick = {}
      )
    }
  }
}

@Composable
fun HistoryScreenRow(
  modifier: Modifier = Modifier,
  state: HistoryRowState,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onClick: () -> Unit
) {
  with(sharedTransitionScope) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .heightIn(min = 180.dp)
        .sharedBounds(
          sharedContentState = rememberSharedContentState(state.date),
          animatedVisibilityScope = animatedVisibilityScope,
          resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
          boundsTransform = { _, _ ->
            tween(durationMillis = DURATION_LONG, easing = EmphasizedEasing)
//            spring(
//              stiffness = Spring.StiffnessLow,
//              dampingRatio = Spring.DampingRatioNoBouncy
//            )
          }
        )
        .clip(RoundedCornerShape(24.dp))
        .background(color = MaterialTheme.colorScheme.surface)
        .clickable(
          indication = ripple(color = MaterialTheme.colorScheme.primary),
          interactionSource = remember { MutableInteractionSource() }
        ) {
          onClick()
        }
        .padding(vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = state.dayDisplay,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp
      )
      if (state.logs.isNotEmpty()) {
        Row(
          horizontalArrangement = Arrangement.spacedBy((-32).dp)
        ) {
          state.logs.take(3).forEachIndexed { index, log ->
            val rotation = remember(index) { (-10..10).random().toFloat() }
            if (LocalInspectionMode.current) {
              Image(
                modifier = Modifier
                  .graphicsLayer {
                    rotationZ = rotation
                  }
                  .size(100.dp)
                  .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(R.drawable.bibimbap),
                contentScale = ContentScale.Crop,
                contentDescription = log.foodTitle,
              )
            } else {
              AsyncImage(
                modifier = Modifier
                  .sharedElement(
                    state = rememberSharedContentState(log.imageUri!!),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                      spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                      )
                    }
                  )
                  .graphicsLayer {
                    rotationZ = rotation
                  }
                  .size(100.dp)
                  .clip(RoundedCornerShape(8.dp)),
                model = log.imageUri,
                contentScale = ContentScale.Crop,
                contentDescription = log.foodTitle,
              )
            }
          }
        }
      }
    }
  }
}

