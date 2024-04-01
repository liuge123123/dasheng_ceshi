<template>
  <div>
    <el-dialog :visible.sync="checkVisible" title="审核" size="small" width="500px" class="container">
      <el-alert type="warning" title="请仔细核对您的数据并确认您的操作是对的！" :closable="false" show-icon></el-alert>
      <el-form>
        <el-form-item label="通道" prop="channelId">
          <el-select v-model="checkForm.channelId">
            <el-option v-for="item in bankList" :value="item.deviceId" :label="item.moneyType+'-'+item.mobile" :key="item.deviceId"></el-option>
          </el-select>
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
  name: "auto-assembly",
  data(){
    return{
      //审核
      checkVisible:false,
      checkForm:{
        id:'',
        channelId:'',
        channelName:'',
      },
      bankList:[],
      checkLoading:false
    }
  },
  methods:{
    //初始化
    init(id){
      this.checkForm.id = id
      this.checkVisible = true
      this.checkLoading=false
    },
    //初始化批量
    initMore(wallet){
      this.checkForm.id=''
      this.checkVisible = true
      this.checkLoading=false
      this.checkForm.channelName = wallet
      this.getWallet()
    },
    getWallet() {
      this.$http({
        url: this.$http.adornUrl(`/cust/devices/list`),
        method: 'get',
        params: this.$http.adornParams({
          'isOpen':1,
          'moneyType': this.checkForm.channelName
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.bankList = data.page.list
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    //审核提交
    checkSubmit(){
      if (this.checkForm.channelId== '') {
        this.$message.warning('请选择通道')
        return
      } 
      this.checkLoading=true
      this.$emit('checkSubmit',this.checkForm)
      this.checkVisible = false
    }
  }
}
</script>

<style scoped>

</style>
