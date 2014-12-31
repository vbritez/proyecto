package com.tigo.cs.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.exception.NoOperatorException;
import com.tigo.cs.android.exception.RetryException;

public class WebService {

    protected static DefaultHttpClient httpClient;
    protected static DefaultHttpClient verifyClient;
    protected HttpContext localContext;

    protected PostRESTRequestData requestData;
    private static boolean initialized;

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public DefaultHttpClient getVerifyClient() {
        return verifyClient;
    }

    private void init() throws NoOperatorException {
        if (!initialized) {
            try {
                HttpParams myParams = new BasicHttpParams();
                HttpParams veriFyParams = new BasicHttpParams();

                HttpConnectionParams.setConnectionTimeout(
                        myParams,
                        CsTigoApplication.getGlobalParameterHelper().getRestConfigurationServiceTimeoutConnection());
                HttpConnectionParams.setSoTimeout(
                        myParams,
                        CsTigoApplication.getGlobalParameterHelper().getRestConfigurationServiceTimeoutSocket());

                HttpConnectionParams.setConnectionTimeout(
                        veriFyParams,
                        CsTigoApplication.getGlobalParameterHelper().getRestConfigurationVerifyTimeoutConnection());
                HttpConnectionParams.setSoTimeout(
                        veriFyParams,
                        CsTigoApplication.getGlobalParameterHelper().getRestConfigurationVerifyTimeoutSocket());

                KeyStore localTrustStore = KeyStore.getInstance("BKS");
                InputStream in = CsTigoApplication.getContext().getResources().openRawResource(
                        R.raw.mytruststore);
                localTrustStore.load(in, "secret".toCharArray());
                SSLSocketFactory sslSocketFactory = new SSLSocketFactory(localTrustStore);
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", new PlainSocketFactory(), CsTigoApplication.getOperationHelper().findOperationData().getRestWsPort()));
                registry.register(new Scheme("https", sslSocketFactory, CsTigoApplication.getOperationHelper().findOperationData().getRestWsPort()));

                httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(myParams, registry), myParams);
                verifyClient = new DefaultHttpClient(new ThreadSafeClientConnManager(myParams, registry), veriFyParams);

                localContext = new BasicHttpContext();
                initialized = true;
            } catch (Exception e) {
                throw new NoOperatorException(e.getMessage());
            }
        }
    }

    public WebService(PostRESTRequestData requestData) {
        this.requestData = requestData;
    }

    public String webInvoke() throws RetryException {
        try {
            init();
        } catch (NoOperatorException e) {
            /*
             * en el caso que la informacion del operador no se recupero
             * retornamos una respuesta vacia
             */
            return null;
        }
        String ret = null;

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.RFC_2109);

        HttpPost httpPost = new HttpPost(requestData.getWebServiceURL()
            + requestData.getWebMethodName());
        HttpResponse response = null;

        StringEntity tmp = null;

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");

        try {
            tmp = new StringEntity(requestData.getData(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("Groshie", "HttpUtils : UnsupportedEncodingException : " + e);
        }

        httpPost.setEntity(tmp);

        try {
            response = httpClient.execute(httpPost, localContext);

            /*
             * verificar los demas tipos de error y manejarlos . anallizar si es
             * posible tener errores ya preparados para retornar al usuario en
             * caso que exista algun problema.
             */
            if (response != null
                && (response.getStatusLine().getStatusCode() == 200)) {
                if (response.getEntity() != null) {
                    ret = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } else {
                /*
                 * se agrega para contemplar modo offline. en el caso que la
                 * operacion no fue exitosa, esta debe enviar una notificacion
                 * al metodo que lo invoco para que el reintento de envio sea
                 * agendado
                 */

                throw new RetryException("El codigo de respuesta http no es valido. Agendar reintento si es necesario.");
            }
        } catch (ClientProtocolException e) {
            /*
             * este error se produce por un mal request o un mal response de
             * nuestro servidor.
             */
        } catch (IOException e) {
        } catch (RetryException e) {
            throw e;
        } catch (Exception e) {
            /*
             * para modo offline debemos contemplar el reenvio de la peticion en
             * el caso de error a nivel de red.
             */
            throw new RetryException("El codigo de respuesta http no es valido. Agendar reintento si es necesario.");
        }

        return ret;
    }

}
