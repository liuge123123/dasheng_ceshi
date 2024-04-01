<template>
   <div style="display: flex;justify-items: center;align-items: center;justify-content: center">
      {{dataForm.child}}
   </div>
</template>

<script>

export default {
  name: "user-child",
  props:{
    value:{
      type:Number,
      default:0
    }
  },
  data(){
    return{
      dataForm:{
        child:'',
        userId:0
      }
    }
  },
  mounted() {
    this.dataForm.userId=this.value
    this.getUserInfo()
  },
  methods:{
      getUserInfo(){
        if(this.dataForm.userId){
          this.$http({
            url: this.$http.adornUrl(`/cust/cust/count/${this.dataForm.userId}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.child = data.child;
            }else{
              this.$message.error(data.msg)
            }
          })
        }
      }
  },
  watch:{
    value(newVal, oldVal){
      if(newVal){
         this.dataForm.userId = newVal
        this.getUserInfo()
      }
    }
  }
}
</script>

<style scoped>

</style>
