# Essensbestellung Borna [Gruppe 18]

![Pipeline](https://git.informatik.uni-leipzig.de/SWS/lehre/ws-2024-2025/swt-p/projects/group-18/badges/main/pipeline.svg)
![Coverage](https://git.informatik.uni-leipzig.de/SWS/lehre/ws-2024-2025/swt-p/projects/group-18/badges/main/coverage.svg)

Projekt: Bor'na Lecker!

- [Projektbeschreibung](docs/Projektbeschreibung.md)
- [Use-Case-Diagramm](docs/Use-Case-Diagramm.md)
- [Klassendiagramm](docs/Klassendiagramm.md)
- [User Stories](docs/User-Stories.md)
- [License](License)

## Live-Demo

URL: http://172.26.92.167/

| Rolle          | Nutzername  | Passwort |
| -------------- | ----------- | -------- |
| Verwaltung     | clindner    | geheim   |
| Gruppenleiter  | amerkel     | geheim   |
| Standortleiter | fmerz       | geheim   |
| Küchenleiter   | klauterbach | geheim   |

## Lokale Entwicklung

> Die Anwendung erstellt beim starten einen Verwaltungsaccount mit Nutzername und Passwort `Admin`.

### Mit Docker

- Mit vorgebauten Images:
  - [docker-compose.yaml](docker-compose.yaml) und [Caddyfile](Caddyfile) in einem Ordner speichern
  - mit `docker compose pull` die Images herunterladen
  - mit `docker compose up` die Anwendung Starten
- Images lokal bauen:
  - Repository clonen
  - mit `docker compose build` die Images bauen
  - mit `docker compose up` die Anwendung Starten

Die Anwendung läuft dann auf http://localhost:80

### Ohne Docker

#### Requirements

- JDK 21
- Node 22
- PostgreSQL 16

#### Postgres

Die Datenbank versucht sich standardmäßig mit folgenden Werten zu verbinden:

- database: `bornalecker`
- user: `postgres`
- password: `postgres`
- host: `localhost`
- port: `5432`

Diese können in `application.properties` oder per mvn args auch angepasst werden.

#### Backend

Das Spring-Boot backend kann mit dem beigefügten maven wrapper gestartet werden:

```sh
cd backend
./mvnw spring-boot:run
```

Default Port: `8090`

#### Frontend

```sh
cd frontend
npm i # dependencies herunterladen
npm run dev
```

Die URL für das frontend kann aus der Kommandozeile entnommen werden.

### Testing

#### Backend

```sh
./mvnw verify
```

#### Frontend

```
npm run test
```

## Mitwirkende

- [Annabel Freno](@gf40oxox)
- [Julian Keppler](@vl09nidi)
- [Max Herzler](@mh63gywa)
- [(Pascal Weidner)](@pw16qeda)
- [Robert Klemm](@zw62oniv)
- [Sumavia Randhawa](@zm33obex)
- [Tom Bogatsch](@tb65faza)
- [Yu-Ju Chen](@nm22gexo)

- [Max Weber](@mweber) (Tutor)
- [Stefan Jahns](@jahns) (Tutor)
