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
                      <!-- {{tool}} -->
                      <tool-card :toolFrom="tool" :isOpenTool="isOpenTool"></tool-card>
                    </div>
                  </el-col>
                </el-row>
              </vue-scroll>
            </div>
            <!-- <all-tools></all-tools> -->
            <!-- <tools-selected></tools-selected> -->
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">Model item</div>
          <div>
            <model-item-info :pageParamsFrom="pageParams" :modelDoi="modelDoi" :selectedData="selectedData"></model-item-info>
            <!-- <router-link :to="{path:modelDoi}">son1</router-link>
            <router-view></router-view>-->
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">data resources</div>
          <div>
            <div v-for="(data,index) in selectedData" :key="index">{{data.name}}</div>
          </div>
        </el-card>
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
export default {
  props: {},
  components: { toolsSelected, allTools, toolCard, modelItemInfo },
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
  },
  data() {
    return {
      userInfo: this.$store.getters.userInfo,
      projectId: this.$route.params.projectId,
      contentWidth: 0,
      contentHeight: 0,
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
      modelUrl: "",
      modelDoi: "16e31602-bd05-4da4-bd01-cb7582c21ae8",
      pageParams: {},
    };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 200;
    },
    init() {
      this.pageParams = {
        pageId: this.projectId,
        userId: this.userInfo.userId,
        userName: this.userInfo.userName,
      };
    },
    async getSelectedTools() {
      let { data } = await get(
        `/GeoProblemSolving/r/resource/get/${this.projectId}`
      );
      this.$set(this, "selectedTools", data.toolItemList);
      this.$set(this, "selectedData", data.dataItemList);
      // this.getDoi();
      this.$set(this, "modelUrl", data.toolItemList[0].url);
      console.log(this.selectedTools);
    },

    useTool(toolInfo) {
      console.log(toolInfo);
      // 记录信息
      //   let toolRecords = {
      //     type: "tools",
      //     time: new Date().Format("yyyy-MM-dd HH:mm:ss"),
      //     who: this.userInfo.userName,
      //     content: "used a tool",
      //     toolType: toolInfo.toolName,
      //   };

      //   this.$emit("toolBehavior", toolRecords);
      //   this.addHistoryEvent(this.stepInfo.stepId, toolRecords);

      if (toolInfo.toolName == "Jupyter notebook") {
        this.jupyterModal = true;
        return;
      }

      var toolURL =
        '<iframe src="' +
        toolInfo.toolUrl +
        "?userName=" +
        this.userInfo.userName +
        "&userID=" +
        this.userInfo.userId +
        "&groupID=" +
        this.projectId +
        '" style="width: 100%;height:100%;" frameborder="0"></iframe>';

      console.log(toolURL);
      var demoPanelTimer = null;
      var panel = jsPanel.create({
        theme: "success",
        headerTitle: toolInfo.toolName,
        footerToolbar: '<p style="height:10px"></p>',
        contentSize: "800 400",
        content: toolURL,
        disableOnMaximized: true,
        dragit: {
          containment: 5,
        },
        closeOnEscape: true,
        onclosed: function (panel, status, closedByUser) {
          window.clearTimeout(demoPanelTimer);
        },
      });
      // panel.resizeit("disable");
      $(".jsPanel-content").css("font-size", "0");
      this.panelList.push(panel);
      //   this.$emit("toolPanel", panel);
    },
  },
  created() {
    this.initSize();
  },
  mounted() {
    if (this.userInfo) {
      this.getSelectedTools();
      // this.init();
    }
  },
};
</script>
<style lang='scss' scoped>
.main {
  margin: 0 20px;
}
</style>