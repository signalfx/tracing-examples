package com.splunk.rum.demoApp.network;

import android.util.Log;

import com.google.gson.Gson;
import com.splunk.rum.demoApp.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

// This is RetrofitError converted to RMRetrofit 2
@SuppressWarnings("ALL")
public class RetrofitException extends RuntimeException {
    public static RetrofitException httpError(String url, Response response, Retrofit retrofit) {
        RetrofitExceptionBodyResponse retrofitException = null;
        try {
            /*{"timestamp":1484314542479,"status":404,"error":"Not Found","message":"No message available","path":"/user/login"}*/
            assert response.errorBody() != null;
            JSONObject jObjError = new JSONObject(response.errorBody().string());
             retrofitException = new Gson().fromJson(jObjError.toString() ,RetrofitExceptionBodyResponse.class);
            Log.d("Ret" ,"Error:"+retrofitException.getError());
        } catch (JSONException | IOException e) {
            AppUtils.handleRumException(e);
            e.printStackTrace();
        }
        String message = response.code() + " " + response.message();
        return new RetrofitException(message, url, response, Kind.HTTP, null, retrofit,retrofitException);
    }

    public static RetrofitException networkError(IOException exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.NETWORK, exception, null,null);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null,null);
    }

    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    private final String url;
    private final Response response;
    private final Kind kind;
    private final Retrofit retrofit;
    public final RetrofitExceptionBodyResponse retrofitExceptionBody;

    public RetrofitException(String message, String url, Response response, Kind kind, Throwable exception, Retrofit retrofit, RetrofitExceptionBodyResponse retrofitExceptionBody) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.kind = kind;
        this.retrofit = retrofit;
        this.retrofitExceptionBody = retrofitExceptionBody;
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * The RMRetrofit this request was executed on
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
        return converter.convert(response.errorBody());
    }

    public RetrofitExceptionBodyResponse getRetrofitExceptionBody() {
        return retrofitExceptionBody;
    }
}