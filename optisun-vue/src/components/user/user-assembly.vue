<template>
   <div style="display: flex;justify-items: center;align-items: center;justify-content: center">
      {{dataForm.userName}}-{{dataForm.userId}}
   </div>
</template>

<script>
import {value} from "../../../test/e2e/custom-assertions/elementCount";

export default {
  name: "user-assembly",
  props:{
    value:{
      type:Number,
      default:0
    }
  },
  data(){
    return{
      dataForm:{
        userName:'',
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
            url: this.$http.adornUrl(`/sys/user/info/${this.dataForm.userId}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.userName = data.user.username;
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
