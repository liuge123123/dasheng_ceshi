<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="dataForm.roleName" placeholder="角色名称"></el-input>
      </el-form-item>
      <el-form-item label="角色类型" prop="type">
        <el-radio-group v-model="dataForm.type">
          <el-radio :label="1" v-if="userInfo.type==0">平台角色</el-radio>
          <el-radio :label="2" v-if="userInfo.type==0 || userInfo.type==1">总代理角色</el-radio>
          <el-radio :label="3" v-if="userInfo.type==0 || userInfo.type==2">分代理角色</el-radio>
          <el-radio :label="4" v-if="userInfo.type==0 || userInfo.type==3">普通员工角色</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
      </el-form-item>
      <el-form-item size="mini" label="授权">
        <el-tree
          :data="menuList"
          :props="menuListTreeProps"
          node-key="menuId"
          ref="menuListTree"
          :default-expand-all="true"
          show-checkbox
        ></el-tree>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { treeDataTranslate } from "@/utils";
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      loading: true,
      menuListTreeProps: {
        label: "name",
        children: "children"
      },
      dataForm: {
        id: 0,
        type: 1,
        roleName: "",
        remark: ""
      },
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "角色类型不能为空", trigger: "change" }
        ]
      },
      tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  computed:{
    userInfo(){
      return this.$store.state.user.userInfo;
    }
  },
  methods: {
    goBack() {
      this.$emit("back", {});
    },
    init(id) {
      this.dataForm.id = id || 0;
      this.$http({
        url: this.$http.adornUrl("/sys/menu/list"),
        method: "get",
        params: this.$http.adornParams()
      })
        .then(({ data }) => {
          this.menuList = treeDataTranslate(data, "menuId");
          this.loading = false;
        })
        .then(() => {
          this.visible = true;
          this.$nextTick(() => {
            this.$refs["dataForm"].resetFields();
            this.$refs.menuListTree.setCheckedKeys([]);
          });
        })
        .then(() => {
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/sys/role/info/${this.dataForm.id}`),
              method: "get",
              params: this.$http.adornParams()
            }).then(({ data }) => {
              if (data && data.code === 0) {
                this.dataForm.roleName = data.role.roleName;
                this.dataForm.remark = data.role.remark;
                this.dataForm.type = data.role.type;
                var idx = data.role.menuIdList.indexOf(this.tempKey);
                if (idx !== -1) {
                  data.role.menuIdList.splice(
                    idx,
                    data.role.menuIdList.length - idx
                  );
                }
                this.$refs.menuListTree.setCheckedKeys(data.role.menuIdList);
              }
            });
          }
        });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/sys/role/${!this.dataForm.id ? "save" : "update"}`
            ),
            method: "post",
            data: this.$http.adornData({
              roleId: this.dataForm.id || undefined,
              roleName: this.dataForm.roleName,
              remark: this.dataForm.remark,
              type:this.dataForm.type,
              menuIdList: [].concat(
                this.$refs.menuListTree.getCheckedKeys(),
                [this.tempKey],
                this.$refs.menuListTree.getHalfCheckedKeys()
              )
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("back", { refresh: true });
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    }
  }
};
</script>
