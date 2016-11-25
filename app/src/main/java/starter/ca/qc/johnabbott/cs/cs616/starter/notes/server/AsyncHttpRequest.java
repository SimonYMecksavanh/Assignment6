package starter.ca.qc.johnabbott.cs.cs616.starter.notes.server;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Performs an asynchronous HTTP request and reports the progress and result, or any error, using
 * an event.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public class AsyncHttpRequest extends AsyncTask<Void, HttpProgress, Either<IOException, HttpResponse>> {

    private static final int BUFFER_SIZE = 10;
    private static final int DEBUG_SLOW_TRANSFER = 200;

    /**
     * Represents the supported HTTP request methods
     */
    public enum Method { GET, POST, PUT, DELETE }

    /**
     * Listener interface
     */
    public interface OnResponse {
        void onResult(HttpResponse response);
        void onProgress(HttpProgress progress);
        void onError(Exception e);
    }

    /*
       Fields
     */
    private String urlStr;
    private Method method;
    private String requestBody;
    private OnResponse listener;

    /**
     * Create an asynchronous HTTP request
     * @param urlStr
     * @param method
     * @param requestBody
     */
    public AsyncHttpRequest(String urlStr, Method method, String requestBody) {
        this.method = method;
        this.requestBody = requestBody;
        this.urlStr = urlStr;
    }

    /**
     * Create an asynchronous HTTP request
     * @param urlStr
     * @param method
     */
    public AsyncHttpRequest(String urlStr, Method method) {
        this(urlStr, method, null);
    }

    /**
     * Set the listener
     * @param listener
     */
    public void setOnResponseListener(OnResponse listener) {
        this.listener = listener;
    }

    /* private helper for doInBackground */
    private void copyStreamBuffered(InputStream in, OutputStream out, HttpProgress currentProgress) throws IOException {
        BufferedInputStream inBuf = new BufferedInputStream(in, BUFFER_SIZE);
        BufferedOutputStream outBuf = new BufferedOutputStream(out, BUFFER_SIZE);

        int transferredSoFar = 0;

        currentProgress.setProgress(transferredSoFar);
        publishProgress(currentProgress);


        int i;
        while((i = inBuf.read()) >= 0) {

            outBuf.write(i);
            transferredSoFar++;
            if(transferredSoFar % BUFFER_SIZE == 0) {

                // slow down the transfer
                try {
                    Thread.sleep(DEBUG_SLOW_TRANSFER);
                } catch (InterruptedException e) {
                }

                currentProgress.setProgress(transferredSoFar);
                publishProgress(currentProgress);
            }
        }

        currentProgress.setProgress(transferredSoFar);
        publishProgress(currentProgress);

        outBuf.flush();
    }

    @Override
    protected Either<IOException, HttpResponse> doInBackground(Void... voids) {

        // if the listener is not definded... create a do nothing listener.
        if(listener == null)
            listener = new OnResponse() {
                @Override
                public void onResult(HttpResponse response) {
                }

                @Override
                public void onProgress(HttpProgress progress) {
                }

                @Override
                public void onError(Exception e) {
                }
            };

        HttpResponse response = new HttpResponse();

        try {
            // Create URL object for HTTP connection
            URL url = new URL(urlStr);

            // Create HTTP connection from URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method.toString().toUpperCase());

            // If the user has specified JSON for the request, configure connection, add content-type header
            // and copy JSON to output stream
            if (requestBody != null) {
                con.setDoOutput(true);
                con.setRequestProperty("content-type", "application/json");
                byte[] bytes = requestBody.getBytes();

                HttpProgress progress = new HttpProgress();
                progress.setPhase(HttpProgress.Phase.SENDING);
                progress.setTotal(bytes.length);

                copyStreamBuffered(new ByteArrayInputStream(bytes), con.getOutputStream(), progress);
            }

            // Retrieve information connection
            Map<String, List<String>> headers = con.getHeaderFields();

            try {
                response.setStatus(con.getResponseCode());
            } catch (IOException e) {
                return Either.left(e);
            }

            try {

                // get the length of the response body from the response headers
                int length = -1;  // default if not specified
                if (headers.containsKey("Content-Length"))
                    length = Integer.parseInt(headers.get("Content-Length").get(0));

                HttpProgress progress = new HttpProgress();
                progress.setPhase(HttpProgress.Phase.RECEIVING);
                progress.setTotal(length);

                // copy JSON from output stream
                ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
                copyStreamBuffered(con.getInputStream(), responseBytes, progress);
                response.setBody(new String(responseBytes.toByteArray()));
                //
                // response.setImage(BitmapFactory.decodeByteArray(responseBytes.toByteArray(), 0, responseBytes.size()));

            } catch (IOException e) { /* no response body */ }
        }
        catch (IOException e) {
            return Either.left(e);
        }

        return Either.right(response);
    }

    @Override
    protected void onProgressUpdate(HttpProgress... values) {
        // report the progress on the UI thread.
        listener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Either<IOException, HttpResponse> result) {
        // report the result on the UI thread
        // results go to onResult()
        // errors go to onError()
        switch (result.getSide()) {
            case RIGHT:
                listener.onResult(result.getRight());
                break;
            case LEFT:
                listener.onError(result.getLeft());
                break;
        }
    }
}

/**
 * Represents a value of either type L or R (referred to as "left" and "rignt" values)
 * @param <L>
 * @param <R>
 */
class Either<L, R> {

    public enum Side { LEFT, RIGHT };

    private Side side;
    private L left;
    private R right;

    /**
     * Create a "left" value.
     * @param left
     * @param <L>
     * @param <R>
     * @return
     */
    public static <L, R> Either<L, R> left(L left) {
        Either<L, R> e = new Either<>();
        e.side = Side.LEFT;
        e.left = left;
        return e;
    }

    /**
     * Create a "right" value
     * @param right
     * @param <L>
     * @param <R>
     * @return
     */
    public static <L, R> Either<L, R> right(R right) {
        Either<L, R> e = new Either<>();
        e.side = Side.RIGHT;
        e.right = right;
        return e;
    }

    private Either() {}

    /**
     * Determine if the value is either left or right
     * @return
     */
    public Side getSide() {
        return side;
    }

    /**
     * Get the "left" value
     * @return
     * @precondition current either must be left
     */
    public L getLeft() {
        if(side != Side.LEFT)
            throw new RuntimeException("Trying to access a RIGHT value as a LEFT.");
        return left;
    }

    /**
     * Get the "right" value
     * @return
     * @precondition current either must be right
     */
    public R getRight() {
        if(side != Side.RIGHT)
            throw new RuntimeException("Trying to access a LEFT value as a RIGHT.");
        return right;
    }
}
