package br.com.carbuapp.menu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.carbuapp.auth.domain.AuthRepository
import br.com.carbuapp.auth.domain.model.User
import br.com.carbuapp.auth.data.local.UserDao
import br.com.carbuapp.auth.data.local.UserEntity
import br.com.carbuapp.core.data.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val userDao: UserDao,
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    val currentUser: StateFlow<User?> = userDao.observeCurrentUser()
        .map { it?.toDomain() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }

    fun trocarOficina(onTrocar: () -> Unit) {
        viewModelScope.launch {
            tokenDataStore.clearSelectedOficinaId()
            onTrocar()
        }
    }

    private fun UserEntity.toDomain() = User(
        id        = id,
        nome      = nome,
        email     = email,
        role      = role,
        oficinaId = oficinaId
    )
}
