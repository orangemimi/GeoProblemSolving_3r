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
              <mx-graph :sendXml="sendXml"></mx-graph>
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

                <el-checkbox v-model="instance.checkedForMap"></el-checkbox>
                <i class="el-icon-connection" @click="folkInstance(instance)"></i>
                <i class="el-icon-close" @click="deleteInstance(instance.id)"></i>
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
      }
    },
    filterModelInstance() {
      let data = this.modelInstances;
      return data.filter((item) => (item.statesJson = JSON.parse(item.states)));
    },
    //create Map instance
    filterCreateMapInstances() {
      return this.filterModelInstance.filter(
        (instance) => instance.checkedForMap == true
      );
    },
    filterDirectDataResource() {
      return this.selectedData.filter((data) => data.isDirect == true);
    },
    filterIndirectDataResource() {
      return this.selectedData.filter((data) => data.isDirect == false);
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
      isDirectInstance: "",
      directInstances: [],
      inDirectInstances: [],
      dataNodes: [],
      mxNodes: [],
      sendXml: "",
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
        stepName: this.$route.params.stepName,
        stepId: this.$route.params.stepId,
        stepType: this.$route.params.stepType,
      };
    },
    async getSelectedResources() {
      let { data } = await get(
        `/GeoProblemSolving/r/resource/get/${this.projectId}`
      );
      this.$set(this, "selectedResources", data);
      this.$set(this, "selectedTools", data.toolItemList);
      this.$set(this, "selectedData", data.dataItemList);
    },

    //get modelinstance
    async getStepInfo() {
      let { data } = await get(
        `/GeoProblemSolving/r/modelInstance/get/${this.stepInfo.stepId}`
      );
      console.log(data);
      this.modelInstances = data;

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

    saveModelInstance(instance) {
      this.modelInstances.push(instance);
    },

    folkInstance(instance) {
      // console.log(instance);
      this.instanceFolk = instance;
    },

    async deleteInstance(instanceId) {
      await del(`/GeoProblemSolving/r/modelInstance/delete/${instanceId}`);
      this.modelInstances = this.modelInstances.filter((item) => {
        return item.id !== instanceId;
      });
    },

    getModelDoi(url) {
      let arr = url.split("/");
      this.modelDoi = arr[arr.length - 1];
    },

    downloadDataResource(data) {
      window.open(data.url);
    },

    //创建mxgraph
    createMap() {
      // getXmlFromInstances
      if (this.filterCreateMapInstances == "") {
        this.$message({
          message: "There is no selected instance",
          type: "error",
        });
      } else {
        let checkedInstances = [];

        let checkedInstances2 = [];
        let directDataResource = [];
        let inDirectDataResource = [];
        Object.assign(checkedInstances, this.filterCreateMapInstances);
        Object.assign(checkedInstances2, this.filterCreateMapInstances);
        Object.assign(directDataResource, this.filterDirectDataResource);
        Object.assign(inDirectDataResource, this.filterIndirectDataResource);
        console.log(directDataResource, inDirectDataResource);
        let directInstance = [];
        let inDirectInstance = [];
        this.dataNodes = [];
        let isInDirectInstance = "";

        console.log(this.dataNodes);
        // console.log(directDataResource);
        //direct data as input
        directDataResource.forEach((data) => {
          if (data.modelInstanceInputList != null) {
            data.modelInstanceInputList.forEach((id) => {
              checkedInstances.forEach((instance) => {
                if (id == instance.id) {
                  if (!this.dataNodes.includes(data)) {
                    this.dataNodes.push(data);
                  }
                  if (!this.directInstances.includes(instance)) {
                    this.directInstances.push(instance);
                  }
                  if (!data.hasOwnProperty("to")) {
                    data["to"] = [];
                  }
                  if (!instance.hasOwnProperty("from")) {
                    instance["from"] = [];
                  }
                  data["to"].push(instance);
                  instance["from"].push(data);
                  console.log(data);
                }
              });
            });
          }
        });

        //indirect data as input ,将 indirect data as line to link
        inDirectDataResource.forEach((data) => {
          let outputId = data.modelInstanceOutput;
          let inputIdList = data.modelInstanceInputList;
          if (inputIdList != null) {
            inputIdList.forEach((id) => {
              checkedInstances.forEach((instance) => {
                if (id == instance.id) {
                  if (!this.inDirectInstances.includes(instance)) {
                    this.inDirectInstances.push(instance);
                  }

                  checkedInstances2.forEach((instance2) => {
                    if (outputId == instance2.id) {
                      if (!instance2.hasOwnProperty("to")) {
                        instance2["to"] = [];
                      }
                      if (!instance.hasOwnProperty("from")) {
                        instance["from"] = [];
                      }

                      instance2["to"].push(instance);
                      instance["from"].push(instance2);
                    }
                  });
                }
              });
            });
          }
        });
        console.log(
          this.dataNodes,
          this.directInstances,
          this.inDirectInstances
        );

        this.getNodes();
      }
    },
    getNodes() {
      this.mxNodes = [];
      let dataNodes = this.dataNodes;

      dataNodes.forEach((node, index) => {
        node.mxIndex = index + 2;
        node.vertex = "1";
        this.mxNodes.push(node);
        if (node.hasOwnProperty("to") && node.to != []) {
          this.getNextInstance(
            node.to,
            node.mxIndex,
            dataNodes.length + node.mxIndex
          );
        }
      });
    },
    getNextInstance(instances, sourceIndex, startIndex) {
      instances.forEach((nextInstance, nextIndex) => {
        nextInstance.mxIndex = startIndex + nextIndex * 2 + 1; //mxTargetIndex=mxIndex
        nextInstance.mxSourceIndex = sourceIndex;
        nextInstance.vertex = "1";
        this.mxNodes.push(nextInstance);

        let lineNode = {
          mxIndex: startIndex + nextIndex * 2,
          source: nextInstance.mxSourceIndex,
          target: nextInstance.mxIndex,
          edge: "1",
        };
        this.mxNodes.push(lineNode);

        if (nextInstance.hasOwnProperty("to") && nextInstance.to != []) {
          this.getNextInstance(
            nextInstance.to,
            nextInstance.mxIndex,
            nextInstance.mxIndex - 1 + instances.length * 2
          );
        } else {
          console.log(this.mxNodes);
          this.createXml();
        }
      });
    },
    createXml() {
      let nodes = this.mxNodes;
      let xml = `<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>`;
      let dataNodeStyle =
        "fillColor=#f8f5ec;strokeColor=rgb(200, 200, 200);strokeWidth=1;shape=ellipse;align=center;imageAlign=center;imageVerticalAlign=top";
      let modelNodeStyle =
        "fillColor=transparent;strokeColor=#000000;strokeWidth=1;shape=rectangle;align=center;imageAlign=center;imageVerticalAlign=top";

      nodes.forEach((node) => {
        if (node.hasOwnProperty("vertex") && node.hasOwnProperty("isDirect")) {
          xml += `<mxCell id="${node.mxIndex}" title="${node.name}" vertex="1" parent="1" style="${dataNodeStyle}"><mxGeometry x="0" y="0" width="150" height="100" as="geometry" /></mxCell>`;
        }
        if (node.hasOwnProperty("vertex") && !node.hasOwnProperty("isDirect")) {
          xml += `<mxCell id="${node.mxIndex}" title="${node.name}" vertex="1" parent="1" style="${modelNodeStyle}"><mxGeometry x="0" y="0" width="150" height="100" as="geometry" /></mxCell>`;
        }
        if (node.hasOwnProperty("edge")) {
          xml += `<mxCell id="${node.mxIndex}"  source="${node.source}" target="${node.target}" edge="1" parent="1"><mxGeometry relative="1" as="geometry"/></mxCell>`;
        }
      });
      xml += `</root></mxGraphModel>`;
      // console.log(xml);
      this.sendXml = xml;
    },

    getPreviousNode(instance) {
      instance.statesJson.forEach((state) => {
        let events = state.Event;
        //获得 instance id
        events.forEach((event) => {
          if (event.type == "response" && event.hasOwnProperty("url")) {
            let inputUrl = event.url;
            this.dataNodes.filter((data) => {
              //  if( ){}
            });
          }
        });
      });
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