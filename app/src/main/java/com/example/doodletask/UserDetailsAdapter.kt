package com.example.doodletask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserDetailsAdapter : RecyclerView.Adapter<UserDetailsAdapter.UserDetailsViewHolder>() {

    private var usersList: ArrayList<UserDetailsEntity?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDetailsViewHolder {
        val userView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inflate_user_details_rv_item, parent, false)
        return UserDetailsViewHolder(userView)
    }

    override fun onBindViewHolder(holder: UserDetailsViewHolder, position: Int) {
        holder.onBind(usersList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class UserDetailsViewHolder(val userView: View) :
        RecyclerView.ViewHolder(userView) {
        fun onBind(userDetailsResponse: UserDetailsEntity?) {
            val userProfilePic = userView.findViewById<ImageView>(R.id.userProfilePic)
            val userName = userView.findViewById<TextView>(R.id.tvUserName)
            val userPhoneNumber = userView.findViewById<TextView>(R.id.tvUserPhoneNumber)

            userProfilePic?.let {
                Glide.with(it.context).load(userDetailsResponse?.profilePic)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(it)
            }
            userName?.text =
                "${userDetailsResponse?.firstName} ${userDetailsResponse?.lastName}"
            userPhoneNumber?.text = userDetailsResponse?.phoneNumber
        }
    }

    fun addUserList(usersList: ArrayList<UserDetailsEntity?>) {
        clearList()
        this.usersList.addAll(usersList)
        notifyDataSetChanged()
    }

    fun clearList() {
        this.usersList.clear()
        notifyDataSetChanged()
    }

}