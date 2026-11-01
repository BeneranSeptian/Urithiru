package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

/**
 * Loads an image from [model] (usually a URL) and shows [UriMediaPlaceHolder] while it loads or if it fails.
 * Size it with [modifier]. Set [useCache] to false for images that change on the server, like onboarding media.
 */
@Composable
fun UriImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    crossfade: Boolean = true,
    useCache: Boolean = true
) {
    val context = LocalPlatformContext.current
    val request = remember(context, model, crossfade, useCache) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(crossfade)
            .apply {
                if (!useCache) {
                    memoryCachePolicy(CachePolicy.DISABLED)
                    diskCachePolicy(CachePolicy.DISABLED)
                }
            }
            .build()
    }
    var isLoaded by remember(request) { mutableStateOf(false) }

    Box(modifier = modifier.clip(shape)) {
        AsyncImage(
            modifier = Modifier.matchParentSize(),
            model = request,
            contentDescription = contentDescription,
            contentScale = contentScale,
            onState = { isLoaded = it is AsyncImagePainter.State.Success }
        )
        if (!isLoaded) {
            UriMediaPlaceHolder(modifier = Modifier.matchParentSize())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriImagePrev() {
    UrithiruTheme {
        UriImage(
            modifier = Modifier.size(120.dp),
            model = null,
            contentDescription = null,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
