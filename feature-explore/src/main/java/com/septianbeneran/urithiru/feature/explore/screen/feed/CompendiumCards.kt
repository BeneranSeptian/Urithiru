package com.septianbeneran.urithiru.feature.explore.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.entity.eldenring.Armor
import com.septianbeneran.urithiru.core.entity.eldenring.Boss
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumEntry
import com.septianbeneran.urithiru.core.entity.eldenring.CompendiumKind
import com.septianbeneran.urithiru.core.entity.eldenring.Item
import com.septianbeneran.urithiru.core.entity.eldenring.Location
import com.septianbeneran.urithiru.core.entity.eldenring.Npc
import com.septianbeneran.urithiru.core.entity.eldenring.Weapon
import com.septianbeneran.urithiru.core.ui.component.UriImage
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.util.extension.formatted

private val GearImageRatios = listOf(0.78f, 1f, 0.9f, 1.15f)
private val ArtImageRatios = listOf(0.72f, 0.82f)
private val LocationImageRatios = listOf(1f, 1.2f)
private val UniformImageRatio = listOf(1.1f)

internal val CompendiumKind.pluralLabel: String
    get() = when (this) {
        CompendiumKind.Bosses -> "Bosses"
        CompendiumKind.Weapons -> "Weapons"
        CompendiumKind.Armors -> "Armor"
        CompendiumKind.Items -> "Items"
        CompendiumKind.Locations -> "Locations"
        CompendiumKind.Npcs -> "NPCs"
    }

internal val CompendiumKind.singularLabel: String
    get() = when (this) {
        CompendiumKind.Bosses -> "Boss"
        CompendiumKind.Weapons -> "Weapon"
        CompendiumKind.Armors -> "Armor"
        CompendiumKind.Items -> "Item"
        CompendiumKind.Locations -> "Location"
        CompendiumKind.Npcs -> "NPC"
    }

internal val CompendiumKind.tagline: String
    get() = when (this) {
        CompendiumKind.Bosses -> "Every foe worth a rematch"
        CompendiumKind.Weapons -> "Arms for every build"
        CompendiumKind.Armors -> "Sets and pieces to wear"
        CompendiumKind.Items -> "Consumables, tools and keys"
        CompendiumKind.Locations -> "Places worth the journey"
        CompendiumKind.Npcs -> "Faces you'll meet on the road"
    }

@Composable
internal fun CompendiumFeedCard(
    modifier: Modifier = Modifier,
    entry: CompendiumEntry,
    showKind: Boolean,
    uniform: Boolean = false,
    onClick: () -> Unit
) {
    val ratios = when {
        uniform -> UniformImageRatio
        entry is Location -> LocationImageRatios
        entry is Boss -> ArtImageRatios
        else -> GearImageRatios
    }
    when (entry) {
        is Npc -> NpcCard(modifier, entry, showKind, uniform, onClick)
        is Location -> ImageCard(modifier, entry, ratios, showKind, subtitleLines = if (uniform) 1 else 3, onClick)
        is Boss, is Weapon, is Armor, is Item -> ImageCard(modifier, entry, ratios, showKind, subtitleLines = 1, onClick)
    }
}

@Composable
internal fun FeaturedEntryCard(
    modifier: Modifier = Modifier,
    entry: CompendiumEntry,
    onClick: () -> Unit
) {
    FeedCardContainer(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Tag(
                    text = entry.kind.singularLabel,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.name,
                    style = UrithiruTheme.typography.h3,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                entry.description?.let {
                    Text(
                        text = it,
                        style = UrithiruTheme.typography.bodyS,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                EntryTags(entry = entry)
            }
            EntryImage(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                entry = entry
            )
        }
    }
}

@Composable
private fun ImageCard(
    modifier: Modifier,
    entry: CompendiumEntry,
    ratios: List<Float>,
    showKind: Boolean,
    subtitleLines: Int,
    onClick: () -> Unit
) {
    FeedCardContainer(
        modifier = modifier,
        onClick = onClick
    ) {
        ImageBox(
            entry = entry,
            ratio = entry.pickRatio(ratios),
            showKind = showKind
        )
        CardBody(
            title = entry.name,
            subtitle = entry.subtitle(),
            entry = entry,
            subtitleLines = subtitleLines
        )
    }
}

@Composable
private fun NpcCard(
    modifier: Modifier,
    entry: Npc,
    showKind: Boolean,
    uniform: Boolean,
    onClick: () -> Unit
) {
    FeedCardContainer(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showKind) {
                KindTag(kind = entry.kind)
            }
            EntryImage(
                modifier = Modifier
                    .size(if (uniform) 72.dp else 88.dp)
                    .clip(CircleShape),
                entry = entry
            )
            Text(
                text = entry.name,
                style = UrithiruTheme.typography.h5,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            entry.role?.let {
                Text(
                    text = it,
                    style = UrithiruTheme.typography.captionM,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            entry.quote?.let {
                Text(
                    text = "“$it”",
                    style = UrithiruTheme.typography.bodyS,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    maxLines = if (uniform) 2 else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            entry.location?.takeUnless { uniform }?.let {
                Tag(
                    text = it,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun FeedCardContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = shape,
        colors = colors,
        content = content
    )
}

@Composable
private fun ImageBox(
    entry: CompendiumEntry,
    ratio: Float,
    showKind: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .padding(8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        EntryImage(
            modifier = Modifier
                .fillMaxSize()
                .then(if (entry.isArtwork) Modifier else Modifier.padding(16.dp)),
            entry = entry
        )
        if (showKind) {
            KindTag(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                kind = entry.kind
            )
        }
    }
}

@Composable
private fun CardBody(
    title: String,
    subtitle: String?,
    entry: CompendiumEntry,
    subtitleLines: Int = 1
) {
    Column(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = UrithiruTheme.typography.h5,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        subtitle?.let {
            Text(
                text = it,
                style = UrithiruTheme.typography.captionM,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = subtitleLines,
                overflow = TextOverflow.Ellipsis
            )
        }
        EntryTags(entry = entry)
    }
}

@Composable
internal fun EntryImage(
    modifier: Modifier = Modifier,
    entry: CompendiumEntry
) {
    if (entry.image == null) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.name.firstOrNull()?.uppercase().orEmpty(),
                style = UrithiruTheme.typography.h1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        UriImage(
            modifier = modifier,
            model = entry.image,
            contentDescription = entry.name,
            contentScale = if (entry.isArtwork) ContentScale.Crop else ContentScale.Fit
        )
    }
}

@Composable
private fun EntryTags(entry: CompendiumEntry) {
    val tags = entry.tags()
    if (tags.isEmpty()) return

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tags.forEach {
            Tag(
                text = it,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun KindTag(
    modifier: Modifier = Modifier,
    kind: CompendiumKind
) {
    Tag(
        modifier = modifier,
        text = kind.singularLabel,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
internal fun Tag(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Text(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        text = text,
        style = UrithiruTheme.typography.captionM,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

internal val CompendiumEntry.isArtwork: Boolean
    get() = this is Boss || this is Location || this is Npc

private fun CompendiumEntry.pickRatio(ratios: List<Float>): Float =
    ratios[Math.floorMod(id.hashCode(), ratios.size)]

private fun CompendiumEntry.subtitle(): String? = when (this) {
    is Weapon -> category
    is Armor -> category
    is Boss -> location ?: region
    is Item -> effect
    is Location -> description
    is Npc -> role
}

private fun CompendiumEntry.tags(): List<String> = when (this) {
    is Weapon -> listOfNotNull(
        attack.maxByOrNull { it.amount }?.let { "${it.name} ${it.amount}" },
        "${weight.formatted()} wt"
    )
    is Armor -> listOfNotNull(
        dmgNegation.maxByOrNull { it.amount }?.let { "${it.name} ${it.amount.formatted()}" },
        weight?.let { "${it.formatted()} wt" }
    )
    is Boss -> listOfNotNull(
        healthPoints?.let { "HP $it" },
        drops.size.takeIf { it > 0 }?.let { if (it == 1) "1 drop" else "$it drops" }
    )
    is Item -> listOfNotNull(type)
    is Location -> listOfNotNull(region)
    is Npc -> emptyList()
}
