package de.uni_leipzig.swtp.borna_lecker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.uni_leipzig.swtp.borna_lecker.services.GruppenService;

@CrossOrigin
@RestController
@RequestMapping("/standorte")
public class StandortController {

    @Autowired
    private GruppenService gruppenService;

    @GetMapping("")
    public ResponseEntity<List<String>> getStandorte() {
        List<String> standorte = gruppenService.getStandorte();

        return new ResponseEntity<>(standorte, HttpStatus.OK);

    }
}
