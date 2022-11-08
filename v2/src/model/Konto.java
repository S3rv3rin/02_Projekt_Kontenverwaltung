package model;

public abstract class Konto /* extends Object */ {
    private int kontonummer;
    private String inhaber;

    protected double kontostand;

    private static int kontozähler = 0;

    public static int getNächsterKontozähler() {
        kontozähler++;
        return kontozähler;
    }

    public static int getKontozähler() {
        return kontozähler;
    }

    // später wenn die Konten aus der Datenbank gelesen werden
    public static void setKontozähler(int kontozähler) {
        if (kontozähler < 0) kontozähler = 0;
        Konto.kontozähler = kontozähler;
    }

    public Konto(int kontonummer, double kontostand, String inhaber) {
        this.kontonummer = kontonummer;
        this.kontostand = kontostand;
        this.inhaber = inhaber;
    }

    public void einzahlen( double betrag ) {
        kontostand += Math.abs(betrag);
    }

    public void abheben( double betrag ) {
        kontostand -= Math.abs(betrag);
    }

    // sollte das vielleicht nicht allgmeiner formuliert werden?
    public void zinsAusschüttung() { }

    public int getKontonummer() {
        return kontonummer;
    }

    public double getKontostand() {
        return kontostand;
    }

    public String getInhaber() {
        return inhaber;
    }

    public String toString() {
        return kontonummer + " (" + inhaber + ")";
    }

    public abstract Konto clone();
}
