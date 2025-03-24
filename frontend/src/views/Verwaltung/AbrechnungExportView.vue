<template>
    <section>
      <h1 class="title">Abrechnung Export</h1>
  
      <AbrechnungExportComponent 
        :selectedStandort="selectedStandort"
        :selectedGruppe="selectedGruppe"
        :selectedMitarbeiter="selectedMitarbeiter"
        :dateFrom="dateFrom"
        :dateTo="dateTo"
      />
    </section>
  </template>
  
  <script>
  import AbrechnungExportComponent from "@/components/Verwaltung/AbrechnungExportComponent.vue";
  
  export default {
    components: {
      AbrechnungExportComponent,
    },
    data() {
      return {
        selectedStandort: null,
        selectedGruppe: null,
        selectedMitarbeiter: null,
        dateFrom: null,
        dateTo: null,
      };
    }
  };
  </script>
