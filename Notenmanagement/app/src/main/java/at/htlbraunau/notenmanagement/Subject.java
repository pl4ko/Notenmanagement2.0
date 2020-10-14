package at.htlbraunau.notenmanagement;

/**
 * Created by Konrad on 24.10.2017.
 */

public class Subject {
    public int subject_ID;
    public String subject;
    public String subjectLabel;

    public Subject(int subject_ID, String subject, String subjectLabel){
        this.subject_ID = subject_ID;
        this.subject = subject;
        this.subjectLabel = subjectLabel;
    }
}
