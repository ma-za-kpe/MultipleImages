package com.maku.multipleimages

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            //take photo
//            dispatchTakePictureIntent()
//        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity!!.applicationContext.packageManager) != null) {
            startActivityForResult(
                takePictureIntent,
                REQUEST_IMAGE_CAPTURE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!["data"] as Bitmap?
            saveIamge(imageBitmap)
            view!!.findViewById<Button>(R.id.button_first).text = "Take another"
//            imageView.setImageBitmap(imageBitmap)

        }
    }

    private fun saveIamge(imgBitmap: Bitmap?) {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val myDir = File("$root/mutliMe")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)

        Timber.d("file 1 %s", file)

        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)

            Timber.d("file 2 %s", file)

            imgBitmap?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

            //add file paths to ArrayList
            val filePaths: ArrayList<String> = ArrayList() //Create ArrayList
            filePaths.add(file.toString())

            Timber.d("arraylist: %s", filePaths.size)

            var imgFile: File
            for (i in 0 until filePaths.size) {
                imgFile = File(filePaths.get(i))
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                    val myImage = ImageView(activity)
                    myImage.setImageBitmap(myBitmap) // In this step you've to create dynamic imageViews to see more than one picture
//                    view!!.findViewById<LinearLayout>(R.id.myLinearLayout).addView(myImage) //Then add your dynamic imageviews to your layout
                }
            }
        } catch (e: Exception) {
            Timber.d("exception %s", e.message)
            e.printStackTrace()
        }
    }

}
