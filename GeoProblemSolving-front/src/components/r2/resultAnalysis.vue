<template>
  <div class="">
    <el-row>
      <el-row>
        <el-col :span="8">
          <el-button @click="returnResultAnalysis">Back</el-button>
        </el-col>
        <el-col :span="8"> <div class="title">Result Analysis</div></el-col>
        <el-col :span="8"
          ><el-button @click="editResultAnalysis">Edit</el-button></el-col
        >
      </el-row>
      <el-row>
        <router-view
          :resultInfo="result"
          :simulationStepList="simulationStepList"
          :allInstances="allInstances"
          :allMaps="allMaps"
        ></router-view>
      </el-row>
    </el-row>
  </div>
</template>

<script>
import { post, get } from "@/axios";
import info from "./components/resultAnalysis/Info";
import edit from "./components/resultAnalysis/Edit";
import Router from "vue-router";

const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err);
};

export default {
  components: {
    info,
    edit,
  },
  data() {
    return {
      projectId: this.$route.params.projectId,
      result: {
        name: "",
        description: "",
        map: "",
        detail: "",
      },
      projectStepList: [],
      simulationStepInfoList: [],
      allInstances: [],
      allMaps: [],
    };
  },
  computed: {
    simulationStepList() {
      return this.projectStepList.filter(
        (step) => step.type == "Simulation execution"
      );
    },
  },
  methods: {
    async init() {
      await this.getResultInfo();
      await this.getSteps();
      await this.getAllModelInstances();
      await this.getAllMaps();
    },

    async getResultInfo() {
      let { data } = await get(
        `/GeoProblemSolving/r/result/get/${this.projectId}`
      );

      // console.log(data);
      if (data == null) {
        this.result = {
          name: "",
          description: "",
          detail: "",
          map: "",
        };
      } else {
        this.result = data;
      }
      this.returnResultAnalysis();
    },

    editResultAnalysis() {
      this.$router.push({
        name: "resultEdit",
      });
    },

    returnResultAnalysis() {
      this.$router.push({
        name: "resultInfo",
      });
    },

    async getSteps() {
      let { data } = await this.axios.get(
        `/GeoProblemSolving/step/inquiry?key=projectId&value=${this.projectId}`
      );
      this.projectStepList = data;
    },

    async getAllModelInstances() {
      let { data } = await get(
        `/GeoProblemSolving/r/modelInstance/getAll/${this.projectId}`
      );
      this.allInstances = data;
      // console.log(data);
    },
    async getAllMaps() {
      let data = await get(`/GeoProblemSolving/r/flowcharts/${this.projectId}`);
      this.allMaps = data;
      console.log(data);
    },

    // async getSimulationStepInfo() {
    //   let stepList = this.simulationStepList;
    //   let instanceList = this.allInstances.filter((item) => {
    //     return stepList.some((step) => step.stepId == item.stepId);
    //   });
    //   console.log(instanceList);
    // },
  },
  mounted() {
    this.init();
  },
  created() {},
};
</script>

<style>
.title {
  font-size: 20px;
  font-weight: 600;
}
</style>