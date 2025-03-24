package de.uni_leipzig.swtp.borna_lecker.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import de.uni_leipzig.swtp.borna_lecker.entities.Kunde;
import de.uni_leipzig.swtp.borna_lecker.repositories.GruppeRepository;
import de.uni_leipzig.swtp.borna_lecker.repositories.KundeRepository;

@Service
public class CSVImportService {

    @Autowired
    private KundeRepository kundeRepository;

    @Autowired
    private GruppeRepository gruppeRepository;

    private void importToDB(InputStream data) {

        Iterable<CSVRecord> records;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(data, "CP1252"));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                                .withDelimiter(';'));) {
            records = csvParser.getRecords();
            System.out.println(csvParser.getHeaderNames());
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }

        for (CSVRecord record : records) {
            // column mapping
            int kunde_nr = Integer.parseInt(record.get("Kunden-Nr."));
            String kuerzel = record.get("Kürzel");
            String standort = record.get("Bereich");
            int gruppe_nr = Integer.parseInt(record.get("Gruppe-Nr."));
            String gruppe_name = record.get("Gruppen-Name 1");
            // String gruppenLeiter = record.get("Gruppen-Name 2");

            Optional<Gruppe> optGruppe = gruppeRepository.findById(gruppe_nr);
            Gruppe gruppe;
            if (optGruppe.isEmpty()) {
                gruppe = new Gruppe();
                gruppe.setGruppenNummer(gruppe_nr);
                gruppe.setGruppenName(gruppe_name);
                gruppe.setStandort(standort);
                gruppeRepository.save(gruppe);
            } else {
                gruppe = optGruppe.get();
            }

            Optional<Kunde> optKunde = kundeRepository.findById(kunde_nr);
            if (optKunde.isEmpty()) {
                String name = kuerzel.replaceAll("\\d+$", "").replaceAll("([a-z])([A-Z])", "$1 $2");
                Kunde newKunde = new Kunde();
                newKunde.setKundenNummer(kunde_nr);
                newKunde.setName(name);
                newKunde.setStandort(standort);
                newKunde.setGruppe(gruppe);
                kundeRepository.save(newKunde);
            }

        }

    }

    public void processFile(MultipartFile file) {
        try {
            importToDB(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
