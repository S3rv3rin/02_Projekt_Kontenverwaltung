package controller;

import dao.KontoDAO;
import dao.SqlDAO;
import dao.TempDAO;
import model.Festzinskonto;
import model.Giro;
import model.Konto;
import model.Sparkonto;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Kontoverwaltung {
    private static DecimalFormat df = new DecimalFormat("#,##0.00 €");
    private static Scanner eingabe = new Scanner(System.in);
    private static KontoDAO kontoDB = new SqlDAO();

    public static int nächsteFreieKontonummer(){
        return kontoDB.letzteAktuelleKontonummer()+1;
    }

    public static void main(String[] args) {

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

            Konto k;
            int kontonummer;
            switch (auswahl) {
                case 1:
                    k = kontoAnlegen();
                    if (k != null)
                        kontoDB.insertKonto(k);
                    break;
                case 2:
                    System.out.print("Bitte Kontonummer eingeben: ");
                    kontonummer = eingabe.nextInt();
                    // Konto k aus Liste holen
                    k = kontoDB.getKontoByKontonummer(kontonummer);
                    if (k != null)
                        manipulieren( k );
                    break;
                case 3:
                    //for (Konto konto : kontenListe) System.out.println(konto);
                    for (Konto konto : kontoDB.getAllKonten()) {
                        System.out.println( konto );
                    }
                    break;
                case 4:
                    System.out.print("Bitte Kontonummer eingeben: ");
                    kontonummer = eingabe.nextInt();
                    kontoDB.deleteKonto(kontonummer);
                    System.out.println("Konto "  + kontonummer + " wurde gelöscht.");
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

        // jetzt benutzten wir mal die neue for-each Schleife
        for (Konto k : kontoDB.getAllKonten()) {
            if ( k instanceof Giro && k.getKontostand() > 0 ) {
                summeGiro += k.getKontostand();
            }
            else if ( k instanceof Festzinskonto ) {
                summeFest += k.getKontostand();
            }
            else if ( k instanceof Sparkonto ) {
                summeSpar += k.getKontostand();
            }
            if (k.getKontostand() < 0)
                summeKredit += -k.getKontostand();
        }

        System.out.println("Summe aller Girokonten: " + df.format(summeGiro));
        System.out.println("Summe aller Sparkonten: " + df.format(summeSpar));
        System.out.println("Summe aller Festzinskt: " + df.format(summeFest));
        System.out.println("Summe aller Kredite:    " + df.format(summeKredit));
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
                    System.out.println("Unbekannte Auswahl, bitte nochmal probierens");
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

        Konto k = null;
        // Alternativ
//        if (auswahl == 1) { /* Case 1 */ }
//        else if (auswahl == 2) { /* Case 2 */ }
//        else if (auswahl == 3) { /* ... */ }
//        else { /* default */ }

        // Neue Switch-Syntax (ab Java v14)
        System.out.print("Kontoinhaber: ");
        String name = eingabe.next();
        switch (auswahl) {
            case 1 -> {
                System.out.print("Kreditlimit:  ");
                double limit = eingabe.nextDouble();
                k = new Giro(nächsteFreieKontonummer(), 0.0, name, limit);
            }
            case 2, 3 -> {
                System.out.print("Zinssatz:     ");
                double zinssatz = eingabe.nextDouble();
                if (auswahl == 2)
                    k = new Sparkonto(nächsteFreieKontonummer(), 0.0, name, zinssatz);
                else {
                    System.out.print("Laufzeit: ");
                    int laufzeit = eingabe.nextInt();
                    k = new Festzinskonto(nächsteFreieKontonummer(), 0.0, name, zinssatz, laufzeit);
                }
            }
            default -> System.out.println("Kontotyp nicht bekannt");
        }
        return k;
    }
}
