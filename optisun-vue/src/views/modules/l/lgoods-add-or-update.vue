<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="产品名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="产品图片" prop="img">
        <img-upload v-model="dataForm.img"></img-upload>
      </el-form-item>
      <el-form-item label="产品价格" prop="price">
        <el-input-number :min="0" precision="2" controls-position="right" v-model="dataForm.price"
                         placeholder="价格"></el-input-number>
      </el-form-item>
      <el-form-item label="产品排序" prop="sort">
        <el-input-number :min="0" precision="0" controls-position="right" v-model="dataForm.sort"></el-input-number>
      </el-form-item>
      <el-form-item label="所属房间" prop="roomId">
        <el-select v-model="dataForm.roomId">
          <el-option v-for="item in roomList" :value="item.id" :label="item.name" :key="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="持有天数" prop="days">
      <el-input v-model="dataForm.days" placeholder="持有天数"></el-input>
    </el-form-item>
    <el-form-item label="收益率" prop="rate">
      <el-input v-model="dataForm.rate" placeholder="收益率"></el-input>
      <div class="form-tips">百分比(%)</div>
    </el-form-item>
    <el-form-item label="可申购数量" prop="maxNum">
      <el-input v-model="dataForm.maxNum" placeholder="可申购数量"></el-input>
      <div class="form-tips">当值为0时不限量,-1时为售罄</div>
    </el-form-item>
    <el-form-item label="背景颜色" prop="backColor">
      <el-color-picker v-model="dataForm.backColor"></el-color-picker>
      <div class="form-tips">前台产品背景颜色,默认#252525</div>
    </el-form-item>
    <el-form-item label="日反收益" prop="incomeDay">
      <el-input v-model="dataForm.incomeDay" placeholder="日反收益"></el-input>
      <div class="form-tips">每天的收益</div>
    </el-form-item>
    <el-form-item label="是否日反" prop="isDay">
        <el-radio-group v-model="dataForm.isDay">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
    </el-form-item>
    <el-form-item label="是否赠送" prop="isGive">
        <el-radio-group v-model="dataForm.isGive">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
    </el-form-item>
    <el-form-item label="是否到期反本" prop="isCapitalReturn">
        <el-radio-group v-model="dataForm.isCapitalReturn">
          <el-radio :label="1">是</el-radio>
          <el-radio :label="0">否</el-radio>
        </el-radio-group>
    </el-form-item>
    <el-form-item label="产品级别" prop="goodsLevel">
      <el-select v-model="dataForm.goodsLevel" placeholder="产品级别(A-E)">
        <el-option
		    v-for="item in goodsLevelLists"
		    :key="item.key"
		    :label="item.level_name"
		    :value="item.id" />  
      </el-select>
    </el-form-item>
    <el-form-item label="状态" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">停用</el-radio>
        </el-radio-group>
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
  data() {
    return {
      loading: false,
      dataForm: {
        id: 0,
        name: '',
        img: '',
        price: 500,
        sort: '0',
        roomId: null,
        days:0,
        rate:0,
        maxNum:0,
        status: 1,
        isDay:0,
        isGive:0,
        isCapitalReturn:0,
        incomeDay:0,
        goodsLevel:'',
      },
      dataRule: {
        name: [
          {required: true, message: '名称不能为空', trigger: 'blur'}
        ],
        img: [
          {required: true, message: '图片不能为空', trigger: 'blur'}
        ],
        price: [
          {required: true, message: '价格不能为空', trigger: 'blur'}
        ],
        roomId: [
          {required: true, message: '所属房间不能为空', trigger: 'blur'}
        ],
        sort: [
          {required: true, message: '排序不能为空', trigger: 'blur'}
        ],
        days: [
          {required: true, message: '天数不能为空', trigger: 'blur'}
        ],
        rate: [
          {required: true, message: '收益率不能为空', trigger: 'blur'}
        ],
        maxNum: [
          {required: true, message: '申购数量不能为空', trigger: 'blur'}
        ],
        isDay: [
          {required: true, message: '是否日反不能为空', trigger: 'blur'}
        ],
        incomeDay: [
          {required: true, message: '日反收益不能为空', trigger: 'blur'}
        ],
        isGive: [
          {required: true, message: '是否赠送不能为空', trigger: 'blur'}
        ],
        isCapitalReturn: [
          {required: true, message: '是否到期反本不能为空', trigger: 'blur'}
        ],
        goodsLevel: [
          {required: true, message: '产品级别不能为空', trigger: 'blur'}
        ]
      },
      goodsLevelLists:[{id:1,level_name:'A级'},{id:2,level_name:'B级'},{id:3,level_name:'C级'},{id:4,level_name:'D级'},{id:5,level_name:'E级'}],
      roomList: []
    }
  },
  created() {
    this.getRoomList()
  },
  methods: {
    getRoomList(){
      this.$http({
        url: this.$http.adornUrl(`/l/lroom/all`),
        method: 'get',
        params: this.$http.adornParams()
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.roomList = data.data
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    goBack() {
      this.$emit('back', {})
    },
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/l/lgoods/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.name = data.lGoods.name
              this.dataForm.img = data.lGoods.img
              this.dataForm.price = data.lGoods.price
              this.dataForm.roomId = data.lGoods.roomId
              this.dataForm.sort = data.lGoods.sort
              this.dataForm.createTime = data.lGoods.createTime
              this.dataForm.createBy = data.lGoods.createBy
              this.dataForm.days = data.lGoods.days
              this.dataForm.rate = data.lGoods.rate
              this.dataForm.maxNum = data.lGoods.maxNum
              this.dataForm.status = data.lGoods.status
              this.dataForm.isDay = data.lGoods.isDay
              this.dataForm.isGive = data.lGoods.isGive
              this.dataForm.isCapitalReturn = data.lGoods.isCapitalReturn
              this.dataForm.incomeDay = data.lGoods.incomeDay
              this.dataForm.goodsLevel = this.goodsLevelLists[(data.lGoods.goodsLevel-1)].id
              this.dataForm.backColor = data.lGoods.backColor
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/l/lgoods/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'name': this.dataForm.name,
              'img': this.dataForm.img,
              'price': this.dataForm.price,
              'roomId': this.dataForm.roomId,
              'sort': this.dataForm.sort,
              'createTime': this.dataForm.createTime,
              'createBy': this.dataForm.createBy,
              'days': this.dataForm.days,
              'rate': this.dataForm.rate,
              'maxNum': this.dataForm.maxNum,
              'status':this.dataForm.status,
              'backColor':this.dataForm.backColor,
              'isDay': this.dataForm.isDay,
              'isGive': this.dataForm.isGive,
              'isCapitalReturn': this.dataForm.isCapitalReturn,
              'incomeDay': this.dataForm.incomeDay,
              'goodsLevel': this.dataForm.goodsLevel,
              
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
