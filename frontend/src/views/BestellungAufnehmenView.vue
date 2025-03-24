<template>

    <LeiterfunktionenComponent />

    <section class="hero is-large-with-navbar">
        <div class="hero-body">
            <section class="container"
                style="height: 50%; display: flex; justify-content: center; align-items: center;">
                <QrcodeStream @detect="onDetect" @error="onError" :track="paintOutline"
                    style="max-width: 50%; max-height: 50%; width: auto; height: auto;"></QrcodeStream>
            </section>
        </div>
    </section>
</template>

<script>
import LeiterfunktionenComponent from "@/components/LeiterfunktionenComponent.vue";
import { QrcodeStream } from "vue-qrcode-reader";

export default {
    name: "LoginComponentBuefy",
    components: {
        QrcodeStream,
        LeiterfunktionenComponent
    },
    data() {
        return {
            labelPosition: 'on-border',

        };
    },
    methods: {

        onDetect(detectedCodes) {
            if (Array.isArray(detectedCodes) && detectedCodes.length > 0) {
                console.log(detectedCodes[0].rawValue + " in QR-Code gefunden")
                if (detectedCodes[0].rawValue.startsWith("bornalecker")) {
                    this.$router.push({ name: 'BestellungBearbeiten', params: { ID: detectedCodes[0].rawValue } });
                }
            }
        },

        paintOutline(detectedCodes, ctx) {
            for (const detectedCode of detectedCodes) {
                const [firstPoint, ...otherPoints] = detectedCode.cornerPoints

                ctx.strokeStyle = 'red'

                ctx.beginPath()
                ctx.moveTo(firstPoint.x, firstPoint.y)
                for (const { x, y } of otherPoints) {
                    ctx.lineTo(x, y)
                }
                ctx.lineTo(firstPoint.x, firstPoint.y)
                ctx.closePath()
                ctx.stroke()
            }
        },

        async onError(error) {
            if (error.name === "NotAllowedError") {
                this.error = "Kamerazugriff verweigert.";
            } else if (error.name === "NotFoundError") {
                this.error = "Keine passende Kamera gefunden.";
            } else if (error.name === "NotSupportedError") {
                this.error =
                    "HTTPS erforderlich oder nicht unterstützter Browser.";
            } else if (error.name === "AbortError") {
                this.error = "Kamera konnte nicht gestartet werden.";
            } else if (error.name === "OverconstrainedError") {
                this.error =
                    "Einschränkung für Kamera kann nicht erfüllt werden.";
            } else if (error.name === "StreamApiNotSupportedError") {
                this.error = "Browser unterstützt die Stream-API nicht.";
            } else {
                this.error = `Unbekannter Fehler (${error.name || "kein Name"
                    }): ${error.message || "keine Nachricht"}`;
            }
        },
    },
}
</script>

<style scoped>
/* Optional: Stil für die Buttons oder andere Anpassungen */
</style>
