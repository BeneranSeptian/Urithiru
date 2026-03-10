package com.septianbeneran.urithiru.feature.b.screen.stateaction

import com.septianbeneran.urithiru.core.ui.base.BaseNonce
import com.septianbeneran.urithiru.feature.b.screen.PersonalizeExperienceScreenProperties.InterestItem

data class PersonalizeExperienceScreenState(
    val interestList: List<InterestItem> = listOf(
        InterestItem("Interest 1", true),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false),
        InterestItem("Interest 2", false)
    )
)

sealed interface PersonalizeExperienceScreenAction {
    data class OnClickInterest(
        val interest: InterestItem,
        val index: Int
    ): PersonalizeExperienceScreenAction
}

sealed interface PersonalizeExperienceScreenNonce: BaseNonce {
    object NavigateTo: PersonalizeExperienceScreenNonce
}