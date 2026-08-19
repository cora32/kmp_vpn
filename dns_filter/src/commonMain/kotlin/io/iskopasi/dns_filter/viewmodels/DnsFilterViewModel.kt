package io.iskopasi.dns_filter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.iskopasi.kmpvpntest.managers.Domain
import io.iskopasi.kmpvpntest.managers.FilterDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface IDnsFilterViewModel {
    val filterListFlow: StateFlow<List<Domain>>
    val hasError: StateFlow<Boolean>
}

class DnsFilterViewModel : IDnsFilterViewModel, ViewModel(), KoinComponent {

    private val dao: FilterDao by inject()

    private val _filterListFlow = MutableStateFlow<List<Domain>>(emptyList())
    override val filterListFlow: StateFlow<List<Domain>> = _filterListFlow.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    override val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchDomainList()
        }
    }

    private fun fetchDomainList() {
        viewModelScope.launch(Dispatchers.IO) {
            _filterListFlow.update { dao.getDomains() }
        }
    }

    fun addDomain(domain: String) {
        _hasError.update { false }

        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(domain)

            fetchDomainList()
        }
    }
}