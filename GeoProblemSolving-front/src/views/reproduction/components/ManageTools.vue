<!--  -->
<template>
  <div>
    <el-row class="main">
      <div class="leftContainer">
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
        <el-card shadow="never" v-show="switchValue" class="card_contain">
          <vue-scroll :ops="ops">
            <el-row>
              <draggable
                element="ul"
                :options="{ group: 'tool' }"
                v-model="filterPublicTools"
              >
                <div v-for="(tool, index) in filterPublicTools" :key="index">
                  <div class="choose_tool_contain">
                    <tool-card :toolFrom="tool"></tool-card>
                  </div>
                </div>
              </draggable>
            </el-row>
          </vue-scroll>
        </el-card>
        <el-card shadow="never" v-show="!switchValue" class="card_contain">
          <vue-scroll :ops="ops" style="height: 380px">
            <el-row>
              <draggable
                element="ul"
                :options="{ group: 'tool' }"
                v-model="filterPersonalTools"
                class="scrollDrag"
              >
                <div v-for="(tool, index) in filterPersonalTools" :key="index">
                  <div class="choose_tool_contain">
                    <tool-card :toolFrom="tool" :isOpenTool="false"></tool-card>
                  </div>
                </div>
              </draggable>
            </el-row>
          </vue-scroll>
        </el-card>
      </div>
      <div class="rightContainer">
        <div class="tool_top">Tools you select</div>

        <el-card shadow="never" class="card_contain">
          <div class="container_back">
            Tools you have selected to apply to this project
          </div>
          <vue-scroll :ops="ops" style="height: 380px">
            <draggable
              element="ul"
              :group="{ name: 'tool', put: true, pull: false }"
              v-model="sentTools"
              @add="addSentTool"
              class="scrollDrag"
            >
              <div v-for="(tool, index) in sentTools" :key="tool.index">
                <div>
                  <el-card class="select_tools_contain">
                    <div class="ellipsis" style="width: 100px">
                      {{ tool.toolName }}
                    </div>
                    <i
                      class="el-icon-remove changeRedColor"
                      size="small"
                      @click="removeSelectedTools(index)"
                    ></i>
                  </el-card>
                </div>
              </div>
            </draggable>
          </vue-scroll>
        </el-card>
      </div>
    </el-row>
  </div>
</template>

<script>
import toolCard from "@/components/r2/ToolCard";
import { get, del, post, put } from "@/axios";
import draggable from "vuedraggable";

export default {
  components: { toolCard, draggable },
  data() {
    return {
      projectId: this.$route.params.projectId,
      publicTools: [],
      personalTools: [],
      userInfo: this.$store.getters.userInfo,
      userId: this.$store.getters.userInfo.userId,
      sentTools: [], //需要发送的工具
      ops: {
        bar: {
          background: "#808695",
          keepShow: true,
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
        }
        // console.log(tools);
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
        // console.log(tools);
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
    },

    async getSelectedTools() {
      let modelItem = await get(
        `/GeoProblemSolving/r/toolItems/${this.projectId}`
      );
      // this.sentTools = modelItem;
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
    async saveRecord(context, tag) {
      let record = {};
      record.eventId = context.id;
      record.eventType = "resourceCollection";
      record.projectId = this.projectInfomation.projectId;
      record.userId = this.userInfo.userId;
      record.content = `${this.userInfo.userName} has ${tag} a context definition in this reproducible project`;
      let data = await post(`/GeoProblemSolving/r/records`, record);
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
      let removeToolInfo = this.sentTools[index];
      await this.deleteToolSelect(removeToolInfo.tid);
      let {
        id,
        userId,
        pid,
        createTime,
        updateTime,
        ...param
      } = removeToolInfo;
      // console.log(param);

      this.sentTools.splice(index, 1);
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
.main {
  // height: 450px;
  .leftContainer {
    float: left;
    width: 220px;
    // margin: 0 5px;
  }
  .rightContainer {
    float: left;
    width: 220px;
    margin-left: 5px;
  }
}

.tool_top {
  padding: 5px 0;
  font-size: 16px;
  font-weight: 600;
  height: 40px;
  .tool_title {
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
.card_contain {
  height: 400px;
  width: 220px;
  clear: both;
  >>> .el-card__body {
    padding: 15px;
  }
  >>> .el-card.is-always-shadow,
  .el-card.is-hover-shadow:focus,
  .el-card.is-hover-shadow:hover {
    box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.1);
  }
  .container_back {
    position: absolute;
    font-weight: 600;
    font-size: 30px;
    color: rgba(153, 153, 153, 0.315);
    user-select: none;
    text-align: center;
    // white-space: normal;
    top: 130px;
    line-height: 35px;
    width: 180px;
  }

  .scrollDrag {
    min-height: 380px;
  }
}
.choose_tool_contain {
  margin-bottom: 10px;
}
.select_tools_contain {
  width: 165px;
  height: 50px;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.1);
  margin: 0 auto;
  margin-bottom: 10px;
  >>> .el-card__body {
    padding: 15px;
  }
}
</style>