package dev.supergooey.pical.features.paywall

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith
import dev.supergooey.pical.ui.theme.PicalTheme

@Preview
@Composable
private fun PaywallScreenPreview() {
  PicalTheme {
    PaywallScreen(
      state = PaywallFeature.State()
    )
  }
}

@Composable
fun PaywallScreen(state: PaywallFeature.State) {
  val activity = (LocalContext.current) as Activity

  Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) { paddingValues ->
    Surface(
      modifier = Modifier.padding(paddingValues),
      color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      ) {
        // Header
        Column {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Unlock Pical Pro",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
          )
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Your gut will thank you",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )
        }

        // Options
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          state.options.forEach { option ->
            val title = when (option.packageType) {
              PackageType.MONTHLY -> "Monthly"
              else -> "Yearly"
            }
            val price = option.product.price.formatted
            PackageOptionRow(
              title = title,
              priceLabel = price,
              onClick = {
                Purchases.sharedInstance.purchaseWith(
                  purchaseParams = PurchaseParams(
                    builder = PurchaseParams.Builder(
                      activity = activity,
                      packageToPurchase = option
                    )
                  ),
                  onSuccess = { storeTransaction, customerInfo ->
                    Log.d("RevenueCat", "Success: ${customerInfo.entitlements}")
                  },
                  onError = { purchasesError, userCancelled ->
                    if (userCancelled) {
                      Log.d("RevenueCat", "Cancelled")
                    }
                    Log.e("RevenueCat", "Error: ${purchasesError.message}")
                  }
                )
              }
            )
          }
        }

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = "Recurring billing. Cancel any time.",
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

@Preview
@Composable
private fun PayOptionRowPreview() {
  PicalTheme {
    PackageOptionRow(
      title = "Monthly",
      priceLabel = "$2.99",
      onClick = {}
    )
  }
}

@Composable
private fun PackageOptionRow(
  title: String,
  priceLabel: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 80.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .clickable {
        onClick()
      }
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
      )
    }
    Spacer(modifier = Modifier.weight(1f))
    Box(
      modifier = Modifier
        .wrapContentSize()
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer)
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        text = priceLabel,
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
      )
    }
  }
}
