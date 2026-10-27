package com.septianbeneran.urithiru.feature.catalog.catalog

data class CatalogEntry(
    val id: String,
    val name: String,
    val description: String
)

data class CatalogSection(
    val title: String,
    val entries: List<CatalogEntry>
)

object CatalogEntries {
    val sections = listOf(
        CatalogSection(
            title = "Actions",
            entries = listOf(
                CatalogEntry("button", "UriButton", "Primary, secondary and tertiary buttons, with optional icons."),
                CatalogEntry("dialog", "UriDialog", "Alert and confirmation dialogs."),
                CatalogEntry("toast", "UriToast", "Short success, info, warning and error messages."),
                CatalogEntry("banner", "UriBanner", "A highlighted message with a call to action.")
            )
        ),
        CatalogSection(
            title = "Inputs",
            entries = listOf(
                CatalogEntry("text_field", "UriTextField", "Labelled form field with error text and a password toggle."),
                CatalogEntry("search_bar", "UriSearchBar", "Search field with a clear button and keyboard search."),
                CatalogEntry("check_box", "UriCheckBox", "Checkboxes in three sizes."),
                CatalogEntry("radio_button", "UriRadioButton", "Single choice from a group.")
            )
        ),
        CatalogSection(
            title = "Loading and states",
            entries = listOf(
                CatalogEntry("loader", "UriLoader", "Spinner, and the full-screen CentralLoadingDialog."),
                CatalogEntry("progress_bar", "UriProgressBar", "Determinate progress."),
                CatalogEntry("skeleton", "UriSkeleton", "Shimmering placeholders shown while content loads."),
                CatalogEntry("error_state", "UriErrorState", "Full-screen failure with a message for each AppError."),
                CatalogEntry("empty_state", "UriEmptyState", "Shown when a request succeeds but returns nothing."),
                CatalogEntry("list_footer", "UriListFooter", "Loading or Retry row at the end of a paginated list.")
            )
        ),
        CatalogSection(
            title = "Content",
            entries = listOf(
                CatalogEntry("list_item", "UriListItem", "Row with a title, description and optional leading or trailing content."),
                CatalogEntry("image", "UriImage", "Network image with a placeholder while loading or on error."),
                CatalogEntry("media_placeholder", "UriMediaPlaceHolder", "Placeholder for missing images or video."),
                CatalogEntry("avatar", "UriAvatar", "User avatar in three sizes."),
                CatalogEntry("badge", "UriBadge", "Number, icon and dot badges."),
                CatalogEntry("dot", "UriDot", "Small indicator, e.g. for pager progress."),
                CatalogEntry("stepper", "UriStepper", "Numbered steps in a flow."),
                CatalogEntry("divider", "UriDivider", "Thin separator line.")
            )
        ),
        CatalogSection(
            title = "Navigation",
            entries = listOf(
                CatalogEntry("top_app_bar", "UriTopAppBar", "Screen title bar with an optional back button and actions.")
            )
        )
    )

    fun find(id: String): CatalogEntry? = sections.flatMap { it.entries }.firstOrNull { it.id == id }
}
