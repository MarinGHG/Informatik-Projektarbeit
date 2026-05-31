# Java-Subset-Parser

> Informatik-Projektarbeit | Gymnasium Couven | 

## Team

| Name | Rolle |
|------|-------|
| Marin | – |
| Diana | – |
| Jannis | – |
| Jiayi | – |

---

## Projektbeschreibung

Scanner und rekursiver Abstiegsparser für ein Java-Subset mit Swing-GUI.
Product Owner: Generali (Jury-Präsentation 18.06.2026).

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
| 2 | `void` / `int` | `typ` |
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
| 13 | `==` | `verglop` |
| 14 | `if` | `wenn` |
| 15 | `else` | `sonst` |

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
