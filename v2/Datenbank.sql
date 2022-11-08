create table 2512_kontodb.konto (
	kontonummer INT PRIMARY KEY,
    kontoinhaber varchar(30),
    kontostand double
);
alter table 2512_kontodb.konto add column (
	kontotyp char(1)
    );
    
create table 2512_kontodb.sparkonto (
	kontonummer int primary key,
    zinssatz double
);
create table 2512_kontodb.festzinskonto (
	kontonummer int primary key,
    laufzeit int,
    abgelaufeneZeit int
    );
    
	insert into 2512_kontodb.konto
		(kontonummer, kontoinhaber, kontostand, kontotyp)
		value
		(1,'Donald', 500.0, 'G'),
		(2, 'Mickey', -500.1, 'G'),
        (3, 'Dagobert', 20.0, 'S'), 
        (4, 'Goofy', 250000.0, 'F');
	insert into 2512_kontodb.giro 
		(kontonummer, kreditlimit)
	value
		(1, 1000.0),
		(2, 5000.0);
    insert into 2512_kontodb.sparkonto 
		(kontonummer, zinssatz)
    value     
		(3, 0.05),
        (4, 0.1);
	insert into 2512_kontodb.festzinskonto
		(kontonummer, laufzeit, abgelaufeneZeit)
    value
		(4, 10, 0);

select * from konto, giro, sparkonto, festzinskonto;

select * from konto as k
	left join giro as g using (kontonummer)
	left join sparkonto as s using (kontonummer)
	left join festzinskonto as f using (kontonummer)
    where kontonummer = 3;

select kontonummer, kontoinhaber, kontostand, kontotyp,
	if(kontotyp = 'G', kreditlimit, 0.0) as kreditlimit,
    if(kontotyp = 'S' or kontotyp ='F', zinssatz, 0.0) as zinssatz,
    if(kontotyp = 'F', laufzeit, 0) as laufzeit,
    if(kontotyp = 'F', abgelaufeneZeit, 0) as abgelaufeneZeit
    from konto as k
	left join giro as g using (kontonummer)
	left join sparkonto as s using (kontonummer)
	left join festzinskonto as f using (kontonummer)
;
