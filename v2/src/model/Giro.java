package model;

public class Giro extends Konto {
    private double kreditlimit;
    private static final double GIRO_GEBÜHR = 0.1;  // Großschreibung wegen "final" (Konstante)

    public Giro(int kontonummer, double kontostand, String inhaber, double kreditlimit) {
        super(kontonummer, kontostand, inhaber);
        this.kreditlimit = Math.abs(kreditlimit);
    }

    public double getKreditlimit() {
        return kreditlimit;
    }

    @Override
    public void abheben(double betrag) {
        double neuerKontostand = this.kontostand - Math.abs(betrag) - GIRO_GEBÜHR;
        if (neuerKontostand < -kreditlimit) {
            // ACHTUNG, große AUSNAHME: Ausgabe in einer Klasse (Model)
            System.out.println("Keine Auszahlung möglich. Kreditlimit erreicht.");
        }
        else
            this.kontostand = neuerKontostand;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + kreditlimit + "]";
    }

    public Konto clone() {
        return new Giro(getKontonummer(), getKontostand(), getInhaber(), getKreditlimit());
    }

}
