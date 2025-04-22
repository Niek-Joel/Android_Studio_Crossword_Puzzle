package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;

public class WebServiceDAO {
    private final String TAG = "WebServiceDAO";
    private final DAOFactory daoFactory;
    private static final String HTTP_METHOD = "GET";
    private static final String ROOT_URL = "http://ec2-3-142-171-53.us-east-2.compute.amazonaws.com:8080/CrosswordMagicServer/puzzle";
    private static final String URL_PUZZLE_QUERY = "?id=";
    private String requestUrl;
    private ExecutorService pool;

    WebServiceDAO(DAOFactory daoFactory) { this.daoFactory = daoFactory; }
    public JSONArray list() {
        requestUrl = ROOT_URL;
        JSONArray result = null;
        try {
            pool = Executors.newSingleThreadExecutor();
            Future<String> pending = pool.submit(new CallableHTTPRequest());
            String response = pending.get();
            pool.shutdown();
            result = new JSONArray(response);
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public JSONObject getSelectedPuzzle(int webID) {
        StringBuilder extendedURL = new StringBuilder();
        extendedURL.append(ROOT_URL).append(URL_PUZZLE_QUERY).append(webID);
        requestUrl = extendedURL.toString();
        JSONObject result = null;
        try {
            pool = Executors.newSingleThreadExecutor();
            Future<String> pending = pool.submit(new CallableHTTPRequest());
            String response = pending.get();
            pool.shutdown();
            result = new JSONObject(response);
        }
        catch (Exception e) {e.printStackTrace();};
        return result;
    }

    public class CallableHTTPRequest implements Callable<String> {
        @Override
        public String call() {
            StringBuilder r = new StringBuilder();
            HttpURLConnection connection = null;

            try {
                URL url = new URL(requestUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(HTTP_METHOD);
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    do {
                        line = reader.readLine();
                        if (line != null) {
                            r.append(line);
                        }
                    }
                    while (line != null);
                }
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return r.toString().trim();
        }
    }
}
