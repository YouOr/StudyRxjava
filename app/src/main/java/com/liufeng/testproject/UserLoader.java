package com.liufeng.testproject;

import com.liufeng.testproject.http.ObjectLoader;
import com.liufeng.testproject.http.RetrofitServiceManager;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;


public class UserLoader extends ObjectLoader {
    private MovieService mMovieService;
    private UpImageService uUpImageService;
    private static final String KEY = "A2BB2C892444B155A2540CBF04AB1752";

    public UserLoader() {
        mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
        uUpImageService = RetrofitServiceManager.getInstance().create(UpImageService.class);
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

    public Observable<String> upImage(String method, String iPhotoName, String VisitDate, String SRID, String sDatingTaskActualID, String sCustomerID, String iPhotoNo, String iDone, String imgData) {
        return observe(uUpImageService.upImage(KEY, method, iPhotoName, VisitDate, SRID, sDatingTaskActualID, sCustomerID, iPhotoNo, iDone, imgData)).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s;
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

    public interface UpImageService {
        @POST("ashx/app0.ashx")
        Observable<String> upImage(
                @Query("key") String key,
                @Query("method") String method,
                @Query("iPhotoName") String iPhotoName,
                @Query("VisitDate") String VisitDate,
                @Query("SRID") String SRID,
                @Query("sDatingTaskActualID") String sDatingTaskActualID,
                @Query("sCustomerID") String sCustomerID,
                @Query("iPhotoNo") String iPhotoNo,
                @Query("iDone") String iDone,
                @Query("imgData") String imgData
        );
    }
}
