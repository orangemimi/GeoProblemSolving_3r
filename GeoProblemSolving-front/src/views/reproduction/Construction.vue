<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-row>{{ projectInfo.name }}</el-row>
        <el-row
          ><div>Contributor</div>
          <div>{{ projectInfo.managerName }}</div>
        </el-row>
        <el-row
          ><div>Category</div>
          <div>{{ projectInfo.Category }}</div>
        </el-row>
        <el-row v-for="(tag, index) in projectInfo.tag" :key="index"
          ><div>tag</div>
          <div>{{ tag }}</div>
        </el-row>
        <el-row
          ><div>Create time</div>
          <div>{{ projectInfo.createTime }}</div>
        </el-row>
        <el-row
          ><div>Update time</div>
          <div>{{ projectInfo.updateTime }}</div>
        </el-row></el-col
      >
      <el-col :span="6" :offset="6">
        <el-button-group>
          <el-button type="info" plain size="small">{{
            projectInfo.privacy
          }}</el-button>
          <el-button type="info" plain size="small">Change Privacy</el-button>
        </el-button-group>
        <el-button-group>
          <el-button type="info" plain size="small"
            ><i class="el-icon-share"></i
          ></el-button>
          <el-button type="info" plain size="small"
            ><i class="el-icon-more"></i
          ></el-button>
        </el-button-group>
      </el-col>
    </el-row>
    <el-row style="margin-top: 15px">
      <el-col :span="4">
        <el-card
          ><el-button>Context Definition</el-button>
          <el-button>Data Processing</el-button>
          <el-button>Simulation Construction</el-button></el-card
        >
      </el-col>
      <el-col :span="20">
        <el-col :span="10" :key="index">
          <step-card
            :cardInfo="{ btnType: 'Context Definition' }"
            :projectInfo="projectInfo"
          ></step-card>
        </el-col>
        <el-col :span="14" :key="index">
          
          <step-card
            :cardInfo="{ btnType: 'Resource Collection' }"
            :projectInfo="projectInfo"
          ></step-card>
        </el-col>
        <el-col :span="24">
          <step-card
            :cardInfo="{ btnType: 'Simulation Construction' }"
            :projectInfo="projectInfo"
          ></step-card>
        </el-col> </el-col
    ></el-row>
    <el-row>
      <el-popover placement="top" width="460" trigger="click">
        <div><history-record></history-record></div>
        <el-button type="success" class="folder_collect" slot="reference" circle
          ><i class="el-icon-folder-opened"></i
        ></el-button>
      </el-popover>
      <el-col :span="4"></el-col>
      <el-col :span="20"></el-col>
    </el-row>
  </div>
</template>

<script>
import stepCard from "./components/StepCard";
import historyRecord from "./components/Record";
export default {
  components: {
    stepCard,
    historyRecord,
  },
  data() {
    return {
      projectId: this.$route.params.projectId,
      userInfo: this.$store.getters.userInfo,
      projectInfo: {},
      folderCollectVisible: false,
      cardInfos: [
        { btnType: "Context Definition" },
        { btnType: "Resource Collection" },
      ],
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

<style lang='scss' scoped>
.folder_collect {
  position: fixed;
  bottom: 50px;
  right: 50px;
}
.scroll {
  height: 500px;
}
</style>