<template>
  <div class="main" :style="{height:contentHeight+'px'}">
    <el-row :gutter="20" :style="{height:contentHeight+'px'}">
      <el-col :span="4">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">
            <el-select v-model="typeSelected" style="width:160px">
              <el-option v-for="item in typeOptions" :key="item.index" :value="item">{{ item }}</el-option>
            </el-select>
            <div class="toolList" :style="{height:contentHeight+'px'}">
              <vue-scroll :ops="ops">
                <el-row>Tools selected</el-row>
                <el-row class="tool_card">
                  <el-col :span="12" v-for="tool in filterSelectedTools" :key="tool.index">
                    <div style="margin: 5px" @click="useTool(tool)">
                      <tool-card :toolFrom="tool" :isOpenTool="isOpenTool"></tool-card>
                    </div>
                  </el-col>
                </el-row>
              </vue-scroll>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-row>
          <el-card class="process_data" shadow="never">
            <div class="process_data_title">Model item</div>
            <div>
              <model-item-info
                :pageParamsFrom="pageParams"
                :currentModelInfo="currentModelInfo"
                :selectedData="selectedData"
                :selectedTools="selectedTools"
                :selectedResources="selectedResources"
                :instanceFolk="instanceFolk"
                @modelInstance="saveModelInstance"
                @outputRecords="outputRecords"
              ></model-item-info>
              <!-- <router-link :to="{path:modelDoi}">son1</router-link>
              <router-view></router-view>-->
            </div>
          </el-card>
        </el-row>
        <el-row>
          <el-card>
            <div class="mxGraph" :style="{height:contentHeight+'px'}">
              <mx-graph></mx-graph>
            </div>
          </el-card>
        </el-row>
      </el-col>
      <el-col :span="4">
        <el-row>
          <el-card class="process_data" shadow="never">
            <div class="process_data_title">Data Resources</div>
            <div>
              <div v-for="(data,index) in selectedData" :key="index">
                <div class="data_info">
                  <div class="data_info_name">{{data.name}}</div>
                  <div class="data_info_hover">
                    <i class="el-icon-download" @click="downloadDataResource(data)"></i>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-row>
        <el-row>
          <el-card shadow="never">
            <div class="process_data_title">Model Instance</div>
            <div v-for="(instance,index) in filterModelInstance" :key="index+'instance'">
              <el-card class="box-card">
                <div class="instance_name">{{instance.name}}</div>
                <i class="el-icon-connection" @click="folkInstance(instance)"></i>
                <el-checkbox v-model="instance.checkedForMap"></el-checkbox>
              </el-card>
            </div>
            <el-button @click="createMap">Create Map</el-button>
          </el-card>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import toolsSelected from "./components/toolsSelected";
import allTools from "./allTools";
import toolCard from "./components/toolCard";
import { get, del, post, put } from "../../axios";
import modelItemInfo from "./components/modelItemInfo";
import conceptMap from "./conceptMap";
import mxGraph from "@/components/utils/mxGraph/mxGraph";
export default {
  props: {},
  components: {
    toolsSelected,
    allTools,
    toolCard,
    modelItemInfo,
    conceptMap,
    mxGraph,
  },
  computed: {
    filterSelectedTools() {
      let tools = this.selectedTools;
      let type = this.typeSelected;
      if (type == "All") {
        return tools;
      } else {
        // return tools.filter((tool) => {
        //   return tool.recomStep.includes(type);
        // });
      }
    },
    filterModelInstance() {
      let data = this.modelInstances;
      return data.filter((item) => {
        return (item.statesJson = JSON.parse(item.states));
      });
    },
    //create Map instance
    filterCreateMapInstances() {
      return this.filterModelInstance.filter((instance) => {
        return instance.checkedForMap == true;
      });
    },
  },
  data() {
    return {
      userInfo: this.$store.getters.userInfo,
      projectId: this.$route.params.projectId,
      stepInfo: {
        stepName: this.$route.params.stepName,
        stepId: this.$route.params.stepId,
        stepType: this.$route.params.stepType,
      },
      contentWidth: 0,
      contentHeight: 0,
      selectedResources: {},
      selectedTools: [],
      selectedData: [],
      ops: {
        bar: {
          background: "#808695",
        },
      },
      isOpenTool: false,
      typeSelected: "All",
      typeOptions: [
        "All",
        "General step",
        "Context definition & resource collection",
        "Data processing",
        "Data visualization",
        "Geographic model construction",
        "Model effectiveness evaluation",
        "Geographical simulation",
        "Quantitative and qualitative analysis",
        "Decision-making for management",
        "Others",
      ],
      panelList: [],
      jupyterModal: false,
      currentModelInfo: {
        toolUrl: "",
        tid: "",
      },
      modelUrl: "",
      modelDoi: "16e31602-bd05-4da4-bd01-cb7582c21ae8",
      pageParams: {},
      intermediate: "",
      updateStepInfo: false,
      modelInstances: [],
      instanceFolk: {},
    };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 200;
    },
    init() {
      this.getPageParams();
    },
    getPageParams() {
      this.pageParams = {
        pageId: this.projectId,
        userId: this.userInfo.userId,
        userName: this.userInfo.userName,
      };
    },
    async getSelectedResources() {
      let { data } = await get(
        `/GeoProblemSolving/r/resource/get/${this.projectId}`
      );
      this.$set(this, "selectedResources", data);
      this.$set(this, "selectedTools", data.toolItemList);
      this.$set(this, "selectedData", data.dataItemList);
      // this.getDoi();
      // this.$set(this, "modelUrl", data.toolItemList[0].url);
    },

    //get modelinstance
    async getStepInfo() {
      let { data } = await get(
        `/GeoProblemSolving/r/modelInstance/get/${this.stepInfo.stepId}`
      );
      // this.modelInstances = data;
      console.log(data);
      this.modelInstances = data;
      // this.modelInstances.modelInstancesJson = instance;

      this.updateStepInfo = true;
    },

    useTool(toolInfo) {
      if (toolInfo.toolName == "Jupyter notebook") {
        this.jupyterModal = true;
        return;
      }
      // this.currentModelInfo = toolInfo;
      this.currentModelInfo.tid = toolInfo.tid;
      this.currentModelInfo.toolUrl = toolInfo.toolUrl;
    },

    outputRecords(val) {
      // console.log(val);
    },

    async saveModelInstance(instance) {
      let stepResource = instance;
      // stepResource["modelInstances"] = this.modelInstances;
      // if (this.updateStepInfo == false) {
      // save
      stepResource["stepName"] = this.stepInfo.stepName;
      stepResource["stepId"] = this.stepInfo.stepId;
      stepResource["type"] = this.stepInfo.stepType;
      stepResource["stepDescription"] = "";
      stepResource["pid"] = this.projectId;

      stepResource["user"] = this.userInfo.userName;
      stepResource["userId"] = this.userInfo.userId;
      this.modelInstances.push(stepResource);
      console.log(this.modelInstances);

      let data = await post(
        `/GeoProblemSolving/r/modelInstance/save`,
        stepResource
      );
      // console.log(data);
      // }
      // else {
      //   let data = await post(
      //     `/GeoProblemSolving/r/modelInstance/update/${this.stepInfo.stepId}`,
      //     stepResource
      //   );
      //   // console.log(data);
      //   if (data.msg == "成功") {
      //     this.$message({
      //       message: "You have update your resource successfully",
      //       type: "success",
      //     });
      //     this.active == 0;
      //   }
      // }
    },

    folkInstance(instance) {
      console.log(instance);
      this.instanceFolk = instance;
      // this.currentModelInfo.tid = instance.tid;
      // this.currentModelInfo.toolUrl = instance.toolUrl;
      // this.getModelDoi(url);
    },

    getModelDoi(url) {
      let arr = url.split("/");
      this.modelDoi = arr[arr.length - 1];
    },
    downloadDataResource(data) {
      // console.log(data);
      window.open(data.url);
    },

    //创建mxgraph
    createMap() {
      console.log(this.filterCreateMapInstances);
      if (this.filterCreateMapInstances == "") {
        this.$message({
          message: "There is no selected instance",
          type: "error",
        });
      } else {
      }
    },
  },
  created() {
    this.initSize();
  },
  mounted() {
    if (this.userInfo) {
      this.getSelectedResources();
      this.getStepInfo();
      this.init();
    }
  },
};
</script>
<style lang='scss' scoped>
.main {
  margin: 0 20px;
}
.instance_name {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  word-break: break-all;
}
.data_info {
  height: 20px;
}
.data_info_hover {
  display: none;
}
.data_info_name {
  float: left;
  width: 250px;
}
.data_info:hover {
  cursor: pointer;
  background-color: rgba(99, 142, 197, 0.2);

//  -webkit-transform: translateY(-4px);
//     -ms-transform: translateY(-4px);
//     transform: translateY(-4px);
    -webkit-transition: all 0.2s ease-out;
    transition: all 0.2s ease-out;
  .data_info_hover {
    display: block;
    float: left;
    // right: 5px;
    color: rgb(45, 54, 92);
    // clear: both;
   
  }
}
</style>