package com.maku.multipleimages

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE = 1

    lateinit var currentPhotoPath: String

    // Create ArrayList and add paths to the list
    val filePaths: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.button_first).setOnClickListener {
            //take photo
            dispatchTakePictureIntent()
        }

        Timber.d("filePaths " + filePaths)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Timber.d("Error occurred while creating the File. ")
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        MultipleImages.applicationContext(),
                        "com.maku.multipleimages.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }

                Timber.d("photo uri and path " + photoFile)
                if (photoFile != null) {
                    showInLinearView(photoFile)
                }
            }
        }

    }

    //save photot
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Timber.d("photo path " + currentPhotoPath)
        }
    }

    //show multiple photos in Linear layout
    private fun showInLinearView(currentPhotoPath: File) {
        Timber.d("photo path " + currentPhotoPath)
        // save the files in shared preferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("photoPath", currentPhotoPath.toString())
            commit()
        }

        //get valeus from shared preferences
        val path = sharedPref.getString("photoPath", null)
        Timber.d(" path " + path)

       //check if file exists
        if (path != null) {
            //add values to shared preferences
            filePaths.add(path)

            //check if arraylist is not empty
            Timber.d(" paths arraylist " + filePaths.size)

            //Show images by filePaths
            var imgFile: File
            for (i in 0 until filePaths.size) {
                imgFile = File(filePaths.get(i))
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    val myImage = ImageView(MultipleImages.applicationContext())
                    myImage.layoutParams = ViewGroup.LayoutParams(100, 80)
                    myImage.maxHeight = 50
                    myImage.maxWidth = 50

                    myImage.setImageBitmap(myBitmap) // In this step you've to create dynamic imageViews to see more than one picture
                    view?.findViewById<LinearLayout>(R.id.myLinearLayout)?.addView(myImage) //Then add your dynamic imageviews to your layout
                }
            }
        }

    }

}
