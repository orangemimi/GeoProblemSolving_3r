<template>
  <div>
    <el-col :span="12" :offset="6"> 
      <el-row>
        <el-row>
          <el-steps :active="active" finish-status="success">
            <el-step title="Step1"></el-step>
            <el-step title="Step2"></el-step>
          </el-steps>
        </el-row>
        <el-row>
          <div v-show="active == 0">
            <el-form ref="form" :model="result" label-width="80px">
              <el-form-item label="name">
                <el-input v-model="result.name"></el-input>
              </el-form-item>
              <el-form-item label="description">
                <el-input v-model="result.description"></el-input>
              </el-form-item>
              <el-form-item label="instances">
                <el-row v-for="(step, index) in instancesByType" :key="index">
                  <el-col>
                    <el-card>
                      <div>{{ step.name }}</div>
                      <el-transfer
                        v-model="result.modelInstances"
                        :data="getTransferData(step.instanceList)"
                      ></el-transfer>
                    </el-card>
                  </el-col>
                </el-row>
              </el-form-item>
            </el-form>
            <div>
              <div class="mxGraph">
                <!-- <mx-graph :sendXml="sendXml"></mx-graph> -->
              </div>
            </div>
          </div>

          <div v-show="active == 1">
            <el-row>
              <tinymce
                ref="editor"
                v-model="result.detail"
                :height="500"
              ></tinymce>
            </el-row>
          </div>
        </el-row>
        <el-row>
          <div>
            <el-button v-if="active == 0" @click="next">Next</el-button>
            <div v-else>
              <el-button @click="previous">Previous</el-button>
              <el-button @click="saveResultInfo">Create</el-button>
            </div>
          </div>
        </el-row>
      </el-row>
    </el-col>
  </div>
</template>

<script>
import { post, get } from "@/axios";
import mxGraph from "@/components/utils/mxGraph/mxGraph";
import tinymce from "@/components/common/tinymce/index";
export default {
  props:{
    resultInfo:{
      type:Object
    }
  },
  components: {
    mxGraph,
    tinymce,
  },
  watch: {
    result: {
      handler(val) {
        console.log(val);
      },
      deep: true,
    },
  },
  data() {
    return {
      active: 0,
      modelInstances: [],
      form: {
        name: "",
        description: "",
        modelInstances: [],
      },
      projectId: this.$route.params.projectId,
      projectInfo: {},
      solvingProcess: [],
      result: this.resultInfo,
    };
  },
  computed: {
    instancesByType() {
      let list = [];
      let solvingProcess = this.solvingProcess.filter((process) => {
        process.instanceList = [];
        return process.category === 3;
      });
      this.modelInstances.forEach((instance) => {
        solvingProcess.forEach((item) => {
          if (item.stepId === instance.stepId) {
            item["instanceList"].push(instance);
          }
        });
      });
      return solvingProcess;
    },

    // instancesByType() {},
  },
  methods: {
    next() {
      if (this.active++ > 1) this.active = 1;
    },
    previous() {
      if (this.active-- < 0) this.active = 0;
    },

    async getProjectInfo() {
      let { data } = await this.axios.get(
        `/GeoProblemSolving/project/inquiry?key=projectId&value=${this.projectId}`
      );
      this.projectInfo = data[0];
      this.solvingProcess = JSON.parse(data[0].solvingProcess);
      //获得不同节点的node
    },

    //get modelinstance
    async getModelInstances() {
      //   let { data } = await get(
      //     `/GeoProblemSolving/r/modelInstance/get/${this.stepInfo.stepId}`
      //   );

      let { data } = await get(
        `/GeoProblemSolving/r/modelInstance/getAll/173d201c-779a-4ba8-8961-981250e60a05`
      );
      this.modelInstances = data;
    },

    getTransferData(instances) {
      let list = [];
      instances.forEach((item) => {
        list.push({
          key: item.id,
          label: item.name,
          disabled: false,
        });
      });
      return list;
    },

    async saveResultInfo() {
      console.log(this.result);
      let result = {
        name: this.result.name,
        description: this.result.description,
        detail: this.result.detail,
        pid: this.projectId,
      };
      console.log(result);

      let {data} = await post(`/GeoProblemSolving/r/result/save`, result);
      this.result = data;
    },
  },
  mounted() {
    this.getProjectInfo();
    this.getModelInstances();
    // this.getSimulationList();
  },
};
</script>

<style>
</style>