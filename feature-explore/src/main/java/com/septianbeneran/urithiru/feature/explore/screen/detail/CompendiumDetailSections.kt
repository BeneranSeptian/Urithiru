package com.septianbeneran.urithiru.feature.explore.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.entity.eldenring.Armor
import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.entity.eldenring.Location
import com.septianbeneran.urithiru.core.entity.eldenring.Npc
import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.feature.explore.R
import com.septianbeneran.urithiru.feature.explore.screen.feed.Tag
import com.septianbeneran.urithiru.core.util.extension.formatted
import com.septianbeneran.urithiru.feature.explore.screen.feed.singularLabel

private data class QuickStat(val value: String, val label: String)

private val ScalingGrades = listOf("S", "A", "B", "C", "D", "E")

private data class StatBar(val name: String, val amount: Float)

@Composable
internal fun DetailTitle(entry: CompendiumEntry) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Tag(
                text = entry.kind.singularLabel,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            entry.subtype()?.let {
                Tag(
                    text = it,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Text(
            text = entry.name,
            style = UrithiruTheme.typography.h1,
            color = MaterialTheme.colorScheme.onBackground
        )
        entry.whereabouts()?.let {
            Text(
                text = it,
                style = UrithiruTheme.typography.bodyM,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        val stats = entry.quickStats()
        if (stats.isNotEmpty()) {
            QuickStats(
                modifier = Modifier.padding(top = 6.dp),
                stats = stats
            )
        }
    }
}

@Composable
internal fun DetailSections(entry: CompendiumEntry) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        when (entry) {
            is Npc -> entry.quote?.let { QuoteSection(quote = it, speaker = entry.name) }
            else -> entry.description?.let { AboutSection(text = it) }
        }

        when (entry) {
            is Weapon -> {
                StatBarsSection(title = stringResource(R.string.explore_attack), bars = entry.attack.map { StatBar(it.name, it.amount.toFloat()) })
                StatBarsSection(title = stringResource(R.string.explore_guard), bars = entry.defence.map { StatBar(it.name, it.amount.toFloat()) })
                ChipsSection(
                    title = stringResource(R.string.explore_scaling),
                    chips = entry.scalesWith.mapNotNull { scaling ->
                        scaling.name?.let { name -> listOfNotNull(name, scaling.scaling).joinToString(" ") }
                    }
                )
                ChipsSection(
                    title = stringResource(R.string.explore_requirements),
                    chips = entry.requiredAttributes.map { "${it.name} ${it.amount}" }
                )
            }

            is Armor -> {
                StatBarsSection(title = stringResource(R.string.explore_damage_negation), bars = entry.dmgNegation.map { StatBar(it.name, it.amount) })
                StatBarsSection(title = stringResource(R.string.explore_resistance), bars = entry.resistance.map { StatBar(it.name, it.amount) })
            }

            is Boss -> ListSection(title = stringResource(R.string.explore_drops), rows = entry.drops)

            is Item -> entry.effect?.let { CalloutSection(title = stringResource(R.string.explore_effect), text = it) }

            is Location -> Unit

            is Npc -> InfoSection(rows = listOf(stringResource(R.string.explore_found_at) to entry.location))
        }
    }
}

@Composable
private fun QuickStats(
    modifier: Modifier = Modifier,
    stats: List<QuickStat>
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.value,
                    style = UrithiruTheme.typography.h4,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stat.label,
                    style = UrithiruTheme.typography.captionM,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AboutSection(text: String) {
    Section(title = stringResource(R.string.explore_about)) {
        Text(
            text = text,
            style = UrithiruTheme.typography.bodyM,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun QuoteSection(quote: String, speaker: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.explore_quote, quote),
                style = UrithiruTheme.typography.bodyL,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.explore_quote_speaker, speaker),
                style = UrithiruTheme.typography.captionM,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatBarsSection(title: String, bars: List<StatBar>) {
    if (bars.isEmpty()) return
    val max = bars.maxOf { it.amount }.coerceAtLeast(1f)

    Section(title = title) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            bars.forEach { bar ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.width(88.dp),
                        text = bar.name,
                        style = UrithiruTheme.typography.bodyS,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(bar.amount / max)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text(
                        modifier = Modifier.width(48.dp),
                        text = bar.amount.formatted(),
                        style = UrithiruTheme.typography.h5,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipsSection(title: String, chips: List<String>) {
    if (chips.isEmpty()) return

    Section(title = title) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach {
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    text = it,
                    style = UrithiruTheme.typography.h5,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ListSection(title: String, rows: List<String>) {
    if (rows.isEmpty()) return

    Section(title = title) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 8.dp)
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = row,
                        style = UrithiruTheme.typography.bodyM,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CalloutSection(title: String, text: String) {
    Section(title = title) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(20.dp),
            text = text,
            style = UrithiruTheme.typography.bodyL,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun InfoSection(rows: List<Pair<String, String?>>) {
    val known = rows.mapNotNull { (label, value) -> value?.let { label to it } }
    if (known.isEmpty()) return

    Section(title = stringResource(CoreUiR.string.common_details)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            known.forEach { (label, value) ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        modifier = Modifier.width(88.dp),
                        text = label,
                        style = UrithiruTheme.typography.bodyS,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = value,
                        style = UrithiruTheme.typography.bodyM,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = UrithiruTheme.typography.h4,
            color = MaterialTheme.colorScheme.onBackground
        )
        content()
    }
}

private fun CompendiumEntry.subtype(): String? = when (this) {
    is Weapon -> category
    is Armor -> category
    is Item -> type
    is Boss, is Location, is Npc -> null
}

private fun CompendiumEntry.whereabouts(): String? = when (this) {
    is Boss -> listOfNotNull(location, region).distinct().joinToString(" · ").ifEmpty { null }
    is Location -> region
    is Npc -> role
    is Weapon, is Armor, is Item -> null
}

@Composable
private fun CompendiumEntry.quickStats(): List<QuickStat> = when (this) {
    is Weapon -> listOfNotNull(
        attack.maxByOrNull { it.amount }?.let { QuickStat(it.amount.toString(), stringResource(R.string.explore_stat_attack, it.name)) },
        QuickStat(weight.formatted(), stringResource(R.string.explore_stat_weight)),
        scalesWith
            .filter { it.name != null && it.scaling in ScalingGrades }
            .minByOrNull { ScalingGrades.indexOf(it.scaling) }
            ?.let { QuickStat(it.scaling.orEmpty(), stringResource(R.string.explore_stat_scaling, it.name.orEmpty())) }
    )

    is Armor -> listOfNotNull(
        dmgNegation.maxByOrNull { it.amount }?.let { QuickStat(it.amount.formatted(), stringResource(R.string.explore_stat_negation, it.name)) },
        weight?.let { QuickStat(it.formatted(), stringResource(R.string.explore_stat_weight)) },
        resistance.maxByOrNull { it.amount }?.let { QuickStat(it.amount.formatted(), it.name) }
    )

    is Boss -> listOfNotNull(
        QuickStat(healthPoints ?: stringResource(R.string.explore_stat_unknown), stringResource(R.string.explore_stat_health)),
        QuickStat(drops.size.toString(), pluralStringResource(R.plurals.explore_stat_drops, drops.size))
    )

    is Item, is Location, is Npc -> emptyList()
}
