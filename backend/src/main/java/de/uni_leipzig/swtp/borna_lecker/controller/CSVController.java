package de.uni_leipzig.swtp.borna_lecker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.uni_leipzig.swtp.borna_lecker.annotations.RequireRole;
import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.services.CSVImportService;

@CrossOrigin
@RestController
@RequestMapping("/csv")
public class CSVController {

    @Autowired
    private CSVImportService csvImportService;

    @RequireRole(Rolle.VERWALTUNG)
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public void importCSV(@RequestParam("file") MultipartFile file) {
        csvImportService.processFile(file);
    }
}
