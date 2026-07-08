package io.iskopasi.dns_filter.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.api.PrefStoreApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Serializable
data class FilterData(
    val domain: String,
)

val String.toFilterList: List<FilterData>
    get() = if (this.isEmpty()) emptyList() else Json.decodeFromString(this)

val List<FilterData>.toJson: String
    get() = Json.encodeToString(this)

class DnsFilterComponent(
    context: ComponentContext
) : ComponentContext by context, KoinComponent {
    private val prefStore: PrefStoreApi by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _filterListFlow = MutableStateFlow<List<FilterData>>(emptyList())
    val filterListFlow: StateFlow<List<FilterData>> = _filterListFlow.asStateFlow()

    init {
        scope.launch {
            fetchDomainList()
        }
    }

    private fun fetchDomainList() {
        _filterListFlow.update { prefStore.filterList.toFilterList }
    }

    fun addDomain(domain: String) {
        scope.launch {
            val newList = prefStore.filterList.toFilterList + FilterData(domain)
            prefStore.filterList = newList.toJson
            fetchDomainList()
        }
    }

    fun onDeleteDomain(filterData: FilterData) {
        scope.launch {
            val newList = prefStore.filterList.toFilterList - filterData
            prefStore.filterList = newList.toJson
            fetchDomainList()
        }
    }
}
