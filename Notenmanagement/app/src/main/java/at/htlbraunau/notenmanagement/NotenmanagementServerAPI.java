package at.htlbraunau.notenmanagement;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import at.htlbraunau.notenmanagement.R;

/**
 * Created by Konrad on 26.09.2017.
 */

public class NotenmanagementServerAPI {

    public static final String NOTENMANAGEMENT_SERVER_BASE_URL = NotenmanagementSingleton.getContext().getResources().getString(R.string.NOTENMANAGEMENT_SERVER_BASE_URL);

    public static final int maxWidthOfSmallScreen = 1080;

    public static final int LOGIN_SUCCESFULL = 1;
    public static final int LOGIN_FAILED = 2;

    public static final int LAST_FIVE_ASSESSMENTS_SUCCESSFUL = 3;
    public static final int LAST_FIVE_ASSESSMENTS_FAILED = 4;

    public static final int OVERVIEW_OF_GRADES_AVAILABLE = 7;
    public static final int NO_OVERVIEW_OF_GRADES_AVAILABLE = 8;

    public static final int ALL_SUBJECTS_AVAILABLE = 11;
    public static final int NO_SUBJECTS_AVAILABLE = 12;


    public static final int EARLY_WARNINGS_AVAILABLE = 15;
    public static final int NO_EARLY_WARNINGS_AVAILABLE = 16;

    public static final int EMAIL_CHANGED_SUCCESSFULL = 17;
    public static final int EMAIL_CHANGED_FAILED = 18;

    public static final int MESSAGE_TO_HELPDESK_SENT_SUCCESSFUL = 19;
    public static final int MESSAGE_TO_HELPDESK_SENT_FAILED = 20;

    //Logindaten an Sever schicken
    public static void login(final String username, final String password, final Handler handler) {
        new Thread() {
            public void run() {
                HttpURLConnection hucLogin = null;
                BufferedReader br = null;
                BufferedWriter bw = null;

                try {

                    //HTTP-Verbindung zum Server
                    URL urlLogin = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/token");
                    hucLogin = (HttpURLConnection) urlLogin.openConnection();
                    hucLogin.setRequestMethod("POST");
                    hucLogin.setDoOutput(true);
                    hucLogin.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    bw = new BufferedWriter(new OutputStreamWriter(hucLogin.getOutputStream()));

                    bw.write("grant_type=password&username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8"));
                    bw.flush();

                    int responseCode = hucLogin.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        br = new BufferedReader(new InputStreamReader(hucLogin.getInputStream()));
                        String response = br.readLine();

                        JSONObject jsonLogin = new JSONObject(response);

                        User user = new User(jsonLogin.getString("role"), jsonLogin.getString("className"),
                                jsonLogin.getString("matrikelNr"),
                                jsonLogin.getString("userName"),
                                jsonLogin.getString("access_token"));

                        NotenmanagementSingleton.getInstance().setUser(user);
                        handler.obtainMessage(LOGIN_SUCCESFULL, user).sendToTarget();
                    } else {
                        handler.obtainMessage(LOGIN_FAILED).sendToTarget();
                    }
                } catch (Exception ex) {
                    handler.obtainMessage(LOGIN_FAILED).sendToTarget();

                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        if (hucLogin != null) {
                            hucLogin.disconnect();
                        }
                    }

                }
            }
        }.start();
    }

    //Die letzten 5 Einträge aus der Datenbank lesen
    public static void getLastFiveAssessmentsOfPupil(final Handler handler) {

        new Thread() {
            public void run() {
                HttpURLConnection hucAssessments = null;
                BufferedReader br = null;

                try {

                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/Schueler/" + NotenmanagementSingleton.getInstance().getUser().matrikelNr + "/Noten?limit=5&sort=-LF_ID");
                    hucAssessments = (HttpURLConnection) url.openConnection();
                    hucAssessments.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);

                    int responseCode = hucAssessments.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        br = new BufferedReader(new InputStreamReader(hucAssessments.getInputStream()));

                        JSONArray jsonAssessments = new JSONArray(br.readLine());
                        Assessment[] assessments = new Assessment[jsonAssessments.length()];
                        ArrayList<Assessment> assessmentsArrayList = new ArrayList<Assessment>();

                        for (int i = 0; i < jsonAssessments.length(); i++) {

                            int lf_id = jsonAssessments.getJSONObject(i).getInt("LF_ID");
                            String date = jsonAssessments.getJSONObject(i).getString("Datum");
                            int teacherID = jsonAssessments.getJSONObject(i).getInt("Lehrer_ID");
                            String subject = jsonAssessments.getJSONObject(i).getString("Fach");
                            String type = jsonAssessments.getJSONObject(i).getString("Typ");
                            String maxPoints = jsonAssessments.getJSONObject(i).getString("MaxPunkte");
                            String lf_comment = jsonAssessments.getJSONObject(i).getString("LF_Kommentar");

                            int mark;

                            try {
                                mark = jsonAssessments.getJSONObject(i).getInt("Note");
                            } catch (JSONException ex) {
                                mark = -1;
                            }

                            double points;

                            try {
                                points = jsonAssessments.getJSONObject(i).getDouble("Punkte");
                            } catch (JSONException ex) {
                                points = -1;
                            }

                            String comment = jsonAssessments.getJSONObject(i).getString("Kommentar");

                            assessments[i] = new Assessment(lf_id, date, teacherID, subject, type, maxPoints, lf_comment, mark, points, comment);
                            assessmentsArrayList.add(assessments[i]);
                        }

                        Bundle bundleAssessments = new Bundle();
                        bundleAssessments.putParcelableArrayList("key1", assessmentsArrayList);
                        handler.obtainMessage(LAST_FIVE_ASSESSMENTS_SUCCESSFUL, bundleAssessments).sendToTarget();
                    } else {
                        handler.obtainMessage(LAST_FIVE_ASSESSMENTS_FAILED).sendToTarget();
                    }

                } catch (Exception ex) {
                    handler.obtainMessage(LAST_FIVE_ASSESSMENTS_FAILED).sendToTarget();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (hucAssessments != null) {
                        hucAssessments.disconnect();
                    }
                }
            }
        }.start();
    }

    //Notenspiegel einer bestimmten Leistungsfestellung
    public static void getOverviewOfGradesOfAssessment(final Handler handler, final int LF_ID) {
        new Thread() {
            public void run() {
                HttpURLConnection huc = null;
                BufferedReader br = null;
                try {

                    //HTTP-Verbindung zum Server
                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/lfs/" + Integer.toString(LF_ID));
                    huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);
                    int responseCode = huc.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                        JSONObject jsonObject = new JSONObject(br.readLine());
                        JSONArray jsonArrayOverview = jsonObject.getJSONArray("Notenspiegel");
                        int[] overview = new int[jsonArrayOverview.length()];

                        for (int i = 0; i < jsonArrayOverview.length(); i++) {
                            overview[i] = jsonArrayOverview.getInt(i);
                        }

                        Bundle bundleOverviewOfGrades = new Bundle();
                        bundleOverviewOfGrades.putIntArray("overviewOfGrades", overview);

                        handler.obtainMessage(OVERVIEW_OF_GRADES_AVAILABLE, overview).sendToTarget();
                    }
                    else{
                        handler.obtainMessage(NO_OVERVIEW_OF_GRADES_AVAILABLE).sendToTarget();
                    }
                }
                catch (Exception ex) {
                    handler.obtainMessage(NO_OVERVIEW_OF_GRADES_AVAILABLE).sendToTarget();

                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (huc != null) {
                        huc.disconnect();
                    }
                }

            }
        }.start();
    }

    //Alle Fächer eines Schülers mit LFs
    public static String[] getAllSubjectsWithAssessmentssOfStudent() {
                HttpURLConnection huc = null;
                BufferedReader br = null;
                try {
                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/schueler/" + NotenmanagementSingleton.getInstance().getUser().matrikelNr + "/Faecher?sort=Fach"); //urL/api/schueler/martikelNr/Faecher?sort=Fach
                    huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);
                    int responseCode = huc.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                        String response = br.readLine();

                        JSONArray jsonArray = new JSONArray(response);

                        String[] subjects = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            subjects[i] = jsonArray.getJSONObject(i).getString("Fach");
                        }

                       return subjects;
                    }
                    else{

                        return null;
                    }

                } catch (Exception ex) {

                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (huc != null) {
                        huc.disconnect();
                    }
                }

        return null;
    }

    //Liefert alle Fächer
    public static void getAllSubjects(final Handler handler) {
        new Thread() {
            public void run() {
                HttpURLConnection huc = null;
                BufferedReader br = null;

                try {

                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/Faecher?sort=Fach");
                    huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);

                    int responseCode = huc.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                        JSONArray jsonSubjects = new JSONArray(br.readLine());
                        Subject[] subjects = new Subject[jsonSubjects.length()];

                        StringTokenizer st;
                        String subjectLabel;

                        for (int i = 0; i < jsonSubjects.length(); i++) {

                            st = new StringTokenizer(jsonSubjects.getJSONObject(i).getString("Fachbezeichnung"), "\r\n");
                            StringBuilder sb = new StringBuilder();
                            while (st.hasMoreTokens()) {
                                sb.append(st.nextToken());
                            }

                            subjects[i] = new Subject(jsonSubjects.getJSONObject(i).getInt("Fach_ID"),
                                    jsonSubjects.getJSONObject(i).getString("Fach"),
                                    sb.toString());
                        }

                        Log.d("", "");
                        handler.obtainMessage(ALL_SUBJECTS_AVAILABLE, subjects).sendToTarget();
                    } else {
                        handler.obtainMessage(NO_SUBJECTS_AVAILABLE).sendToTarget();
                    }

                } catch (Exception ex) {
                    handler.obtainMessage(NO_SUBJECTS_AVAILABLE).sendToTarget();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (huc != null) {
                        huc.disconnect();
                    }
                }
            }
        }.start();
    }

    //Alle Lfs des Schülers
    public static Assessment[] getAllAssessmentsOfStudent(){

        HttpURLConnection huc = null;
        BufferedReader br = null;

        try {

            URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/Schueler/" + NotenmanagementSingleton.getInstance().getUser().matrikelNr + "/Noten?sort=Fach|-Datum|-LF_ID");
            huc = (HttpURLConnection) url.openConnection();
            huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);

            int responseCode = huc.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                JSONArray jsonAssessments = new JSONArray(br.readLine());
                Assessment[] assessments = new Assessment[jsonAssessments.length()];

                for (int i = 0; i < jsonAssessments.length(); i++) {

                    int lf_id = jsonAssessments.getJSONObject(i).getInt("LF_ID");
                    String date = jsonAssessments.getJSONObject(i).getString("Datum");
                    int teacherID = jsonAssessments.getJSONObject(i).getInt("Lehrer_ID");
                    String subject = jsonAssessments.getJSONObject(i).getString("Fach");
                    String type = jsonAssessments.getJSONObject(i).getString("Typ");
                    String maxPoints = jsonAssessments.getJSONObject(i).getString("MaxPunkte");
                    String lf_comment = jsonAssessments.getJSONObject(i).getString("LF_Kommentar");

                    int mark;

                    try {
                        mark = jsonAssessments.getJSONObject(i).getInt("Note");
                    } catch (JSONException ex) {
                        mark = -1;
                    }

                    double points;

                    try {
                        points = jsonAssessments.getJSONObject(i).getDouble("Punkte");
                    } catch (JSONException ex) {
                        points = -1;
                    }

                    String comment = jsonAssessments.getJSONObject(i).getString("Kommentar");

                    assessments[i] = new Assessment(lf_id, date, teacherID, subject, type, maxPoints, lf_comment, mark, points, comment);
                }

                return assessments;

            } else {

                return null;
            }

        } catch (Exception ex) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (huc != null) {
                huc.disconnect();
            }
        }

        return null;
    }

    //Änderung der zweiten E-Mail Adress
    public static void change2ndEmail(final Handler handler, final String email) {
        new Thread() {
            public void run() {
                HttpURLConnection huc = null;
                BufferedWriter bw = null;
                BufferedReader br = null;

                try {

                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/Schueler/" + NotenmanagementSingleton.getInstance().getUser().matrikelNr);
                    huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    int responseCode = huc.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                        JSONObject jsonObject = new JSONObject(br.readLine());

                        if (huc != null) {
                            huc.disconnect();
                        }

                        huc = (HttpURLConnection) url.openConnection();
                        huc.setRequestMethod("PUT");
                        huc.setDoOutput(true);
                        huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);
                        huc.setRequestProperty("Content-Type", "application/json");


                        bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));

                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("Matrikelnummer", jsonObject.getInt("Matrikelnummer"));
                        jsonObject2.put("Nachname", jsonObject.getString("Nachname"));
                        jsonObject2.put("Vorname", jsonObject.getString("Vorname"));
                        jsonObject2.put("Klasse", jsonObject.getString("Klasse"));
                        jsonObject2.put("EMailAdresse1", jsonObject.getString("EMailAdresse1"));
                        jsonObject2.put("EMailAdresse2", email);

                        bw.write(jsonObject2.toString());
                        bw.flush();

                        responseCode = huc.getResponseCode();

                        if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_NO_CONTENT)) {

                            handler.obtainMessage(EMAIL_CHANGED_SUCCESSFULL, email).sendToTarget();
                        } else {
                            handler.obtainMessage(EMAIL_CHANGED_FAILED).sendToTarget();
                        }
                    } else {

                    }

                } catch (Exception ex) {
                    Log.e("Notenmanagement", ex.getMessage());
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (huc != null) {
                        huc.disconnect();
                    }
                }
            }
        }.start();
    }

    //Alle Frühwarnungen eines bestimmten Schülers
    public static void getAllEarlyWarnings(final Handler handler) {
        new Thread() {
            public void run() {
                HttpURLConnection hucAssessments = null;
                BufferedReader br = null;
                Bundle bundle = new Bundle();

                try {

                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/Schueler/" + NotenmanagementSingleton.getInstance().getUser().matrikelNr + "/Fruehwarnungen");
                    hucAssessments = (HttpURLConnection) url.openConnection();
                    hucAssessments.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);

                    int responseCode = hucAssessments.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        br = new BufferedReader(new InputStreamReader(hucAssessments.getInputStream()));

                        JSONArray jsonArray = new JSONArray(br.readLine());

                        ArrayList<EarlyWarning> earlyWarnings = new ArrayList<EarlyWarning>(jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {

                            earlyWarnings.add(new EarlyWarning(jsonArray.getJSONObject(i).getString("Fach"),
                                    jsonArray.getJSONObject(i).getString("Eingetragen")));
                        }

                        Collections.sort(earlyWarnings, new EarlyWarningComparator());

                        bundle.putParcelableArrayList("earlywarnings", earlyWarnings);

                        //handler.obtainMessage(EARLY_WARNINGS_AVAILABLE, earlyWarnings).sendToTarget();

                    } else {
                        handler.obtainMessage(NO_EARLY_WARNINGS_AVAILABLE).sendToTarget();
                    }

                } catch (Exception ex) {
                    handler.obtainMessage(NO_EARLY_WARNINGS_AVAILABLE).sendToTarget();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (hucAssessments != null) {
                        hucAssessments.disconnect();
                    }
                }

                try {
                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/Fruehwarnungen/Einstellungen");
                    hucAssessments = (HttpURLConnection) url.openConnection();
                    hucAssessments.setRequestMethod("GET");
                    hucAssessments.setDoInput(true);
                    hucAssessments.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);

                    int responseCode = hucAssessments.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(hucAssessments.getInputStream()));
                        JSONObject jsonObject = new JSONObject(br.readLine());

                        String endOfterm = jsonObject.getString("SemesterDatum");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY);

                        Date d = null;

                        d = sdf.parse(endOfterm);

                        bundle.putSerializable("endOfTerm", d);

                        handler.obtainMessage(EARLY_WARNINGS_AVAILABLE, bundle).sendToTarget();

                    } else {
                        Log.e("Notenmanagement", Integer.toString(responseCode));
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (hucAssessments != null) {
                        hucAssessments.disconnect();
                    }
                }
            }
        }.start();
    }

    //Neuen Token für FCM setzen
    public static void sendRegistrationToServerFCM(final String refreshedToken) {
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection huc = null;
                BufferedReader br = null;
                BufferedWriter bw = null;
                String push_ID;
                int matrikelnummer = -1;
                int lehrer_ID = -1;
                int responseCode = 0;

                try {
                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/notificationdevices/" + NotenmanagementSingleton.getInstanceID());
                    huc = (HttpURLConnection) url.openConnection();

                    responseCode = huc.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                        JSONObject jsonObjectGET = new JSONObject(br.readLine());

                        //Abfrage ob ein Schüler- oder ein Lehrergerät eingelesen wird
                        try {
                            matrikelnummer = jsonObjectGET.getInt("Matrikelnummer");
                        } catch (JSONException e) {
                            lehrer_ID = jsonObjectGET.getInt("Lehrer_ID");
                        }
                    }
                } catch (Exception ex) {
                    Log.e("Notenmanagement", ex.getMessage());
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (huc != null) {
                        huc.disconnect();
                    }
                }

                switch (responseCode) {
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        // Gerät noch nicht registriert -> RegistrationToken an den Server senden
                        //POST-Methode: schickt neuen Token falls keiner vorhanden ist
                        sendDeviceID(refreshedToken);
                        break;

                    case HttpURLConnection.HTTP_OK:
                        // Gerät ist bereits registriert -> RegistrationToken auf dem Server updaten
                        //PUT-Methode: ändert Token falls einer vorhanden ist
                        changeDeviceID(refreshedToken, matrikelnummer, lehrer_ID);
                        break;

                    default:
                        Log.e("Notenmanagement", "Fehler beim Abfragen der registrierten NotificationDevices: HTTP Responsecode: " + responseCode);
                        break;
                }
            }
        }.start();
    }

    //Falls kein Token vorhanden ist neuen schicken
    public static void sendDeviceID(String refreshedToken) {
        HttpURLConnection huc = null;
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/notificationdevices");
            huc = (HttpURLConnection) url.openConnection();

            huc.setRequestMethod("POST");
            huc.setDoOutput(true);
            huc.setRequestProperty("Content-type", "application/json");

            JSONObject jsonObjectPUT = new JSONObject();
            jsonObjectPUT.put("Device_ID", NotenmanagementSingleton.getInstanceID());
            jsonObjectPUT.put("Push_ID", refreshedToken);
            jsonObjectPUT.put("Matrikelnummer", NotenmanagementSingleton.getUser().matrikelNr);
            jsonObjectPUT.put("Lehrer_ID", JSONObject.NULL);
            jsonObjectPUT.put("NotificationPlatform", "FCM");

            bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));
            bw.write(jsonObjectPUT.toString());
            bw.flush();

            int responseCode = huc.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("Notenmanagement", Integer.toString(responseCode));
            } else {
                Log.i("Notenmanagement", Integer.toString(responseCode));
            }
        } catch (Exception ex) {
            Log.e("Notenmanagement", ex.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (huc != null) {
                huc.disconnect();
            }
        }
    }

    //Vorhandenen Token ändern
    public static void changeDeviceID(String refreshedToken, int matrikelnummer, int lehrer_ID) {
        HttpURLConnection huc = null;
        BufferedWriter bw = null;
        try {
            URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/notificationdevices/" + NotenmanagementSingleton.getInstanceID());
            huc = (HttpURLConnection) url.openConnection();

            huc.setRequestMethod("PUT");
            huc.setDoOutput(true);
            huc.setRequestProperty("Content-Type", "application/json");

            bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));

            JSONObject jsonObjectPUT = new JSONObject();
            jsonObjectPUT.put("Device_ID", NotenmanagementSingleton.getInstanceID());
            jsonObjectPUT.put("Push_ID", refreshedToken);

            if (matrikelnummer != -1) {
                jsonObjectPUT.put("Matrikelnummer", matrikelnummer);
                jsonObjectPUT.put("Lehrer_ID", JSONObject.NULL);
            } else {
                jsonObjectPUT.put("Matrikelnummer", JSONObject.NULL);
                jsonObjectPUT.put("Lehrer_ID", lehrer_ID);
            }

            jsonObjectPUT.put("NotificationPlatform", "FCM");

            bw.write(jsonObjectPUT.toString());
            bw.flush();
            int responseCode = huc.getResponseCode();

            if ((responseCode / 100) == 2) {
                Log.i("Notenmanagement", Integer.toString(responseCode));
            } else {
                Log.i("Notenmanagement", Integer.toString(responseCode));
            }


        } catch (Exception ex) {
            Log.e("Notenmanagement", ex.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (huc != null) {
                huc.disconnect();
            }
        }
    }

    //Schicken einer Nachricht an den HelpDesk
    public static void sendMessageToHelpDesk(final Handler handler, final String subjectMail, final String email, final String message) {
        new Thread() {
            public void run() {
                HttpURLConnection huc = null;
                BufferedWriter bw = null;

                try {
                    URL url = new URL(NOTENMANAGEMENT_SERVER_BASE_URL + "/api/AdminNachrichten");
                    huc = (HttpURLConnection) url.openConnection();

                    huc.setDoOutput(true);
                    huc.setRequestMethod("POST");
                    huc.setRequestProperty("Authorization", "bearer " + NotenmanagementSingleton.getInstance().getUser().access_token);
                    huc.setRequestProperty("Content-type", "application/json");

                    bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("Verfasser", NotenmanagementSingleton.getUser().userName);
                    jsonObject.put("EMailAdresse", email);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY);//DateTime - Format in Android
                    Date currentDate = new Date();
                    String currentDateString = sdf.format(currentDate);

                    jsonObject.put("Datum", currentDateString);
                    jsonObject.put("Betreff", subjectMail);
                    jsonObject.put("Nachricht", message);

                    bw.write(jsonObject.toString());
                    bw.flush();

                    int responseCode = huc.getResponseCode();

                    if (responseCode / 100 == 2) {

                        handler.obtainMessage(MESSAGE_TO_HELPDESK_SENT_SUCCESSFUL).sendToTarget();
                    }

                    handler.obtainMessage(MESSAGE_TO_HELPDESK_SENT_FAILED).sendToTarget();
                } catch (Exception ex) {
                    ex.getMessage();
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (huc != null) {
                        huc.disconnect();
                    }
                }
            }
        }.start();
    }
}

//Comparator der die Frühwarnungen aufsteigend nach Fach und aufsteigend nach Datum sortiert
class EarlyWarningComparator implements Comparator<EarlyWarning> {

        @Override
        public int compare(EarlyWarning e1, EarlyWarning e2) {

            if (e1.getSubject().equals(e2.getSubject())) {

                return e1.getDateOfinsert().compareTo(e2.getDateOfinsert());
            }
            return e1.getSubject().compareTo(e2.getSubject());
        }
    }



