package io.github.numq.navigation

sealed interface NavigationState {
    data object Menu : NavigationState
}