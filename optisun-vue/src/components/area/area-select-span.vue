<template>
  <div>{{areaText}}</div>
</template>

<script>
  import { regionData,CodeToText } from 'element-china-area-data'

  export default {
    name: "area-select-span",
    props: {
      "value": [String, Number, Array],
    },
    filters: {

    },
    data(){
      return {
        CodeToText: CodeToText
      }
    },
    computed: {
      areaText(){
        if(this.value && this.value != '') {
          if (this.value.length == 3) {
            return this.CodeToText[this.value[0]] + '-' + this.CodeToText[this.value[1]] + '-' + this.CodeToText[this.value[2]]
          }
        }
        return ''
      }
    }
  }
</script>

<style scoped>

</style>
