# Medical-Record-Manager-Java
Aplicație Java modulară (Layered Architecture) cu persistență polimorfică: SQL, Binary, Text &amp; RAM. Include interfață dublă (JavaFX &amp; CLI) configurabilă din settings.properties. Folosește Generic Repository, Java Streams pentru rapoarte, Unit Testing (JUnit >90% coverage) și Java Faker pentru populare baze de date. 
# Medical Appointment Management System

## Descriere Proiect
O aplicație Java dezvoltată pe o arhitectură stratificată (Layered Architecture), concepută pentru a gestiona entități complexe și programări. Proiectul pune accent pe modularitate, utilizarea genericității și oferă multiple modalități de persistență a datelor, toate configurabile prin fișiere externe.

## Arhitectură și Module
Proiectul este împărțit în patru straturi principale:
1. **Domain**: Conține entitățile și clasele abstracte. Toate obiectele sunt unic identificabile printr-un ID generat automat și persistent.
2. **Repository**: Implementare generică pentru stocarea datelor. Suportă multiple mecanisme de persistență.
3. **Service**: Conține logica de business și procesarea datelor folosind Java 8 Streams pentru generarea de rapoarte.
4. **User Interface**: Interfață duală (Linie de comandă și Interfață Grafică JavaFX).

## Caracteristici Tehnice
- **Persistență Multi-Sursă**: Sistemul permite stocarea datelor în format Text, Binar (Serializare Java), Memorie (RAM) sau bază de date SQL.
- **Configurare Externă**: Tipul de repository (SQL, Binary, Text) și modul de afișare (CLI, JavaFX) se modifică prin fișierul settings.properties, fără modificarea codului sursă.
- **Genericitate**: Utilizarea interfețelor și claselor generice pentru operațiile CRUD, permițând extinderea ușoară cu noi entități.
- **Sistem de Identificare**: Generator de ID-uri care salvează ultima stare într-un fișier text pentru a asigura unicitatea la repornirea aplicației.
- **Validare și Excepții**: Ierarhie proprie de excepții (RepositoryException, DuplicateIDException etc.) pentru gestionarea robustă a erorilor de input sau de sistem.

## Tehnologii Utilizate
- Java 17+
- JavaFX pentru interfața grafică
- JDBC pentru integrarea cu baza de date SQL
- JUnit 5 pentru testare unitară (Code coverage > 90%)
- Java Faker pentru generarea de date pseudo-aleatoare

## Instrucțiuni de Configurare
Aplicația folosește un fișier de tip properties pentru controlul execuției. Exemplu de configurare în settings.properties:

Repository = database
Patients = patients.bin
Appointments = appointments.bin
Display = javafx

## Instalare și Rulare
1. Clonează repository-ul local.
2. Configurează parametrii doriți în src/main/resources/settings.properties.
3. Rulează clasa Main pentru a porni aplicația în modul selectat.
