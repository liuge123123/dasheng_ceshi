<template>
   <div>
     <el-dialog :visible.sync="visible" title="下级" size="small" fullscreen class="container">
       <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
         <el-tab-pane label="第一级" name="first">
            <cust-list ref="custFirst"></cust-list>
         </el-tab-pane>
         <el-tab-pane  label="第二级" name="second">
            <cust-list ref="custSecond"></cust-list>
         </el-tab-pane>
         <el-tab-pane  label="第三级" name="third">
            <cust-list ref="custThird"></cust-list>
         </el-tab-pane>
         <el-tab-pane  label="第四级" name="four">
           <cust-list ref="custFour"></cust-list>
         </el-tab-pane>
         <el-tab-pane  label="第五级" name="five">
           <cust-list ref="custFive"></cust-list>
         </el-tab-pane>
         <el-tab-pane  label="第六级" name="six">
           <cust-list ref="custSix"></cust-list>
         </el-tab-pane>
         <el-tab-pane  label="第七级" name="serven">
           <cust-list ref="custServen"></cust-list>
         </el-tab-pane>
       </el-tabs>
     </el-dialog>
   </div>
</template>

<script>
import custList from "./common/cust-list";
export default {
  name: "cust-grade-show",
  components:{
    custList
  },
  data(){
    return{
      visible:false,
      activeName: 'first',
      custId:''
    }
  },
  mounted() {
  },
  methods:{
    init(val){
      this.visible=true
      this.custId=val
      this.$nextTick(() => {
        this.$refs.custFirst.getNextDataList(this.custId,1)
      })
    },
    handleClick(tab, event){
      console.log(tab.name);
      if(tab.name=='first'){
        this.$nextTick(() => {
          this.$refs.custFirst.getNextDataList(this.custId,1)
        })
      }
      else if(tab.name=='second'){
        this.$nextTick(() => {
          this.$refs.custSecond.getNextDataList(this.custId,2)
        })
      }
      else if(tab.name=='third'){
        this.$nextTick(() => {
          this.$refs.custThird.getNextDataList(this.custId,3)
        })
      }
      else if(tab.name=='four'){
        this.$nextTick(() => {
          this.$refs.custFour.getNextDataList(this.custId,4)
        })
      }
      else if(tab.name=='five'){
        this.$nextTick(() => {
          this.$refs.custFive.getNextDataList(this.custId,5)
        })
      }
      else if(tab.name=='six'){
        this.$nextTick(() => {
          this.$refs.custSix.getNextDataList(this.custId,6)
        })
      }
      else if(tab.name=='serven'){
        this.$nextTick(() => {
          this.$refs.custServen.getNextDataList(this.custId,7)
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
