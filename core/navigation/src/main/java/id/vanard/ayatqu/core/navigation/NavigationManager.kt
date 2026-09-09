package id.vanard.ayatqu.core.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NavigationManager {
    val commands: SharedFlow<AppNavigationCommand>

    fun navigate(command: AppNavigationCommand)
}

class NavigationManagerImpl : NavigationManager {
    private val _commands = MutableSharedFlow<AppNavigationCommand>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val commands: SharedFlow<AppNavigationCommand> = _commands.asSharedFlow()

    override fun navigate(command: AppNavigationCommand) {
        _commands.tryEmit(command)
    }
}
