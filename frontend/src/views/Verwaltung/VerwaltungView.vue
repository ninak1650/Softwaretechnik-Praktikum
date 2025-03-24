VerwaltungView:
<template>
    <BareboneNavbar />
    <div class="hero is-small">
        <div class="hero-body">
            <section>
                <b-tabs type="is-toggle-rounded" size="is-medium" position="is-centered">
                    <b-tab-item 
                        label="Standort auswählen" 
                        icon="store-marker" 
                        name="Standort"
                    >
                        <StandortauswahlComponent/>
                    </b-tab-item>
                    <b-tab-item 
                        label="Benutzerkontenverwaltung" 
                        icon="account-edit" 
                        name="Nutzerdaten bearbeiten"
                    >
                        <NutzerKontoComponent/>
                    </b-tab-item>
                    <b-tab-item 
                        label="CSV-einlesen" 
                        icon="file-upload-outline" 
                        name="CSV-einlesen"
                    >
                        <CsvImportComponent/>
                    </b-tab-item>
                    <b-tab-item 
                        label="Abrechnung Exportieren" 
                        icon="download" 
                        name="Abrechnung Exportieren"
                    >
                        <AbrechnungExportComponent/>
                    </b-tab-item>
                </b-tabs>
            </section>
        </div>
    </div>
</template>

<script>
import BareboneNavbar from '../../components/navbars/BareboneNavbar.vue'
import StandortauswahlComponent from '../../components/Verwaltung/StandortauswahlComponent.vue'
import CsvImportComponent from '../../components/Verwaltung/CsvImportComponent.vue'
import NutzerKontoComponent from '../../components/Verwaltung/NutzerKontoComponent.vue'
import AbrechnungExportComponent from '../../components/Verwaltung/AbrechnungExportComponent.vue'

export default {
    name: "VerwaltungView",
    components: {
        BareboneNavbar,
        StandortauswahlComponent,
        CsvImportComponent,
        NutzerKontoComponent,
        AbrechnungExportComponent
    },
};
</script>