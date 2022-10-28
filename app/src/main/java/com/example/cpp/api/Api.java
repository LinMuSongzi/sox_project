package com.example.cpp.api;

import com.example.cpp.data.response.EffectDatasRespones;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface Api {


    @GET("getEffects2")
    @NonNull
    Observable<EffectDatasRespones> getEffects();


}
