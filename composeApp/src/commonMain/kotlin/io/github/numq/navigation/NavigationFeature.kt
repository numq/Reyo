package io.github.numq.navigation

import io.github.numq.feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class NavigationFeature(reducer: NavigationReducer) : Feature<NavigationCommand, NavigationState, NavigationEvent>(
    initialState = NavigationState.Menu,
    coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    reducer = reducer
)