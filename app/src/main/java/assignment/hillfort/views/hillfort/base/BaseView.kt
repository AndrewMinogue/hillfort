package assignment.hillfort.views.hillfort.base

import android.content.Intent

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import assignment.hillfort.views.hillfort.map.HillfortMapView
import assignment.hillfort.models.HillfortModel
import assignment.hillfort.models.Location
import assignment.hillfort.views.hillfort.editlocation.EditLocationView
import assignment.hillfort.views.hillfort.hillfort.HillfortView
import assignment.hillfort.views.hillfort.hillfortlist.HillfortListView
import assignment.hillfort.views.hillfort.login.LoginView
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.AnkoLogger


val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2
val IMAGE_REQUEST1 = 3
val IMAGE_REQUEST2 = 4
val IMAGE_REQUEST3 = 5

enum class VIEW {
    LOCATION, HILLFORT, MAPS, LIST, LOGIN
}

open abstract class BaseView : AppCompatActivity(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, HillfortListView::class.java)
        when (view) {
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.HILLFORT -> intent = Intent(this, HillfortView::class.java)
            VIEW.MAPS -> intent = Intent(this, HillfortMapView::class.java)
            VIEW.LIST -> intent = Intent(this, HillfortListView::class.java)
            VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
        }
        if (key != "") {
            intent.putExtra(key, value)
        }
        startActivityForResult(intent, code)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        basePresenter = presenter
        return presenter
    }

    fun init(toolbar: Toolbar, upEnabled: Boolean) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            toolbar.title = "${title}: ${user.email}"
        }
    }


        override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun showHillfort(hillfort: HillfortModel) {}
    open fun showHillforts(hillfort: List<HillfortModel>) {}
    open fun showLocation(location : Location) {}
    open fun showProgress() {}
    open fun hideProgress() {}
}