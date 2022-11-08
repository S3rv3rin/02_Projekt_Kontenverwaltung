import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Kontoverwaltung {
    private static Scanner eingabe = new Scanner(System.in);
    private static ArrayList<Konto> kontenListe = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat("#,##0.00 €");

    public static void main(String[] args) {
        // ein paar Daten wollen wir hier schon haben, zum Spielen
        kontenListe.add( new Giro(Konto.getNächsterKontozähler(), 500.0, "Donald", 1000.0) );
        kontenListe.add( new Giro(Konto.getNächsterKontozähler(), -500.10, "Mickey", 5000.0) );
        kontenListe.add( new Sparkonto(Konto.getNächsterKontozähler(), 200005.0, "Dagobert", 0.05) );
        kontenListe.add( new Festzinskonto(Konto.getNächsterKontozähler(), 20.0, "Goofy", 0.1, 10));

        hauptschleife:
        while (true) {
            System.out.println("Hauptmenü");
            System.out.println("================");
            System.out.println("1 - Konto anlegen");
            System.out.println("2 - Konto manipulieren");
            System.out.println("3 - Alle Konten anzeigen");
            System.out.println("4 - Konto löschen");
            System.out.println("5 - Bilanz erstellen");
            System.out.println("6 - Programm beenden");

            System.out.print("Ihre Auswahl: ");
            int auswahl = eingabe.nextInt();

            int kontonummer;
            switch (auswahl) {
                case 1:
                    Konto k = kontoAnlegen();
                    if (k != null)
                        kontenListe.add(k);
                    break;
                case 2:
                    System.out.print("Bitte Kontonummer eingeben: ");
                    kontonummer = eingabe.nextInt();
                    // Konto k aus Liste holen
                    for (int i=0; i < kontenListe.size(); i++) {
                        if ( kontenListe.get(i).getKontonummer() == kontonummer ) {
                            manipulieren( kontenListe.get(i) );
                            break;
                        }
                    }
                    break;
                case 3:
                    // for (Konto konto : kontenListe) System.out.println(konto);
                    for (int i=0; i < kontenListe.size(); i++)
                        System.out.println( kontenListe.get(i) );
                    break;
                case 4:
                    System.out.print("Bitte Kontonummer eingeben: ");
                    kontonummer = eingabe.nextInt();
                    for (int i=0; i < kontenListe.size(); i++) {
                        if ( kontenListe.get(i).getKontonummer() == kontonummer ) {
                            kontenListe.remove(i);
                            System.out.println("Konto "  + kontonummer + " wurde gelöscht.");
                            break;
                        }
                    }
                    break;
                case 5:
                    bilanzErstellen();
                    break;
                case 6:
                    break hauptschleife;
                default:
                    System.out.println("Unbekannte Auswahl, bitte nochmal probieren");
            }
        }

        System.out.println("Vielen Dank und auf Wiedersehen");
    }


    private static void bilanzErstellen() {

        // Geldbestände oder Gelddefizite aller Konten erfassen
        // Summe aller Giro, Spar und Festzinskonten
        double summeGiro = 0;
        double summeSpar = 0;
        double summeFest = 0;
        double summeKredit = 0; // Summer aller überzogenen Konten

        /*
         * Führt zu einem Problem, weil ein Festzinskonto ist auch ein Sparkonto!
         * Der Betrag wird also immer zu summeSpar und summeFest dazu addiert.
         * Aus diesem Grund habe ich, den Betrag nochmal bei "if (i instanceof Festzinskonto)"
         * von summeSpar abgezogen.
         */
        for (Konto konto : kontenListe) {

            if (konto instanceof Giro && konto.getKontostand() > 0 )
                summeGiro += konto.getKontostand();
            else if (konto instanceof Festzinskonto)
                summeFest += konto.getKontostand();
            else if (konto instanceof Sparkonto)
                summeSpar += konto.getKontostand();

            if (konto.getKontostand() < 0)
                summeKredit -= konto.getKontostand();
        }

        System.out.println("Die Summe aller Girokonten beträgt: " + df.format(summeGiro));
        System.out.println("Die Summe aller Sparkonten beträgt: " + df.format(summeSpar));
        System.out.println("Die Summe aller Festzinskonten beträgt: " + df.format(summeFest));
        System.out.println("Die Summe aller vergebenen Kredite beträgt: " + df.format(summeKredit));
    }


    private static void manipulieren(Konto konto) {
        unterschleife:
        while (true) {
            System.out.println("Konto Manipulieren Menü");
            System.out.println("-----------------------");
            System.out.println("1 - Einzahlen");
            System.out.println("2 - Abheben");
            System.out.println("3 - Zinsen zuschlagen");
            System.out.println("4 - Kontostand anzeigen");
            System.out.println("5 - Zurück ins Hauptmenü");

            System.out.print("Ihre Auswahl: ");
            int auswahl = eingabe.nextInt();

            double betrag;
            switch (auswahl) {
                case 1:
                    System.out.print("Einzuzahlender Betrag: ");
                    betrag = eingabe.nextDouble();
                    konto.einzahlen(betrag);
                    break;
                case 2:
                    System.out.print("Abzuhebender Betrag: ");
                    betrag = eingabe.nextDouble();
                    konto.abheben(betrag);
                    break;
                case 3:
                    System.out.println("Zinsen werden ausgeschüttet");
                    konto.zinsAusschüttung();
                    break;
                case 4:
                    System.out.println(konto);
                    System.out.println("Kontostand: " + df.format(konto.getKontostand()));
                    break;
                case 5:
                    break unterschleife;
                default:
                    System.out.println("Unbekannte Auswahl, bitte nochmal probieren");
            }
        }
    }

    private static Konto kontoAnlegen() {
        System.out.println("Konto Anlegen Menü");
        System.out.println("------------------");
        System.out.println("1 - Girokonto anlegen");
        System.out.println("2 - Sparkonto anlegen");
        System.out.println("3 - Festzinskonto anlegen");

        System.out.print("Ihre Auswahl: ");
        int auswahl = eingabe.nextInt();

        System.out.print("Kontoinhaber: ");
        String name = eingabe.next();
        Konto k = null;
        switch (auswahl) {
            case 1 -> {
                System.out.print("Kreditlimit:  ");
                double limit = eingabe.nextDouble();
                k = new Giro(Konto.getNächsterKontozähler(), 0.0, name, limit);
            }
            case 2 , 3 -> {
                    System.out.print("Zinssatz:    ");
                double zinssatz = eingabe.nextDouble();
                if (auswahl == 2)
                    k = new Sparkonto(Konto.getNächsterKontozähler(),0.0, name, zinssatz);
                else {
                    System.out.print("Laufzeit:    ");
                    int laufzeit = eingabe.nextInt();
                    k = new Festzinskonto(Konto.getNächsterKontozähler(),0.0, name, zinssatz, laufzeit);
                }
            }
            default -> System.out.println("Kontotyp nicht bekannt");
        }

        return k;
    }
}
