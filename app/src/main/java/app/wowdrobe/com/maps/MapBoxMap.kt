package app.wowdrobe.com.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import app.wowdrobe.com.R
import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.zoom
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnScaleListener
import com.mapbox.maps.plugin.gestures.gestures

@Composable
fun MapBoxMap(
    currentLocation: Point,
    modifier: Modifier = Modifier,
    onPointChange: (Point) -> Unit,
    isClicked: MutableState<Boolean> = mutableStateOf(false),
    isReset: MutableState<Boolean> = mutableStateOf(false),
    points: List<MapItem>,
    currentPoint: MutableState<Point?>
) {

    val context = LocalContext.current

    var pointAnnotationManager: PointAnnotationManager? by remember {
        mutableStateOf(null)
    }



    AndroidView(
        factory = { context ->
            val cameraOptions = CameraOptions.Builder()
                .center(currentLocation)
                .zoom(15.0)
                .pitch(10.0)
                .bearing(1.0)
                .build()
            MapView(context).also { mapView ->
                mapView.getMapboxMap().loadStyle(
                    style(Style.TRAFFIC_NIGHT) {
                        val zoom = zoom()
                        Log.i("Zoommmmmmmm", "${zoom.literalValue}")
                        +terrain("terrain-enable")
                        +projection(ProjectionName.GLOBE)
//                        +atmosphere {
//                            color(rgb(220.0, 159.0, 159.0)) // Pink fog / lower atmosphere
//                            highColor(rgb(220.0, 159.0, 159.0)) // Blue sky / upper atmosphere
//                            horizonBlend(0.4) // Exaggerate atmosphere (default is .1)
//                        }
                        +rasterDemSource("raster-dem") {
                            url("mapbox://mapbox.terrain-rgb")
                        }
                        +terrain("raster-dem") {
                            exaggeration(1.5)
                        }
                    }
                )
                isScalingOut(mapView = mapView, onScaleOutClick = {
                    isClicked.value = false
                } ) {
                    isClicked.value = true
                }
                mapView.getMapboxMap().flyTo(
                    cameraOptions,
                    MapAnimationOptions.mapAnimationOptions {
                        duration(10000L)
                    }
                )


//                val annotationApi = mapView.annotations
//                pointAnnotationManager = annotationApi.createPointAnnotationManager()
//
//                mapView.getMapboxMap().addOnMapClickListener { p ->
//                    onPointChange(p)
//                    true
//                }
            }
        },
        update = { mapView ->
            isScalingOut(mapView = mapView, onScaleOutClick = {
                isClicked.value = false
            } ) {
                isClicked.value = true
            }
            val cameraOptions = if (isReset.value) CameraOptions.Builder()
                .center(currentLocation)
                .zoom(15.0)
                .pitch(0.0)
                .bearing(0.0)
                .build()
            else
                CameraOptions.Builder()
                    .center(currentPoint.value)
                    .zoom(19.5)
                    .build()
            mapView.annotations.cleanup()
            points.forEach { mapItem ->
                addAnnotationToMap(
                    context = context,
                    mapView = mapView,
                    point = mapItem.point,
                    icon = R.drawable.location
                )
            }
            if (isClicked.value) {
                mapView.getMapboxMap().flyTo(
                    cameraOptions,
                    MapAnimationOptions.mapAnimationOptions {
                        duration(5000L)
                    }
                )
            }
            if (isReset.value) {
                mapView.getMapboxMap().flyTo(
                    cameraOptions,
                    MapAnimationOptions.mapAnimationOptions {
                        duration(5000L)
                    }
                )
            }


//            if (point != null) {
//                pointAnnotationManager?.let {
//                    it.deleteAll()
//                    val pointAnnotationOptions = PointAnnotationOptions()
//                        .withPoint(point)
//                        .withIconImage(marker)
//
//                    it.create(pointAnnotationOptions)
//                    mapView.getMapboxMap()
//                        .flyTo(CameraOptions.Builder().zoom(16.0).center(point).build())
//                }
//            }
//            NoOpUpdate
        },
        modifier = modifier
    )
}

private fun addAnnotationToMap(
    icon: Int,
    context: Context,
    mapView: MapView,
    point: Point
) {
    bitmapFromDrawableRes(
        context,
        icon
    )?.let {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(it)
        pointAnnotationManager.create(pointAnnotationOptions)
    }
}

private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
    convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
    if (sourceDrawable == null) {
        return null
    }
    return if (sourceDrawable is BitmapDrawable) {
        sourceDrawable.bitmap
    } else {
        val constantState = sourceDrawable.constantState ?: return null
        val drawable = constantState.newDrawable().mutate()
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    }
}

fun isScalingOut(
    mapView: MapView,
    onScaleOutClick: () -> Unit = {},
    onScaleInClick: () -> Unit = {}
) {
    mapView.gestures.addOnScaleListener(object : OnScaleListener {
        override fun onScale(detector: StandardScaleGestureDetector) {
            if (detector.isScalingOut) {
                onScaleOutClick()
            } else {
                onScaleInClick()
            }
        }

        override fun onScaleBegin(detector: StandardScaleGestureDetector) {
            if (detector.isScalingOut) {
                onScaleOutClick()
            } else {
                onScaleInClick()
            }
        }

        override fun onScaleEnd(detector: StandardScaleGestureDetector) {
            if (detector.isScalingOut) {
                onScaleOutClick()
            } else {
                onScaleInClick()
            }
        }

    })
}

