package dev.supergooey.pical.features.paywall

import android.util.Log
import androidx.lifecycle.ViewModel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.Period
import com.revenuecat.purchases.purchaseWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PackageOptionState(
  val id: String,
  val title: String,
  val priceFormatted: String,
)

interface PaywallFeature {
  data class State(
    val options: List<Package> = emptyList()
  )

  sealed interface Action {
    data class SelectOption(
      val option: Package
    ): Action
  }
}

class PaywallViewModel : ViewModel() {
  private val internalState = MutableStateFlow(PaywallFeature.State())
  val state = internalState.asStateFlow()

  init {
    Purchases.sharedInstance.getOfferingsWith(
      onError = { error: PurchasesError ->
        Log.e("RevenueCat Error", "$error")
      },
      onSuccess = { offerings ->
        offerings.current?.let { currentOffering ->
          Log.d("RevenueCat Success", "$currentOffering")
          internalState.update { previousState ->
            previousState.copy(
              options = currentOffering.availablePackages
            )
          }
        }
      }
    )
  }

  fun actions(action: PaywallFeature.Action) {
    when (action) {
      is PaywallFeature.Action.SelectOption -> {

      }
    }
  }
}

fun Package.toPackageOptionState(): PackageOptionState {
  val title = if (product.period?.unit == Period.Unit.MONTH) {
    "Monthly"
  } else {
    "Annual"
  }
  return PackageOptionState(
    id = identifier,
    title = title,
    priceFormatted = product.price.formatted
  )
}

