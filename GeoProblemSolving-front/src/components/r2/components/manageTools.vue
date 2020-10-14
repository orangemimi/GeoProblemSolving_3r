<!--  -->
<template>
  <div>
    <el-row>
      <el-col class="leftContainer" :span="16">
        <div class="tool_top">
          <div class="tool_title">
            <el-row v-show="switchValue">Public Tools</el-row>
            <el-row v-show="!switchValue">Private Tools</el-row>
          </div>
          <el-switch
            v-model="switchValue"
            active-color="#13ce66"
            inactive-color="#ff4949"
          ></el-switch>
        </div>
        <el-card shadow="never" v-show="switchValue">
          <vue-scroll :ops="ops" style="height: 400px">
            <el-row :gutter="16">
              <draggable
                element="ul"
                :options="{ group: 'tool' }"
                v-model="filterPublicTools"
              >
                <div v-for="(tool, index) in filterPublicTools" :key="index">
                  <el-col :span="6" style="margin-bottom: 10px">
                    <tool-card :toolFrom="tool"></tool-card>
                  </el-col>
                </div>
              </draggable>
            </el-row>
          </vue-scroll>
        </el-card>
        <el-card shadow="never" v-show="!switchValue">
          <vue-scroll :ops="ops" style="height: 400px">
            <el-row :gutter="16">
              <draggable
                element="ul"
                :options="{ group: 'tool' }"
                v-model="filterPersonalTools"
              >
                <div v-for="(tool, index) in filterPersonalTools" :key="index">
                  <el-col :span="6" style="margin-bottom: 10px">
                    <tool-card :toolFrom="tool" :isOpenTool="false"></tool-card>
                  </el-col>
                </div>
              </draggable>
            </el-row>
          </vue-scroll>
        </el-card>
      </el-col>
      <el-col class="rightContainer" :span="8">
        <div class="tool_select">Tools you select</div>

        <el-card shadow="never">
          <vue-scroll :ops="ops" style="height: 400px">
            <draggable
              element="ul"
              :group="{ name: 'tool', put: true, pull: false }"
              v-model="sentTools"
              @add="addSentTool"
              style="min-height: 400px"
            >
              <div
                v-for="(tool, index) in sentTools"
                :key="tool.index"
                style="margin-bottom: 5px"
              >
                <el-col>
                  <el-card style="width: 100%">
                    <div class="ellipsis" style="width: 150px">
                      {{ tool.toolName }}
                    </div>
                    <i
                      class="el-icon-remove changeRedColor"
                      size="small"
                      @click="removeSelectedTools(index)"
                    ></i>
                    <!-- <el-button
                      circle
                      icon="el-icon-remove"
                      class="changeRedColor"
                      size="small"
                      @click="removeSelectedTools(index)"
                    ></el-button> -->
                  </el-card>
                </el-col>
              </div>
            </draggable>
          </vue-scroll>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import toolCard from "./toolCard";
import { get, del, post, put } from "@/axios";
import draggable from "vuedraggable";

export default {
  components: { toolCard, draggable },
  // watch: {
  //   sentTools: {
  //     handler(val) {
  //       this.$emit("selectTools", val);
  //     },
  //     deep: true,
  //   },
  // },

  data() {
    return {
      projectId: this.$route.params.projectId,
      publicTools: [],
      personalTools: [],
      userId: this.$store.getters.userInfo.userId,
      sentTools: [], //需要发送的工具
      ops: {
        bar: {
          background: "#808695",
        },
      },
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
      switchValue: true,
      // initToolItems: [],
    };
  },

  computed: {
    filterPublicTools: {
      get() {
        let tools = this.publicTools;
        let type = this.typeSelected;
        let initTools = this.sentTools;

        if (initTools != undefined) {
          tools = tools.filter((item) =>
            initTools.every((e) => e.toolName != item.toolName)
          );
          // this.publicTools = tools;
        }
        console.log(tools);
        this.$set(this, "publicTools", tools);

        if (type == "All") {
          return tools;
        } else {
          return tools.filter((tool) => {
            return tool.recomStep.includes(type);
          });
        }
      },
      set(val) {
        let tools = (this.publicTools = val);
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
    filterPersonalTools: {
      get() {
        let tools = this.personalTools;
        let type = this.typeSelected;
        let initTools = this.sentTools;
        if (initTools != undefined) {
          tools = tools.filter((item) =>
            initTools.every((e) => e.toolName != item.toolName)
          );
        }
        console.log(tools);
        this.$set(this, "personalTools", tools);
        if (type == "All") {
          return tools;
        } else {
          return tools.filter((tool) => {
            return tool.recomStep.includes(type);
          });
        }
      },
      set(val) {
        let tools = (this.personalTools = val);

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
  },

  mounted() {
    this.init();
  },

  methods: {
    async init() {
      await this.getPublicTools();
      await this.getPersonalTools();
      await this.getSelectedTools();
      // this.sentTools = this.initToolItems;
    },

    async getSelectedTools() {
      let modelItem = await get(
        `/GeoProblemSolving/r/toolItems/${this.projectId}`
      );
      this.sentTools = modelItem;
      // this.initToolItems = modelItem;
      this.$set(this, "sentTools", modelItem);
    },

    async getPublicTools() {
      let data = await get(
        "/GeoProblemSolving/tool/inquiry/?key=privacy&value=Public"
      );
      this.$set(this, "publicTools", data);
    },

    async getPersonalTools() {
      let data = await get(
        `/GeoProblemSolving/tool/findByProvider/${this.userId}`
      );
      this.$set(this, "personalTools", data);
    },

    //add需要发送的tools
    async addSentTool(evt) {
      let addedToolId = this.sentTools[evt.newDraggableIndex].tid;
      await this.saveToolSelect(this.sentTools[evt.newDraggableIndex]);
      this.publicTools.forEach((tool) => {
        if (tool.tid == addedToolId) {
          this.publicTools.splice(i, 1);
        }
      });

      this.personalTools.forEach((tool) => {
        if (tool.tid == addedToolId) {
          this.personalTools.splice(i, 1);
        }
      });
    },
    async saveToolSelect(tool) {
      let { id, createTime, updateTime, ...tool2 } = tool;
      let resource = {
        userId: this.userId,
        pid: this.projectId,
        ...tool2,
      };
      let data = await post(`/GeoProblemSolving/r/toolItems`, resource);
    },

    async removeSelectedTools(index) {
      console.log(this.sentTools);
      let removeToolInfo = this.sentTools[index];
      console.log(removeToolInfo.id);
      await this.deleteToolSelect(removeToolInfo.tid);
      let {
        id,
        userId,
        pid,
        createTime,
        updateTime,
        ...param
      } = removeToolInfo;
      console.log(param);

      this.sentTools.splice(index, 1);
      // this.publicTools.push(removeToolInfo);
      if (removeToolInfo.privacy == "Public") {
        this.publicTools.push(param);
      } else {
        this.personalTools.push(param);
      }
    },

    async deleteToolSelect(tid) {
      await del(`/GeoProblemSolving/r/toolItems/${tid}`);
    },
  },
};
</script>
<style lang='scss' scoped>
.ellipsis {
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: top;
}
.tool_top {
  margin: 5px 0;
  .tool_title {
    font-size: 16px;
    font-weight: 600;
    float: left;
    width: 100px;
  }
}
.tool_select {
  margin: 5px 0;
  font-size: 16px;
  font-weight: 600;
}
.changeRedColor {
  font-size: 18px;
  color: red;
  float: right;
}
.changeRedColor:hover {
  cursor: pointer;
}
</style>