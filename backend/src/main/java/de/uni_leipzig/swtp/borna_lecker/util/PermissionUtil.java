package de.uni_leipzig.swtp.borna_lecker.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni_leipzig.swtp.borna_lecker.entities.Account.Rolle;
import de.uni_leipzig.swtp.borna_lecker.entities.Gruppe;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PermissionUtil {

    @Autowired
    private JwtUtil jwtUtil;

    public void DenyFremdeGruppenFürGruppenleitung(HttpServletRequest request, Gruppe gruppe)
            throws SecurityException {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (jwtUtil.extractRole(token) != Rolle.GRUPPENLEITUNG && jwtUtil.extractRole(token) != Rolle.KÜCHE) {
            return;
        }
        if (jwtUtil.extractGruppe(token) == gruppe.getGruppenNummer()) {
            return;
        }
        if (gruppe.getVertretung() != null
                && gruppe.getVertretung().getBenutzername().equals(jwtUtil.extractUsername(token))) {
            return;
        }
        throw new SecurityException("Gruppenleiter dürfen nur auf die eigene oder vertretene Gruppe zugreifen!");

    }

    public void DenyFremdeStandorteFürStandortleitung(HttpServletRequest request, String standort)
            throws SecurityException {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (jwtUtil.extractRole(token).equals(Rolle.STANDORTLEITUNG)) {
            if (jwtUtil.extractStandort(token) != standort) {
                throw new SecurityException("Standortleiter dürfen nur auf den eigenen Standort zugreifen!");
            }
        }
    }

    public void DenyFremdeAccounts(HttpServletRequest request, String benutzername) throws SecurityException {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (jwtUtil.extractUsername(token).equals(benutzername)) {
            return;
        }
        throw new SecurityException("Benutzer darf nur auf eigene Daten zugreifen!");
    }
}
