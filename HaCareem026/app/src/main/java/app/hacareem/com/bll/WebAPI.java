package app.hacareem.com.bll;

import app.hacareem.com.bean.WebResponse;
import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Numair Qadir on 12/15/2016.
 */

public interface WebAPI {

    /**
     * Retrofit get annotation with our URL
     * And our method that will return us details of user.
     */
    @GET(Urls.URL_DESTINATIONS)
    Call<WebResponse> getDestinationList(@Query("user_id") String user_id, @Query("hour") long hour);
}