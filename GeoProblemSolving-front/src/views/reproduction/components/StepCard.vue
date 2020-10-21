<template>
  <div>
    <el-row>
      <el-col :span="24" v-for="(info, index) in infos" :key="index">
        <div class="card">
          <div class="top">
            <div class="title">{{ info.btnType }}</div>
            <div class="top-icon"><i class="el-icon-top-right"></i></div>
          </div>
          <div class="content">
            <vue-scroll class="scroll">
              <div class="content-comp">
                <component
                  :is="typeMapping(info.btnType)"
                  :projectInfo="projectInfo2"
                ></component>
              </div>
            </vue-scroll>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import ContextContent from "./ContextContent";
import ConstructionContent from "./ConstructionContent";
import ResourceContent from "./ResourceContent";
import ResultContent from "./ResultContent";
import AnalysisContent from "./AnalysisContent";
export default {
  components: {
    ContextContent,
    ConstructionContent,
    ResourceContent,
    ResultContent,
    AnalysisContent,
  },
  props: {
    cardInfos: {
      type: Array,
    },
    projectInfo: {
      type: Object,
    },
  },
  watch: {
    cardInfo: {
      handler(val) {
        this.info = val;
      },
      deep: true,
    },
    projectInfo: {
      handler(val) {
        console.log(val);
        this.projectInfo2 = val;
      },
      deep: true,
    },
  },
  data() {
    return {
      infos: this.cardInfos,
      projectInfo2: this.projectInfo,
    };
  },
  methods: {
    typeMapping(type) {
      let vueType;
      switch (type) {
        case "Context Definition":
          {
            vueType = "ContextContent";
          }
          break;
        case "Simulation Construction":
          {
            vueType = "ConstructionContent";
          }
          break;
      }
      return vueType;
    },
  },
};
</script>

<style lang='scss' scoped>
.card {
  background: #ffffff;
  box-shadow: 0px 3px 6px rgba(0, 0, 0, 0.16);
  opacity: 1;
  //   height: 500px;
  margin: 10px;
  .top {
    background: #444444;
    color: rgb(255, 255, 255);
    font-size: 16px;
    line-height: 30px;
    height: 30px;
    .title {
      margin-left: 10px;
      //   width: 200px;
      float: left;
    }
    .top-icon {
      float: right;
      margin-right: 10px;
      width: 20px;
    }
  }
  .content {
  }
  .scroll {
    height: 300px;
    .content-comp {
      margin: 10px;
    }
  }
}
</style>