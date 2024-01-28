package com.rahim.bookhub.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rahim.bookhub.R
import com.rahim.bookhub.database.BookDatabase
import com.rahim.bookhub.database.BookEntity
import com.rahim.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName : TextView
    lateinit var txtBookAuthor : TextView
    lateinit var txtBookPrice : TextView
    lateinit var txtBookRating : TextView
    lateinit var txtBookImage : ImageView
    lateinit var txtBookDesc : TextView
    lateinit var btnAddToFav : TextView
    lateinit var progressBar : ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar


    var bookId :String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

       if(intent!=null)
       {
           bookId = intent.getStringExtra("book_id")
       }
       else
       {
           finish()
           Toast.makeText(this@DescriptionActivity, "Unexpected error", Toast.LENGTH_SHORT).show()
       }

        if(bookId=="100")
        {
            finish()
            Toast.makeText(this@DescriptionActivity, "Unexpected error", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookId)

        if( ConnectionManager().checkConnectivity(this@DescriptionActivity))
        {
            val jsonObjectRequest = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            object: JsonObjectRequest(
                Method.POST,url,jsonParams,
                Response.Listener{
                    try{
                        val success = it.getBoolean("success")
                        if ( success )
                        {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")
                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image")).into(txtBookImage)

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav = checkFav.get()

                            if ( isFav )
                            {
                                btnAddToFav.text = getString(R.string.Remove_From_Favourites)
                                btnAddToFav.backgroundTintList =
                                    ContextCompat.getColorStateList(applicationContext, R.color.colorFavourite)
                            }
                            else
                            {
                                btnAddToFav.text = getString(R.string.Add_to_Favourites)
                                btnAddToFav.backgroundTintList =
                                    ContextCompat.getColorStateList(applicationContext, R.color.colorPrimary)
                            }

                            btnAddToFav.setOnClickListener {

                                if ( !DBAsyncTask(applicationContext,bookEntity,1).execute().get() ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result)
                                    {
                                        Toast.makeText(this@DescriptionActivity, "Book added to favourites", Toast.LENGTH_SHORT).show()
                                        btnAddToFav.text= getString(R.string.Remove_From_Favourites)
                                        btnAddToFav.backgroundTintList =
                                            ContextCompat.getColorStateList(applicationContext, R.color.colorFavourite)


                                    }
                                    else
                                    {
                                        Toast.makeText(this@DescriptionActivity, "Some error Occurred", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else
                                {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if ( result)
                                    {
                                        Toast.makeText(this@DescriptionActivity, "Book removed from favourites", Toast.LENGTH_SHORT).show()
                                        btnAddToFav.text= getString(R.string.Add_to_Favourites)
                                        btnAddToFav.backgroundTintList =
                                            ContextCompat.getColorStateList(applicationContext, R.color.colorPrimary)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@DescriptionActivity, "Some error Occurred", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }

                        }
                        else
                        {
                            Toast.makeText(this@DescriptionActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
                    catch (e:JSONException)
                    {
                        Toast.makeText(this@DescriptionActivity, "Some Unexpected error Occurred", Toast.LENGTH_SHORT).show()
                    }


                }, Response.ErrorListener {

                    Toast.makeText(this@DescriptionActivity, "Volley error Occurred", Toast.LENGTH_SHORT).show()
                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["content-type"] = "application/json"
                    headers["token"] = "ae3d4fafda9a8b"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else
        {
            val dailog = AlertDialog.Builder(this@DescriptionActivity)
            dailog.setTitle("Error")
            dailog.setMessage("Internet Connection is not found")
            dailog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@DescriptionActivity.finish()
            }
            dailog.setNegativeButton("Exit"){
                    text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dailog.create()
            dailog.show()
        }
    }




    class DBAsyncTask(@SuppressLint("StaticFieldLeak") val context:Context, private val bookEntity:BookEntity, private val Mode:Int) : AsyncTask<Void,Void,Boolean>() {
        private val db = Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): Boolean {

            when(Mode)
            {
                1 ->{
                    val book:BookEntity = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!= null
                }

                2 ->{

                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3 ->{
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}