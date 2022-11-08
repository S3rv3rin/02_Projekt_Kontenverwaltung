package dao;

import model.Festzinskonto;
import model.Giro;
import model.Konto;
import model.Sparkonto;

import java.util.ArrayList;
import java.util.List;

public class TempDAO implements KontoDAO {
    // Pre-Konstruktor (wird sogar vor dem Konstruktor ausgeführt)
    private List<Konto> inmemoryDB = new ArrayList<>();

    public TempDAO() {
        // hier können bereits Testdaten angelegt werden...
        // ... ein paar Daten wollen wir hier schon haben, zum Spielen
        insertKonto( new Giro(Konto.getNächsterKontozähler(), 500.0, "Donald", 1000.0) );
        insertKonto( new Giro(Konto.getNächsterKontozähler(), -500.1, "Mickey", 5000.0) );
        insertKonto( new Sparkonto(Konto.getNächsterKontozähler(), 20.0, "Dagobert", 0.05) );
        insertKonto( new Festzinskonto(Konto.getNächsterKontozähler(), 250000.0, "Goofy", 0.1, 10));
    }

    private Konto searchKonto(int kontonummer) {
        for (Konto konto : inmemoryDB) {
            if (konto.getKontonummer() == kontonummer)
                return konto;
        }
        return null;
    }

    @Override
    public boolean insertKonto(Konto k) {
        if (k == null) return false;

        // gibt's das Konto bereits in unserer Liste?
        if (searchKonto(k.getKontonummer()) != null )
           return false;

        inmemoryDB.add(k.clone());
        return true;
    }

    @Override
    public Konto getKontoByKontonummer(int kontonummer) {
        Konto k = searchKonto(kontonummer);
        if (k != null) return k.clone();
        return null;
    }

    @Override
    public List<Konto> getAllKonten() {
        List<Konto> copyList = new ArrayList<>( inmemoryDB.size() );
        for (int i = 0; i < inmemoryDB.size(); i++) {
            Konto konto = inmemoryDB.get(i);
            copyList.add(konto.clone());         // iter-Template
            }
        return copyList;
    }

    @Override
    public void updateKonto(int kontonummer, Konto k) {
        deleteKonto(kontonummer);
        insertKonto(k);
    }

    @Override
    public void deleteKonto(int kontonummer) {
//        // Variante 2
//        Konto k = searchKonto(kontonummer);
//        if (k != null) inmemoryDB.remove(k);

        // Variante 1
        for (int i=0; i<inmemoryDB.size(); i++) {
            if (inmemoryDB.get(i).getKontonummer() == kontonummer) {
                inmemoryDB.remove(i);
                break;
            }
        }
    }

    @Override
    public int letzteAktuelleKontonummer() {
        return Konto.getKontozähler();
    }
}
