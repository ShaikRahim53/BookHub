package com.rahim.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.rahim.bookhub.R
import com.rahim.bookhub.activity.DescriptionActivity
import com.rahim.bookhub.model.Book
import com.squareup.picasso.Picasso
import java.util.ArrayList


class DashboardRecyclerAdapter(val context:Context,val itemList: ArrayList<Book>) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
            return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
         val book = itemList[position]
         holder.txtBookName.text = book.bookName
         holder.txtBookAuthor.text = book.bookAuthor
         holder.txtBookCost.text = book.bookPrice
         holder.txtBookRating.text = book.bookRating
         Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.txtBookImage)
         holder.linearLayoutContent.setOnClickListener {
             val intent = Intent(context,DescriptionActivity::class.java)
             intent.putExtra("book_id",book.bookId)
             context.startActivity(intent)
         }
    }

    class DashboardViewHolder(view:View) : RecyclerView.ViewHolder(view)
    {
        val txtBookName: TextView= view.findViewById(R.id.txtBookName)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtBookAuthor)            
        val txtBookCost: TextView = view.findViewById(R.id.txtBookCost)
        val txtBookRating: TextView= view.findViewById(R.id.txtBookRating)
        val txtBookImage: ImageView = view.findViewById(R.id.imgBookImage)
        val linearLayoutContent: LinearLayout = view.findViewById(R.id.linearLayoutContent)
    }

}