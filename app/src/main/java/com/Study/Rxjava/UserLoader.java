package com.Study.Rxjava;

import com.Study.Rxjava.http.ObjectLoader;
import com.Study.Rxjava.http.RetrofitServiceManager;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;


public class UserLoader extends ObjectLoader {
    private MovieService mMovieService;
    private static final String KEY = "B2F145B12F23554185991E471C9C0BB44";

    public UserLoader() {
        mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
    }

    public Observable<List<UserBean>> getUser(String method, String empId, String stopType, String stopLevel, String day, int pageNo) {
        return observe(mMovieService.login(KEY, method, empId, stopType, stopLevel, day, pageNo))
                .map(new Func1<List<UserBean>, List<UserBean>>() {
                    @Override
                    public List<UserBean> call(List<UserBean> userBeans) {
                        return userBeans;
                    }
                });
    }

    public interface MovieService {
        @GET("ashx/MengNiuApp.ashx")
        Observable<List<UserBean>> login(
                @Query("key") String key,
                @Query("method") String method,
                @Query("EmpID") String EmpID,
                @Query("StopType") String StopType,
                @Query("StopLevel") String StopLevel,
                @Query("Day") String Day,
                @Query("PageNo") int PageNo
        );
    }
}
