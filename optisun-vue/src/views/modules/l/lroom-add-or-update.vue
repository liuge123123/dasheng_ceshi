<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="名称"></el-input>
    </el-form-item>
    <el-form-item label="图片" prop="img">
      <img-upload v-model="dataForm.img" placeholder="图片"></img-upload>
    </el-form-item>
    <el-form-item label="房间类型" prop="isFund">
      <el-select v-model="dataForm.isFund" placeholder="房间类型">
        <!-- <el-option value="0" label="普通房间"></el-option>
        <el-option value="1" label="体验金房间"></el-option> -->
        <el-option
		    v-for="item in statusLists"
		    :key="item.key"
		    :label="item.status_name"
		    :value="item.id" />  
      </el-select>
    </el-form-item>
    <el-form-item label="金额限制" prop="buyLimit">
      <el-input v-model="dataForm.buyLimit" placeholder="金额限制"></el-input>
    </el-form-item>
    <el-form-item label="说明" prop="remark">
      <el-input v-model="dataForm.remark" placeholder="说明" type="textarea"></el-input>
    </el-form-item>
    <el-form-item label="领取周期(时)" prop="receiveCircle">
      <el-input v-model="dataForm.receiveCircle" placeholder="领取周期(时)"></el-input>
    </el-form-item>
    <!-- <el-form-item label="持有天数" prop="days">
      <el-input v-model="dataForm.days" placeholder="持有天数"></el-input>
    </el-form-item>
    <el-form-item label="收益率" prop="rate">
      <el-input v-model="dataForm.rate" placeholder="收益率"></el-input>
      <div class="form-tips">百分比(%)</div>
    </el-form-item>
    <el-form-item label="可申购数量" prop="maxNum">
      <el-input v-model="dataForm.maxNum" placeholder="可申购数量"></el-input>
      <div class="form-tips">当值为0时不限量</div>
    </el-form-item> -->
    <el-form-item label="排序" prop="sort">
      <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
    </el-form-item>
        <el-form-item style="text-align: center">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
        </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
  export default {
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          name: '',
          isFund:'',
          img: '',
          remark: '',
          days: '5',
          rate: '1',
          maxNum: '0',
          sort: '0',
          receiveCircle:0,
          buyLimit:'0'
        },
        statusLists:[{id:0,status_name:'普通房间'},{id:1,status_name:'体验金房间'},{id:2,status_name:'稳健区'}],
        dataRule: {
          name: [
            { required: true, message: '名称不能为空', trigger: 'blur' }
          ],
          days: [
            { required: true, message: '持有天数不能为空', trigger: 'blur' }
          ],
          rate: [
            { required: true, message: '收益率不能为空', trigger: 'blur' }
          ],
          maxNum: [
            { required: true, message: '可申购数量不能为空', trigger: 'blur' }
          ],
          sort: [
            { required: true, message: '排序不能为空', trigger: 'blur' }
          ],
          receiveCircle: [
            { required: true, message: '领取周期不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/l/lroom/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.name = data.lRoom.name
                this.dataForm.img = data.lRoom.img
                this.dataForm.isFund = this.statusLists[data.lRoom.isFund].id
                this.dataForm.remark = data.lRoom.remark
                this.dataForm.days = data.lRoom.days
                this.dataForm.rate = data.lRoom.rate
                this.dataForm.maxNum = data.lRoom.maxNum
                this.dataForm.sort = data.lRoom.sort
                this.dataForm.receiveCircle = data.lRoom.receiveCircle
                this.dataForm.buyLimit = data.lRoom.buyLimit
              }else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/l/lroom/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'img': this.dataForm.img,
                'isFund':this.dataForm.isFund,
                'remark': this.dataForm.remark,
                'days': this.dataForm.days,
                'rate': this.dataForm.rate,
                'maxNum': this.dataForm.maxNum,
                'sort': this.dataForm.sort,
                'receiveCircle':this.dataForm.receiveCircle,
                'buyLimit':this.dataForm.buyLimit
              })
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('back', {refresh: true})
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
