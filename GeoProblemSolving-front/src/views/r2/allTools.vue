<template>
  <div class>
    <el-select v-model="typeSelected" style="width: 160px">
      <el-option v-for="item in typeOptions" :key="item.index" :value="item">{{
        item
      }}</el-option>
    </el-select>
    <div class="toolList" :style="{ height: contentHeight + 'px' }">
      <vue-scroll :ops="ops">
        <el-row>publicTools</el-row>
        <el-row class="tool_card">
          <el-col
            :span="24"
            v-for="tool in filterPublicTools"
            :key="tool.index"
          >
            <div style="margin: 5px" @click="useTool(tool)">
              <tool-card :toolFrom="tool" :isOpenTool="isOpenTool"></tool-card>
            </div>
          </el-col>
        </el-row>
        <el-row>personalTools</el-row>
        <el-row class="tool_card">
          <el-col
            :span="24"
            v-for="tool in filterPersonalTools"
            :key="tool.index"
          >
            <div style="margin: 5px" @click="useTool(tool)">
              <tool-card :toolFrom="tool" :isOpenTool="isOpenTool"></tool-card>
            </div>
          </el-col>
        </el-row>
      </vue-scroll>
    </div>
  </div>
</template>

<script>
import { get, del, post, put } from "@/axios";
import toolCard from "@/components/r2/ToolCard";
export default {
  props: {},

  components: { toolCard },

  computed: {
    filterPersonalTools() {
      let tools = this.personalTools;
      let type = this.typeSelected;
      if (type == "All") {
        return tools;
      } else {
        return tools.filter((tool) => {
          return tool.recomStep.includes(type);
        });
      }
    },

    filterPublicTools() {
      let tools = this.publicTools;
      let type = this.typeSelected;
      if (type == "All") {
        return tools;
      } else {
        return tools.filter((tool) => {
          return tool.recomStep.includes(type);
        });
      }
    },
  },

  data() {
    return {
      contentWidth: 0,
      contentHeight: 0,
      userInfo: this.$store.getters.userInfo,
      projectId: this.$route.params.projectId,
      publicTools: [],
      personalTools: [],
      showMenuItem: "publicTools",
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
    };
  },

  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 250;
      console.log(this.contentHeight);
    },
    async getPublicTools() {
      let data = await get(
        "/GeoProblemSolving/tool/inquiry/?key=privacy&value=Public"
      );
      this.$set(this, "publicTools", data);
    },

    async getPersonalTools() {
      let data = await get(
        `/GeoProblemSolving/tool/findByProvider/${this.userInfo.userId}`
      );
      this.$set(this, "personalTools", data);
      // this.filterShowListByType();
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
    console.log(this.userInfo);
    this.initSize();
  },
  mounted() {
    this.getPublicTools();
    if (this.userInfo) {
      this.getPersonalTools();
    }
  },
};
</script>
<style lang='scss' scoped>
.tool_card {
  max-height: 500px;
  max-width: 400px;
}
</style>