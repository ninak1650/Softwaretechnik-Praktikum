<template>
    <BareboneNavbar />
    <div class="container">
        <div class="hero-body">
            <h1 class="title">QR-Codesfür {{gruppenname}} </h1>
        </div>
        <b-button class="button is-light" @click="goBack">Zurück</b-button>
    <b-button class="button is-primary" @click="exportToPDF">
        <span class="has-text-weight-bold">QR-Codes als PDF exportieren</span>
    </b-button>
    <ul>
    </ul>
    </div>
    <div class="qr-container">
    <div v-for="kunde in kundennummerList" :key="kunde.kundeID" class="qr-item">
        <vue-qrcode :value="kunde.qrData" :size="150" ref="qrcodes"></vue-qrcode>
        <p>{{ kunde.name }}</p>
        <p>({{ kunde.kundeID }})</p>
    </div>
    </div>
   
</template>

<script>
import BareboneNavbar from '@/components/navbars/BareboneNavbar.vue';
import VueQrcode from '@chenfengyuan/vue-qrcode';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

export default {
    name: "QRCodesErstellenView",
    components: {
        BareboneNavbar,
        VueQrcode,
    },
    data(){
        return {
            gruppenname: this.$route.params.gruppenname,
            kundennummerList: [],

        };
    },

    mounted() {
        this.kundennummerList = history.state?.kundennummerList || [];
        if(this.kundennummerList.length === 0){
            console.error("keine kundenliste gefunden");
        }
    },
       
    methods: {
        goBack() {
            this.$router.go(-1);
        },

        async exportToPDF() {
            const pdf = new jsPDF({
            orientation: "portrait",
            unit: "mm",
            format: "a4"
        });
        const pageWidth = pdf.internal.pageSize.getWidth();
        const pageHeight = pdf.internal.pageSize.getHeight();
        const qrSize = 100;
        const qrX = (pageWidth - qrSize) / 2;
        const qrY = (pageHeight - qrSize) / 4;
        
        for(let i = 0; i < this.kundennummerList.length; i++){
            if(i > 0) pdf.addPage();
            const qrElement = document.querySelectorAll(".qr-item canvas")[i];
            if (!qrElement) continue;
            
            const canvas = await html2canvas(qrElement, {
                scale: 2, 
                useCors: true
            });
            const imgData = canvas.toDataURL("image/png");
            pdf.addImage(imgData, "PNG", qrX, qrY, qrSize, qrSize);
            pdf.setFontSize(16);
            pdf.text(this.kundennummerList[i].name, pageWidth/2, qrY + qrSize + 20, {align: "center"});
            pdf.text(`Kundennummer: ${this.kundennummerList[i].kundeID}`, pageWidth/2, qrY + qrSize + 40, {align: "center"});
        }
        pdf.save(`QR-Codes-${String(this.gruppenname || "QR-Codes Unbenannt")}.pdf`)
    }
}};


</script>

<style scoped>
.qr-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: left;
    gap: 30px;
    padding: 20px;
    margin-top: 40px;
    margin-left: 100px;
}
.qr-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    border-radius: 10px;
    background: #f8f8f8;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); 
}

</style>