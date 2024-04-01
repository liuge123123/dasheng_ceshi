<template>
  <span>{{label}}</span>
</template>

<script>
  export default {
    props: {
      "value": [String, Number, Array],
      "optionId":[String, Number],
    },
    data() {
      return {
        label: null,
      }
    },

    methods: {
      getDictVal(){
          if(this.value){
            let list = JSON.parse(this.value)
            list.forEach((item) =>{
               if(item.optional_value == this.optionId){
                 this.label = item.optional_label
               }
            })
          }
      },
    },
    created(){
      this.getDictVal()
    },
    watch: {
      value(newVal, oldVal){
        this.getDictVal()
      }
    }
  }
</script>

<style>

</style>
