package io.iskopasi.dns_filter.decompose

import com.arkivanov.decompose.ComponentContext
import io.iskopasi.kmpvpntest.managers.Domain
import io.iskopasi.kmpvpntest.managers.FilterDao
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
    private val dao: FilterDao by inject()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _filterListFlow = MutableStateFlow<List<Domain>>(emptyList())
    val filterListFlow: StateFlow<List<Domain>> = _filterListFlow.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    init {
        scope.launch {
            fetchDomainList()
        }
    }

    private fun fetchDomainList() {
        scope.launch {
            _filterListFlow.update { dao.getDomains() }
        }
    }

    fun addDomain(domain: String) {
        _hasError.update { false }

        scope.launch {
            val trimmed = Regex("^(?:https?://)?([^/]+).*$")
                .find(domain.trim())?.groupValues?.get(1) ?: domain.trim()

            if (trimmed.isBlank()) {
                _hasError.update { true }
                return@launch
            }

            dao.insert(Domain(domain = trimmed))

            fetchDomainList()
        }
    }

    fun onDeleteDomain(domain: Domain) {
        scope.launch {
            dao.delete(domain)

            fetchDomainList()
        }
    }
}
