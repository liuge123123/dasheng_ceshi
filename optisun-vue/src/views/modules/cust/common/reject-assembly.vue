<template>
  <div>
    <el-dialog :visible.sync="checkVisible" title="驳回" size="small" width="500px" class="container">
      <el-alert type="warning" title="请仔细核对您的数数据并确认，驳回操作是不可以逆的！" :closable="false" show-icon></el-alert>
      <el-form>
        <el-form-item v-if="checkForm.checkStatus == 2">
          <el-input v-model="checkForm.rejectReason" type="textarea" placeholder="请填写原因" :rows="5" maxlength="100"  show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer" style="display: flex; justify-content: center">
          <el-button size="small" @click="checkVisible = false" v-loading="checkLoading">取消</el-button>
          <el-button size="small" type="primary" v-loading="checkLoading" @click="checkSubmit()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "check-assembly",
  data(){
    return{
      //审核
      checkVisible:false,
      checkForm:{
        id:'',
        checkStatus:2,
        rejectReason:''
      },
      checkLoading:false
    }
  },
  methods:{
    //初始化
    init(id){
      this.checkForm.id = id
      this.checkVisible = true
    },
    //审核提交
    checkSubmit(){
      if (this.checkForm.checkStatus == 2 && this.checkForm.rejectReason == '') {
        this.$message.warning('请输入审核不通过理由')
        return
      } else {
        this.$emit('checkSubmit',this.checkForm)
        this.checkVisible = false
      }
    }
  }
}
</script>

<style scoped>

</style>
