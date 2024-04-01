<template>
  <div>{{areaText}}</div>
</template>

<script>
  import {pca, pcaa} from 'area-data'

  export default {
    name: "area-span",
    props: {
      province: String,
      city: String,
      county: String
    },
    filters: {
      areaFilter(val){
        return pcaa[val]
      }
    },
    data(){
      return {
        pcaa: pcaa,
        pca: pca
      }
    },
    computed: {
      areaText(){
        return this.pcaa[86][this.province] + this.pcaa[this.province][this.city] + this.pcaa[this.city][this.county]
      }
    }
  }
</script>

<style scoped>

</style>
