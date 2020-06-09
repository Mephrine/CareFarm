package kr.smart.carefarm.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    @Throws(IOException::class)
    fun bitmapToFile(context: Context, filePath: String?): File {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(
            filePath,
            options
        ) // inJustDecodeBounds 설정을 해주지 않으면 이부분에서 큰 이미지가 들어올 경우 outofmemory Exception이 발생한다.
        var imageHeight = options.outHeight
        var imageWidth = options.outWidth
        val exifDegree = exifOrientationToDegrees(filePath)
        var samplesize = 1
        val resize = 400
        while (true) { //2번
            if (imageWidth / 2 < resize || imageHeight / 2 < resize) break
            imageWidth /= 2
            imageHeight /= 2
            samplesize *= 2
        }
        val bitmapResize =
            getResizeFileImage(filePath, samplesize, imageWidth, imageHeight)
        val bitmapRotate = rotate(bitmapResize, exifDegree.toFloat())
        //create a file to write bitmap data
        val nowTime = System.currentTimeMillis()
        val tempFile = File(context.cacheDir, "temp_$nowTime.jpg")
        tempFile.createNewFile()
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmapRotate.compress(Bitmap.CompressFormat.JPEG, 90 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()
        //write the bytes in file
        val fos = FileOutputStream(tempFile)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return tempFile
    }

    @Throws(IOException::class)
    fun bitmapToFile(
        context: Context,
        bit: Bitmap
    ): File { //create a file to write bitmap data
        val nowTime = System.currentTimeMillis()
        val tempFile = File(context.cacheDir, "temp_$nowTime.jpg")
        tempFile.createNewFile()
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bit.compress(Bitmap.CompressFormat.JPEG, 90 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()
        //write the bytes in file
        val fos = FileOutputStream(tempFile)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return tempFile
    }

    @Throws(IOException::class)
    fun imageToBitmap(context: Context, filePath: String?): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(
            filePath,
            options
        ) // inJustDecodeBounds 설정을 해주지 않으면 이부분에서 큰 이미지가 들어올 경우 outofmemory Exception이 발생한다.
        var imageHeight = options.outHeight
        var imageWidth = options.outWidth
        val exifDegree = exifOrientationToDegrees(filePath)
        var samplesize = 1
        val resize = 400
        while (true) { //2번
            if (imageWidth / 2 < resize || imageHeight / 2 < resize) break
            imageWidth /= 2
            imageHeight /= 2
            samplesize *= 2
        }
        val bitmapResize =
            getResizeFileImage(filePath, samplesize, imageWidth, imageHeight)
        val bitmapRotate = rotate(bitmapResize, exifDegree.toFloat())
        //create a file to write bitmap data
        val nowTime = System.currentTimeMillis()
        val tempFile = File(context.cacheDir, "temp_$nowTime.jpg")
        tempFile.createNewFile()
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmapRotate.compress(Bitmap.CompressFormat.JPEG, 90 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()
        //write the bytes in file
        val fos = FileOutputStream(tempFile)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return bitmapRotate
    }

    fun exifOrientationToDegrees(filePath: String?): Int {
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filePath)
        } catch (e: IOException) {
            Log.e("tag","sendPicture:::IOException")
        } catch (e: Exception) {
            Log.e("tag","sendPicture:::Exception")
        }
        val exifOrientation = exif!!.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    fun getResizeFileImage(file_route: String?, size: Int, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = size
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val src = BitmapFactory.decodeFile(file_route, options)
        return Bitmap.createScaledBitmap(src, width, height, true)
    }

    fun rotate(src: Bitmap, degree: Float): Bitmap { // Matrix 객체 생성
        val matrix = Matrix()
        // 회전 각도 셋팅
        matrix.postRotate(degree)
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(
            src, 0, 0, src.width,
            src.height, matrix, true
        )
    }

    fun getRealPathFromURI(
        context: Context,
        contentUri: Uri?
    ): String {
        var path = ""
        var column_index = 0
        try {
            val proj =
                arrayOf(MediaStore.Images.Media.DATA)
            contentUri?.let {
                val vCursor =
                    context.contentResolver.query(it, proj, null, null, null)
                vCursor?.let { cursor ->
                    if (cursor.moveToFirst()) {
                        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    }
                    path = cursor.getString(column_index)
                    cursor.close()
                }

            }
        } catch (e: Exception) {
            Log.e("tag",e.toString())
        }
        return path
    }

    fun getTotalfileSize(filePathList: List<String?>): Int {
        var fSize: Long = 0
        try {
            for (i in filePathList.indices) {
                val size = ""
                val filePath = filePathList[i] ?: continue
                val mFile = File(filePath)
                if (mFile.exists() == true) {
                    val fileSize = mFile.length()
                    fSize += fileSize
                } else {
                }
            }
        } catch (e: Exception) {
            Log.e("tag",e.toString())
        }
        return fSize.toInt()
    }
}