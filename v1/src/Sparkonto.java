public class Sparkonto extends Konto {
    private double zinssatz;
    private static final double SPAR_GEBÜHR = 0.2;  // Großschreibung wegen "final" (Konstante)

    public Sparkonto(int kontonummer, double kontostand, String inhaber, double zinssatz) {
        super(kontonummer, kontostand, inhaber);
        this.zinssatz = zinssatz;
    }

    public double getZinssatz() {
        return zinssatz;
    }

    public void abheben(double betrag) {
        double neuerKontostand = this.kontostand - Math.abs(betrag) - SPAR_GEBÜHR;
        if (neuerKontostand < 0.0) {
            // ACHTUNG, große AUSNAHME: Ausgabe in einer Klasse (Model)
            System.out.println("Keine Auszahlung möglich. Kein Kreditlimit vorhanden.");
        }
        else
            this.kontostand = neuerKontostand;
    }

    public void zinsAusschüttung() {
        kontostand *= 1 + zinssatz;
//        double zinsBetrag = kontostand * zinssatz;
//        kontostand += zinsBetrag;
    }

    public String toString() {
        return super.toString() + " {" + zinssatz + "}";
    }
}
