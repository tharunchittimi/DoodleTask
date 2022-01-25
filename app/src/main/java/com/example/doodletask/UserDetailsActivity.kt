package com.example.doodletask

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_user_details.*
import retrofit2.Call
import retrofit2.Response
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.room.Room
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class UserDetailsActivity : AppCompatActivity() {

    private var usersAdapter: UserDetailsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        setUpRecyclerView()
        getDataFromDbIfExits()
    }

    private fun getDataFromDbIfExits() {
        doAsync {
            val db = Room.databaseBuilder(
                applicationContext,
                UsersRoom::class.java, "usersdatabase"
            ).build()
            if (db.usersDao().getAllUserDetails().size > 0) {
                db.usersDao().getAllUserDetails().let {
                    usersAdapter?.addUserList(it as ArrayList<UserDetailsEntity?>)
                }
            } else {
                callUserDetailsApi()
            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private fun callUserDetailsApi() {
        if (isNetworkConnected()) {
            val apiInterface =
                ApiInterface.create().getUserDetails("https://randomuser.me/api/", "25")
            apiInterface.enqueue(object : retrofit2.Callback<UserDetailsResponse> {
                override fun onResponse(
                    call: Call<UserDetailsResponse>,
                    response: Response<UserDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.e("task", "Success")
                        insertDataToDataBase(response.body()?.results)
                    } else {
                        showToast(response.message())
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    showToast(t.message)
                }

            })
        } else {
            showToast("No Internet")
        }
    }

    private fun insertDataToDataBase(userDetailsResponse: ArrayList<UserDetailsResponse.Result?>?) {
        doAsync {
            val db = Room.databaseBuilder(
                applicationContext,
                UsersRoom::class.java, "usersdatabase"
            ).build()
            for ((pos, i) in userDetailsResponse?.withIndex() ?: ArrayList()) {
                val userDetailsEntity = UserDetailsEntity(
                    userId = pos,
                    firstName = i?.name?.first,
                    lastName = i?.name?.last,
                    phoneNumber = i?.cell, profilePic = i?.picture?.medium
                )
                db.usersDao().insert(userDetailsEntity).let {
                    Log.e("task", "${it}")
                    if (it > 0) {
                        db.usersDao().getAllUserDetails().let {data->
                            uiThread {
                                usersAdapter?.addUserList(data as ArrayList<UserDetailsEntity?>)
                            }
                        }
                    }
                }
                Log.e("task", "Inserted")
            }
            tvLoading?.visibility = View.GONE
            rvUserDetails?.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this@UserDetailsActivity, "$message", Toast.LENGTH_LONG).show()
        tvLoading?.text = message
        tvLoading?.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        usersAdapter = UserDetailsAdapter()
        rvUserDetails?.apply {
            layoutManager = LinearLayoutManager(this@UserDetailsActivity)
            adapter = usersAdapter
        }
    }
}