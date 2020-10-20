<template>
  <div>
    <el-col :span="18" :offset="3">
      <el-card shadow="never">
        <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
          <el-tab-pane label="Construction" name="construction">
            <construction ></construction>
          </el-tab-pane>

          <el-tab-pane label="Contributors" name="contributors">
            <contributor></contributor>
          </el-tab-pane> </el-tabs
      ></el-card>
    </el-col>
  </div>
</template>

<script>
import { post, get } from "@/axios";
import construction from "./components/Construction";
import contributor from "./components/Contributor";
export default {
  components: {
    construction,
    contributor,
  },
  data() {
    return {
      projectId: this.$route.params.projectId,
      userInfo: this.$store.getters.userInfo,
      projectInfo: {},
      activeName: "construction",
    };
  },
  methods: {
    async getProjectInfo() {
      if (
        JSON.stringify(this.projectInfo) == "{}" ||
        this.projectInfo == undefined
      ) {
        let { data } = await this.axios.get(
          `/GeoProblemSolving/project/inquiry?key=projectId&value=${this.projectId}`
        );
        if (data == "Offline") {
          parent.location.href = "/GeoProblemSolving/login";
        }
        if (data != "None" && data != "Fail") {
          this.projectInfo = data[0];

          this.$store.commit("setProjectInfo", data[0]);
        } else {
        }
      }
    },
  },
  mounted() {
    this.getProjectInfo();
  },
};
</script>

<style>
</style>