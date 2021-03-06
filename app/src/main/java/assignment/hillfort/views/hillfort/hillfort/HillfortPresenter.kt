package assignment.hillfort.views.hillfort.hillfort

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.getIntent
import androidx.core.content.ContextCompat.startActivity
import assignment.hillfort.R
import assignment.hillfort.helpers.checkLocationPermissions
import assignment.hillfort.helpers.createDefaultLocationRequest
import assignment.hillfort.helpers.isPermissionGranted
import assignment.hillfort.helpers.showImagePicker
import assignment.hillfort.models.HillfortModel
import assignment.hillfort.models.Location
import assignment.hillfort.views.hillfort.base.*
import assignment.hillfort.views.hillfort.hillfortlist.HillfortListPresenter
import assignment.hillfort.views.hillfort.hillfortlist.HillfortListView
import assignment.hillfort.views.hillfort.login.LoginView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class HillfortPresenter(view: BaseView): BasePresenter(view) {

    var map: GoogleMap? = null
    var hillfort = HillfortModel()
    var defaultLocation = Location(52.245696, -7.139102, 15f)
    var edit = false;
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    val locationRequest = createDefaultLocationRequest()

    init {
        if (view.intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = view.intent.extras?.getParcelable<HillfortModel>("hillfort_edit")!!
            view.showHillfort(hillfort)
        } else {
            if (checkLocationPermissions(view)) {
                doSetCurrentLocation()
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            locationUpdate(Location(it.latitude, it.longitude))
        }
    }

    override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isPermissionGranted(requestCode, grantResults)) {
            doSetCurrentLocation()
        } else {
            locationUpdate(defaultLocation)
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        locationUpdate(hillfort.location)
    }

    fun locationUpdate(location : Location) {
        hillfort.location = location
        hillfort.location.zoom = 15f
        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        val options = MarkerOptions().title(hillfort.title).position(LatLng(hillfort.location.lat, hillfort.location.lng))
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(hillfort.location.lat, hillfort.location.lng), hillfort.location.zoom))
        view?.showLocation(hillfort.location)
    }


    @SuppressLint("MissingPermission")
    fun doResartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()
                    locationUpdate(Location(l.latitude, l.longitude))
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    fun doAddOrSave(title: String, description: String, additionalnotes : String,visited: Boolean , favourite: Boolean , rating: Float) {
        hillfort.title = title
        hillfort.description = description
        hillfort.notes = additionalnotes
        hillfort.visited = visited
        hillfort.favourite = favourite
        hillfort.rating = rating

        doAsync {
            if (edit) {
                app.hillforts.update(hillfort)
            } else {
                app.hillforts.create(hillfort)
            }
            uiThread {
                view?.finish()
            }
        }
    }

    fun doCancel() {
        view?.finish()
    }


    // For some reason when the visited checkbox is true the app needs me to logout to update the hillforts????
    fun doDelete() {
        doAsync {
            app.hillforts.delete(hillfort)
            uiThread {
                view?.finish()
            }
        }
    }


    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }
    fun doSelectImage1() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST1)
        }
    }
    fun doSelectImage2() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST2)
        }
    }
    fun doSelectImage3() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST3)
        }
    }

    fun doSetLocation() {
        view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(hillfort.location.lat, hillfort.location.lng, hillfort.location.zoom))
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                hillfort.image = data.data.toString()
                view?.showHillfort(hillfort)
            }
            IMAGE_REQUEST1 -> {
                hillfort.image1 = data.data.toString()
                view?.showHillfort(hillfort)
            }
            IMAGE_REQUEST2 -> {
                hillfort.image2 = data.data.toString()
                view?.showHillfort(hillfort)
            }
            IMAGE_REQUEST3 -> {
                hillfort.image3 = data.data.toString()
                view?.showHillfort(hillfort)
            }
            LOCATION_REQUEST -> {
                val location = data.extras?.getParcelable<Location>("location")!!
                hillfort.location = location
                locationUpdate(location)
            }
        }
    }
}