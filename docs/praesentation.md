---
marp: true
theme: default
paginate: true
title: "Bau eines Parsers für Auszüge der Sprache Java"
---

<!--
Präsentationsfolien (Markdown / Marp).
Platzhalter `> 📸 Screenshot:` markieren Stellen, an denen später Screenshots
aus Plane, GitHub bzw. der laufenden Software eingefügt werden.
Rendern z. B. mit: `marp docs/praesentation.md` oder der Marp-VS-Code-Extension.
-->

# Bau eines Parsers für Auszüge der Sprache Java

### Informatik-Projektarbeit · LK Informatik · Gymnasium Couven

**Team:** Marin Benke · Jiayi · Jannis Holtmann · Diana Butsch
**Jury-Präsentation:** 18.06.2026 (Generali)

> Scanner + rekursiver Abstiegsparser für ein Java-Subset mit Swing-GUI –
> agil entwickelt mit Plane & GitHub.

---

## Agenda

1. **Ziel** – Aufgabe & User Stories
2. **Prozess** – agiles Vorgehen, Sprints
3. **Tools** – Plane, GitHub, JetBrains
4. **Agiles Projektmanagement** – Cycles, Story Points, Rollen
5. **Technische Entscheidungen** – Architektur & modernes Java
6. **Endprodukt** – Live-Demo
7. **Fazit & Reflexion**

---

## 1 · Ziel der Aufgabe

**Bezug:** Theoretische Informatik – Automaten, formale Sprachen & Grammatiken.

Ein zweistufiger Analysator für ein **Subset von Java** bauen:

- **Scanner (Lexer):** Quelltext → klassifizierte **Tokenliste**
- **Parser:** Tokenliste → Prüfung gegen eine **kontextfreie Grammatik**
- **GUI:** Eingabe, getrennte Buttons für Scannen & Parsen, sofortiges Feedback inkl. **Fehlerposition**

Unterstütztes Subset: Methodendeklaration, Zuweisungen, arithmetische
Ausdrücke, `if/else`, `while`.

---

## 1 · Die drei User Stories

| # | Als Anwender möchte ich … | Akzeptanzkriterium |
|---|---------------------------|--------------------|
| **1** | einen **Scanner**, der den Programmtext scannt | erkannte Token werden als Liste ausgegeben |
| **2** | einen **Parser**, der die Token gegen die Grammatik prüft | korrektes Programm → bestätigt; Fehler → **Meldung + Position** |
| **3** | eine **grafische Oberfläche** | Quelltext eingeben, Scanner/Parser getrennt starten, Fehlerposition sehen |

> User Story 2 war das am stärksten gewichtete, „urgent" eingestufte Kriterium.

---

## 2 · Prozess – Leitidee

**Strikte Trennung, einseitiger Datenfluss:**

```
String → Scanner → Tokenliste → Parser → ParseResult
                                            ↓
                                           GUI
```

- Die **GUI enthält keine Logik** – nur Verdrahtung in den Button-Handlern.
- Das **`token`-Paket ist der gemeinsame Vertrag** und wurde **zuerst** fixiert
  → Scanner, Parser und GUI konnten **parallel** entwickelt werden.

---

## 2 · Prozess – Sprintverlauf

| Sprint | Zeitraum | Ziel |
|--------|----------|------|
| **1** | 02.–05.06. | Token-Schnittstelle fixiert, Scanner-Kern |
| **2** | 05.–09.06. | Scanner fertig + getestet, Parser-Gerüst |
| **3** | 09.–12.06. | Parser-Kern + Fehlerposition (US 2) |
| **4** | 12.–16.06. | GUI + End-to-End-Verdrahtung (US 3) |
| **5** | 16.–18.06. | Härtung, volle Grammatik, Präsentation |

> Das Projekt wurde kurz vor Schluss **um eine Woche verlängert** –
> sichtbar in nachgezogenen Cycles im Board.

> 📸 Screenshot: Plane – Cycles-Übersicht (plane.marinbenke.dev)

---

## 3 · Tools

| Tool | Einsatz |
|------|---------|
| **Plane** (selfhosted) | Backlog, Sprints (Cycles), Work Items, Story Points, Dailies |
| **GitHub** | Versionskontrolle, `dev`-Branch, Worktrees, CI (GitHub Actions) |
| **JetBrains IDE** (IntelliJ IDEA) | einheitliche Entwicklungsumgebung im Team |
| **Maven** | Build, abhängigkeiten, ausführbares JAR |
| **JUnit 5** | Unit-Tests für Scanner & Parser |

> Plane unter **plane.marinbenke.dev** – in der Basis-Version selbst hostbar.

> 📸 Screenshot: Plane-Board & GitHub-Repo

---

## 4 · Agiles Projektmanagement (1/2)

**Warum Plane + GitHub?** Die geforderte agile/SCRUM-Arbeitsweise sauber
abbilden – Planung und Code an einem durchgehenden roten Faden.

- **Cycles = Sprints:** feste Zeiträume, Aufgaben aus dem Backlog zugeteilt
- **Work Items = Todos** mit Akzeptanzkriterien, Zuweisung & Priorität
- **Story Points** über **Fibonacci** (1, 2, 3, 5, 8, 13) – steigende
  Ungenauigkeit für große Tasks
- **Dailies & Sprint-Retros** je Sprint dokumentiert (benotet)

> 📸 Screenshot: Work Item mit Story Points & Akzeptanzkriterien

---

## 4 · Agiles Projektmanagement (2/2) – Rollen & Team

| Person | Rolle |
|--------|-------|
| **Marin Benke** | **Product Owner** & Developer – Planung, Backlog, Delegation |
| **Jannis Holtmann** | Developer – GUI, UML |
| **Diana Butsch** | Developer – Scanner / Tokenizer |
| **Jiayi** | Developer – Token-Modell, Tests |

**Git-Workflow:** Feature-Entwicklung über `dev`-Branch, zwischenzeitlich
**Worktrees**, die zurückgemmerged wurden – nachvollziehbar in der Commit-History.

> 📸 Screenshot: GitHub Network-Graph / Commit-History

---

## 5 · Architektur – Pakete & Klassen

| Paket | Klasse | Verantwortung |
|-------|--------|---------------|
| `token` | `TokenType` (enum) | alle Typen mit Nr. 0–16 |
| `token` | `Token` (record) | type, lexeme, Position |
| `token` | `Position` (record) | zeile, spalte |
| `scanner` | `Scanner` / `Tokenizer` | `tokenize(): List<Token>` |
| `parser` | `Parser` | `parse(): ParseResult` |
| `parser` | `ParseResult` (record) | ok, message, errorPos |
| `gui` | `MainWindow` (JFrame) | Eingabe, Buttons, Ausgabe |

> 📸 Screenshot: UML-Klassendiagramm

---

## 5 · Token-Tabelle (0–16)

| Nr | Token | Nr | Token | Nr | Token |
|----|-------|----|-------|----|-------|
| 0 | fehler | 6 | klammerauf | 12 | name |
| 1 | klasse | 7 | klammerzu | 13 | verglop |
| 2 | typ | 8 | zuweisungsop | 14 | wenn |
| 3 | blockauf | 9 | punktop | 15 | sonst |
| 4 | blockzu | 10 | strichop | 16 | solange |
| 5 | semikolon | 11 | zahl | | |

`klasse(1)` / `wenn(14)` / `sonst(15)` / `solange(16)` von Anfang an für die
**Maximal-Grammatik** reserviert.

---

## 5 · Technische Entscheidungen (modernes Java)

- **`record` als Datentyp** (`Token`, `Position`, `ParseResult`) – unveränderlich,
  knapp, ohne Boilerplate; idiomatisches modernes Java (21).
- **`TokenType` als `enum` mit Nummern-Feld** (`TYP(2)`) – typsicher,
  Nummer bleibt für die GUI verfügbar. *Alternative:* rohe `int`s – näher an der
  Vorlage, aber unsicher.
- **Fehler über Token Nr. 0** statt Exceptions – unbekanntes Zeichen wird
  direkt im Output sichtbar.
- **`switch`-Expressions** im Tokenizer (`case "if" -> …`).
- **`Position` (Zeile/Spalte) verpflichtend** im Token – Voraussetzung für die
  Fehleranzeige (User Story 2).

---

## 5 · Der Parser – rekursiver Abstieg

- **Eine Methode pro Nichtterminal** – der **Aufrufstack** bildet das geforderte
  **Stackprinzip** ab.
- Grammatik **rechtsrekursiv / LL(1)**, keine Linksrekursion.
- **Infrastruktur:** `peek()`, `advance()`, `match()`, `expect()` + `EOF`-Sentinel.
- **`ParseResult` nach außen** statt Exception → saubere GUI-Schnittstelle;
  interne `ParseException` reicht nur die Fehlerposition hoch.

```java
private void parseFaktor() {
    if (match(NAME) || match(ZAHL)) advance();
    else if (match(KLAMMERAUF)) { advance(); parseAusdruck(); expect(KLAMMERZU); }
    else throw new ParseException("Erwartet: NAME, ZAHL oder '('", peek().position());
}
```

---

## 5 · Grammatik (umgesetzt: Maximal-Variante)

```
Methode    -> TYP NAME KLAMMERAUF KLAMMERZU Block
Block      -> BLOCKAUF Anwfolg BLOCKZU
Anwfolg    -> (Anweisung)*
Anweisung  -> Block | Zuweisung | BedAnw | SolangeAnw
BedAnw     -> WENN KLAMMERAUF Vergleich KLAMMERZU Anweisung (SONST Anweisung)?
SolangeAnw -> SOLANGE KLAMMERAUF Vergleich KLAMMERZU Anweisung
Zuweisung  -> NAME ZUWEISUNGSOP Ausdruck SEMIKOLON
Vergleich  -> Ausdruck VERGLOP Ausdruck
Ausdruck   -> Term (STRICHOP Ausdruck)?
Term       -> Faktor (PUNKTOP Term)?
Faktor     -> NAME | ZAHL | KLAMMERAUF Ausdruck KLAMMERZU
```

> Über die **Mindestanforderung** (reduzierte Grammatik) hinaus: `if/else` &
> `while` umgesetzt.

---

## 6 · Endprodukt – Demo

**Beispiel aus der Aufgabe:**

```java
void main() {
    x = 2 * 3;
}
```

→ Tokenliste: `2→12→6→7→3→12→8→11→9→11→5→4` → **„Syntaktisch korrekt."**

**Fehlerfall** (fehlendes `;`) → Popup mit Meldung **+ Zeile/Spalte**.

> 📸 Screenshot: Software – erfolgreicher Parse
> 📸 Screenshot: Software – Syntaxfehler-Popup mit Position

---

## 6 · Qualitätssicherung

- **JUnit-5-Tests** für Scanner (Edge-Cases: Whitespace, mehrstellige Zahlen,
  ungültige Zeichen, leere Eingabe) und Parser (gültig / fehlendes Semikolon /
  falsche Reihenfolge).
- **GitHub Actions**: Tests laufen automatisch bei jedem Push.
- End-to-End-Test mit dem `void main()`-Beispiel der Aufgabenstellung.

> 📸 Screenshot: Grüner CI-Run auf GitHub

---

## 7 · Fazit & Reflexion

**Was lief gut**
- Token-Vertrag zuerst → echtes paralleles Arbeiten
- Konsequentes Tracking in Plane, durchgehender roter Faden Planung ↔ Code
- Saubere Architektur dank `record` & klarer Paketgrenzen

**Probleme & Lösungen**
- Scanner-Bug: alles wurde als `name` erkannt → gemeinsam von Jannis & Marin gefixt
- Klausurphase (Mathe-LK, EK) reduzierte Kapazität → Wochenenden eingeplant
- Projekt um eine Woche verlängert → Sprint-Planung nachgezogen

**Nächstes Mal**
- Tests früher (TDD), kleinere & häufigere Merges, Branch-Schutz auf `dev`

---

# Vielen Dank!

### Fragen?

**Repo:** github.com/MarinGHG/Informatik-Projektarbeit
**Board:** plane.marinbenke.dev
