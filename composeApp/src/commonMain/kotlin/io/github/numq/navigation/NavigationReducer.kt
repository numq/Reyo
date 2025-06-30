package io.github.numq.navigation

import io.github.numq.feature.Reducer

class NavigationReducer : Reducer<NavigationCommand, NavigationState, NavigationEvent> {
    override suspend fun reduce(state: NavigationState, command: NavigationCommand) = transition(state)
}