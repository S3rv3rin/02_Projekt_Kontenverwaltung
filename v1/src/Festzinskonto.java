public class Festzinskonto extends Sparkonto {
    private int laufzeit;
    private int abgelaufeneZeit;

    public Festzinskonto(int kontonummer, double kontostand, String inhaber, double zinssatz, int laufzeit, int abgelaufeneZeit) {
        super(kontonummer, kontostand, inhaber, zinssatz);
        this.laufzeit = laufzeit;
        this.abgelaufeneZeit = abgelaufeneZeit;
    }

    public Festzinskonto(int kontonummer, double kontostand, String inhaber, double zinssatz, int laufzeit) {
        this(kontonummer, kontostand, inhaber, zinssatz, laufzeit, 0);
    }

    public int getLaufzeit() {
        return laufzeit;
    }

    public int getAbgelaufeneZeit() {
        return abgelaufeneZeit;
    }

    public void abheben(double betrag) {
        if (abgelaufeneZeit >= laufzeit)
            super.abheben(betrag);
        else
            System.out.println("Noch nicht zugreifbar. Laufzeitende noch nicht erreicht!");
    }

    public void zinsAusschüttung() {
        super.zinsAusschüttung();
        abgelaufeneZeit++;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + abgelaufeneZeit + "/" + laufzeit + ")";
    }
}
