package gateways;

import exceptions.UnauthorizedException;
import exceptions.UnknownException;
import mvc.controllers.ViewSwitcher;
import mvc.models.Person;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PersonGateway {
    private static final String URL = "http://localhost:8080";
    private String token;

    public PersonGateway() {}
    public PersonGateway(String token) {
        this.token = token;
    }

    public static ArrayList<Person> fetchPeople(String token) {
        ArrayList people = new ArrayList<>();

        try {
            String response = executeGetRequest(URL + "/people", token);
            JSONArray peopleList = new JSONArray(response);
            for(Object person: peopleList) {
                people.add(Person.fromJSONObject((JSONObject) person));
            }
        } catch(RuntimeException | IOException e) {
            throw new UnknownException(e);
        }

        return people;
    }

    public static void updatePerson(String token, JSONObject updates) {
        try {
            String response = executePutRequest(URL + "/people/" + updates.get("id"), token, updates);

        } catch(RuntimeException | IOException e) {
            throw new UnknownException(e);
        }
    }

    public static void insertPerson(String token, JSONObject newPersonInfo) {
        try {
            String response = executePostRequest(URL + "/people", token, newPersonInfo);
        } catch(RuntimeException | IOException e) {
            throw new UnknownException(e);
        }
    }

    public static void deletePerson(String token, Person person) throws IOException {
        String response = executeDeleteRequest(URL + "/people/" + person.getId(), token);
        ViewSwitcher.getInstance().getPeople().remove(person);
    }

    private static String checkResponse (CloseableHttpResponse response) throws IOException {
        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                System.out.println("200");
                return getStringFromResponse(response);
            case 401:
                throw new UnauthorizedException(response.getStatusLine().getReasonPhrase());
            case 404:
                throw new UnknownException(response.getStatusLine().getReasonPhrase());
            default:
                throw new UnknownException(response.getStatusLine().getReasonPhrase());
        }
    }

    private static String executePostRequest(String url, String token, JSONObject newPersonInfo) throws IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(url);

            if (token != null && token.length() > 0) {
                postRequest.setHeader("Authorization", token);
                postRequest.setHeader("Content-type", "application/json");
            }

            String formDataString = newPersonInfo.toString();
            StringEntity reqEntity = new StringEntity(formDataString);
            postRequest.setEntity(reqEntity);

            response = httpclient.execute(postRequest);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkResponse(response);
    }

    private static String executePutRequest(String url, String token, JSONObject updates) throws UnauthorizedException, UnknownException, IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            HttpPut putRequest = new HttpPut(url);

            if (token != null && token.length() > 0) {
                putRequest.setHeader("Authorization", token);
                putRequest.setHeader("Content-type", "application/json");
            }

            updates.remove("id");
            String formDataString = updates.toString();
            StringEntity reqEntity = new StringEntity(formDataString);
            putRequest.setEntity(reqEntity);

            response = httpclient.execute(putRequest);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkResponse(response);
    }

        private static String executeGetRequest(String url, String token) throws UnauthorizedException, UnknownException, IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpGet getRequest = new HttpGet(url);

            if(token != null && token.length() > 0)
                getRequest.setHeader("Authorization", token);

            response = httpclient.execute(getRequest);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkResponse(response);
    }

    private static String executeDeleteRequest(String url, String token) throws IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            HttpDelete deleteRequest = new HttpDelete(url);

            if(token != null && token.length() > 0) {
                deleteRequest.setHeader("Authorization", token);
                deleteRequest.setHeader("Content-type", "application/json");
            }

            response = httpclient.execute(deleteRequest);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return checkResponse(response);
    }

    private static String getStringFromResponse(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        EntityUtils.consume(entity);
        return strResponse;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


