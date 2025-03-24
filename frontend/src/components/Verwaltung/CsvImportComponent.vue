<template>
  <section class="section">
    <div class="container">
      <b-notification type="is-success" v-if="successMessage" @close="successMessage = ''">
        {{ successMessage }}
      </b-notification>
      <b-notification type="is-danger" v-if="errorMessage" @close="errorMessage = ''">
        {{ errorMessage }}
      </b-notification>
      <div class="box has-text-centered" @dragover.prevent @dragenter.prevent @drop="handleDrop">
        <b-icon icon="upload" size="is-large"/>
        <p class="subtitle">CSV-Datei hochladen </p>
        <input type="file" ref="fileInput" accept=".csv" style="display: none" @change="handleFileSelect" />
        <b-button class="button is-primary" icon-left="folder-arrow-up" size="is-large" @click="selectFile">
          Datei auswählen
        </b-button>
      </div>
    </div>
  </section>
</template>

<script>
import { api } from '@/api/api';

export default {

  data() {
    return {
      successMessage: '',
      errorMessage: '',
    };
  },
  methods: {
    selectFile() {
      this.$refs.fileInput.click();
    },
    handleFileSelect(event) {
      const file = event.target.files[0];
      if (file) {
        this.uploadFile(file);
      }
    },
    handleDrop(event) {
      const file = event.dataTransfer.files[0];
      if (file) {
        this.uploadFile(file);
      }
    },
    async uploadFile(file) {

      try {
        const response = await api.csv.uploadCSV(file);

        if (response.ok) {
          this.successMessage = 'File uploaded successfully!';
        } else {
          this.errorMessage = 'Failed to upload the file.';
        }
      } catch (error) {
        this.errorMessage = 'An error occurred during the upload.';
      }
    },
  },
};
</script>