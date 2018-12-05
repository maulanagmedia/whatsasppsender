package id.net.gmedia.whatsappsender;

public class PesanModel {

    private String nomor, pesan;

    public PesanModel(String nomor, String pesan) {
        this.nomor = nomor;
        this.pesan = pesan;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
}
