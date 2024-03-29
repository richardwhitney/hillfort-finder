package org.wit.hillfortfinder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfortfinder.R
import org.wit.hillfortfinder.helpers.readImage
import org.wit.hillfortfinder.helpers.readImageFromPath
import org.wit.hillfortfinder.helpers.showImagePicker
import org.wit.hillfortfinder.main.MainApp
import org.wit.hillfortfinder.models.HillfortModel
import org.wit.hillfortfinder.models.Location
import java.text.SimpleDateFormat
import java.util.*

class HillfortActivity : AppCompatActivity(), AnkoLogger {

    var hillfort = HillfortModel()
    lateinit var app: MainApp
    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        app = application as MainApp

        if (intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = intent.extras?.getParcelable<HillfortModel>("hillfort_edit")!!
            hillfortTitle.setText(hillfort.title)
            hillfortDescription.setText(hillfort.description)
            hillfortVisited.isChecked = hillfort.visited
            dateVisited.setText(hillfort.dateVisited)
            addtionalNotes.setText(hillfort.additionalNotes)
            btnAdd.setText(R.string.save_hillfort)
            if (hillfort.images.size > 0) {
                for (image in hillfort.images) {
                    val imageView: ImageView = ImageView(this)
                    var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        600,
                        600
                    )
                    params.setMargins(0, 0, 10, 0)
                    imageView.layoutParams = params
                    imageView.setImageBitmap(readImageFromPath(this, image))
                    imageGallery.addView(imageView)
                }
                if (hillfort.images[0] != null) {
                    chooseImage.setText(R.string.change_hillfort_image)
                }
            }
        }
        else {
            edit = false
        }

        btnAdd.setOnClickListener {
            hillfort.title = hillfortTitle.text.toString()
            hillfort.description = hillfortDescription.text.toString()
            hillfort.additionalNotes = addtionalNotes.text.toString()
            hillfort.dateVisited = dateVisited.text.toString()
            hillfort.userId = app.currentUser?.id!!
            if (hillfort.title.isEmpty()) {
                toast(R.string.enter_hillfort_title)
            }
            else {
                if (edit) {
                    app.hillforts.update(hillfort.copy())
                }
                else {
                    app.hillforts.create(hillfort.copy())
                }
            }
            info("Add Button Pressed: $hillfortTitle")
            setResult(AppCompatActivity.RESULT_OK)
            finish()
        }

        hillfortVisited.setOnClickListener {
            hillfort.visited = hillfortVisited.isChecked
            if(hillfort.visited) {
                val currentDate: Date = Calendar.getInstance().time
                val dateFormat: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate: String = dateFormat.format(currentDate)
                dateVisited.setText(formattedDate)
            }
            else {
                dateVisited.setText("")
            }
        }

        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        hillfortLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (hillfort.zoom != 0f) {
                location.lat = hillfort.lat
                location.lng = hillfort.lng
                location.zoom = hillfort.zoom
            }
            startActivityForResult(intentFor<MapActivity>().putExtra("location", location), LOCATION_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_hillfort, menu)
        var deleteItem: MenuItem = menu?.findItem(R.id.item_delete)!!
        deleteItem.isVisible = edit
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_delete -> {
                app.hillforts.delete(hillfort)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    hillfort.images.add(data.getData().toString())
                    hillfortImage.setImageBitmap(readImage(this, resultCode, data))
                    chooseImage.setText(R.string.change_hillfort_image)
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("location")!!
                    hillfort.lat = location.lat
                    hillfort.lng = location.lng
                    hillfort.zoom = location.zoom
                }
            }
        }
    }

}
