<template>
  <div class="main" :style="{height:contentHeight+'px'}">
    <el-row :gutter="20" :style="{height:contentHeight+'px'}">
      <el-col :span="8">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">
            Tools
            <all-tools></all-tools>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">Process data</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">Process data</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import toolToModal from "./../tools/toolToStepModal";
import allTools from "./allTools";
export default {
  props: {},
  components: { toolToModal, allTools },
  data() {
    return { showMenuItem: "publicTools", contentWidth: 0, contentHeight: 0 };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 200;
    },
  },
  created() {
    this.initSize();
  },
  mounted() {},
};
</script>
<style lang='scss' scoped>
.main {
  margin: 0 20px;
}
</style>