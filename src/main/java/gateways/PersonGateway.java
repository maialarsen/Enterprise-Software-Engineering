package gateways;

import exceptions.UnauthorizedException;
import exceptions.UnknownException;
import javafx.Alerts;
import mvc.controllers.PeopleListController;
import mvc.controllers.ViewSwitcher;
import mvc.models.Person;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.text.View;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class PersonGateway {
    private static final String URL = "http://localhost:8080";
    private String token;
    private static final Logger logger = LogManager.getLogger();

    public PersonGateway() {}
    public PersonGateway(String token) {
        this.token = token;
    }

    public static ArrayList<Person> fetchPeople(String token) {
        ArrayList people = new ArrayList<>();

        try {
            String response = executeGetRequest(URL + "/people", token);

            if (response != null) {
                JSONArray peopleList = new JSONArray(response);
                for (Object person : peopleList) {
                    people.add(Person.fromJSONObject((JSONObject) person));
                }
            }
        } catch(RuntimeException | IOException e) {
            throw new UnknownException(e);
        }

        return people;
    }

    public static void updatePerson(String token, JSONObject updates) {
        try {
            int id = updates.getInt("id");
            String response = executePutRequest(URL + "/people/" + updates.getInt("id"), token, updates);

            if (response != null) {
                Person personToUpdate = ViewSwitcher.getInstance().findById(id);

                Iterator<String> keys = updates.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.equals("firstName")) {
                        personToUpdate.setFirstName(updates.getString("firstName"));
                    }
                    if (key.equals("lastName")) {
                        personToUpdate.setLastName(updates.getString("lastName"));
                    }
                    if (key.equals("dateOfBirth")) {
                        personToUpdate.setDateOfBirth(LocalDate.parse(updates.getString("dateOfBirth")));
                    }
                }

                logger.info("UPDATING " + personToUpdate.toString());
            }

        } catch(RuntimeException | IOException e) {
            throw new UnknownException(e);
        }
    }

    public static void insertPerson(String token, JSONObject newPersonInfo) {
        try {
            String response = executePostRequest(URL + "/people", token, newPersonInfo);

            if (response != null) {
                Person person = new Person(newPersonInfo.getString("firstName"), newPersonInfo.getString("lastName").toString(), LocalDate.parse(newPersonInfo.getString("dateOfBirth")));
                ViewSwitcher.getInstance().getPeople().add(person);
                PeopleListController.getInstance().getPeopleList().getItems().add(person);
                logger.info("CREATING " + person.toString());
            }

        } catch(RuntimeException | IOException e) {
            throw new UnknownException(e);
        }
    }

    public static void deletePerson(String token, Person person) throws IOException {
        String response = executeDeleteRequest(URL + "/people/" + person.getId(), token);
        if (response != null) {
            ViewSwitcher.getInstance().getPeople().remove(person);
            PeopleListController.getInstance().getPeopleList().getItems().remove(person);
            logger.info("DELETING " + person.toString());
        }
    }

    private static String checkResponse (CloseableHttpResponse response) throws IOException {
        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                return getStringFromResponse(response);
            case 400:
                Alerts.infoAlert("400 Error", "Bad Request");
                break;
            case 401:
                Alerts.infoAlert("401 Error", "Unauthorized Request");
                break;
            case 404:
                Alerts.infoAlert("404 Error", "Request Resource Not Found");
                break;
            default:
                Alerts.infoAlert("Unknown Error", "Something went wrong, please try again");
                break;
        }
        return null;
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


