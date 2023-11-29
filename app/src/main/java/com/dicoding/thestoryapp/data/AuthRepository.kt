package com.dicoding.thestoryapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.thestoryapp.api.ApiService
import com.dicoding.thestoryapp.model.ResponseLogin
import com.dicoding.thestoryapp.model.ResponseGeneral
import com.dicoding.thestoryapp.model.User
import com.dicoding.thestoryapp.util.Result
import com.dicoding.thestoryapp.util.parseErrorData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    fun login(email: String, password: String): LiveData<Result<ResponseLogin>> {
        val data: MutableLiveData<Result<ResponseLogin>> = MutableLiveData()
        data.postValue(Result.Loading())

        val user = User(email = email, password = password)
        try {
            apiService.login(user).enqueue(object : Callback<ResponseLogin>{
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        data.postValue(Result.Success(response.body() as ResponseLogin))
                    }
                    else {
                        val errorData = response.errorBody()?.string()?.let { parseErrorData(it) }
                        data.postValue(Result.Error(errorData?.message, response.code(), null))
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    data.postValue(Result.Error(t.message.toString(), null, null))
                }

            })
        }catch (e: Exception) {
            e.printStackTrace()
            data.postValue(Result.Error("error convert data", null, null))
        }


        return data

    }

    fun register(name: String, email: String, password:String): LiveData<Result<ResponseGeneral>> {

        val data: MutableLiveData<Result<ResponseGeneral>> = MutableLiveData()
        data.postValue(Result.Loading())
        val userData = User(name = name, email = email, password = password)

        try {
            apiService.register(userData).enqueue(object: Callback<ResponseGeneral>{
                override fun onResponse(
                    call: Call<ResponseGeneral>,
                    response: Response<ResponseGeneral>
                ) {
                    if (response.isSuccessful) {
                        data.postValue(Result.Success(response.body() as ResponseGeneral))
                    }
                    else {
                        val errorData = response.errorBody()?.let { parseErrorData(it.string()) }
                        data.postValue(Result.Error(errorData?.message, response.code(), null))
                    }
                }

                override fun onFailure(call: Call<ResponseGeneral>, t: Throwable) {
                    data.postValue(Result.Error(t.message.toString(), null, null))
                }

            })
        }catch (e: Exception) {
            e.printStackTrace()
            data.postValue(Result.Error("error convert data", null, null))
        }



        return data
    }

}