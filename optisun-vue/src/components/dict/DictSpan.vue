<template>
  <span>{{label}}</span>
</template>

<script>
  export default {
    props: {
      "value": [String, Number, Array],
      "type": { type: String, required: true }
    },
    data() {
      return {
        label: null,
        selectedVal:null
      }
    },

    methods: {
      getDictVal(){
        if(this.type == null || this.type == ''){
          return;
        }
        this.$http({
          url: this.$http.adornUrl("/sys/dictionary/codeVal"),
          method: 'get',
          params: this.$http.adornParams({
                    code: this.type,
                    key: this.value
                  })
        }).then(({data}) => {
          if(data.code == 0){
            this.label = data.codeVal;
          }else{
            this.$message.error(data.msg);
          }
        })
      },
    },
    created(){
      this.getDictVal()
    },
    watch: {
      value(newVal, oldVal){
        this.selectedVal = newVal
        this.getDictVal()
      }
    }
  }
</script>

<style>

</style>
