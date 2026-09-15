package id.vanard.ayatqu.presentation.qibla

import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.QiblaDirection
import id.vanard.ayatqu.presentation.qibla.contract.QiblaEvent
import id.vanard.ayatqu.presentation.qibla.contract.QiblaSideEffect
import id.vanard.ayatqu.util.LocationHelper
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun QiblaRouter(
    modifier: Modifier = Modifier,
    viewModel: QiblaViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        viewModel.onEvent(QiblaEvent.InitialData(permissions.values.any { it }))
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.isLocationPermissionGranted(context)) {
            viewModel.onEvent(QiblaEvent.InitialData(fetchLocation = true))
        } else {
            permissionLauncher.launch(LocationHelper.getLocationPermissions())
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                QiblaSideEffect.NavigateBack -> navigationManager.navigate(QiblaDirection.back)
                QiblaSideEffect.RequestLocationPermission -> {
                    if (LocationHelper.isLocationPermissionGranted(context)) {
                        viewModel.onEvent(QiblaEvent.InitialData(fetchLocation = true))
                    } else {
                        permissionLauncher.launch(LocationHelper.getLocationPermissions())
                    }
                }
            }
        }
    }

    DisposableEffect(state.qibla?.latitude, state.qibla?.longitude) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        viewModel.onEvent(QiblaEvent.CompassAvailabilityChanged(rotationSensor != null))

        val listener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientation = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val magneticHeading = Math.toDegrees(orientation[0].toDouble()).toFloat()
                val declination = state.qibla?.let { qibla ->
                    GeomagneticField(
                        qibla.latitude.toFloat(),
                        qibla.longitude.toFloat(),
                        0f,
                        System.currentTimeMillis(),
                    ).declination
                } ?: 0f
                viewModel.onEvent(QiblaEvent.HeadingChanged(magneticHeading + declination))
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    QiblaScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
