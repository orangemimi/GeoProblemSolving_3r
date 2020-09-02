<!--  -->
<template>
  <div>
    <Row>
      <Col class="leftContainer" :span="16">
        <div class="tool_top">
          <div class="tool_title">
            <el-row v-show="switchValue">Public Tools</el-row>
            <el-row v-show="!switchValue">Private Tools</el-row>
          </div>
          <el-switch v-model="switchValue" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </div>
        <Card dis-hover v-show="switchValue">
          <vue-scroll :ops="ops" style="height:400px;">
            <Row :gutter="16">
              <draggable element="ul" :options="{group:'tool'}" v-model="filterPublicTools">
                <div v-for="(tool,index) in filterPublicTools" :key="index">
                  <Col :span="6" style="margin-bottom:10px">
                    <tool-card :toolFrom="tool"></tool-card>
                  </Col>
                </div>
              </draggable>
            </Row>
          </vue-scroll>
        </Card>
        <Card dis-hover v-show="!switchValue">
          <vue-scroll :ops="ops" style="height:400px;">
            <Row :gutter="16">
              <draggable element="ul" :options="{group:'tool'}" v-model="filterPersonalTools">
                <div v-for="(tool,index) in filterPersonalTools" :key="index">
                  <Col :span="6" style="margin-bottom:10px">
                    <tool-card :toolFrom="tool" :isOpenTool="false"></tool-card>
                  </Col>
                </div>
              </draggable>
            </Row>
          </vue-scroll>
        </Card>
      </Col>
      <Col class="rightContainer" :span="8">
        <div class="tool_select">Tools you select</div>

        <Card dis-hover>
          <vue-scroll :ops="ops" style="height:400px;">
            <draggable
              element="ul"
              :group="{name:'tool', put:true, pull:false}"
              v-model="sentTools"
              @add="addSentTool"
              style="min-height:400px"
            >
              <div v-for="(tool,index) in sentTools" :key="tool.index" style="margin-bottom:5px">
                <Col>
                  <Card style="width:100%">
                    <div class="ellipsis" style="width:150px">{{tool.toolName}}</div>
                    <Button
                      shape="circle"
                      icon="md-remove"
                      class="changeRedColor"
                      size="small"
                      style="float:right"
                      @click="removeSelectedTools(index)"
                    ></Button>
                  </Card>
                </Col>
              </div>
            </draggable>
          </vue-scroll>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import toolCard from "./toolCard";
import { get, del, post, put } from "@/axios";
import draggable from "vuedraggable";

export default {
  props: {
    userId: {
      type: String,
    },
    initTools: {
      type: Array,
    },
  },
  components: { toolCard, draggable },
  watch: {
    sentTools: {
      handler(val) {
        this.$emit("selectTools", val);
      },
      deep: true,
    },
    initTools: {
      handler(val) {
        this.initToolItems = this.initTools;
      },
      deep: true,
    },
  },

  data() {
    return {
      publicTools: [],
      personalTools: [],
      user: this.userId,
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
      initToolItems: this.initTools,
    };
  },

  computed: {
    filterPublicTools: {
      get() {
        let tools = this.publicTools;
        let type = this.typeSelected;
        let initTools = this.initToolItems;

        tools = tools.filter(
          (item) => !initTools.some((e) => e.toolName === item.toolName)
        );
        this.publicTools = tools;
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
        let initTools = this.initToolItems;

        tools = tools.filter(
          (item) => !initTools.some((e) => e.toolName === item.toolName)
        );
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
  async created() {
    await this.getPublicTools();
    await this.getPersonalTools();
  },
  methods: {
    init() {
      this.sentTools = this.initToolItems;

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
      console.log(this.personalTools);

      // this.filterShowListByType();
    },

    //add需要发送的tools
    addSentTool(evt) {
      let addedToolId = this.sentTools[evt.newDraggableIndex].tid;

      for (let i = 0; i < this.publicTools.length; i++) {
        if (this.publicTools[i].tid == addedToolId) {
          this.publicTools.splice(i, 1);
          //   this.$set(this, "filterPublicTools", this.publicTools);
          break;
        }
      }
      for (let i = 0; i < this.personalTools.length; i++) {
        if (this.personalTools[i].tid == addedToolId) {
          this.personalTools.splice(i, 1);
          //   this.$set(this, "filterPersonalTools", this.personalTools);
          break;
        }
      }
    },
    removeSelectedTools(index) {
      this.initToolItems = [];
      var removeToolInfo = this.sentTools[index];
      this.sentTools.splice(index, 1);
      // this.publicTools.push(removeToolInfo);
      if (removeToolInfo.privacy == "Public") {
        this.publicTools.push(removeToolInfo);
      } else {
        this.personalTools.push(removeToolInfo);
      }
    },
  },
};
</script>
<style lang='scss' scoped>
//@import url(); 引入公共css类
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
</style>