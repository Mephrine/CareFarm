package kr.smart.carefarm.utils

//class ProgressRequestBody(private val mFile: File) : RequestBody() {
//
//    private final val UPLOAD_PROGRESS = "PROGRESS"
//    override fun contentType(): MediaType? {
//        return MediaType.parse("multipart/form-data")
//    }
//
//    override fun writeTo(sink: BufferedSink) {
//        val fileLength = mFile.length()
//        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//        val inputStream = FileInputStream(mFile)
//        var uploaded = 0L
//
//        try {
//            var read: Int
//            var num = 0
//            while (true) {
//                read = inputStream.read(buffer)
//                if (read == -1) break
//                val progress = (100 * uploaded / fileLength).toInt()
//                if (progress > num + 1) {
//// update progress on UI thread
//                    val intent = Intent(UPLOAD_PROGRESS)
//                    intent.putExtra(UPLOAD_PROGRESS, progress)
//                    num = progress
//                    LocalBroadcastManager.getInstance(GlobalApplication.getContext()).sendBroadcast(intent)
//                    Log.d("upload progress=$progress")
//                }
//
//                uploaded += read.toLong()
//                sink.write(buffer, 0, read)
//            }
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            inputStream.close()
//        }
//
//    }
//
//}