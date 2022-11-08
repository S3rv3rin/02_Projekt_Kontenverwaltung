package dao;

import model.Festzinskonto;
import model.Giro;
import model.Konto;
import model.Sparkonto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlDAO implements KontoDAO{

    private static final String SQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String SQL_SERVER = "127.0.0.1";
    private static final String SQL_SERVER_PORT = "3306";
    private static final String SQL_DATABASE = "2512_kontodb";
    private static final String SQL_USER = "2512_kontoadm";
    private static final String SQL_PASSWORT = "geheim123";
    private Connection sqlConnection;

    public SqlDAO() {
        try {
            Class.forName(SQL_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL jdbc Treiber nicht gefunden. ;( ");
            System.exit(-1);
        }
        openConnection();

    }
    private void openConnection(){
        try {
            sqlConnection =
            DriverManager.getConnection(
                    "jdbc:mysql://"+ SQL_SERVER +":"+ SQL_SERVER_PORT +"/"+ SQL_DATABASE,
                    SQL_USER, SQL_PASSWORT);
            sqlConnection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Problem beim Verbindungsaufbau. ;(" );
            e.printStackTrace();
        }
    }
    private void executeSQLStatements (List<String> SQLStrings){
        Savepoint sqlTransaction = null;
        try {
            sqlTransaction = sqlConnection.setSavepoint();
            Statement sqlCommand = sqlConnection.createStatement();
            for (String sqlString : SQLStrings) {
                sqlCommand.execute(sqlString);
            }
            sqlCommand.close();
            sqlConnection.commit();
        } catch (SQLException e) {
            if (sqlTransaction != null) {
                try {
                    sqlConnection.rollback(sqlTransaction);
                } catch (SQLException ignored) { }
            }
            System.err.println("SQL Transaction war erfolglos ;(");
            e.printStackTrace();
        }
    }
    @Override
    public boolean insertKonto(Konto k) {
        if (k == null) return false;
        List<String> insertStrings = new ArrayList<>();
        char kontotyp = 'G';
        if (k instanceof Giro) {
            Giro g = (Giro)k;
            insertStrings.add(
                    "insert into giro (kontonummer, kreditlimit) " +
                            "value (" + k.getKontonummer() + ", " + g.getKreditlimit() +")"
            );
        }
        else if (k instanceof Sparkonto){
            kontotyp = 'S';
            Sparkonto s = (Sparkonto)k;
            insertStrings.add(
                    "insert into sparkonto (kontonummer, zinssatz) " +
                            "value (" + k.getKontonummer() + ", " + s.getZinssatz() + ")"
            );
            if (k instanceof Festzinskonto) {
                kontotyp = 'F';
                Festzinskonto f = (Festzinskonto) k;
                insertStrings.add(
                        "insert into festzinskonto (kontonummer, laufzeit, abgelaufeneZeit) " +
                                "value (" + k.getKontonummer() + ", " + f.getLaufzeit() + ", " + f.getAbgelaufeneZeit() + ")"
                );
            }
        }

        insertStrings.add(
                "insert into konto (kontonummer, kontoinhaber, kontostand, kontotyp) "+
                "value ("+ k.getKontonummer() +", '"+k.getInhaber()+"', "+ k.getKontostand() +", '"+kontotyp+"')"
        );
        executeSQLStatements(insertStrings);
        return false;
    }

    @Override
    public Konto getKontoByKontonummer(int kontonummer) {
        PreparedStatement selectCommand = null;
        try {
            selectCommand = sqlConnection.prepareStatement("""
                            
                    select kontonummer, kontoinhaber, kontostand, kontotyp, 
                            if(kontotyp = 'G', kreditlimit, 0.0) as kreditlimit, 
                            if(kontotyp = 'S' or kontotyp = 'F', zinssatz, 0.0) as zinssatz, 
                            if(kontotyp = 'F', laufzeit, 0) as laufzeit, 
                            if(kontotyp = 'F', abgelaufeneZeit, 0) as abgelaufeneZeit 
                            from konto as k 
                            left join giro as g using (kontonummer) 
                            left join sparkonto as s using (kontonummer) 
                            left join festzinskonto as f using (kontonummer) 
                            where kontonummer = ?""");
            selectCommand.setInt(1,kontonummer);
            /*  Die Funktion "setInt()" durchsucht einen String nach "?" und ersetzt das "?" an der im
                Funktionsparameter angegebenen Stelle durch eine Variable.
                In diesem Fall wird das PreparedStatement selectCommand nach dem ersten (1) "?" durchsucht und
                ersetzt dieses durch die Variable Kontonummer.
             */
            ResultSet ergebnisZeile = selectCommand.executeQuery();
            if (ergebnisZeile.next()){
                int dbKontonummer = ergebnisZeile.getInt("kontonummer");
                String dbKontoinhaber = ergebnisZeile.getString("kontoinhaber");
                double dbKontostand = ergebnisZeile.getDouble("kontostand");
                char dbKontotyp = ergebnisZeile.getString("kontotyp").charAt(0);
                double dbKreditlimit = ergebnisZeile.getDouble("kreditlimit");
                double dbZinssatz = ergebnisZeile.getDouble("zinssatz");
                int dbLaufzeit = ergebnisZeile.getInt("laufzeit");
                int dbAbgelaufeneZeit = ergebnisZeile.getInt("abgelaufeneZeit");

                return neuesKonto(dbKontotyp, dbKontonummer, dbKontoinhaber, dbKontostand,
                        dbKreditlimit, dbZinssatz, dbLaufzeit, dbAbgelaufeneZeit);
            }
        } catch (SQLException e) {e.printStackTrace();}

        return null;
    }

    private Konto neuesKonto(char dbKontotyp, int dbKontonummer,
                             String dbKontoinhaber, double dbKontostand,
                             double dbKreditlimit, double dbZinssatz,
                             int dbLaufzeit, int dbAbgelaufeneZeit) {
        if (dbKontotyp == 'G')
            return new Giro(dbKontonummer, dbKontostand, dbKontoinhaber, dbKreditlimit);
        if (dbKontotyp == 'S')
            return new Sparkonto(dbKontonummer, dbKontostand, dbKontoinhaber,dbZinssatz);
        if (dbKontotyp == 'F')
            return new Festzinskonto(dbKontonummer, dbKontostand, dbKontoinhaber,dbZinssatz,
                                        dbLaufzeit, dbAbgelaufeneZeit);
        return null;
    }

    @Override
    public List<Konto> getAllKonten() {
        List<Konto> resultList = new ArrayList<>(); // wir wollen eine Liste mit allen Konten, also muss diese angelegt werden.
        try {
            PreparedStatement selectCommand = sqlConnection.prepareStatement("""              
                    select kontonummer, kontoinhaber, kontostand, kontotyp,
                            if(kontotyp = 'G', kreditlimit, 0.0) as kreditlimit,
                            if(kontotyp = 'S' or kontotyp ='F', zinssatz, 0.0) as zinssatz,
                            if(kontotyp = 'F', laufzeit, 0) as laufzeit,
                            if(kontotyp = 'F', abgelaufeneZeit, 0) as abgelaufeneZeit 
                            from konto as k 
                            left join giro as g using (kontonummer) 
                            left join sparkonto as s using (kontonummer) 
                            left join festzinskonto as f using (kontonummer)
                            """);
            //"where kontonummer = ? ")      // wird nicht mehr benötigt, da wir alle Ergebnisse wollen und nicht nur ein Ergebnis.

            ResultSet ergebnisZeile = selectCommand.executeQuery();
            while (ergebnisZeile.next()){
                /* eine while-Schleife, da wir nicht wissen, wie viele Zeilen unsere Ergebnistabelle hat,
                   die wir der Datenbankabfrage "ResultSet ergebnisZeile = selectCommand.executeQuery();" erhalten
                   Die Schleife prüft nach einem boolschen Wert. In der Variable "ergebnisZeile" befindet sich eine Tabelle,
                   in der wir uns zum start in der 0. Zeile (Attributnamen/ Überschriftenzeile) befinden.
                   Wenn wir nach "ergebnisZeile.next()" prüfen, wird geprüft, ob die Tabelle eine Zeile 1 hat.
                   Wenn ja, wird die Schleife so lange ausgeführt, bis wir an der letzten Zeile der Tabelle angekommen sind.
                   Dabei spielt es z.B. keine Rolle, ob nach der Kontonummer "1" die Kontonummer "4" kommt.
                   In der Ergebnistabelle wären die beiden in Zeile 1 und 2.
                 */

                int dbKontonummer = ergebnisZeile.getInt("kontonummer");
                String dbKontoinhaber = ergebnisZeile.getString("kontoinhaber");
                double dbKontostand = ergebnisZeile.getDouble("kontostand");
                char dbKontotyp = ergebnisZeile.getString("kontotyp").charAt(0);
                double dbKreditlimit = ergebnisZeile.getDouble("kreditlimit");
                double dbZinssatz = ergebnisZeile.getDouble("zinssatz");
                int dbLaufzeit = ergebnisZeile.getInt("laufzeit");
                int dbAbgelaufeneZeit = ergebnisZeile.getInt("abgelaufeneZeit");
                // In diesem Block werden die Werte der Datenbankattribute der Tabellenzeile den Java Attributen zugeordnet.
                // Anschließend wird daraus ein neues Objekt erstellt und dieses wird dann der resultList hinzugefügt.
                Konto konto = neuesKonto(dbKontotyp, dbKontonummer, dbKontoinhaber, dbKontostand,
                        dbKreditlimit, dbZinssatz, dbLaufzeit, dbAbgelaufeneZeit);
                resultList.add(konto);
            }
        } catch (SQLException e) {e.printStackTrace();}
        return resultList;
        }
    @Override
    public void updateKonto(int kontonummer, Konto k) {
        deleteKonto(kontonummer);
        insertKonto(k);
    }

    @Override
    public void deleteKonto(int kontonummer) {
        List<String> deleteStrings = new ArrayList<>();
        String[] tableNames = {"konto", "giro", "sparkonto","festzinskonto"};
        for (String tableName : tableNames) {
            deleteStrings.add("delete from " + tableName + " where kontonummer = " + kontonummer );
        }
        executeSQLStatements(deleteStrings);
    }

    @Override
    public int letzteAktuelleKontonummer() {
        try {
            Statement statement = sqlConnection.createStatement();
            ResultSet ergebnisZeile = statement.executeQuery(
                    """
                            select max(kontonummer) as nächsteKontonummer from konto
                            """
            );
            if (ergebnisZeile.next())
                return ergebnisZeile.getInt("nächsteKontonummer");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}