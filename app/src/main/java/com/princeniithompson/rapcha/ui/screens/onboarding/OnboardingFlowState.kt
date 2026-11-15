package com.princeniithompson.rapcha.ui.screens.onboarding

/** v25 lean: the voice sandbox and apps showcase steps are gone. */
enum class OnboardingStage(val progressIndex: Int, val showsProgressBar: Boolean) {
    WELCOME_HERO(progressIndex = -1, showsProgressBar = false),
    GOOGLE_SIGN_IN(progressIndex = -1, showsProgressBar = false),
    GOOGLE_AUTH_HANDOFF(progressIndex = -1, showsProgressBar = false),
    INTRO(progressIndex = 0, showsProgressBar = true),
    HOW_IT_WORKS(progressIndex = 1, showsProgressBar = true),
    OVERLAY_PERMISSION(progressIndex = 2, showsProgressBar = true),
    KEEP_AWAKE(progressIndex = 3, showsProgressBar = true),
    ACCESSIBILITY_GUIDE(progressIndex = 4, showsProgressBar = true),
    NOTIFICATION_PERMISSION(progressIndex = 5, showsProgressBar = true),
    ALL_SET(progressIndex = 5, showsProgressBar = true)
}
