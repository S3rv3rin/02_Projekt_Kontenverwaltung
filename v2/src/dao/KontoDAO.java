package dao;

import model.Konto;

import java.util.List;

/*
 * Interface (wie eine abstrakte Klasse):
 * - enthält keine Attribute (normalerweise)
 * - enthält nur Methodenköpfe (ohne Implementierung)
 * - kann Konstanten enthalten (static final)
 */
public interface KontoDAO {

    // Datenzugriffe:
    // Erzeugen - Create (Insert)
    // Lesen - Read
    // Nachführen - Update
    // Löschen - Delete
    // -> CRUD (oder RUDI)

    // Create
    public boolean insertKonto( Konto k );  // true, wenn die DB verändert wurde

    // Read
    public Konto getKontoByKontonummer( int kontonummer );
    public List<Konto> getAllKonten();

    // Update
    public void updateKonto( int kontonummer, Konto k );

    // Delete
    public void deleteKonto( int kontonummer );

    // Management
    public int letzteAktuelleKontonummer();
}
