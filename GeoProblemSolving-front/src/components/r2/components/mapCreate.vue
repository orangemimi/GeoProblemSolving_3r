<template>
  <div class>
    <el-row>
      <mx-graph :sendXml="sendFlowChartFromXml"></mx-graph>
    </el-row>
  </div>
</template>

<script>
import mxGraph from "@/components/utils/mxGraph/mxGraph";
import { get, del, post, patch } from "../../../axios";

export default {
  components: { mxGraph },

  props: {
    createMapInstances: {
      type: Array,
    },
    dataItems: {
      type: Array,
    },
    initXml: {
      type: String,
    },
  },

  watch: {
    dataItems: {
      handler(val) {
        this.dataItemList = val;
      },
      deep: true,
    },
    initXml: {
      handler(val) {
        this.sendXml = val;
      },
      deep: true,
    },

    createMapInstances: {
      handler(val) {
        console.log(val);
        this.checkedInstances = val;
        this.initCreate();
      },
      deep: true,
    },
  },

  computed: {
    filterDirectDataResource() {
      return this.dataItemList.filter((data) => data.isDirect == true);
    },
    filterIndirectDataResource() {
      return this.dataItemList.filter((data) => data.isDirect == false);
    },
    sendFlowChartFromXml() {
      return `<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>${this.sendXml}</root></mxGraphModel>`;
    },
  },

  data() {
    return {
      checkedInstances: this.createMapInstances,
      dataItemList: this.dataItems,
      contentHeight: 0,

      sendXml: "",

      mxNodes: [],

      dataNodes: [],
      dataNodesIntermedia: [],

      nextStartIndex: 0,
      // updateXml: false,
      stepInfo: this.$route.params,
    };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      // console.log(this.$route.params);
      this.contentHeight = window.innerHeight - 270;
    },

    // async getMap() {
    //   let data = await get(
    //     `/GeoProblemSolving/r/flowcharts/one/${this.stepInfo.stepId}`
    //   );
    //   if (data != null) {
    //     //   this.updateXml = false;
    //     // } else {
    //     //   // this.getSelectedInstances(data.modelInstanceIdList);
    //     //   this.updateXml = true;
    //     this.sendXml = data.mapXml;
    //     // console.log(this.sendXml);
    //   }
    // },

    async initCreate() {
      // console.log(this.$route.params.stepId);

      this.dataNodes = [];
      this.dataNodesIntermedia = [];

      let directDataResource = [...this.filterDirectDataResource];

      let inDirectDataResource = [...this.filterIndirectDataResource];
      this.getNodeLinkInstance(directDataResource, this.checkedInstances);
      this.getNodeLinkInstance(inDirectDataResource, this.checkedInstances);
      this.mxNodes = [];
      this.getNodes(this.dataNodes);
      this.createXml();

      this.$emit("emitxml", this.sendXml);
      // await this.createFlowChart();
    },

    getNodeLinkInstance(dataResource, checkedInstances) {
      // console.log(checkedInstances);
      checkedInstances.forEach((instance) => {
        dataResource.forEach((data) => {
          let toModelInstanceList = data.toModelInstanceList;

          if (toModelInstanceList != null) {
            toModelInstanceList.forEach((id) => {
              if (instance.id == id) {
                data.hasOwnProperty("to") ? "" : (data["to"] = []);
                instance.hasOwnProperty("from") ? "" : (instance["from"] = []);
                data["to"].push(instance);
                instance["from"].push(data);
                if (!data.hasOwnProperty("from")) {
                  this.dataNodes.includes(data)
                    ? ""
                    : this.dataNodes.push(data);
                }
              }
            });
          }

          let fromModelInstance = data.fromModelInstance;
          if (fromModelInstance != null) {
            if (instance.id == fromModelInstance) {
              this.dataNodesIntermedia.includes(data)
                ? ""
                : this.dataNodesIntermedia.push(data);
              data.hasOwnProperty("from") ? "" : (data["from"] = []);
              instance.hasOwnProperty("to") ? "" : (instance["to"] = []);
              data["from"].push(instance);
              instance["to"].push(data);
            }
          }
        });
      });
    },

    getNodes(dataNodes) {
      this.nextStartIndex = 2;
      dataNodes.forEach((node, index) => {
        node.mxIndex = this.nextStartIndex;
        node.vertex = "1";
        this.mxNodes.push(node);

        if (node.hasOwnProperty("to") && node.to != []) {
          this.nextStartIndex += node.to.length * 2;
        } else {
          this.nextStartIndex += 2;
        }
        this.getNextInstance(node.to, node.mxIndex);
      });
    },

    getNextInstance(instances, sourceNodeIndex) {
      instances.forEach((nextInstance, nextIndex) => {
        let lineNode = {
          mxIndex: this.nextStartIndex - 1,
          source: sourceNodeIndex,
          target: this.nextStartIndex,
          edge: "1",
        };
        this.mxNodes.push(lineNode);

        nextInstance.mxIndex = this.nextStartIndex; //mxTargetIndex=mxIndex
        nextInstance.mxSourceIndex = sourceNodeIndex;
        nextInstance.vertex = "1";
        this.mxNodes.push(nextInstance);

        if (nextInstance.hasOwnProperty("to") && nextInstance.to != []) {
          // for (let i = 0; i < nextInstance.to.length; i++) {
          this.nextStartIndex += nextInstance.to.length * 2;
          // }
        } else {
          this.nextStartIndex += 2;
        }
        if (nextInstance.hasOwnProperty("to")) {
          this.getNextInstance(nextInstance.to, nextInstance.mxIndex);
        }
      });
    },

    createXml() {
      let nodes = this.mxNodes;
      // console.log(this.mxNodes);
      let xml = "";

      let dataNodeStyle =
        "fillColor=#f8f5ec;strokeColor=rgb(200, 200, 200);strokeWidth=1;shape=ellipse;align=center;imageAlign=center;imageVerticalAlign=top";
      let modelNodeStyle =
        "fillColor=transparent;strokeColor=#000000;strokeWidth=1;shape=rectangle;align=center;imageAlign=center;imageVerticalAlign=top";
      let directNodes = nodes.filter((node) => {
        return node.isDirect == true;
      });

      directNodes.forEach((node, index) => {
        xml += this.xmlVertex(
          node.mxIndex,
          node.name,
          dataNodeStyle,
          index * 100,
          index * 400
        );
      });
      nodes.forEach((node, index) => {
        if (node.hasOwnProperty("vertex")) {
          if (node.hasOwnProperty("isDirect") && node.isDirect == false) {
            xml += this.xmlVertex(
              node.mxIndex,
              node.name,
              dataNodeStyle,
              index * 60,
              300
            );
          } else if (!node.hasOwnProperty("isDirect")) {
            xml += this.xmlVertex(
              node.mxIndex,
              node.name,
              modelNodeStyle,
              index * 60,
              150
            );
          }
        } else if (node.hasOwnProperty("edge")) {
          xml += this.xmlEdge(node.mxIndex, node.source, node.target);
        }
      });

      this.sendXml = xml;
    },

    xmlVertex(id, title, style, x, y) {
      let stepId = this.$route.params.stepId;
      let content = `<mxCell id="${stepId}+${id}" title="${title}" vertex="1" parent="1" style="${style}"><mxGeometry x="${x}" y="${y}" width="150" height="100" as="geometry" /></mxCell>`;
      return content;
    },

    xmlEdge(id, source, target) {
      let stepId = this.$route.params.stepId;
      let content = `<mxCell id="${stepId}+${id}"  source="${stepId}+${source}" target="${stepId}+${target}" edge="1" parent="1"><mxGeometry relative="1" as="geometry"/></mxCell>`;
      return content;
    },
  },
  created() {
    this.initSize();
  },
  // mounted() {
  //   this.getMap();
  // },
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