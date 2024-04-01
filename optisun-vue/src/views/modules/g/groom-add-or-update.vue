<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">

      <el-form-item label="封面图" prop="image">
        <img-up-load v-model="dataForm.image"></img-up-load>
      </el-form-item>
      <el-form-item label="房间名称" prop="nameZhCn">
        <el-input v-model="dataForm.roomName" placeholder="房间名称"></el-input>
      </el-form-item>
      <el-form-item label="价格区间" prop="priceStart">
        <div class="disF">
          <el-input-number style="width: 30%" v-model="dataForm.priceStart" :step="1" :max="100000000000"></el-input-number>
          <div style="margin-left: 10px;margin-right: 10px">--</div>
          <el-input-number style="width: 30%" v-model="dataForm.priceEnd" :step="1" :max="100000000000"></el-input-number>
        </div>
      </el-form-item>
      <el-form-item label="佣金比例" prop="comRate">
        <el-input-number v-model="dataForm.comRate"
                         style="width: 30%"
                         :step="0.01" :max="1"></el-input-number>
      </el-form-item>
      <el-form-item label="关联等级" prop="gradeId">
        <el-select v-model="dataForm.gradeId" style="width: 30%" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.gradeName"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="开启" prop="isOpen">
        <el-switch
          v-model="dataForm.isOpen"
          active-color="#13ce66"
          active-value="1"
          inactive-value="0"
          inactive-color="lightGray">
        </el-switch>
      </el-form-item>
    <el-form-item label="房间简介" prop="roomDesc">
      <el-input v-model="dataForm.roomDesc" placeholder="房间简介"
                type="textarea"
                maxlength="125"
                show-word-limit
                :rows="3"
      ></el-input>
    </el-form-item>
        <el-form-item style="text-align: center">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
        </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import imgUpLoad from '@/components/upload/img-upload'
  export default {
    components:{
      imgUpLoad
    },
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          roomName: '',
          roomDesc: '',
          priceStart: '',
          priceEnd: '',
          comRate: '',
          allNum: '',
          gradeId: '',
          isOpen: ''
        },
        options:[],
        dataRule: {
          roomName: [
            { required: true, message: '房间名不能为空', trigger: 'blur' }
          ],
          roomDesc: [
            { required: true, message: '房间简介不能为空', trigger: 'blur' }
          ],
          // image: [
          //   { required: true, message: '封面图不能为空', trigger: 'change' }
          // ],
          priceStart: [
            { required: true, message: '价格开始不能为空', trigger: 'blur' }
          ],
          priceEnd: [
            { required: true, message: '价格结束不能为空', trigger: 'blur' }
          ],
          comRate: [
            { required: true, message: '佣金比例不能为空', trigger: 'blur' }
          ],
          allNum: [
            { required: true, message: '共计抢单不能为空', trigger: 'blur' }
          ],
          gradeId: [
            { required: true, message: '请选择等级', trigger: 'blur' }
          ],
        }
      }
    },
    mounted() {
      this.getDataList()
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
              url: this.$http.adornUrl(`/g/groom/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.roomName = data.gRoom.roomName
                this.dataForm.roomDesc = data.gRoom.roomDesc
                this.dataForm.image = data.gRoom.image
                this.dataForm.priceStart = data.gRoom.priceStart.toFixed(4)
                this.dataForm.priceEnd = data.gRoom.priceEnd.toFixed(4)
                this.dataForm.comRate = data.gRoom.comRate
                this.dataForm.allNum = data.gRoom.allNum
                this.dataForm.gradeId = data.gRoom.gradeId
                this.dataForm.isOpen = data.gRoom.isOpen
              }else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/t/tgoodsgrade/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': 1,
            'limit': 10,
            'key': ''
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            console.log(data.page.list)
            this.options = data.page.list
          } else {
            this.options = []
          }
          this.dataListLoading = false
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/g/groom/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'roomName': this.dataForm.roomName,
                'roomDesc': this.dataForm.roomDesc,
                'image': this.dataForm.image,
                'priceStart': this.dataForm.priceStart,
                'priceEnd': this.dataForm.priceEnd,
                'comRate': this.dataForm.comRate,
                'allNum': this.dataForm.allNum,
                'gradeId': this.dataForm.gradeId,
                'isOpen': this.dataForm.isOpen
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
