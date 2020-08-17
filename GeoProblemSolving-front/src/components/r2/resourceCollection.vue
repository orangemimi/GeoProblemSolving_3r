<template>
  <div class>
    <el-steps :active="active" finish-status="success" simple style="margin-top: 20px">
      <el-step title="Select Model"></el-step>
      <el-step title="Upload Data"></el-step>
    </el-steps>

    <div v-show="active==0" style="height:400px"></div>
    <div v-show="active==1">
      <data-upload @uploadDataList="uploadDataList"></data-upload>
    </div>
    <div class="stepBtn">
      <el-button @click="nextStep" v-show="active!=1">Next</el-button>
      <el-button @click="preStep" v-show="active!=0">Previous</el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: {},
  components: {},
  data() {
    return {};
  },
  methods: {},
  created() {},
  mounted() {},
};
</script>
<style lang='scss' scoped>
</style>