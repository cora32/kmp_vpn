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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class DnsFilterComponent(
    context: ComponentContext
) : ComponentContext by context, KoinComponent {
    private val prefStore: PrefStoreApi by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _filterListFlow = MutableStateFlow<Set<String>>(emptySet())
    val filterListFlow: StateFlow<Set<String>> = _filterListFlow.asStateFlow()

    init {
        scope.launch {
            fetchDomainList()
        }
    }

    private fun fetchDomainList() {
        _filterListFlow.update { prefStore.filterList }
    }

    fun addDomain(domain: String) {
        scope.launch {
            val trimmed = Regex("^(?:https?://)?([^/]+).*$")
                .find(domain.trim())?.groupValues?.get(1) ?: domain.trim()

            prefStore.filterList += trimmed
            fetchDomainList()
        }
    }

    fun onDeleteDomain(domain: String) {
        scope.launch {
            prefStore.filterList -= domain
            fetchDomainList()
        }
    }
}
