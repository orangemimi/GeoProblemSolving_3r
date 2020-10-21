<template>
  <div>
    <el-row>
      <div>
        <el-menu
          class="el-menu-demo"
          mode="horizontal"
          :default-active="this.$route.path"
          @select="handleClick"
        >
          <el-menu-item
            v-for="(item, i) in navList"
            :key="i"
            :index="item.name"
            >{{ item.navItem }}</el-menu-item
          >
        </el-menu>
      </div>
    </el-row>
    <el-row></el-row>
    <el-col :span="18" :offset="3" :style="{ height: contentHeight + 'px' }">
      <el-card shadow="never"
        ><vue-scroll :style="{ height: contentHeight - 60 + 'px' }">
          <router-view class="scroll-item"></router-view> </vue-scroll
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
      navList: [
        { name: "construction", navItem: "construction" },
        { name: "contributor", navItem: "contributor" },
      ],

      projectId: this.$route.params.projectId,
      userInfo: this.$store.getters.userInfo,
      projectInfo: {},
      activeName: "construction",
      contentWidth: 0,
      contentHeight: 0,
    };
  },
  methods: {
    handleClick(val) {
      console.log(val);
      this.$router.push({
        name: val,
      });
    },
    initSize() {
      this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 200;
    },

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
  async created() {
    this.initSize();
  },
};
</script>

<style lang='scss' scoped>
</style>