# Java-Subset-Parser

> Informatik-Projektarbeit · LK Informatik · Gymnasium Couven · Jury-Präsentation 18.06.2026 (Generali)

## Team

| Name | Rolle |
|------|-------|
| Marin Benke | Product Owner & Developer (Planung, Backlog, Delegation) |
| Jannis Holtmann | Developer (GUI, UML) |
| Diana Butsch | Developer (Scanner / Tokenizer) |
| Jiayi | Developer (Token-Modell, Tests) |

---

## Projektbeschreibung

Zweistufiger Analysator für ein **Subset von Java**: ein **Scanner** zerlegt den
Quelltext in eine klassifizierte Tokenliste, ein **rekursiver Abstiegsparser**
prüft diese gegen eine kontextfreie Grammatik. Eine **Swing-GUI** erlaubt
Eingabe, getrenntes Starten von Scanner und Parser sowie sofortiges Feedback
inkl. Fehlerposition.

Unterstütztes Subset: Methodendeklaration, Zuweisungen, arithmetische Ausdrücke,
`if/else`, `while`.

Das Projekt wurde **agil** entwickelt – Sprintplanung in **Plane**
(plane.marinbenke.dev, selfhosted) kombiniert mit **GitHub** zur
Versionskontrolle. Einheitliche IDE: **JetBrains IntelliJ IDEA**.

📑 Präsentationsfolien: [`docs/praesentation.md`](docs/praesentation.md)

---

## Komponenten

| Paket | Beschreibung |
|-------|-------------|
| `de.couven.token` | Token-Typen und Datenmodell |
| `de.couven.scanner` | Scanner (Lexer) – Quelltext → Tokenliste |
| `de.couven.parser` | Rekursiver Abstiegsparser – Tokenliste → Syntaxprüfung |
| `de.couven.gui` | Swing-GUI – Eingabe, Scan-Button, Parse-Button |

---

## Token-Tabelle

| Nr | Symbol | Token |
|----|--------|-------|
| 0 | unbekanntes Zeichen | `fehler` |
| 1 | `class` | `klasse` |
| 2 | `void` / `int` / `String` | `typ` |
| 3 | `{` | `blockauf` |
| 4 | `}` | `blockzu` |
| 5 | `;` | `semikolon` |
| 6 | `(` | `klammerauf` |
| 7 | `)` | `klammerzu` |
| 8 | `=` | `zuweisungsop` |
| 9 | `*` `/` `%` | `punktop` |
| 10 | `+` `-` | `strichop` |
| 11 | Zahl | `zahl` |
| 12 | Bezeichner | `name` |
| 13 | `==` `<` `>` | `verglop` |
| 14 | `if` | `wenn` |
| 15 | `else` | `sonst` |
| 16 | `while` | `solange` |
| 17 | `for` | `fuer` |

`EOF` (-1) ist ein interner Sentinel und gehört nicht zur Tabelle.
`klasse` (1) und `fuer` (17) werden vom Scanner erkannt, sind aber (noch) nicht
Teil der geparsten Grammatik.

---

## Grammatik

### Reduziert (Mindestanforderung)

```
METHODE    ::= typ name klammerauf klammerzu BLOCK
BLOCK      ::= blockauf ZUWEISUNG blockzu
ZUWEISUNG  ::= name zuweisungsop AUSDRUCK semikolon
AUSDRUCK   ::= TERM | TERM strichop AUSDRUCK
TERM       ::= FAKTOR | FAKTOR punktop TERM
FAKTOR     ::= name | zahl | klammerauf AUSDRUCK klammerzu
```

### Maximal (volle Anforderung)

```
ANWFOLG    ::= ANWEISUNG (ANWFOLG)
ANWEISUNG  ::= BLOCK | ZUWEISUNG | BEDANW | SOLANGEANW
BEDANW     ::= wenn klammerauf VERGLEICH klammerzu ANWEISUNG (sonst ANWEISUNG)
SOLANGEANW ::= solange klammerauf VERGLEICH klammerzu ANWEISUNG
VERGLEICH  ::= AUSDRUCK verglop AUSDRUCK
```

---

## Build & Run

```bash
# Kompilieren
mvn compile

# Tests ausführen
mvn test

# Ausführbares JAR bauen
mvn package

# Starten
java -jar target/java-parser-1.0-SNAPSHOT.jar
```

---

## Projektstruktur

```
src/
├── main/java/de/couven/
│   ├── Main.java
│   ├── token/        # TokenType, Token
│   ├── scanner/      # Scanner
│   ├── parser/       # Parser
│   └── gui/          # MainWindow, ...
└── test/java/de/couven/
    ├── scanner/      # ScannerTest
    └── parser/       # ParserTest
```

---

## Technische Entscheidungen

- **`record` für Datentypen** (`Token`, `Position`, `ParseResult`) – unveränderlich
  und ohne Boilerplate, idiomatisches modernes Java (21).
- **`TokenType` als `enum` mit Nummern-Feld** (`TYP(2)`) – typsicher, die Nummer
  bleibt für die GUI-Anzeige verfügbar.
- **Fehler über Token Nr. 0** statt Exceptions im Scanner; unbekannte Zeichen
  werden direkt sichtbar.
- **Rekursiver Abstieg** – eine Methode pro Nichtterminal; der Aufrufstack bildet
  das geforderte Stackprinzip ab. Grammatik LL(1), keine Linksrekursion.
- **`ParseResult` nach außen** statt Exception → saubere GUI-Schnittstelle; die
  Fehlerposition (Zeile/Spalte) erfüllt das Akzeptanzkriterium aus User Story 2.

---

## Tools & Arbeitsweise

Das Projekt wurde **agil** umgesetzt:

- **Plane** (selfhosted, `plane.marinbenke.dev`) – Backlog, Sprints (Cycles),
  Work Items, Story Points nach Fibonacci (1, 2, 3, 5, 8, 13), Dailies & Retros.
- **GitHub** – Versionskontrolle, Entwicklung über `dev`-Branch und Worktrees,
  CI über **GitHub Actions** (Tests bei jedem Push).
- **JetBrains IntelliJ IDEA** – einheitliche IDE im Team.
- **Maven** + **JUnit 5** – Build und automatisierte Tests.
