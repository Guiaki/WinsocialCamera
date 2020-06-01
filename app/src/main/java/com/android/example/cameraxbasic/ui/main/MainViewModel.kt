package com.android.example.cameraxbasic.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.example.cameraxbasic.BuildConfig
import com.android.example.cameraxbasic.data.model.DetectedFaces
import com.android.example.cameraxbasic.data.remote.AgeGenderRequestBody
import com.android.example.cameraxbasic.data.service.ApiService
import com.android.example.cameraxbasic.utils.ViewModelEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainViewModel(private val apiService: ApiService): ViewModel() {
    var resultFaces = MutableLiveData<DetectedFaces?>()
    var logged = MutableLiveData<Boolean>()
    private val observableEvents = MutableLiveData<ViewModelEvent>()

    fun observeViewModelEvents(): LiveData<ViewModelEvent> = observableEvents

    protected fun postViewModelEvent(event: ViewModelEvent) {
        observableEvents.postValue(event)
    }

    private val CLIENT_ID = BuildConfig.IMGUR_ID;

    private fun getBase64Image(image: Bitmap, complete: (String) -> Unit) {
        GlobalScope.launch {
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val b = outputStream.toByteArray()
            complete(Base64.encodeToString(b, Base64.DEFAULT))
        }
    }

    fun uploadImageToImgur(imagePath: String) {
        Log.d("Guiaki", imagePath)
        val bitmapOrg = BitmapFactory.decodeFile(imagePath)
        val matrix = Matrix()
        matrix.postRotate(-90f)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.width, bitmapOrg.height, true)
        val image = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        getBase64Image(image, complete = { base64Image ->
            GlobalScope.launch(Dispatchers.Default) {
                val url = URL("https://api.imgur.com/3/image")

                val boundary = "Boundary-${System.currentTimeMillis()}"

                val httpsURLConnection =
                        withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                httpsURLConnection.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
                httpsURLConnection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=$boundary"
                )

                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = true

                var body = ""
                body += "--$boundary\r\n"
                body += "Content-Disposition:form-data; name=\"image\""
                body += "\r\n\r\n$base64Image\r\n"
                body += "--$boundary--\r\n"


                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                withContext(Dispatchers.IO) {
                    outputStreamWriter.write(body)
                    outputStreamWriter.flush()
                }

                val response = httpsURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                val jsonObject = JSONTokener(response).nextValue() as JSONObject
                val data = jsonObject.getJSONObject("data")

                val ImageLink = data.getString("link")
                Log.d("Guiaki", "Link is : ${ImageLink}")

                val evento = ViewModelEvent("imgur", ImageLink, false)
                postViewModelEvent(evento)
                calculateAgeAndGender(ImageLink)
            }
        })
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    fun calculateAgeAndGender(imageURL: String) {
        val request = AgeGenderRequestBody(imageURL)
        GlobalScope.launch (Dispatchers.Default) {
            val response = apiService.requestFace(request)
            if(response != null && response.detectedFaces.size > 0){
                val ageString = "${response.detectedFaces[0].age.ageRange.low} ~ ${response.detectedFaces[0].age.ageRange.high}"
                val ageEvento = ViewModelEvent("age", ageString, false)
                postViewModelEvent(ageEvento)
                var genero = ""
                if(response.detectedFaces[0].gender.gender.toLowerCase() == "male"){
                    genero = "Homem"
                }else{
                    genero = "Mulher"
                }
                val genderString = "${genero} (${response.detectedFaces[0].gender.probability.format(2)}%)"
                val genderEvento = ViewModelEvent("gender", genderString, false)
                postViewModelEvent(genderEvento)
            }else{
                val ageEvento = ViewModelEvent("age", "Não foi possivel obter dados.", false)
                postViewModelEvent(ageEvento)
            }
        }

    }
}