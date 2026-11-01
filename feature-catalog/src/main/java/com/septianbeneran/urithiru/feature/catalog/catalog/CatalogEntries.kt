package com.septianbeneran.urithiru.feature.catalog.catalog

import androidx.annotation.StringRes
import com.septianbeneran.urithiru.feature.catalog.R

data class CatalogEntry(
    val id: String,
    val name: String,
    @param:StringRes val description: Int
)

data class CatalogSection(
    @param:StringRes val title: Int,
    val entries: List<CatalogEntry>
)

object CatalogEntries {
    val sections = listOf(
        CatalogSection(
            title = R.string.catalog_section_actions,
            entries = listOf(
                CatalogEntry("button", "UriButton", R.string.catalog_entry_button),
                CatalogEntry("dialog", "UriDialog", R.string.catalog_entry_dialog),
                CatalogEntry("toast", "UriToast", R.string.catalog_entry_toast),
                CatalogEntry("banner", "UriBanner", R.string.catalog_entry_banner)
            )
        ),
        CatalogSection(
            title = R.string.catalog_section_inputs,
            entries = listOf(
                CatalogEntry("text_field", "UriTextField", R.string.catalog_entry_text_field),
                CatalogEntry("search_bar", "UriSearchBar", R.string.catalog_entry_search_bar),
                CatalogEntry("check_box", "UriCheckBox", R.string.catalog_entry_check_box),
                CatalogEntry("radio_button", "UriRadioButton", R.string.catalog_entry_radio_button),
                CatalogEntry("theme_picker", "UriThemePicker", R.string.catalog_entry_theme_picker)
            )
        ),
        CatalogSection(
            title = R.string.catalog_section_states,
            entries = listOf(
                CatalogEntry("loader", "UriLoader", R.string.catalog_entry_loader),
                CatalogEntry("progress_bar", "UriProgressBar", R.string.catalog_entry_progress_bar),
                CatalogEntry("skeleton", "UriSkeleton", R.string.catalog_entry_skeleton),
                CatalogEntry("error_state", "UriErrorState", R.string.catalog_entry_error_state),
                CatalogEntry("empty_state", "UriEmptyState", R.string.catalog_entry_empty_state),
                CatalogEntry("list_footer", "UriListFooter", R.string.catalog_entry_list_footer)
            )
        ),
        CatalogSection(
            title = R.string.catalog_section_content,
            entries = listOf(
                CatalogEntry("list_item", "UriListItem", R.string.catalog_entry_list_item),
                CatalogEntry("image", "UriImage", R.string.catalog_entry_image),
                CatalogEntry("media_placeholder", "UriMediaPlaceHolder", R.string.catalog_entry_media_placeholder),
                CatalogEntry("avatar", "UriAvatar", R.string.catalog_entry_avatar),
                CatalogEntry("badge", "UriBadge", R.string.catalog_entry_badge),
                CatalogEntry("dot", "UriDot", R.string.catalog_entry_dot),
                CatalogEntry("stepper", "UriStepper", R.string.catalog_entry_stepper),
                CatalogEntry("divider", "UriDivider", R.string.catalog_entry_divider)
            )
        ),
        CatalogSection(
            title = R.string.catalog_section_navigation,
            entries = listOf(
                CatalogEntry("top_app_bar", "UriTopAppBar", R.string.catalog_entry_top_app_bar)
            )
        )
    )

    fun find(id: String): CatalogEntry? = sections.flatMap { it.entries }.firstOrNull { it.id == id }
}
