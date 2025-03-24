<template>
    <BareboneNavbar />
    <div class="hero is-small">
        <div class="hero-body">
            <section>
                <b-tabs type="is-toggle-rounded" size="is-medium" position="is-centered">
                    <b-tab-item 
                        label="Gruppenbestellung" 
                        icon="format-list-checks" 
                        name="gruppenbestellung"
                    >
                        <EssensbestellungComponent />
                    </b-tab-item>
                    <b-tab-item 
                        label="Essensbestellung" 
                        icon="qrcode-scan"
                        name="essensbestellung"
                    >
                    <!-- Button zum Weiterleiten zur Scan-Ansicht -->
                        <div class="has-text-centered">
                            <router-link to="/BestellungAufnehmen">
                                <b-button class="mt-6" type="is-primary" size="is-large" icon-left="qrcode-scan">
                                    &nbsp;Scannen starten
                                </b-button>
                            </router-link>
                        </div>
                    </b-tab-item>
                    <b-tab-item 
                        label="Bestellungen Drucken" 
                        icon="printer" 
                        name="Drucken"
                    >                      
                    <b-tabs type="is-boxed">
                        <b-tab-item
                        label="Heute">
                            <BestellungenExportEinTagComponent/>  
                        </b-tab-item>
                        <b-tab-item
                        label="14-Tage-Bestellübersicht">
                            <BestellungenExport14TageComponent/>
                        </b-tab-item>
                    </b-tabs>
                    </b-tab-item>
                </b-tabs>
            </section>
        </div>
    </div>
</template>

<script>
import BareboneNavbar from '../components/navbars/BareboneNavbar.vue'
import EssensbestellungComponent from '../components/EssensbestellungComponent.vue'
import BestellungAufnehmenView from './BestellungAufnehmenView.vue';
import BestellungenExportEinTagComponent from '@/components/BestellungenExportEinTagComponent.vue';
import BestellungenExport14TageComponent from '@/components/BestellungenExport14TageComponent.vue';

export default {
    name: "GruppenleitungKuecheView",
    components: {
        BareboneNavbar,
        EssensbestellungComponent,
        BestellungenExportEinTagComponent,
        BestellungenExport14TageComponent,
    },
};
</script>
