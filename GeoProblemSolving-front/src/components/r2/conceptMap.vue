<template>
  <div class>
    <el-row>
      <el-col :span="20" :offset="2">
        <el-row class="mapTitle">
          <div>Concept Map</div>
        </el-row>

        <el-row>
          <el-card>
            <div class="mxGraph" :style="{height:contentHeight+'px'}">
              <mx-graph></mx-graph>
            </div>
          </el-card>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import mxGraph from "@/components/utils/mxGraph/mxGraph";
export default {
  props: {},
  components: { mxGraph },
  data() {
    return { contentHeight: 0 };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 270;
    },
  },
  created() {
    this.initSize();
  },
  mounted() {},
};
</script>
<style lang='scss' scoped>
.mapTitle {
  //   line-height: 35px;
  font-size: 35px;
  font-weight: 600;
  margin: 30px 0 10px 0;
}
</style>