package androidbuffer.com.simplecontactviewer1;

import java.util.List;


// Class to store the data relative to the contact

public class ContactData {


    //attributes

    private String contactName;

    private List<String> contactNumber;


    //Getters and setters

    public List<String> getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(List<String> contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
