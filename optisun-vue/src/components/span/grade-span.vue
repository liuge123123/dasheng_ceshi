<template>
  <span>{{label}}</span>
</template>

<script>
  export default {
    props: {
      value:{
        type:Number,
        default:()=>{return undefined}
      }
    },
    data() {
      return {
        label: null,
        selectedVal:null
      }
    },

    methods: {
      getDictVal(){
        if(this.selectedVal == undefined || this.selectedVal == ''){
          return;
        }
        this.$http({
          url: this.$http.adornUrl(`/t/tgoodsgrade/info/${this.selectedVal}`),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          if(data.code == 0){
            this.label = data.tGoodsGrade.gradeName;
          }else{
            this.$message.error(data.msg);
          }
        })
      },
    },
    created(){
      this.selectedVal = this.value
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
