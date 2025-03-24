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
                </b-tabs>
            </section>
        </div>
    </div>
</template>

<script>
import BareboneNavbar from '../components/navbars/BareboneNavbar.vue'
import EssensbestellungComponent from '../components/EssensbestellungComponent.vue'

export default {
    name: "GruppenleitungNormalView",
    components: {
        BareboneNavbar,
        EssensbestellungComponent,
    },
};
</script>
