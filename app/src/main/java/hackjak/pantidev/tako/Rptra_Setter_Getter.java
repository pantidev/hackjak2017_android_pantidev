package hackjak.pantidev.tako;

/**
 * Created by bsupriadi on 12/6/2017.
 */

public class Rptra_Setter_Getter {
    String id,nama,address,luas,waktuperesmian,email;

    public Rptra_Setter_Getter(String id,String nama, String address, String luas, String waktuperesmian, String email) {
        this.id = id;
        this.nama = nama;
        this.address = address;
        this.luas = luas;
        this.waktuperesmian = waktuperesmian;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAddress() {
        return address;
    }

    public String getLuas() {
        return luas;
    }

    public String getWaktuperesmian() {
        return waktuperesmian;
    }

    public String getEmail() {
        return email;
    }
}
